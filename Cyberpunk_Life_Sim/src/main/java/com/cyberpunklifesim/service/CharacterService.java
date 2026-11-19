package com.cyberpunklifesim.service;

import com.cyberpunklifesim.dto.CharacterResponse;
import com.cyberpunklifesim.dto.CreateCharacterRequest;
import com.cyberpunklifesim.dto.LifeSummaryResponse;
import com.cyberpunklifesim.model.CharacterEvent;
import com.cyberpunklifesim.model.GameCharacter;
import com.cyberpunklifesim.model.Relationship;
import com.cyberpunklifesim.repository.CharacterEventRepository;
import com.cyberpunklifesim.repository.GameCharacterRepository;
import com.cyberpunklifesim.repository.InventoryItemRepository;
import com.cyberpunklifesim.repository.RelationshipRepository;
import com.cyberpunklifesim.repository.TransactionRepository;
import com.cyberpunklifesim.util.RandomUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Alles rondom het personage zelf: aanmaken, ophalen, verwijderen,
 * en de basisregels die andere services gebruiken (leeft hij nog? zit hij vast? heeft hij acties over?).
 */
@Service
public class CharacterService {

    private final GameCharacterRepository characterRepository;
    private final CharacterEventRepository characterEventRepository;
    private final InventoryItemRepository inventoryRepository;
    private final RelationshipRepository relationshipRepository;
    private final TransactionRepository transactionRepository;
    private final LifeHistoryService historyService;

    public CharacterService(GameCharacterRepository characterRepository,
                            CharacterEventRepository characterEventRepository,
                            InventoryItemRepository inventoryRepository,
                            RelationshipRepository relationshipRepository,
                            TransactionRepository transactionRepository,
                            LifeHistoryService historyService) {
        this.characterRepository = characterRepository;
        this.characterEventRepository = characterEventRepository;
        this.inventoryRepository = inventoryRepository;
        this.relationshipRepository = relationshipRepository;
        this.transactionRepository = transactionRepository;
        this.historyService = historyService;
    }

    // ---------- Ophalen ----------

    public List<CharacterResponse> getAllCharacters() {
        return characterRepository.findAll().stream().map(this::toResponse).toList();
    }

    public GameCharacter getCharacter(Long id) {
        return characterRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Personage met id " + id + " bestaat niet"));
    }

    /** Haalt een personage op en controleert dat hij nog leeft. */
    public GameCharacter getAliveCharacter(Long id) {
        GameCharacter character = getCharacter(id);
        if (!character.isAlive()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    character.getFullName() + " is overleden. Begin een nieuw leven.");
        }
        return character;
    }

    /** Haalt een personage op dat leeft én niet in de gevangenis zit (voor winkelen, solliciteren, ...). */
    public GameCharacter getFreeCharacter(Long id) {
        GameCharacter character = getAliveCharacter(id);
        if (character.isInPrison()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Je zit in de gevangenis. Dat kan nu niet. Nog " + character.getPrisonYearsLeft() + " jaar.");
        }
        return character;
    }

    public CharacterResponse toResponse(GameCharacter character) {
        int openEvents = characterEventRepository.countByCharacterIdAndResolvedFalse(character.getId());
        return CharacterResponse.from(character, openEvents);
    }

    public GameCharacter save(GameCharacter character) {
        return characterRepository.save(character);
    }

    // ---------- Aanmaken en verwijderen ----------

    public GameCharacter createCharacter(CreateCharacterRequest request) {
        // Het spel begint in 2077 op je 18e. Je kiest zelf je verjaardag, het geboortejaar is dus altijd 2059.
        int birthYear = GameCharacter.START_YEAR - GameCharacter.START_AGE;

        GameCharacter character = new GameCharacter(
                request.firstName().trim(),
                request.lastName().trim(),
                request.gender().trim(),
                request.birthDate().withYear(birthYear),
                request.birthPlace().trim(),
                request.nationality().trim());
        character = characterRepository.save(character);

        // Iedereen begint met een moeder en een vader
        relationshipRepository.save(new Relationship(character.getId(), RandomUtil.firstName() + " " + character.getLastName(),
                RandomUtil.between(40, 55), Relationship.FAMILY, "Moeder", RandomUtil.between(60, 90)));
        relationshipRepository.save(new Relationship(character.getId(), RandomUtil.firstName() + " " + character.getLastName(),
                RandomUtil.between(42, 58), Relationship.FAMILY, "Vader", RandomUtil.between(50, 90)));

        historyService.logImportant(character, "Je bent " + GameCharacter.START_AGE + " en begint je eigen leven in Neon City. "
                + "Het is " + GameCharacter.START_YEAR + ". Je hebt €$" + character.getMoney() + " op zak.");
        return character;
    }

    // @Transactional: alles wordt samen verwijderd, of (bij een fout) helemaal niets
    @Transactional
    public void deleteCharacter(Long id) {
        GameCharacter character = getCharacter(id);
        characterEventRepository.deleteByCharacterId(id);
        inventoryRepository.deleteByCharacterId(id);
        relationshipRepository.deleteByCharacterId(id);
        transactionRepository.deleteByCharacterId(id);
        historyService.deleteHistory(id);
        characterRepository.delete(character);
    }

    // ---------- Regels die andere services gebruiken ----------

    /** Iedere actie (solliciteren, misdaad, tijd doorbrengen) kost één van je acties van dit jaar. */
    public void useAction(GameCharacter character) {
        if (character.getActionsLeft() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Je hebt geen acties meer dit jaar. Klik op Next Year.");
        }
        character.setActionsLeft(character.getActionsLeft() - 1);
    }

    public void sendToPrison(GameCharacter character, int years, String reason) {
        character.setConvictions(character.getConvictions() + 1);
        character.setPrisonYearsLeft(character.getPrisonYearsLeft() + years);
        character.setLocation(GameCharacter.PRISON_LOCATION);
        character.changeHappiness(-20);
        historyService.logImportant(character, reason + " Je krijgt " + years + " jaar gevangenisstraf.");

        // In de gevangenis raak je je baan en opleiding kwijt
        if (character.getCareer() != null) {
            historyService.log(character, "Je bent je baan als " + character.getPosition() + " kwijt.");
            character.setCareer(null);
            character.setSalary(0);
        }
        if (character.getEducation() != null) {
            historyService.log(character, "Je moest stoppen met je opleiding " + character.getEducation().getName() + ".");
            character.setEducation(null);
            character.setEducationYearsLeft(0);
        }
    }

    public void releaseFromPrison(GameCharacter character, String text) {
        character.setPrisonYearsLeft(0);
        character.setLocation(GameCharacter.HOME_LOCATION);
        historyService.logImportant(character, text);
    }

    public void die(GameCharacter character, String cause) {
        character.die(cause);
        historyService.logImportant(character, "Overleden op " + character.getAge() + "-jarige leeftijd. Oorzaak: " + cause);

        // Openstaande gebeurtenissen kunnen niet meer afgehandeld worden
        for (CharacterEvent event : characterEventRepository.findByCharacterIdAndResolvedFalse(character.getId())) {
            event.resolve("Niet afgehandeld: het personage is overleden.");
            characterEventRepository.save(event);
        }
    }

    // ---------- Eindoverzicht ----------

    public LifeSummaryResponse getSummary(Long id) {
        GameCharacter c = getCharacter(id);
        return new LifeSummaryResponse(
                c.getFullName(),
                c.getBirthDate(),
                c.isAlive() ? null : c.getBirthDate().plusYears(c.getAge()),
                c.getAge(),
                c.isAlive(),
                c.getCauseOfDeath(),
                c.getHighestPosition(),
                c.getTotalEarned(),
                c.getCriminalRecord(),
                relationshipRepository.findByCharacterId(id),
                historyService.getImportantHistory(id));
    }
}

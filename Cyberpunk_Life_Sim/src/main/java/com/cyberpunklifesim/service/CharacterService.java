package com.cyberpunklifesim.service;

import com.cyberpunklifesim.model.GameCharacter;
import com.cyberpunklifesim.model.Item;
import com.cyberpunklifesim.model.Job;
import com.cyberpunklifesim.model.LifeEvent;
import com.cyberpunklifesim.model.RandomEvent;
import com.cyberpunklifesim.repository.GameCharacterRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Hier staan de spelregels voor characters:
 * aanmaken, ophalen, verwijderen, ouder worden, jobs uitvoeren en items kopen.
 */
@Service
public class CharacterService {

    private final GameCharacterRepository characterRepository;
    private final JobService jobService;
    private final ItemService itemService;
    private final LifeEventService lifeEventService;
    private final RandomEventService randomEventService;

    public CharacterService(GameCharacterRepository characterRepository, JobService jobService,
                            ItemService itemService, LifeEventService lifeEventService,
                            RandomEventService randomEventService) {
        this.characterRepository = characterRepository;
        this.jobService = jobService;
        this.itemService = itemService;
        this.lifeEventService = lifeEventService;
        this.randomEventService = randomEventService;
    }

    public List<GameCharacter> getAllCharacters() {
        return characterRepository.findAll();
    }

    public GameCharacter getCharacter(Long id) {
        return characterRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Character met id " + id + " bestaat niet"));
    }

    public List<LifeEvent> getEvents(Long id) {
        getCharacter(id); // geeft 404 als het character niet bestaat
        return lifeEventService.getEvents(id);
    }

    public GameCharacter createCharacter(String name) {
        // Een character moet een naam hebben
        if (name == null || name.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Naam mag niet leeg zijn");
        }

        // Nieuwe characters krijgen automatisch de startwaarden (zie GameCharacter)
        GameCharacter character = characterRepository.save(new GameCharacter(name.trim()));
        lifeEventService.log(character, "Je kwam aan in Neon City met €$" + character.getMoney() + " op zak.");
        return character;
    }

    // @Transactional: beide deletes gebeuren samen, of geen van beide
    @Transactional
    public void deleteCharacter(Long id) {
        GameCharacter character = getCharacter(id);
        lifeEventService.deleteEvents(id);
        characterRepository.delete(character);
    }

    /** Het character wordt een jaar ouder en er gebeurt iets willekeurigs. */
    public GameCharacter ageUp(Long id) {
        GameCharacter character = getCharacter(id);
        checkAlive(character);

        character.setAge(character.getAge() + 1);

        // Na je 40e word je langzaam zwakker
        if (character.getAge() > 40) {
            character.changeHealth(-3);
        }

        // Een willekeurige gebeurtenis
        RandomEvent event = randomEventService.pickRandomEvent();
        character.changeMoney(event.money());
        character.changeHealth(event.health());
        character.changeHappiness(event.happiness());
        character.changeSmarts(event.smarts());
        lifeEventService.log(character, event.text());

        // Kun je doodgaan? Bij 0 health altijd, en na je 60e steeds vaker van ouderdom
        if (character.getHealth() <= 0) {
            die(character, "Je lichaam gaf het op.");
        } else if (character.getAge() >= 60 && randomEventService.chance((character.getAge() - 60) * 4)) {
            die(character, "Je stierf rustig van ouderdom.");
        }

        return characterRepository.save(character);
    }

    public GameCharacter doJob(Long characterId, Long jobId) {
        GameCharacter character = getCharacter(characterId);
        checkAlive(character);
        Job job = jobService.getJob(jobId);

        // Spelregel: zwaardere jobs vereisen Street Cred en smarts
        if (character.getStreetCred() < job.getRequiredStreetCred()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Niet genoeg Street Cred. Nodig: " + job.getRequiredStreetCred()
                            + ", jij hebt: " + character.getStreetCred());
        }
        if (character.getSmarts() < job.getRequiredSmarts()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Niet slim genoeg. Nodig: " + job.getRequiredSmarts()
                            + " smarts, jij hebt: " + character.getSmarts());
        }

        // Beloning: geld en Street Cred erbij, maar het kost wel health
        character.changeMoney(job.getPayout());
        character.setStreetCred(character.getStreetCred() + job.getStreetCredReward());
        character.setJobsCompleted(character.getJobsCompleted() + 1);
        character.changeHealth(-job.getHealthCost());
        lifeEventService.log(character, "Je deed de job '" + job.getName() + "' en verdiende €$" + job.getPayout() + ".");

        if (character.getHealth() <= 0) {
            die(character, "Je kwam om tijdens de job '" + job.getName() + "'.");
        }

        return characterRepository.save(character);
    }

    public GameCharacter buyItem(Long characterId, Long itemId) {
        GameCharacter character = getCharacter(characterId);
        checkAlive(character);
        Item item = itemService.getItem(itemId);

        // Spelregel: je kunt alleen kopen als je genoeg geld hebt
        if (character.getMoney() < item.getPrice()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Niet genoeg geld. Prijs: " + item.getPrice() + ", jij hebt: " + character.getMoney());
        }

        // Geld eraf, item in de inventory en de stats-bonus erbij
        character.changeMoney(-item.getPrice());
        character.getInventory().add(item);
        character.changeHealth(item.getHealthBoost());
        character.changeHappiness(item.getHappinessBoost());
        character.changeSmarts(item.getSmartsBoost());
        lifeEventService.log(character, "Je kocht een " + item.getName() + " voor €$" + item.getPrice() + ".");

        return characterRepository.save(character);
    }

    // --- Hulpmethodes ---

    private void checkAlive(GameCharacter character) {
        if (!character.isAlive()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, character.getName() + " is overleden. Start een nieuw leven.");
        }
    }

    private void die(GameCharacter character, String cause) {
        character.die(cause);
        lifeEventService.log(character, cause + " R.I.P. " + character.getName() + " (" + character.getAge() + " jaar)");
    }
}

package com.cyberpunklifesim.service;

import com.cyberpunklifesim.dto.AgeUpResponse;
import com.cyberpunklifesim.model.GameCharacter;
import com.cyberpunklifesim.util.RandomUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * De belangrijkste gameplay: Next Year (UC02).
 * Deze service roept de andere services aan in een vaste volgorde.
 */
@Service
public class AgeService {

    /** Huur en levensonderhoud per jaar. */
    public static final int LIVING_COSTS = 2_000;

    private final CharacterService characterService;
    private final FinanceService financeService;
    private final LifeHistoryService historyService;
    private final CareerService careerService;
    private final EducationService educationService;
    private final RelationshipService relationshipService;
    private final EventService eventService;

    public AgeService(CharacterService characterService, FinanceService financeService,
                      LifeHistoryService historyService, CareerService careerService,
                      EducationService educationService, RelationshipService relationshipService,
                      EventService eventService) {
        this.characterService = characterService;
        this.financeService = financeService;
        this.historyService = historyService;
        this.careerService = careerService;
        this.educationService = educationService;
        this.relationshipService = relationshipService;
        this.eventService = eventService;
    }

    public AgeUpResponse ageUp(Long characterId) {
        // 0. Alleen een levend personage, zonder openstaande gebeurtenissen, kan ouder worden
        GameCharacter character = characterService.getAliveCharacter(characterId);
        if (eventService.hasOpenEvents(characterId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Maak eerst een keuze bij je openstaande gebeurtenissen.");
        }

        // 1. Een jaar ouder, en weer nieuwe acties
        character.setAge(character.getAge() + 1);
        character.setActionsLeft(GameCharacter.ACTIONS_PER_YEAR);
        historyService.log(character, "Je bent " + character.getAge() + " geworden. Het is nu " + character.getCurrentYear() + ".");

        // 2. Gevangenis, of anders: werk, studie en vaste lasten
        if (character.isInPrison()) {
            prisonYear(character);
        } else {
            careerService.yearlyUpdate(character);
            educationService.yearlyUpdate(character);
            if (!character.isInPrison()) {
                financeService.change(character, -LIVING_COSTS, "Huur en levensonderhoud");
            }
        }

        // 3. Gezondheid en levensstijl
        lifestyle(character);

        // 4. Relaties
        relationshipService.yearlyUpdate(character);

        // 5. Overlijden?
        checkDeath(character);

        // 6. Nieuwe gebeurtenissen
        if (character.isAlive()) {
            eventService.generateEvents(character);
        }

        characterService.save(character);

        // 7. Overzicht van het jaar terugsturen
        List<String> yearSummary = historyService.getTextsForAge(characterId, character.getAge());
        return new AgeUpResponse(characterService.toResponse(character), yearSummary,
                eventService.getOpenEvents(characterId));
    }

    private void prisonYear(GameCharacter character) {
        character.setPrisonYearsLeft(character.getPrisonYearsLeft() - 1);
        character.changeHappiness(-5);
        if (character.getPrisonYearsLeft() == 0) {
            characterService.releaseFromPrison(character, "Je bent vrijgelaten uit de gevangenis.");
        } else {
            historyService.log(character, "Weer een jaar in de gevangenis. Nog " + character.getPrisonYearsLeft() + " jaar.");
        }
    }

    private void lifestyle(GameCharacter character) {
        // Je lichaam herstelt ieder jaar een beetje (in de gevangenis minder)
        character.changeHealth(character.isInPrison() ? 2 : 5);

        // Na je 50e word je ieder jaar sneller minder gezond
        if (character.getAge() > 50) {
            character.changeHealth(-(1 + (character.getAge() - 50) / 4));
        }
        // Schulden geven stress
        if (character.getMoney() < 0) {
            character.changeHappiness(-5);
            historyService.log(character, "Je schulden (€$" + character.getMoney() + ") bezorgen je stress.");
        }
        // Heel ongelukkig zijn is slecht voor je gezondheid
        if (character.getHappiness() < 20) {
            character.changeHealth(-3);
        }
    }

    private void checkDeath(GameCharacter character) {
        if (character.getHealth() <= 0) {
            characterService.die(character, "Slechte gezondheid");
        } else if (character.getAge() >= 65 && RandomUtil.chance((character.getAge() - 64) * 3)) {
            characterService.die(character, "Ouderdom");
        }
    }
}

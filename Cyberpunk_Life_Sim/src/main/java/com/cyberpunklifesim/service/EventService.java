package com.cyberpunklifesim.service;

import com.cyberpunklifesim.dto.ActionResponse;
import com.cyberpunklifesim.dto.EventResponse;
import com.cyberpunklifesim.model.CharacterEvent;
import com.cyberpunklifesim.model.EventChoice;
import com.cyberpunklifesim.model.GameCharacter;
import com.cyberpunklifesim.model.GameEvent;
import com.cyberpunklifesim.repository.CharacterEventRepository;
import com.cyberpunklifesim.repository.GameEventRepository;
import com.cyberpunklifesim.util.RandomUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Het gebeurtenissensysteem:
 * 1. Bij Next Year kiezen we willekeurig gebeurtenissen die passen bij het personage.
 * 2. De speler kiest een optie.
 * 3. Wij berekenen de gevolgen (geld, stats, relaties, gevangenis, ...).
 */
@Service
public class EventService {

    private final GameEventRepository eventRepository;
    private final CharacterEventRepository characterEventRepository;
    private final CharacterService characterService;
    private final FinanceService financeService;
    private final LifeHistoryService historyService;
    private final RelationshipService relationshipService;
    private final CareerService careerService;
    private final ShopService shopService;

    public EventService(GameEventRepository eventRepository, CharacterEventRepository characterEventRepository,
                        CharacterService characterService, FinanceService financeService,
                        LifeHistoryService historyService, RelationshipService relationshipService,
                        CareerService careerService, ShopService shopService) {
        this.eventRepository = eventRepository;
        this.characterEventRepository = characterEventRepository;
        this.characterService = characterService;
        this.financeService = financeService;
        this.historyService = historyService;
        this.relationshipService = relationshipService;
        this.careerService = careerService;
        this.shopService = shopService;
    }

    // ---------- Gebeurtenissen ophalen ----------

    public boolean hasOpenEvents(Long characterId) {
        return characterEventRepository.countByCharacterIdAndResolvedFalse(characterId) > 0;
    }

    public List<EventResponse> getOpenEvents(Long characterId) {
        characterService.getCharacter(characterId); // 404 als het personage niet bestaat
        return characterEventRepository.findByCharacterIdAndResolvedFalse(characterId).stream()
                .map(EventResponse::from)
                .toList();
    }

    // ---------- Gebeurtenissen kiezen (bij Next Year) ----------

    public void generateEvents(GameCharacter character) {
        List<GameEvent> possible = new ArrayList<>();
        for (GameEvent event : eventRepository.findAll()) {
            if (isPossible(character, event)) {
                possible.add(event);
            }
        }
        if (possible.isEmpty()) {
            return;
        }

        // Meestal één gebeurtenis per jaar, soms twee
        Collections.shuffle(possible, RandomUtil.random());
        int count = (possible.size() > 1 && RandomUtil.chance(35)) ? 2 : 1;
        for (int i = 0; i < count; i++) {
            characterEventRepository.save(new CharacterEvent(character.getId(), possible.get(i), character.getAge()));
        }
    }

    /** Past deze gebeurtenis bij het personage? (leeftijd, baan, partner, gevangenis, ...) */
    private boolean isPossible(GameCharacter character, GameEvent event) {
        if (character.getAge() < event.getMinAge() || character.getAge() > event.getMaxAge()) {
            return false;
        }

        // In de gevangenis krijg je alleen gevangenis-gebeurtenissen, daarbuiten nooit
        boolean prisonEvent = event.getRequirement().equals(GameEvent.IN_PRISON);
        if (character.isInPrison() != prisonEvent) {
            return false;
        }

        Long id = character.getId();
        if (event.isOncePerLife() && characterEventRepository.existsByCharacterIdAndEvent_Id(id, event.getId())) {
            return false;
        }
        return switch (event.getRequirement()) {
            case GameEvent.NO_JOB -> character.getCareer() == null;
            case GameEvent.HAS_JOB -> character.getCareer() != null;
            case GameEvent.NO_PARTNER -> !relationshipService.hasPartner(id);
            case GameEvent.HAS_PARTNER -> relationshipService.hasPartner(id) && !relationshipService.isMarried(id);
            case GameEvent.MARRIED -> relationshipService.isMarried(id);
            case GameEvent.HAS_CYBERWARE -> shopService.hasCyberware(id);
            case GameEvent.STUDYING -> character.getEducation() != null;
            default -> true; // ANYONE en IN_PRISON
        };
    }

    // ---------- Een keuze maken (UC03) ----------

    public ActionResponse makeChoice(Long characterId, Long characterEventId, Long choiceId) {
        GameCharacter character = characterService.getAliveCharacter(characterId);

        CharacterEvent characterEvent = characterEventRepository.findById(characterEventId)
                .filter(e -> e.getCharacterId().equals(characterId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Gebeurtenis bestaat niet"));

        // Een gebeurtenis mag maar één keer afgehandeld worden
        if (characterEvent.isResolved()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Deze gebeurtenis heb je al afgehandeld.");
        }

        GameEvent event = characterEvent.getEvent();
        EventChoice choice = event.getChoices().stream()
                .filter(c -> c.getId().equals(choiceId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Deze keuze hoort niet bij deze gebeurtenis."));

        // Is er een kans dat er iets anders gebeurt? Dan gooien we "de dobbelsteen".
        String result;
        if (choice.getChance() > 0 && RandomUtil.chance(choice.getChance())) {
            result = applyChanceOutcome(character, choice, event.getTitle());
        } else {
            result = applyNormalOutcome(character, choice, event.getTitle());
        }

        characterEvent.resolve(result);
        characterEventRepository.save(characterEvent);
        historyService.log(character, event.getTitle() + " - " + choice.getText() + ": " + result);

        if (character.isAlive() && character.getHealth() <= 0) {
            characterService.die(character, "Overleden na: " + event.getTitle());
        }

        characterService.save(character);
        return new ActionResponse(true, result, characterService.toResponse(character));
    }

    private String applyNormalOutcome(GameCharacter character, EventChoice choice, String title) {
        financeService.change(character, choice.getMoney(), title);
        character.changeHealth(choice.getHealth());
        character.changeHappiness(choice.getHappiness());
        character.changeIntelligence(choice.getIntelligence());
        character.changeStreetCred(choice.getStreetCred());
        character.changeAppearance(choice.getAppearance());
        return addText(choice.getResultText(), doAction(character, choice.getAction()));
    }

    private String applyChanceOutcome(GameCharacter character, EventChoice choice, String title) {
        financeService.change(character, choice.getChanceMoney(), title);
        character.changeHealth(choice.getChanceHealth());
        character.changeHappiness(choice.getChanceHappiness());
        character.changeStreetCred(choice.getChanceStreetCred());
        if (choice.getChancePrisonYears() > 0) {
            characterService.sendToPrison(character, choice.getChancePrisonYears(), title + ": " + choice.getChanceText());
        }
        return addText(choice.getChanceText(), doAction(character, choice.getChanceAction()));
    }

    /** Speciale gevolgen van een keuze. Geeft een extra tekst terug (of null). */
    private String doAction(GameCharacter character, String action) {
        if (action == null) {
            return null;
        }
        if (action.startsWith(EventChoice.JOB_OFFER)) {
            String careerName = action.substring(EventChoice.JOB_OFFER.length());
            return careerService.offerJob(character, careerName);
        }
        return switch (action) {
            case EventChoice.NEW_FRIEND -> relationshipService.newFriend(character);
            case EventChoice.NEW_PARTNER -> relationshipService.newPartner(character);
            case EventChoice.MARRY -> relationshipService.marry(character);
            case EventChoice.HAVE_CHILD -> relationshipService.haveChild(character);
            case EventChoice.BREAK_UP -> relationshipService.breakUp(character);
            case EventChoice.MAKE_ENEMY -> relationshipService.makeEnemy(character);
            case EventChoice.GET_FIRED -> careerService.fire(character, "Je baas pikte het niet.");
            case EventChoice.ESCAPE_PRISON -> {
                characterService.releaseFromPrison(character, "Je bent ontsnapt uit de gevangenis!");
                yield "Je bent vrij!";
            }
            default -> null;
        };
    }

    private String addText(String text, String extra) {
        return extra == null ? text : text + " " + extra;
    }
}

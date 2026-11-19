package com.cyberpunklifesim.service;

import com.cyberpunklifesim.dto.ActionResponse;
import com.cyberpunklifesim.model.GameCharacter;
import com.cyberpunklifesim.model.Relationship;
import com.cyberpunklifesim.repository.RelationshipRepository;
import com.cyberpunklifesim.util.RandomUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Vrienden, partners, familie en vijanden.
 * Relaties ontstaan vooral door keuzes bij gebeurtenissen (zie EventService).
 */
@Service
public class RelationshipService {

    private final RelationshipRepository relationshipRepository;
    private final CharacterService characterService;
    private final LifeHistoryService historyService;

    public RelationshipService(RelationshipRepository relationshipRepository, CharacterService characterService,
                               LifeHistoryService historyService) {
        this.relationshipRepository = relationshipRepository;
        this.characterService = characterService;
        this.historyService = historyService;
    }

    public List<Relationship> getRelationships(Long characterId) {
        characterService.getCharacter(characterId); // 404 als het personage niet bestaat
        return relationshipRepository.findByCharacterId(characterId);
    }

    public boolean hasPartner(Long characterId) {
        return !relationshipRepository.findByCharacterIdAndType(characterId, Relationship.PARTNER).isEmpty();
    }

    public boolean isMarried(Long characterId) {
        return relationshipRepository.findByCharacterIdAndType(characterId, Relationship.PARTNER).stream()
                .anyMatch(Relationship::isMarried);
    }

    /** Tijd doorbrengen met iemand: relatie +10, happiness +5. Kost één actie. */
    public ActionResponse spendTime(Long characterId, Long relationshipId) {
        GameCharacter character = characterService.getFreeCharacter(characterId);
        Relationship relationship = relationshipRepository.findById(relationshipId)
                .filter(r -> r.getCharacterId().equals(characterId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Relatie bestaat niet"));

        if (relationship.getType().equals(Relationship.ENEMY)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Je wilt geen tijd doorbrengen met een vijand.");
        }

        characterService.useAction(character);
        relationship.changeLevel(10);
        character.changeHappiness(5);
        relationshipRepository.save(relationship);

        String message = "Je bracht tijd door met " + relationship.getName() + " (" + relationship.getLabel() + ").";
        historyService.log(character, message);
        characterService.save(character);
        return new ActionResponse(true, message, characterService.toResponse(character));
    }

    // ---------- Gebruikt door gebeurtenissen (acties van keuzes) ----------

    public String newFriend(GameCharacter character) {
        Relationship friend = add(character, RandomUtil.fullName(), character.getAge() + RandomUtil.between(-3, 3),
                Relationship.FRIEND, "Vriend", 50);
        String text = "Je hebt een nieuwe vriend: " + friend.getName() + ".";
        historyService.log(character, text);
        return text;
    }

    public String newPartner(GameCharacter character) {
        if (hasPartner(character.getId())) {
            return "Je hebt al een partner, dus je hield het bij flirten.";
        }
        Relationship partner = add(character, RandomUtil.fullName(), character.getAge() + RandomUtil.between(-4, 4),
                Relationship.PARTNER, "Partner", 60);
        String text = "Je hebt een relatie gekregen met " + partner.getName() + ".";
        historyService.logImportant(character, text);
        return text;
    }

    public String marry(GameCharacter character) {
        List<Relationship> partners = relationshipRepository.findByCharacterIdAndType(character.getId(), Relationship.PARTNER);
        if (partners.isEmpty()) {
            return "Je hebt geen partner om mee te trouwen.";
        }
        Relationship partner = partners.get(0);
        partner.setMarried(true);
        partner.setLabel("Echtgeno(o)t(e)");
        partner.changeLevel(20);
        relationshipRepository.save(partner);
        String text = "Je bent getrouwd met " + partner.getName() + "!";
        historyService.logImportant(character, text);
        return text;
    }

    public String haveChild(GameCharacter character) {
        Relationship child = add(character, RandomUtil.firstName() + " " + character.getLastName(), 0,
                Relationship.FAMILY, "Kind", 90);
        String text = "Je kind " + child.getName() + " is geboren!";
        historyService.logImportant(character, text);
        return text;
    }

    public String breakUp(GameCharacter character) {
        List<Relationship> partners = relationshipRepository.findByCharacterIdAndType(character.getId(), Relationship.PARTNER);
        if (partners.isEmpty()) {
            return null;
        }
        relationshipRepository.deleteAll(partners);
        String text = "Je relatie met " + partners.get(0).getName() + " is voorbij.";
        historyService.logImportant(character, text);
        return text;
    }

    public String makeEnemy(GameCharacter character) {
        Relationship enemy = add(character, RandomUtil.fullName(), character.getAge() + RandomUtil.between(-5, 10),
                Relationship.ENEMY, "Vijand", 0);
        String text = enemy.getName() + " is nu je vijand.";
        historyService.log(character, text);
        return text;
    }

    // ---------- Ieder jaar ----------

    /** Bij Next Year: iedereen wordt een jaar ouder en relaties verslechteren een beetje als je niets doet. */
    public void yearlyUpdate(GameCharacter character) {
        for (Relationship relationship : relationshipRepository.findByCharacterId(character.getId())) {
            relationship.setAge(relationship.getAge() + 1);
            relationship.changeLevel(-3);

            boolean isParent = relationship.getLabel().equals("Moeder") || relationship.getLabel().equals("Vader");
            if (isParent && relationship.getAge() > 85 && RandomUtil.chance(25)) {
                relationshipRepository.delete(relationship);
                character.changeHappiness(-15);
                historyService.logImportant(character, "Je " + relationship.getLabel().toLowerCase() + " "
                        + relationship.getName() + " is overleden op " + relationship.getAge() + "-jarige leeftijd.");
            } else if (relationship.getType().equals(Relationship.PARTNER) && relationship.getRelationshipLevel() == 0) {
                relationshipRepository.delete(relationship);
                character.changeHappiness(-10);
                historyService.logImportant(character, relationship.getName() + " heeft het uitgemaakt. Jullie zagen elkaar te weinig.");
            } else {
                relationshipRepository.save(relationship);
            }
        }
    }

    private Relationship add(GameCharacter character, String name, int age, String type, String label, int level) {
        return relationshipRepository.save(new Relationship(character.getId(), name, Math.max(0, age), type, label, level));
    }
}

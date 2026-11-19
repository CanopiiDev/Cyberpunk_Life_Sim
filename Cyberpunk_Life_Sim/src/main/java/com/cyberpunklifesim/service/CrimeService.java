package com.cyberpunklifesim.service;

import com.cyberpunklifesim.dto.ActionResponse;
import com.cyberpunklifesim.model.Crime;
import com.cyberpunklifesim.model.GameCharacter;
import com.cyberpunklifesim.repository.CrimeRepository;
import com.cyberpunklifesim.util.RandomUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * De onderwereld: misdaden plegen voor geld en Street Cred, met kans op arrestatie.
 */
@Service
public class CrimeService {

    private final CrimeRepository crimeRepository;
    private final CharacterService characterService;
    private final FinanceService financeService;
    private final LifeHistoryService historyService;
    private final ShopService shopService;
    private final CareerService careerService;
    private final RelationshipService relationshipService;

    public CrimeService(CrimeRepository crimeRepository, CharacterService characterService,
                        FinanceService financeService, LifeHistoryService historyService, ShopService shopService,
                        CareerService careerService, RelationshipService relationshipService) {
        this.crimeRepository = crimeRepository;
        this.characterService = characterService;
        this.financeService = financeService;
        this.historyService = historyService;
        this.shopService = shopService;
        this.careerService = careerService;
        this.relationshipService = relationshipService;
    }

    public List<Crime> getAllCrimes() {
        return crimeRepository.findAll();
    }

    public ActionResponse commitCrime(Long characterId, Long crimeId) {
        GameCharacter character = characterService.getFreeCharacter(characterId);
        Crime crime = crimeRepository.findById(crimeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Misdaad met id " + crimeId + " bestaat niet"));

        if (character.getIntelligence() < crime.getRequiredIntelligence()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Je hebt minimaal " + crime.getRequiredIntelligence() + " Intelligence nodig voor " + crime.getName() + ".");
        }
        characterService.useAction(character);

        // Kans om gepakt te worden: lager met veel Street Cred en met cyberware zoals een Reflex Booster
        int arrestChance = crime.getArrestChance() - shopService.getCrimeBonus(characterId) - character.getStreetCred() / 10;
        arrestChance = Math.max(5, arrestChance);

        String message;
        boolean success;
        if (RandomUtil.chance(arrestChance)) {
            success = false;
            financeService.change(character, -crime.getFine(), "Boete voor " + crime.getName());
            if (crime.getPrisonYears() > 0) {
                characterService.sendToPrison(character, crime.getPrisonYears(),
                        "Je werd gearresteerd voor " + crime.getName() + " en kreeg een boete van €$" + crime.getFine() + ".");
                message = "Gepakt! Boete van €$" + crime.getFine() + " en " + crime.getPrisonYears() + " jaar cel.";
            } else {
                character.setConvictions(character.getConvictions() + 1);
                message = "Gepakt! Je kreeg een boete van €$" + crime.getFine() + ".";
                historyService.logImportant(character, "Je werd gepakt voor " + crime.getName() + ". " + message);
                // Een baan waarvoor je geen strafblad mag hebben (zoals Police Officer) ben je kwijt
                if (character.getCareer() != null && character.getCareer().isRequiresCleanRecord()) {
                    message += " " + careerService.fire(character, "Je werkgever ontdekte je strafblad.");
                }
            }
        } else {
            success = true;
            int reward = RandomUtil.between(crime.getMinReward(), crime.getMaxReward());
            financeService.change(character, reward, "Opbrengst: " + crime.getName());
            character.changeStreetCred(crime.getStreetCredReward());
            message = "Gelukt! " + crime.getName() + " leverde €$" + reward + " op.";
            historyService.log(character, message);

            if (RandomUtil.chance(15)) {
                message += " " + relationshipService.makeEnemy(character);
            }
        }

        characterService.save(character);
        return new ActionResponse(success, message, characterService.toResponse(character));
    }
}

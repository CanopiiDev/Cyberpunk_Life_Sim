package com.cyberpunklifesim.service;

import com.cyberpunklifesim.dto.ActionResponse;
import com.cyberpunklifesim.dto.CareerResponse;
import com.cyberpunklifesim.model.Career;
import com.cyberpunklifesim.model.GameCharacter;
import com.cyberpunklifesim.repository.CareerRepository;
import com.cyberpunklifesim.util.RandomUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Banen: solliciteren, salaris, promotie, ontslag.
 */
@Service
public class CareerService {

    private final CareerRepository careerRepository;
    private final CharacterService characterService;
    private final FinanceService financeService;
    private final LifeHistoryService historyService;

    public CareerService(CareerRepository careerRepository, CharacterService characterService,
                         FinanceService financeService, LifeHistoryService historyService) {
        this.careerRepository = careerRepository;
        this.characterService = characterService;
        this.financeService = financeService;
        this.historyService = historyService;
    }

    public List<Career> getAllCareers() {
        return careerRepository.findAll();
    }

    public CareerResponse getCareerStatus(Long characterId) {
        return CareerResponse.from(characterService.getCharacter(characterId));
    }

    /** Controleert de minimumvereisten. Geeft null terug als alles klopt, anders de reden. */
    public String checkRequirements(GameCharacter character, Career career) {
        if (character.getIntelligence() < career.getRequiredIntelligence()) {
            return "Je hebt minimaal " + career.getRequiredIntelligence() + " Intelligence nodig.";
        }
        if (character.getStreetCred() < career.getRequiredStreetCred()) {
            return "Je hebt minimaal " + career.getRequiredStreetCred() + " Street Cred nodig.";
        }
        if (career.getRequiredDiploma() != null && !character.getDiplomas().contains(career.getRequiredDiploma())) {
            return "Je hebt een diploma " + career.getRequiredDiploma() + " nodig.";
        }
        if (career.isRequiresCleanRecord() && character.getConvictions() > 0) {
            return "Met een strafblad word je niet aangenomen.";
        }
        return null;
    }

    /** UC04: solliciteren. Vereisten niet gehaald = 400. Wel gehaald = kans op aangenomen of afgewezen. */
    public ActionResponse apply(Long characterId, Long careerId) {
        GameCharacter character = characterService.getFreeCharacter(characterId);
        Career career = careerRepository.findById(careerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Carrière met id " + careerId + " bestaat niet"));

        if (character.getCareer() != null && character.getCareer().getId().equals(careerId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Je werkt al als " + career.getName() + ".");
        }
        String reason = checkRequirements(character, career);
        if (reason != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Je voldoet niet aan de eisen. " + reason);
        }

        characterService.useAction(character);

        // Kans om aangenomen te worden: slimmer dan nodig en een goed uiterlijk helpen
        int chance = 50 + (character.getIntelligence() - career.getRequiredIntelligence())
                + (character.getAppearance() - 50) / 5;
        chance = Math.max(20, Math.min(95, chance));

        boolean hired = RandomUtil.chance(chance);
        String message;
        if (hired) {
            hire(character, career);
            message = "Gefeliciteerd! Je bent aangenomen als " + character.getPosition() + " (" + career.getName() + ").";
        } else {
            message = "Helaas, je bent afgewezen voor de baan " + career.getName() + ".";
            historyService.log(character, message);
        }

        characterService.save(character);
        return new ActionResponse(hired, message, characterService.toResponse(character));
    }

    public ActionResponse quit(Long characterId) {
        GameCharacter character = characterService.getAliveCharacter(characterId);
        if (character.getCareer() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Je hebt geen baan.");
        }
        String message = "Je nam ontslag als " + character.getPosition() + ".";
        historyService.logImportant(character, message);
        clearCareer(character);
        characterService.save(character);
        return new ActionResponse(true, message, characterService.toResponse(character));
    }

    // ---------- Gebruikt door andere services ----------

    /** Een baan aangeboden krijgen via een gebeurtenis. Geeft een tekst terug voor de speler. */
    public String offerJob(GameCharacter character, String careerName) {
        Career career = careerRepository.findByName(careerName);
        if (career == null || character.isInPrison()) {
            return null;
        }
        String reason = checkRequirements(character, career);
        if (reason != null) {
            return "Ze vonden je niet geschikt: " + reason;
        }
        hire(character, career);
        return "Je bent aangenomen als " + character.getPosition() + "!";
    }

    public String fire(GameCharacter character, String reason) {
        if (character.getCareer() == null) {
            return null;
        }
        String text = reason + " Je bent ontslagen als " + character.getPosition() + ".";
        historyService.logImportant(character, text);
        character.changeHappiness(-10);
        clearCareer(character);
        return text;
    }

    /** Bij Next Year: salaris ontvangen, en misschien promotie, ontslag of (bij illegale banen) arrestatie. */
    public void yearlyUpdate(GameCharacter character) {
        Career career = character.getCareer();
        if (career == null) {
            return;
        }

        financeService.change(character, character.getSalary(), "Salaris als " + character.getPosition());
        historyService.log(character, "Je verdiende €$" + character.getSalary() + " als " + character.getPosition() + ".");
        character.setYearsInPosition(character.getYearsInPosition() + 1);

        if (career.isIllegal() && RandomUtil.chance(15)) {
            characterService.sendToPrison(character, 1, "Je werd opgepakt tijdens je werk als " + career.getName() + ".");
            return;
        }

        int fireChance = character.getHappiness() < 30 ? 15 : 4;
        if (RandomUtil.chance(fireChance)) {
            fire(character, "Je baas was niet tevreden over je.");
            return;
        }

        boolean canBePromoted = character.getCareerLevel() < career.getMaxLevel() && character.getYearsInPosition() >= 2;
        if (canBePromoted && RandomUtil.chance(20 + character.getIntelligence() / 5)) {
            promote(character);
        }
    }

    // ---------- Hulpmethodes ----------

    private void hire(GameCharacter character, Career career) {
        if (character.getCareer() != null) {
            historyService.log(character, "Je stopte als " + character.getPosition() + ".");
        }
        character.setCareer(career);
        character.setCareerLevel(0);
        character.setSalary(career.getStartSalary());
        character.setYearsInPosition(0);
        updateHighestPosition(character);
        historyService.logImportant(character, "Je begon een nieuwe baan als " + character.getPosition()
                + " (" + career.getName() + ") voor €$" + career.getStartSalary() + " per jaar.");
    }

    private void promote(GameCharacter character) {
        character.setCareerLevel(character.getCareerLevel() + 1);
        character.setSalary(character.getSalary() * 125 / 100); // 25% opslag
        character.setYearsInPosition(0);
        character.changeHappiness(10);
        updateHighestPosition(character);
        historyService.logImportant(character, "Promotie! Je bent nu " + character.getPosition()
                + " en verdient €$" + character.getSalary() + " per jaar.");
    }

    private void updateHighestPosition(GameCharacter character) {
        if (character.getSalary() > character.getHighestSalary()) {
            character.setHighestSalary(character.getSalary());
            character.setHighestPosition(character.getPosition() + " (" + character.getCareer().getName() + ")");
        }
    }

    private void clearCareer(GameCharacter character) {
        character.setCareer(null);
        character.setCareerLevel(0);
        character.setSalary(0);
        character.setYearsInPosition(0);
    }
}

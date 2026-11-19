package com.cyberpunklifesim.service;

import com.cyberpunklifesim.dto.ActionResponse;
import com.cyberpunklifesim.model.Education;
import com.cyberpunklifesim.model.GameCharacter;
import com.cyberpunklifesim.repository.EducationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Opleidingen: beginnen, ieder jaar studeren, en uiteindelijk een diploma halen.
 */
@Service
public class EducationService {

    private final EducationRepository educationRepository;
    private final CharacterService characterService;
    private final FinanceService financeService;
    private final LifeHistoryService historyService;

    public EducationService(EducationRepository educationRepository, CharacterService characterService,
                            FinanceService financeService, LifeHistoryService historyService) {
        this.educationRepository = educationRepository;
        this.characterService = characterService;
        this.financeService = financeService;
        this.historyService = historyService;
    }

    public List<Education> getAllEducations() {
        return educationRepository.findAll();
    }

    public ActionResponse start(Long characterId, Long educationId) {
        GameCharacter character = characterService.getFreeCharacter(characterId);
        Education education = educationRepository.findById(educationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Opleiding met id " + educationId + " bestaat niet"));

        if (character.getEducation() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Je volgt al de opleiding " + character.getEducation().getName() + ".");
        }
        if (character.getDiplomas().contains(education.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Je hebt het diploma " + education.getName() + " al.");
        }
        if (character.getIntelligence() < education.getRequiredIntelligence()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Je hebt minimaal " + education.getRequiredIntelligence() + " Intelligence nodig voor deze opleiding.");
        }

        character.setEducation(education);
        character.setEducationYearsLeft(education.getYears());
        String message = "Je bent begonnen met de opleiding " + education.getName() + " (" + education.getYears()
                + " jaar, €$" + education.getYearlyCost() + " per jaar).";
        historyService.logImportant(character, message);

        characterService.save(character);
        return new ActionResponse(true, message, characterService.toResponse(character));
    }

    /** Bij Next Year: studiegeld betalen, slimmer worden en misschien je diploma halen. */
    public void yearlyUpdate(GameCharacter character) {
        Education education = character.getEducation();
        if (education == null) {
            return;
        }

        // Studiegeld. Heb je te weinig geld? Dan krijg je een studieschuld (saldo wordt negatief).
        financeService.change(character, -education.getYearlyCost(), "Studiegeld " + education.getName());
        character.changeIntelligence(education.getIntelligencePerYear());
        character.setEducationYearsLeft(character.getEducationYearsLeft() - 1);

        if (character.getEducationYearsLeft() <= 0) {
            character.getDiplomas().add(education.getName());
            character.setEducation(null);
            character.changeHappiness(10);
            historyService.logImportant(character, "Je hebt je diploma " + education.getName() + " behaald!");
        } else {
            historyService.log(character, "Je studeerde een jaar " + education.getName() + ". Nog "
                    + character.getEducationYearsLeft() + " jaar te gaan.");
        }
    }
}

package com.cyberpunklifesim.dto;

import com.cyberpunklifesim.model.GameCharacter;

import java.time.LocalDate;
import java.util.Set;

/**
 * Alles wat React nodig heeft voor het dashboard van een personage.
 * We sturen een DTO terug in plaats van de entity, zodat we zelf bepalen wat React te zien krijgt.
 */
public record CharacterResponse(
        Long id,
        String firstName,
        String lastName,
        String fullName,
        String gender,
        LocalDate birthDate,
        String birthPlace,
        String nationality,
        int age,
        int currentYear,
        String location,
        boolean alive,
        String causeOfDeath,
        boolean inPrison,
        int prisonYearsLeft,
        int health,
        int happiness,
        int intelligence,
        int streetCred,
        int appearance,
        int money,
        String wealth,
        String criminalRecord,
        String career,
        String position,
        int salary,
        String education,
        int educationYearsLeft,
        Set<String> diplomas,
        int actionsLeft,
        int openEvents
) {
    /** Zet een GameCharacter om naar een CharacterResponse. */
    public static CharacterResponse from(GameCharacter c, int openEvents) {
        return new CharacterResponse(
                c.getId(),
                c.getFirstName(),
                c.getLastName(),
                c.getFullName(),
                c.getGender(),
                c.getBirthDate(),
                c.getBirthPlace(),
                c.getNationality(),
                c.getAge(),
                c.getCurrentYear(),
                c.getLocation(),
                c.isAlive(),
                c.getCauseOfDeath(),
                c.isInPrison(),
                c.getPrisonYearsLeft(),
                c.getHealth(),
                c.getHappiness(),
                c.getIntelligence(),
                c.getStreetCred(),
                c.getAppearance(),
                c.getMoney(),
                c.getWealth(),
                c.getCriminalRecord(),
                c.getCareer() == null ? null : c.getCareer().getName(),
                c.getPosition(),
                c.getSalary(),
                c.getEducation() == null ? null : c.getEducation().getName(),
                c.getEducationYearsLeft(),
                c.getDiplomas(),
                c.getActionsLeft(),
                openEvents
        );
    }
}

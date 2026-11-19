package com.cyberpunklifesim.dto;

import com.cyberpunklifesim.model.GameCharacter;

/**
 * De huidige baan van een personage. hasJob = false als het personage werkloos is.
 */
public record CareerResponse(boolean hasJob, Long careerId, String career, String position, int salary,
                             int yearsInPosition, boolean canBePromoted, String highestPosition) {

    public static CareerResponse from(GameCharacter c) {
        if (c.getCareer() == null) {
            return new CareerResponse(false, null, null, null, 0, 0, false, c.getHighestPosition());
        }
        boolean canBePromoted = c.getCareerLevel() < c.getCareer().getMaxLevel();
        return new CareerResponse(true, c.getCareer().getId(), c.getCareer().getName(), c.getPosition(),
                c.getSalary(), c.getYearsInPosition(), canBePromoted, c.getHighestPosition());
    }
}

package com.cyberpunklifesim.dto;

import com.cyberpunklifesim.model.LifeHistory;
import com.cyberpunklifesim.model.Relationship;

import java.time.LocalDate;
import java.util.List;

/**
 * Het eindoverzicht voor het Death Screen (of Character History).
 */
public record LifeSummaryResponse(
        String fullName,
        LocalDate birthDate,
        LocalDate deathDate,
        int age,
        boolean alive,
        String causeOfDeath,
        String highestPosition,
        int totalEarned,
        String criminalRecord,
        List<Relationship> relationships,
        List<LifeHistory> importantEvents
) {
}

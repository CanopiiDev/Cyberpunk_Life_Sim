package com.cyberpunklifesim.dto;

import jakarta.validation.constraints.NotNull;

/**
 * De keuze die de speler maakt bij een gebeurtenis: { "choiceId": 3 }
 */
public record ChoiceRequest(@NotNull(message = "choiceId is verplicht") Long choiceId) {
}

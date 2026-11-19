package com.cyberpunklifesim.dto;

import java.util.List;

/**
 * Wat React terugkrijgt na Next Year:
 * - het bijgewerkte personage
 * - een overzicht van wat er dit jaar gebeurde (salaris, promotie, ...)
 * - de nieuwe gebeurtenissen waar de speler een keuze voor moet maken
 */
public record AgeUpResponse(CharacterResponse character, List<String> yearSummary, List<EventResponse> events) {
}

package com.cyberpunklifesim.dto;

/**
 * De JSON die de speler meestuurt bij het aanmaken van een character, bijvoorbeeld:
 * { "name": "V" }
 *
 * We gebruiken hier een aparte klasse (in plaats van GameCharacter zelf),
 * zodat een speler niet zelf zijn geld of Street Cred kan meesturen.
 * Een "record" is een korte manier om een klasse te maken die alleen data bevat.
 */
public record CreateCharacterRequest(String name) {
}

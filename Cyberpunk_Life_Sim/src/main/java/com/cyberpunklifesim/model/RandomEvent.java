package com.cyberpunklifesim.model;

/**
 * Een willekeurige gebeurtenis die kan gebeuren als je een jaar ouder wordt.
 * De getallen geven aan hoeveel je geld en stats veranderen (mag ook negatief zijn).
 *
 * Dit is geen @Entity: deze gebeurtenissen staan gewoon in de code (zie RandomEventService)
 * en worden niet in de database opgeslagen.
 */
public record RandomEvent(String text, int money, int health, int happiness, int smarts) {
}

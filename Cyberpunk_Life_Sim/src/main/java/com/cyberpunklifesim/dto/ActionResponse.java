package com.cyberpunklifesim.dto;

/**
 * Het resultaat van een actie (keuze maken, solliciteren, item kopen, misdaad, ...).
 *
 * success  = is de actie gelukt? (bijvoorbeeld: sollicitatie aangenomen of afgewezen)
 * message  = tekst die React aan de speler kan laten zien
 * character = het bijgewerkte personage, zodat React het scherm meteen kan verversen
 */
public record ActionResponse(boolean success, String message, CharacterResponse character) {
}

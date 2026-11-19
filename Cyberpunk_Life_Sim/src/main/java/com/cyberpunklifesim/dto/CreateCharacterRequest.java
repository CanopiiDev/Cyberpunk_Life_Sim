package com.cyberpunklifesim.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * De JSON die React stuurt om een personage aan te maken, bijvoorbeeld:
 * {
 *   "firstName": "Kai", "lastName": "Nakamura", "gender": "Male",
 *   "birthDate": "2059-04-12", "birthPlace": "Neon City", "nationality": "Japanese"
 * }
 *
 * De annotaties (@NotBlank, @Size, ...) zorgen dat de API een 400 Bad Request teruggeeft
 * als er iets niet klopt, nog voordat onze eigen code draait.
 * Geld en stats zitten hier bewust NIET in: die bepaalt de API zelf.
 */
public record CreateCharacterRequest(
        @NotBlank(message = "Voornaam is verplicht") @Size(max = 30) String firstName,
        @NotBlank(message = "Achternaam is verplicht") @Size(max = 30) String lastName,
        @NotBlank(message = "Geslacht is verplicht") String gender,
        @NotNull(message = "Geboortedatum is verplicht") LocalDate birthDate,
        @NotBlank(message = "Geboorteplaats is verplicht") String birthPlace,
        @NotBlank(message = "Nationaliteit is verplicht") String nationality
) {
}

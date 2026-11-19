package com.cyberpunklifesim.dto;

import com.cyberpunklifesim.model.CharacterEvent;
import com.cyberpunklifesim.model.GameEvent;

import java.util.List;

/**
 * Een openstaande gebeurtenis met de keuzes.
 * De gevolgen van keuzes sturen we NIET mee: die berekent alleen de API.
 *
 * Let op: "id" is het id van de gebeurtenis voor dit personage.
 * Dat id gebruik je in POST /api/characters/{id}/events/{eventId}/choices
 */
public record EventResponse(Long id, String title, String description, String category, int age,
                            List<Option> choices) {

    public record Option(Long id, String text) {
    }

    public static EventResponse from(CharacterEvent characterEvent) {
        GameEvent event = characterEvent.getEvent();
        List<Option> options = event.getChoices().stream()
                .map(choice -> new Option(choice.getId(), choice.getText()))
                .toList();
        return new EventResponse(characterEvent.getId(), event.getTitle(), event.getDescription(),
                event.getCategory(), characterEvent.getAge(), options);
    }
}

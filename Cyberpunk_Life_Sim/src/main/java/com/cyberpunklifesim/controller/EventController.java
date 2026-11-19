package com.cyberpunklifesim.controller;

import com.cyberpunklifesim.dto.ActionResponse;
import com.cyberpunklifesim.dto.ChoiceRequest;
import com.cyberpunklifesim.dto.EventResponse;
import com.cyberpunklifesim.service.EventService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/characters/{id}/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    // GET /api/characters/1/events -> openstaande gebeurtenissen met keuzes
    @GetMapping
    public List<EventResponse> getOpenEvents(@PathVariable Long id) {
        return eventService.getOpenEvents(id);
    }

    // POST /api/characters/1/events/7/choices  met body { "choiceId": 3 } -> keuze maken
    @PostMapping("/{eventId}/choices")
    public ActionResponse makeChoice(@PathVariable Long id, @PathVariable Long eventId,
                                     @Valid @RequestBody ChoiceRequest request) {
        return eventService.makeChoice(id, eventId, request.choiceId());
    }
}

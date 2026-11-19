package com.cyberpunklifesim.controller;

import com.cyberpunklifesim.dto.CreateCharacterRequest;
import com.cyberpunklifesim.model.GameCharacter;
import com.cyberpunklifesim.service.CharacterService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller = de "voordeur" van de API.
 * Hier staan de URL's (endpoints). De controller doet zelf geen logica,
 * maar geeft het werk door aan de service.
 *
 * @RestController zorgt dat alles wat we teruggeven automatisch JSON wordt.
 * @RequestMapping zet "/api/characters" voor alle endpoints in deze klasse.
 */
@RestController
@RequestMapping("/api/characters")
public class CharacterController {

    private final CharacterService characterService;

    public CharacterController(CharacterService characterService) {
        this.characterService = characterService;
    }

    // GET /api/characters  -> alle characters
    @GetMapping
    public List<GameCharacter> getAllCharacters() {
        return characterService.getAllCharacters();
    }

    // GET /api/characters/1  -> één character met zijn geld en statistieken
    @GetMapping("/{id}")
    public GameCharacter getCharacter(@PathVariable Long id) {
        return characterService.getCharacter(id);
    }

    // POST /api/characters  met body { "name": "V" }  -> nieuw character
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // 201 Created
    public GameCharacter createCharacter(@RequestBody CreateCharacterRequest request) {
        return characterService.createCharacter(request.name());
    }

    // DELETE /api/characters/1  -> character verwijderen
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204 No Content
    public void deleteCharacter(@PathVariable Long id) {
        characterService.deleteCharacter(id);
    }

    // POST /api/characters/1/jobs/2  -> character 1 voert job 2 uit
    @PostMapping("/{id}/jobs/{jobId}")
    public GameCharacter doJob(@PathVariable Long id, @PathVariable Long jobId) {
        return characterService.doJob(id, jobId);
    }

    // POST /api/characters/1/buy/3  -> character 1 koopt item 3
    @PostMapping("/{id}/buy/{itemId}")
    public GameCharacter buyItem(@PathVariable Long id, @PathVariable Long itemId) {
        return characterService.buyItem(id, itemId);
    }
}

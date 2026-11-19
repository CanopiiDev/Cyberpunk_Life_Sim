package com.cyberpunklifesim.controller;

import com.cyberpunklifesim.dto.AgeUpResponse;
import com.cyberpunklifesim.dto.CharacterResponse;
import com.cyberpunklifesim.dto.CreateCharacterRequest;
import com.cyberpunklifesim.dto.LifeSummaryResponse;
import com.cyberpunklifesim.dto.StatsResponse;
import com.cyberpunklifesim.model.LifeHistory;
import com.cyberpunklifesim.model.Transaction;
import com.cyberpunklifesim.service.AgeService;
import com.cyberpunklifesim.service.CharacterService;
import com.cyberpunklifesim.service.FinanceService;
import com.cyberpunklifesim.service.LifeHistoryService;
import jakarta.validation.Valid;
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
 * Controller = de "voordeur" van de API. Hier staan alleen de URL's.
 * Het echte werk (de spelregels) gebeurt in de services.
 */
@RestController
@RequestMapping("/api/characters")
public class CharacterController {

    private final CharacterService characterService;
    private final AgeService ageService;
    private final LifeHistoryService historyService;
    private final FinanceService financeService;

    public CharacterController(CharacterService characterService, AgeService ageService,
                               LifeHistoryService historyService, FinanceService financeService) {
        this.characterService = characterService;
        this.ageService = ageService;
        this.historyService = historyService;
        this.financeService = financeService;
    }

    // GET /api/characters -> alle personages (voor Continue en Character History)
    @GetMapping
    public List<CharacterResponse> getAllCharacters() {
        return characterService.getAllCharacters();
    }

    // GET /api/characters/1 -> één personage (voor het dashboard)
    @GetMapping("/{id}")
    public CharacterResponse getCharacter(@PathVariable Long id) {
        return characterService.toResponse(characterService.getCharacter(id));
    }

    // POST /api/characters -> nieuw personage. @Valid controleert de regels in CreateCharacterRequest.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CharacterResponse createCharacter(@Valid @RequestBody CreateCharacterRequest request) {
        return characterService.toResponse(characterService.createCharacter(request));
    }

    // DELETE /api/characters/1 -> personage en al zijn gegevens verwijderen
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCharacter(@PathVariable Long id) {
        characterService.deleteCharacter(id);
    }

    // POST /api/characters/1/age -> Next Year
    @PostMapping("/{id}/age")
    public AgeUpResponse ageUp(@PathVariable Long id) {
        return ageService.ageUp(id);
    }

    // GET /api/characters/1/stats -> alleen de statistieken
    @GetMapping("/{id}/stats")
    public StatsResponse getStats(@PathVariable Long id) {
        return StatsResponse.from(characterService.getCharacter(id));
    }

    // GET /api/characters/1/life-history -> de tijdlijn (oudste eerst)
    @GetMapping("/{id}/life-history")
    public List<LifeHistory> getLifeHistory(@PathVariable Long id) {
        characterService.getCharacter(id);
        return historyService.getHistory(id);
    }

    // GET /api/characters/1/transactions -> alle bij- en afschrijvingen
    @GetMapping("/{id}/transactions")
    public List<Transaction> getTransactions(@PathVariable Long id) {
        characterService.getCharacter(id);
        return financeService.getTransactions(id);
    }

    // GET /api/characters/1/summary -> eindoverzicht (Death Screen)
    @GetMapping("/{id}/summary")
    public LifeSummaryResponse getSummary(@PathVariable Long id) {
        return characterService.getSummary(id);
    }
}

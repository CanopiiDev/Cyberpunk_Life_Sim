package com.cyberpunklifesim.service;

import com.cyberpunklifesim.model.GameCharacter;
import com.cyberpunklifesim.model.LifeHistory;
import com.cyberpunklifesim.repository.LifeHistoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Houdt de levensgeschiedenis bij: alles wat er met een personage gebeurt.
 */
@Service
public class LifeHistoryService {

    private final LifeHistoryRepository historyRepository;

    public LifeHistoryService(LifeHistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    /** Een gewone regel in de geschiedenis. */
    public void log(GameCharacter character, String text) {
        save(character, text, false);
    }

    /** Een belangrijke regel: komt ook in het eindoverzicht (Death Screen). */
    public void logImportant(GameCharacter character, String text) {
        save(character, text, true);
    }

    private void save(GameCharacter character, String text, boolean important) {
        historyRepository.save(new LifeHistory(character.getId(), character.getAge(),
                character.getCurrentYear(), text, important));
    }

    public List<LifeHistory> getHistory(Long characterId) {
        return historyRepository.findByCharacterIdOrderByIdAsc(characterId);
    }

    public List<LifeHistory> getImportantHistory(Long characterId) {
        return historyRepository.findByCharacterIdAndImportantTrueOrderByIdAsc(characterId);
    }

    /** Alle teksten van één leeftijd: het jaaroverzicht na Next Year. */
    public List<String> getTextsForAge(Long characterId, int age) {
        return historyRepository.findByCharacterIdAndAgeOrderByIdAsc(characterId, age).stream()
                .map(LifeHistory::getText)
                .toList();
    }

    public void deleteHistory(Long characterId) {
        historyRepository.deleteByCharacterId(characterId);
    }
}

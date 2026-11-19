package com.cyberpunklifesim.service;

import com.cyberpunklifesim.model.GameCharacter;
import com.cyberpunklifesim.model.LifeEvent;
import com.cyberpunklifesim.repository.LifeEventRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Houdt het levensverhaal (de "log") van ieder character bij.
 */
@Service
public class LifeEventService {

    private final LifeEventRepository lifeEventRepository;

    public LifeEventService(LifeEventRepository lifeEventRepository) {
        this.lifeEventRepository = lifeEventRepository;
    }

    /** Schrijf een nieuwe regel in het levensverhaal. */
    public void log(GameCharacter character, String text) {
        lifeEventRepository.save(new LifeEvent(character.getId(), character.getAge(), text));
    }

    public List<LifeEvent> getEvents(Long characterId) {
        return lifeEventRepository.findByCharacterIdOrderByIdDesc(characterId);
    }

    public void deleteEvents(Long characterId) {
        lifeEventRepository.deleteByCharacterId(characterId);
    }
}

package com.cyberpunklifesim.repository;

import com.cyberpunklifesim.model.CharacterEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CharacterEventRepository extends JpaRepository<CharacterEvent, Long> {

    List<CharacterEvent> findByCharacterIdAndResolvedFalse(Long characterId);

    int countByCharacterIdAndResolvedFalse(Long characterId);

    // Heeft dit personage deze gebeurtenis al eens gehad?
    boolean existsByCharacterIdAndEvent_Id(Long characterId, Long eventId);

    void deleteByCharacterId(Long characterId);
}

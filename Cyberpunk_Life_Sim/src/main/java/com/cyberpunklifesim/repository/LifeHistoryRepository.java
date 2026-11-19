package com.cyberpunklifesim.repository;

import com.cyberpunklifesim.model.LifeHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LifeHistoryRepository extends JpaRepository<LifeHistory, Long> {

    // Chronologisch: oudste eerst
    List<LifeHistory> findByCharacterIdOrderByIdAsc(Long characterId);

    List<LifeHistory> findByCharacterIdAndAgeOrderByIdAsc(Long characterId, int age);

    List<LifeHistory> findByCharacterIdAndImportantTrueOrderByIdAsc(Long characterId);

    void deleteByCharacterId(Long characterId);
}

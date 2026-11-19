package com.cyberpunklifesim.repository;

import com.cyberpunklifesim.model.LifeEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LifeEventRepository extends JpaRepository<LifeEvent, Long> {

    // Spring maakt de query automatisch op basis van de naam van de methode:
    // "zoek alle events met dit characterId, nieuwste eerst"
    List<LifeEvent> findByCharacterIdOrderByIdDesc(Long characterId);

    // "verwijder alle events met dit characterId"
    void deleteByCharacterId(Long characterId);
}

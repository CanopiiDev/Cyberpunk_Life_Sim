package com.cyberpunklifesim.repository;

import com.cyberpunklifesim.model.GameEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameEventRepository extends JpaRepository<GameEvent, Long> {
}

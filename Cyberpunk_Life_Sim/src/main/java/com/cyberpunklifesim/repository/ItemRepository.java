package com.cyberpunklifesim.repository;

import com.cyberpunklifesim.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}

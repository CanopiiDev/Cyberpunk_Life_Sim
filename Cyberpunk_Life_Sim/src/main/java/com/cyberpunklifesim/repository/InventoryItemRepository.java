package com.cyberpunklifesim.repository;

import com.cyberpunklifesim.model.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {

    List<InventoryItem> findByCharacterId(Long characterId);

    boolean existsByCharacterIdAndItemId(Long characterId, Long itemId);

    // "Item_Category" betekent: kijk naar het veld category van het gekoppelde item
    boolean existsByCharacterIdAndItem_Category(Long characterId, String category);

    void deleteByCharacterId(Long characterId);
}

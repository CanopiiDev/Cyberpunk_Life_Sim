package com.cyberpunklifesim.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Eén item in de inventory van een character.
 * Koppelt een character (via characterId) aan een item uit de winkel.
 */
@Entity
@Table(name = "inventory")
public class InventoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long characterId;

    @ManyToOne(fetch = FetchType.EAGER)
    private Item item;

    private int purchasedAtAge;

    public InventoryItem() {
    }

    public InventoryItem(Long characterId, Item item, int purchasedAtAge) {
        this.characterId = characterId;
        this.item = item;
        this.purchasedAtAge = purchasedAtAge;
    }

    public Long getId() { return id; }
    public Long getCharacterId() { return characterId; }
    public Item getItem() { return item; }
    public int getPurchasedAtAge() { return purchasedAtAge; }
}

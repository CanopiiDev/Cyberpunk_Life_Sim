package com.cyberpunklifesim.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Een item dat een character kan kopen in de winkel.
 * Een item kan je stats verbeteren (bijvoorbeeld een Med Kit geeft health).
 */
@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    /** Prijs in eurodollars. */
    private int price;

    // Hoeveel het item je stats verhoogt als je het koopt
    private int healthBoost;
    private int happinessBoost;
    private int smartsBoost;

    public Item() {
    }

    public Item(String name, String description, int price, int healthBoost, int happinessBoost, int smartsBoost) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.healthBoost = healthBoost;
        this.happinessBoost = happinessBoost;
        this.smartsBoost = smartsBoost;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getPrice() {
        return price;
    }

    public int getHealthBoost() {
        return healthBoost;
    }

    public int getHappinessBoost() {
        return happinessBoost;
    }

    public int getSmartsBoost() {
        return smartsBoost;
    }
}

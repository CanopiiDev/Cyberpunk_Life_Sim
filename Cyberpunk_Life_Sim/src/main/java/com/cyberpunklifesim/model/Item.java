package com.cyberpunklifesim.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Een item uit de winkel: kleding, cyberware of iets anders.
 * Een item kan je stats verbeteren, maar cyberware kan ook bijwerkingen hebben (negatieve bonus).
 */
@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private int price;
    private String category;   // "Clothing", "Cyberware" of "Other"
    private String type;       // bijvoorbeeld "Jacket", "Neural Implant", "Food"

    // Wat het item doet met je stats (mag negatief zijn)
    private int healthBonus;
    private int happinessBonus;
    private int intelligenceBonus;
    private int appearanceBonus;
    private int crimeBonus;    // verlaagt de kans om gepakt te worden bij misdaden (in procenten)

    private String sideEffect; // uitleg van een eventuele bijwerking

    public Item() {
    }

    public Item(String name, String description, int price, String category, String type) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.type = type;
    }

    // Korte methodes om bonussen in te stellen, zodat de startdata leesbaar blijft:
    // new Item(...).health(10).happiness(-5)

    public Item health(int bonus) { this.healthBonus = bonus; return this; }
    public Item happiness(int bonus) { this.happinessBonus = bonus; return this; }
    public Item intelligence(int bonus) { this.intelligenceBonus = bonus; return this; }
    public Item appearance(int bonus) { this.appearanceBonus = bonus; return this; }
    public Item crime(int bonus) { this.crimeBonus = bonus; return this; }
    public Item sideEffect(String text) { this.sideEffect = text; return this; }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getPrice() { return price; }
    public String getCategory() { return category; }
    public String getType() { return type; }
    public int getHealthBonus() { return healthBonus; }
    public int getHappinessBonus() { return happinessBonus; }
    public int getIntelligenceBonus() { return intelligenceBonus; }
    public int getAppearanceBonus() { return appearanceBonus; }
    public int getCrimeBonus() { return crimeBonus; }
    public String getSideEffect() { return sideEffect; }
}

package com.cyberpunklifesim.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Een criminele activiteit. Levert geld en Street Cred op, maar je kunt gepakt worden.
 */
@Entity
@Table(name = "crimes")
public class Crime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private int minReward;
    private int maxReward;
    private int streetCredReward;
    private int arrestChance;          // kans in procenten om gepakt te worden
    private int fine;                  // boete als je gepakt wordt
    private int prisonYears;           // celstraf als je gepakt wordt (0 = alleen een boete)
    private int requiredIntelligence;

    public Crime() {
    }

    public Crime(String name, String description, int minReward, int maxReward, int streetCredReward,
                 int arrestChance, int fine, int prisonYears, int requiredIntelligence) {
        this.name = name;
        this.description = description;
        this.minReward = minReward;
        this.maxReward = maxReward;
        this.streetCredReward = streetCredReward;
        this.arrestChance = arrestChance;
        this.fine = fine;
        this.prisonYears = prisonYears;
        this.requiredIntelligence = requiredIntelligence;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getMinReward() { return minReward; }
    public int getMaxReward() { return maxReward; }
    public int getStreetCredReward() { return streetCredReward; }
    public int getArrestChance() { return arrestChance; }
    public int getFine() { return fine; }
    public int getPrisonYears() { return prisonYears; }
    public int getRequiredIntelligence() { return requiredIntelligence; }
}

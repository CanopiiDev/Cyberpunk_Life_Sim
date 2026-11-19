package com.cyberpunklifesim.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Een opleiding. Duurt meerdere jaren en levert een diploma op.
 */
@Entity
@Table(name = "educations")
public class Education {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private int years;                 // hoe lang de opleiding duurt
    private int yearlyCost;            // studiegeld per jaar
    private int intelligencePerYear;   // hoeveel Intelligence je per jaar krijgt
    private int requiredIntelligence;  // minimaal nodig om te mogen beginnen

    public Education() {
    }

    public Education(String name, String description, int years, int yearlyCost,
                     int intelligencePerYear, int requiredIntelligence) {
        this.name = name;
        this.description = description;
        this.years = years;
        this.yearlyCost = yearlyCost;
        this.intelligencePerYear = intelligencePerYear;
        this.requiredIntelligence = requiredIntelligence;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getYears() { return years; }
    public int getYearlyCost() { return yearlyCost; }
    public int getIntelligencePerYear() { return intelligencePerYear; }
    public int getRequiredIntelligence() { return requiredIntelligence; }
}

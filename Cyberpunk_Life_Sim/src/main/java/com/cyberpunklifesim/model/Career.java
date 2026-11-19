package com.cyberpunklifesim.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.List;

/**
 * Een carrière (baan) waar je op kunt solliciteren.
 */
@Entity
@Table(name = "careers")
public class Career {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private int startSalary;

    // Minimumvereisten
    private int requiredIntelligence;
    private int requiredStreetCred;
    private String requiredDiploma;        // null = geen diploma nodig
    private boolean requiresCleanRecord;   // true = geen strafblad toegestaan

    /** Illegale banen kunnen tot arrestatie leiden. */
    private boolean illegal;

    /** De functies van laag naar hoog, gescheiden door komma's. Promotie = één stap verder. */
    private String positions;

    public Career() {
    }

    public Career(String name, String description, int startSalary, int requiredIntelligence, int requiredStreetCred,
                  String requiredDiploma, boolean requiresCleanRecord, boolean illegal, String positions) {
        this.name = name;
        this.description = description;
        this.startSalary = startSalary;
        this.requiredIntelligence = requiredIntelligence;
        this.requiredStreetCred = requiredStreetCred;
        this.requiredDiploma = requiredDiploma;
        this.requiresCleanRecord = requiresCleanRecord;
        this.illegal = illegal;
        this.positions = positions;
    }

    /** Alle functies als lijst, bijvoorbeeld ["Cadet", "Officer", "Sergeant"]. */
    public List<String> getPositionList() {
        return List.of(positions.split(","));
    }

    public String getPositionTitle(int level) {
        return getPositionList().get(level);
    }

    /** Het hoogste level dat je kunt bereiken met promoties. */
    public int getMaxLevel() {
        return getPositionList().size() - 1;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getStartSalary() { return startSalary; }
    public int getRequiredIntelligence() { return requiredIntelligence; }
    public int getRequiredStreetCred() { return requiredStreetCred; }
    public String getRequiredDiploma() { return requiredDiploma; }
    public boolean isRequiresCleanRecord() { return requiresCleanRecord; }
    public boolean isIllegal() { return illegal; }
}

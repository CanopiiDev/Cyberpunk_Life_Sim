package com.cyberpunklifesim.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Een relatie van een character met een ander (niet-speelbaar) personage.
 */
@Entity
@Table(name = "relationships")
public class Relationship {

    public static final String FRIEND = "Friend";
    public static final String PARTNER = "Partner";
    public static final String FAMILY = "Family";
    public static final String ENEMY = "Enemy";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long characterId;
    private String name;
    private int age;
    private String type;              // Friend, Partner, Family of Enemy
    private String label;             // bijvoorbeeld "Moeder", "Vriend", "Echtgenoot"
    private int relationshipLevel;    // 0 - 100: hoe goed de relatie is
    private boolean married;

    public Relationship() {
    }

    public Relationship(Long characterId, String name, int age, String type, String label, int relationshipLevel) {
        this.characterId = characterId;
        this.name = name;
        this.age = age;
        this.type = type;
        this.label = label;
        this.relationshipLevel = relationshipLevel;
    }

    public void changeLevel(int amount) {
        relationshipLevel = Math.max(0, Math.min(100, relationshipLevel + amount));
    }

    public Long getId() { return id; }
    public Long getCharacterId() { return characterId; }
    public String getName() { return name; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getType() { return type; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public int getRelationshipLevel() { return relationshipLevel; }

    public boolean isMarried() { return married; }
    public void setMarried(boolean married) { this.married = married; }
}

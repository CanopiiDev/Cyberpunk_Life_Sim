package com.cyberpunklifesim.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Eén regel in de levensgeschiedenis, bijvoorbeeld "Age 22: Je werd gearresteerd."
 */
@Entity
@Table(name = "life_history")
public class LifeHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long characterId;
    private int age;
    private int gameYear;          // het jaar in het spel, bijvoorbeeld 2079
    private String text;

    /** Belangrijke momenten (baan, diploma, huwelijk, gevangenis, ...) komen in het eindoverzicht. */
    private boolean important;

    public LifeHistory() {
    }

    public LifeHistory(Long characterId, int age, int gameYear, String text, boolean important) {
        this.characterId = characterId;
        this.age = age;
        this.gameYear = gameYear;
        this.text = text;
        this.important = important;
    }

    public Long getId() { return id; }
    public Long getCharacterId() { return characterId; }
    public int getAge() { return age; }
    public int getGameYear() { return gameYear; }
    public String getText() { return text; }
    public boolean isImportant() { return important; }
}

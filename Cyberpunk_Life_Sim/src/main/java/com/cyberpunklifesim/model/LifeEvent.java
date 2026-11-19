package com.cyberpunklifesim.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Eén regel in het levensverhaal van een character,
 * bijvoorbeeld "Leeftijd 23: Je adopteerde een robotkat."
 *
 * We slaan alleen het id van het character op (characterId).
 * Dat is simpeler dan een echte relatie tussen de twee klassen.
 */
@Entity
@Table(name = "life_events")
public class LifeEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long characterId;

    /** De leeftijd van het character toen dit gebeurde. */
    private int age;

    private String text;

    public LifeEvent() {
    }

    public LifeEvent(Long characterId, int age, String text) {
        this.characterId = characterId;
        this.age = age;
        this.text = text;
    }

    public Long getId() {
        return id;
    }

    public Long getCharacterId() {
        return characterId;
    }

    public int getAge() {
        return age;
    }

    public String getText() {
        return text;
    }
}

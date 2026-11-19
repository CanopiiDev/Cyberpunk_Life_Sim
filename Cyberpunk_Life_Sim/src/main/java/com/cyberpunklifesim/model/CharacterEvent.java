package com.cyberpunklifesim.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Een gebeurtenis die een specifiek character heeft gekregen.
 *
 * GameEvent is de template ("An Offer from the Underground"),
 * CharacterEvent is "character 5 kreeg deze gebeurtenis op zijn 23e".
 * Met "resolved" onthouden we of de speler al een keuze heeft gemaakt,
 * zodat je een gebeurtenis maar één keer kunt afhandelen.
 */
@Entity
@Table(name = "character_events")
public class CharacterEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long characterId;

    @ManyToOne(fetch = FetchType.EAGER)
    private GameEvent event;

    private int age;
    private boolean resolved = false;
    private String resultText;

    public CharacterEvent() {
    }

    public CharacterEvent(Long characterId, GameEvent event, int age) {
        this.characterId = characterId;
        this.event = event;
        this.age = age;
    }

    public void resolve(String resultText) {
        this.resolved = true;
        this.resultText = resultText;
    }

    public Long getId() { return id; }
    public Long getCharacterId() { return characterId; }
    public GameEvent getEvent() { return event; }
    public int getAge() { return age; }
    public boolean isResolved() { return resolved; }
    public String getResultText() { return resultText; }
}

package com.cyberpunklifesim.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * Een vooraf bedachte gebeurtenis (de "template"), bijvoorbeeld "An Offer from the Underground".
 * Bij Next Year kiest de API willekeurig gebeurtenissen die passen bij het character.
 *
 * De klasse heet GameEvent omdat "event" een gereserveerd woord is in MySQL.
 */
@Entity
@Table(name = "game_events")
public class GameEvent {

    // Voorwaarden: wanneer kan deze gebeurtenis gebeuren?
    public static final String ANYONE = "ANYONE";
    public static final String NO_JOB = "NO_JOB";
    public static final String HAS_JOB = "HAS_JOB";
    public static final String NO_PARTNER = "NO_PARTNER";
    public static final String HAS_PARTNER = "HAS_PARTNER";
    public static final String MARRIED = "MARRIED";
    public static final String HAS_CYBERWARE = "HAS_CYBERWARE";
    public static final String STUDYING = "STUDYING";
    public static final String IN_PRISON = "IN_PRISON";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String category;       // Personal, Career, Financial, Criminal, Health, Relationship, Random, Cyberware
    private int minAge;
    private int maxAge;
    private String requirement;    // een van de voorwaarden hierboven
    private boolean oncePerLife;   // true = kan maar één keer per leven gebeuren (bijvoorbeeld een erfenis)

    /** De keuzes bij deze gebeurtenis. cascade = keuzes worden automatisch mee opgeslagen. */
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<EventChoice> choices = new ArrayList<>();

    public GameEvent() {
    }

    public GameEvent(String title, String description, String category, int minAge, int maxAge, String requirement) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.minAge = minAge;
        this.maxAge = maxAge;
        this.requirement = requirement;
    }

    /** Markeer deze gebeurtenis als eenmalig. */
    public GameEvent once() {
        this.oncePerLife = true;
        return this;
    }

    /** Voeg een keuze toe. Geeft "this" terug, zodat je meerdere keuzes achter elkaar kunt toevoegen. */
    public GameEvent choice(EventChoice choice) {
        choice.setEvent(this);
        choices.add(choice);
        return this;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public int getMinAge() { return minAge; }
    public int getMaxAge() { return maxAge; }
    public String getRequirement() { return requirement; }
    public boolean isOncePerLife() { return oncePerLife; }
    public List<EventChoice> getChoices() { return choices; }
}

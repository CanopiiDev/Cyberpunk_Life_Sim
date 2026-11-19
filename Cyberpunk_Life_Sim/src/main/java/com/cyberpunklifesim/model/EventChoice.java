package com.cyberpunklifesim.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Eén keuze bij een gebeurtenis, met de gevolgen.
 *
 * Een keuze heeft een normaal gevolg. Daarnaast kan er een "kans" zijn (bijvoorbeeld 30%)
 * dat er iets anders gebeurt. Dan geldt het kans-gevolg IN PLAATS VAN het normale gevolg.
 *
 * Voorbeeld: "Pakket accepteren" geeft normaal €$2.000, maar met 30% kans word je gearresteerd.
 */
@Entity
@Table(name = "event_choices")
public class EventChoice {

    // Acties: speciale gevolgen die meer doen dan alleen stats veranderen
    public static final String NEW_FRIEND = "NEW_FRIEND";
    public static final String NEW_PARTNER = "NEW_PARTNER";
    public static final String MARRY = "MARRY";
    public static final String HAVE_CHILD = "HAVE_CHILD";
    public static final String BREAK_UP = "BREAK_UP";
    public static final String MAKE_ENEMY = "MAKE_ENEMY";
    public static final String GET_FIRED = "GET_FIRED";
    public static final String ESCAPE_PRISON = "ESCAPE_PRISON";
    public static final String JOB_OFFER = "JOB_OFFER:";   // gevolgd door de naam van de carrière

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private GameEvent event;

    private String text;          // de tekst op de knop, bijvoorbeeld "Accepteren"
    private String resultText;    // wat de speler te zien krijgt na de keuze

    // Normaal gevolg
    private int money;
    private int health;
    private int happiness;
    private int intelligence;
    private int streetCred;
    private int appearance;
    private String action;

    // Kans-gevolg (alleen als chance > 0)
    private int chance;           // kans in procenten
    private String chanceText;
    private int chanceMoney;
    private int chanceHealth;
    private int chanceHappiness;
    private int chanceStreetCred;
    private int chancePrisonYears;
    private String chanceAction;

    public EventChoice() {
    }

    public EventChoice(String text, String resultText) {
        this.text = text;
        this.resultText = resultText;
    }

    // Korte methodes om de gevolgen in te stellen, zodat de startdata leesbaar blijft:
    // new EventChoice("Accepteren", "...").money(2000).chance(30, "Je werd gepakt!").chancePrison(1)

    public EventChoice money(int value) { this.money = value; return this; }
    public EventChoice health(int value) { this.health = value; return this; }
    public EventChoice happiness(int value) { this.happiness = value; return this; }
    public EventChoice intelligence(int value) { this.intelligence = value; return this; }
    public EventChoice streetCred(int value) { this.streetCred = value; return this; }
    public EventChoice appearance(int value) { this.appearance = value; return this; }
    public EventChoice action(String value) { this.action = value; return this; }

    public EventChoice chance(int percentage, String text) {
        this.chance = percentage;
        this.chanceText = text;
        return this;
    }

    public EventChoice chanceMoney(int value) { this.chanceMoney = value; return this; }
    public EventChoice chanceHealth(int value) { this.chanceHealth = value; return this; }
    public EventChoice chanceHappiness(int value) { this.chanceHappiness = value; return this; }
    public EventChoice chanceStreetCred(int value) { this.chanceStreetCred = value; return this; }
    public EventChoice chancePrison(int years) { this.chancePrisonYears = years; return this; }
    public EventChoice chanceAction(String value) { this.chanceAction = value; return this; }

    public void setEvent(GameEvent event) { this.event = event; }

    public Long getId() { return id; }
    public String getText() { return text; }
    public String getResultText() { return resultText; }
    public int getMoney() { return money; }
    public int getHealth() { return health; }
    public int getHappiness() { return happiness; }
    public int getIntelligence() { return intelligence; }
    public int getStreetCred() { return streetCred; }
    public int getAppearance() { return appearance; }
    public String getAction() { return action; }
    public int getChance() { return chance; }
    public String getChanceText() { return chanceText; }
    public int getChanceMoney() { return chanceMoney; }
    public int getChanceHealth() { return chanceHealth; }
    public int getChanceHappiness() { return chanceHappiness; }
    public int getChanceStreetCred() { return chanceStreetCred; }
    public int getChancePrisonYears() { return chancePrisonYears; }
    public String getChanceAction() { return chanceAction; }
}

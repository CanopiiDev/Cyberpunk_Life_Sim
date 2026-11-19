package com.cyberpunklifesim.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * Een character (personage) van een speler.
 *
 * We noemen de klasse GameCharacter en niet Character,
 * omdat Java zelf al een klasse "Character" heeft (java.lang.Character).
 *
 * @Entity betekent: van deze klasse wordt een tabel in de database gemaakt.
 */
@Entity
@Table(name = "game_characters")
public class GameCharacter {

    /** Ieder character begint met 500 eurodollars. */
    public static final int START_MONEY = 500;

    /** Je komt als volwassene aan in de stad. */
    public static final int START_AGE = 18;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // De database bedenkt zelf het id (1, 2, 3, ...)
    private Long id;

    private String name;

    private int age = START_AGE;

    private boolean alive = true;

    /** Waaraan het character is overleden (leeg zolang hij leeft). */
    private String causeOfDeath;

    private int money = START_MONEY;

    private int streetCred = 0;

    private int jobsCompleted = 0;

    // Statistieken, altijd tussen 0 en 100
    private int health = 100;
    private int happiness = 70;
    private int smarts = 50;

    /**
     * De items die dit character heeft gekocht.
     * Dit wordt opgeslagen in een koppeltabel "character_items".
     * EAGER = de items worden meteen mee geladen als we een character ophalen.
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "character_items",
            joinColumns = @JoinColumn(name = "character_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    private List<Item> inventory = new ArrayList<>();

    // JPA heeft altijd een lege constructor nodig
    public GameCharacter() {
    }

    public GameCharacter(String name) {
        this.name = name;
    }

    // --- Handige methodes om stats aan te passen ---
    // Ze zorgen dat een stat nooit onder 0 of boven 100 komt.

    public void changeHealth(int amount) {
        health = limit(health + amount);
    }

    public void changeHappiness(int amount) {
        happiness = limit(happiness + amount);
    }

    public void changeSmarts(int amount) {
        smarts = limit(smarts + amount);
    }

    /** Geld kan niet onder 0 komen. */
    public void changeMoney(int amount) {
        money = Math.max(0, money + amount);
    }

    public void die(String cause) {
        alive = false;
        causeOfDeath = cause;
    }

    private static int limit(int value) {
        return Math.max(0, Math.min(100, value));
    }

    // --- Getters en setters ---

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isAlive() {
        return alive;
    }

    public String getCauseOfDeath() {
        return causeOfDeath;
    }

    public int getMoney() {
        return money;
    }

    public int getStreetCred() {
        return streetCred;
    }

    public void setStreetCred(int streetCred) {
        this.streetCred = streetCred;
    }

    public int getJobsCompleted() {
        return jobsCompleted;
    }

    public void setJobsCompleted(int jobsCompleted) {
        this.jobsCompleted = jobsCompleted;
    }

    public int getHealth() {
        return health;
    }

    public int getHappiness() {
        return happiness;
    }

    public int getSmarts() {
        return smarts;
    }

    public List<Item> getInventory() {
        return inventory;
    }
}

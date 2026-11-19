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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // De database bedenkt zelf het id (1, 2, 3, ...)
    private Long id;

    private String name;

    private int money = START_MONEY;

    private int streetCred = 0;

    private int jobsCompleted = 0;

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

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
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

    public List<Item> getInventory() {
        return inventory;
    }
}

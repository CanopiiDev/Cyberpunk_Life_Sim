package com.cyberpunklifesim.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Eén financiële transactie (bij- of afschrijving) op de bankrekening van een character.
 */
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long characterId;
    private int age;
    private int amount;          // positief = erbij, negatief = eraf
    private String description;
    private int balanceAfter;    // saldo na deze transactie

    public Transaction() {
    }

    public Transaction(Long characterId, int age, int amount, String description, int balanceAfter) {
        this.characterId = characterId;
        this.age = age;
        this.amount = amount;
        this.description = description;
        this.balanceAfter = balanceAfter;
    }

    public Long getId() { return id; }
    public Long getCharacterId() { return characterId; }
    public int getAge() { return age; }
    public int getAmount() { return amount; }
    public String getDescription() { return description; }
    public int getBalanceAfter() { return balanceAfter; }
}

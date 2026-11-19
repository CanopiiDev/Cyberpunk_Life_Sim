package com.cyberpunklifesim.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Een job die een character kan uitvoeren om geld en Street Cred te verdienen.
 */
@Entity
@Table(name = "jobs")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    /** Hoeveel geld je verdient met deze job. */
    private int payout;

    /** Hoeveel Street Cred je krijgt als je deze job voltooit. */
    private int streetCredReward;

    /** Hoeveel Street Cred je minimaal nodig hebt om deze job te mogen doen. */
    private int requiredStreetCred;

    public Job() {
    }

    public Job(String name, String description, int payout, int streetCredReward, int requiredStreetCred) {
        this.name = name;
        this.description = description;
        this.payout = payout;
        this.streetCredReward = streetCredReward;
        this.requiredStreetCred = requiredStreetCred;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getPayout() {
        return payout;
    }

    public int getStreetCredReward() {
        return streetCredReward;
    }

    public int getRequiredStreetCred() {
        return requiredStreetCred;
    }
}

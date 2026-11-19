package com.cyberpunklifesim.service;

import com.cyberpunklifesim.model.RandomEvent;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

/**
 * Kiest een willekeurige gebeurtenis als een character een jaar ouder wordt.
 * Wil je meer gebeurtenissen? Voeg gewoon een regel toe aan de lijst hieronder.
 */
@Service
public class RandomEventService {

    private final Random random = new Random();

    //                         tekst                                                   geld  health  happy  smarts
    private final List<RandomEvent> events = List.of(
            new RandomEvent("Je vond een creditchip op straat.",                       100,  0,      5,     0),
            new RandomEvent("Je werd beroofd door een straatbende.",                  -100, -10,    -10,    0),
            new RandomEvent("Je feestte de hele nacht door in een neonclub.",          -30,  -5,     15,    0),
            new RandomEvent("Je volgde een gratis online cursus netrunning.",          0,    0,      0,     10),
            new RandomEvent("Je werd ziek van de zure regen.",                         0,    -15,    -5,    0),
            new RandomEvent("Je adopteerde een robotkat.",                             -50,  0,      15,    0),
            new RandomEvent("Je las een verboden datablad van een megacorp.",          0,    0,      5,     5),
            new RandomEvent("Een ripperdoc gaf je een gratis check-up.",               0,    10,     0,     0),
            new RandomEvent("Je verloor geld met een gok-app.",                        -80,  0,      -10,   0),
            new RandomEvent("Een rustig jaar. Er gebeurde niet veel.",                 0,    0,      0,     0),
            new RandomEvent("Je kreeg ruzie met je buren over harde synthwave.",       0,    0,      -5,    0),
            new RandomEvent("Je won een kleine prijs in de neonloterij.",              250,  0,      10,    0),
            new RandomEvent("Je ging elke dag hardlopen over de daken van de stad.",   0,    10,     5,     0),
            new RandomEvent("Je zat het hele jaar in VR-games.",                       0,    -5,     10,    -5)
    );

    public RandomEvent pickRandomEvent() {
        return events.get(random.nextInt(events.size()));
    }

    /** Geeft true terug met een kans van "percentage" procent (0 - 100). */
    public boolean chance(int percentage) {
        return random.nextInt(100) < percentage;
    }
}

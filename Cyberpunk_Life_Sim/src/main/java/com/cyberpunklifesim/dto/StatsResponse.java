package com.cyberpunklifesim.dto;

import com.cyberpunklifesim.model.GameCharacter;

/**
 * Alleen de statistieken van een personage (voor de stat-balken).
 */
public record StatsResponse(int health, int happiness, int intelligence, int streetCred,
                            int appearance, int money, String wealth) {

    public static StatsResponse from(GameCharacter c) {
        return new StatsResponse(c.getHealth(), c.getHappiness(), c.getIntelligence(), c.getStreetCred(),
                c.getAppearance(), c.getMoney(), c.getWealth());
    }
}

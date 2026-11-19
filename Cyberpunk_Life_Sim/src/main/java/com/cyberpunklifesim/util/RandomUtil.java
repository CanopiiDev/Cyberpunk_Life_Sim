package com.cyberpunklifesim.util;

import java.util.List;
import java.util.Random;

/**
 * Alle willekeur van het spel op één plek.
 * "static" betekent dat je geen object hoeft te maken: je schrijft gewoon RandomUtil.chance(30).
 */
public final class RandomUtil {

    private static final Random RANDOM = new Random();

    private static final List<String> FIRST_NAMES = List.of(
            "Kai", "Nova", "Rex", "Luna", "Jax", "Mira", "Zane", "Yuki", "Dex", "Aria",
            "Niko", "Sera", "Taro", "Vex", "Lyra", "Orion", "Ivy", "Rook", "Sol", "Echo");

    private static final List<String> LAST_NAMES = List.of(
            "Nakamura", "Silva", "Kowalski", "Reyes", "Okafor", "Volkov", "Chen", "Jansen", "Moreau", "Haddad");

    private RandomUtil() {
    }

    /** Geeft true met een kans van "percentage" procent. chance(30) is dus 30% kans op true. */
    public static boolean chance(int percentage) {
        return RANDOM.nextInt(100) < percentage;
    }

    /** Een willekeurig getal van min tot en met max. */
    public static int between(int min, int max) {
        return min + RANDOM.nextInt(max - min + 1);
    }

    public static String firstName() {
        return FIRST_NAMES.get(RANDOM.nextInt(FIRST_NAMES.size()));
    }

    public static String fullName() {
        return firstName() + " " + LAST_NAMES.get(RANDOM.nextInt(LAST_NAMES.size()));
    }

    public static Random random() {
        return RANDOM;
    }
}

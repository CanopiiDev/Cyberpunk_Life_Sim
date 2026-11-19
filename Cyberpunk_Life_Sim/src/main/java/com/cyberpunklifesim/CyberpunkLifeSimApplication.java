package com.cyberpunklifesim;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Startpunt van de applicatie.
 * @SpringBootApplication zorgt dat Spring alle klassen in dit package (en sub-packages) vindt,
 * zoals controllers, services en repositories.
 */
@SpringBootApplication
public class CyberpunkLifeSimApplication {

    public static void main(String[] args) {
        SpringApplication.run(CyberpunkLifeSimApplication.class, args);
    }
}

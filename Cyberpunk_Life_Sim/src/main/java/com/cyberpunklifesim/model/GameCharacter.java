package com.cyberpunklifesim.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Het personage van de speler.
 *
 * We noemen de klasse GameCharacter en niet Character,
 * omdat Java zelf al een klasse "Character" heeft (java.lang.Character).
 */
@Entity
@Table(name = "game_characters")
public class GameCharacter {

    public static final int START_AGE = 18;
    public static final int START_YEAR = 2077;
    public static final int START_MONEY = 500;
    public static final int ACTIONS_PER_YEAR = 3;
    public static final String HOME_LOCATION = "Neon City - Lower District";
    public static final String PRISON_LOCATION = "Neon City Penitentiary";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- Persoonlijke gegevens ---
    private String firstName;
    private String lastName;
    private String gender;
    private LocalDate birthDate;
    private String birthPlace;
    private String nationality;

    private int age = START_AGE;
    private String location = HOME_LOCATION;
    private boolean alive = true;
    private String causeOfDeath;

    // --- Statistieken (0 - 100) ---
    private int health = 100;
    private int happiness = 75;
    private int intelligence = 50;
    private int streetCred = 0;
    private int appearance = 50;

    // --- Geld (mag negatief worden = schulden) ---
    private int money = START_MONEY;
    private int totalEarned = 0;

    // --- Criminaliteit ---
    private int convictions = 0;      // aantal keer veroordeeld
    private int prisonYearsLeft = 0;  // > 0 betekent: zit in de gevangenis

    /** Acties per jaar (solliciteren, misdaad, tijd met iemand doorbrengen). Reset bij Next Year. */
    private int actionsLeft = ACTIONS_PER_YEAR;

    // --- Carrière ---
    @ManyToOne
    private Career career;            // null = geen baan
    private int careerLevel = 0;      // 0 = eerste functie, hoger = promotie
    private int salary = 0;           // jaarsalaris
    private int yearsInPosition = 0; // jaren in de huidige functie (voor promotie)
    private String highestPosition = "Geen";
    private int highestSalary = 0;

    // --- Opleiding ---
    @ManyToOne
    private Education education;      // null = studeert niet
    private int educationYearsLeft = 0;

    /** Behaalde diploma's. Wordt opgeslagen in een aparte tabel "character_diplomas". */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "character_diplomas", joinColumns = @JoinColumn(name = "character_id"))
    @Column(name = "diploma")
    private Set<String> diplomas = new HashSet<>();

    public GameCharacter() {
    }

    public GameCharacter(String firstName, String lastName, String gender, LocalDate birthDate,
                         String birthPlace, String nationality) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.birthDate = birthDate;
        this.birthPlace = birthPlace;
        this.nationality = nationality;
    }

    // --- Stats aanpassen: blijft altijd tussen 0 en 100 ---

    public void changeHealth(int amount) {
        health = limit(health + amount);
    }

    public void changeHappiness(int amount) {
        happiness = limit(happiness + amount);
    }

    public void changeIntelligence(int amount) {
        intelligence = limit(intelligence + amount);
    }

    public void changeStreetCred(int amount) {
        streetCred = limit(streetCred + amount);
    }

    public void changeAppearance(int amount) {
        appearance = limit(appearance + amount);
    }

    private static int limit(int value) {
        return Math.max(0, Math.min(100, value));
    }

    // --- Berekende waarden ---

    public String getFullName() {
        return firstName + " " + lastName;
    }

    /** Het spel begint in 2077 op je 18e. */
    public int getCurrentYear() {
        return START_YEAR + (age - START_AGE);
    }

    public boolean isInPrison() {
        return prisonYearsLeft > 0;
    }

    public String getPosition() {
        return career == null ? null : career.getPositionTitle(careerLevel);
    }

    /** Wealth wordt bepaald door het geld dat je hebt. */
    public String getWealth() {
        if (money < 0) return "In de schulden";
        if (money < 1_000) return "Blut";
        if (money < 10_000) return "Rondkomen";
        if (money < 100_000) return "Welgesteld";
        return "Rijk";
    }

    public String getCriminalRecord() {
        return convictions == 0 ? "Geen" : convictions + " veroordeling(en)";
    }

    public void die(String cause) {
        alive = false;
        causeOfDeath = cause;
    }

    // --- Getters en setters ---

    public Long getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getGender() { return gender; }
    public LocalDate getBirthDate() { return birthDate; }
    public String getBirthPlace() { return birthPlace; }
    public String getNationality() { return nationality; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public boolean isAlive() { return alive; }
    public String getCauseOfDeath() { return causeOfDeath; }

    public int getHealth() { return health; }
    public int getHappiness() { return happiness; }
    public int getIntelligence() { return intelligence; }
    public int getStreetCred() { return streetCred; }
    public int getAppearance() { return appearance; }

    public int getMoney() { return money; }
    public void setMoney(int money) { this.money = money; }

    public int getTotalEarned() { return totalEarned; }
    public void setTotalEarned(int totalEarned) { this.totalEarned = totalEarned; }

    public int getConvictions() { return convictions; }
    public void setConvictions(int convictions) { this.convictions = convictions; }

    public int getPrisonYearsLeft() { return prisonYearsLeft; }
    public void setPrisonYearsLeft(int prisonYearsLeft) { this.prisonYearsLeft = prisonYearsLeft; }

    public int getActionsLeft() { return actionsLeft; }
    public void setActionsLeft(int actionsLeft) { this.actionsLeft = actionsLeft; }

    public Career getCareer() { return career; }
    public void setCareer(Career career) { this.career = career; }

    public int getCareerLevel() { return careerLevel; }
    public void setCareerLevel(int careerLevel) { this.careerLevel = careerLevel; }

    public int getSalary() { return salary; }
    public void setSalary(int salary) { this.salary = salary; }

    public int getYearsInPosition() { return yearsInPosition; }
    public void setYearsInPosition(int yearsInPosition) { this.yearsInPosition = yearsInPosition; }

    public String getHighestPosition() { return highestPosition; }
    public void setHighestPosition(String highestPosition) { this.highestPosition = highestPosition; }

    public int getHighestSalary() { return highestSalary; }
    public void setHighestSalary(int highestSalary) { this.highestSalary = highestSalary; }

    public Education getEducation() { return education; }
    public void setEducation(Education education) { this.education = education; }

    public int getEducationYearsLeft() { return educationYearsLeft; }
    public void setEducationYearsLeft(int educationYearsLeft) { this.educationYearsLeft = educationYearsLeft; }

    public Set<String> getDiplomas() { return diplomas; }
}

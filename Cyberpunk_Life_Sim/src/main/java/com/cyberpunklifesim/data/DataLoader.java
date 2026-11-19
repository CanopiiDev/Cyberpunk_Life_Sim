package com.cyberpunklifesim.data;

import com.cyberpunklifesim.model.Career;
import com.cyberpunklifesim.model.Crime;
import com.cyberpunklifesim.model.Education;
import com.cyberpunklifesim.model.Item;
import com.cyberpunklifesim.repository.CareerRepository;
import com.cyberpunklifesim.repository.CrimeRepository;
import com.cyberpunklifesim.repository.EducationRepository;
import com.cyberpunklifesim.repository.ItemRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Zet bij het opstarten de vaste spelgegevens in de database: carrières, opleidingen, items en misdaden.
 * (De gebeurtenissen staan in EventData.)
 *
 * We voegen alleen data toe als de tabel nog leeg is, anders krijg je bij iedere herstart dubbele rijen.
 */
@Component
public class DataLoader implements CommandLineRunner {

    private final CareerRepository careerRepository;
    private final EducationRepository educationRepository;
    private final ItemRepository itemRepository;
    private final CrimeRepository crimeRepository;

    public DataLoader(CareerRepository careerRepository, EducationRepository educationRepository,
                      ItemRepository itemRepository, CrimeRepository crimeRepository) {
        this.careerRepository = careerRepository;
        this.educationRepository = educationRepository;
        this.itemRepository = itemRepository;
        this.crimeRepository = crimeRepository;
    }

    @Override
    public void run(String... args) {
        loadEducations();
        loadCareers();
        loadItems();
        loadCrimes();
    }

    private void loadEducations() {
        if (educationRepository.count() > 0) return;
        //                                    naam                beschrijving                                              jaren  kosten/jaar  int/jaar  min. int
        educationRepository.save(new Education("Computer Science", "Programmeren, AI en netwerken. Nodig voor Netrunner.",  4,     3000,        5,        50));
        educationRepository.save(new Education("Engineering",      "Techniek en cyberware. Nodig voor Technician.",          3,     2500,        4,        40));
        educationRepository.save(new Education("Medicine",         "Word arts of ripperdoc. Zwaar, maar het loont.",         6,     5000,        6,        60));
        educationRepository.save(new Education("Business",         "Leer hoe megacorporaties werken.",                       3,     3500,        3,        40));
        educationRepository.save(new Education("Criminal Studies", "Criminologie... of hoe je niet gepakt wordt.",           2,     1500,        2,        30));
    }

    private void loadCareers() {
        if (careerRepository.count() > 0) return;
        //                              naam                    beschrijving                                    salaris  int  cred  diploma             schoon strafblad  illegaal  functies (laag -> hoog)
        careerRepository.save(new Career("Delivery Runner",      "Bezorgt pakketjes door de hele stad.",         5000,    0,   0,    null,               false,            false,    "Rookie Runner,Runner,Senior Runner,Route Manager"));
        careerRepository.save(new Career("Street Dealer",        "Illegale handelaar op straat.",                8000,    0,   0,    null,               false,            true,     "Corner Dealer,Supplier,Distributor,Kingpin"));
        careerRepository.save(new Career("Police Officer",       "Politieagent in een gevaarlijke stad.",        14000,   45,  0,    null,               true,             false,    "Cadet,Officer,Sergeant,Lieutenant,Captain"));
        careerRepository.save(new Career("Mercenary",            "Professionele huurling voor gevaarlijke klussen.", 16000, 0, 20,  null,               false,            false,    "Rookie Merc,Merc,Veteran Merc,Legendary Merc"));
        careerRepository.save(new Career("Freelance Programmer", "Zelfstandig programmeur voor allerlei klanten.", 12000,  55,  0,    null,               false,            false,    "Junior Freelancer,Freelancer,Senior Freelancer,Tech Guru"));
        careerRepository.save(new Career("Technician",           "Technicus die cyberware repareert en installeert.", 15000, 40, 0, "Engineering",      false,            false,    "Apprentice Tech,Technician,Senior Technician,Chief Technician"));
        careerRepository.save(new Career("Netrunner",            "Hacker en digitale specialist.",               22000,   65,  10,   "Computer Science", false,            false,    "Script Kiddie,Netrunner,Elite Netrunner,Legendary Netrunner"));
        careerRepository.save(new Career("Corporate Employee",   "Werknemer bij een megacorporatie.",            25000,   50,  0,    "Business",         true,             false,    "Intern,Associate,Manager,Director,Executive"));
        careerRepository.save(new Career("Ripperdoc",            "Cyberware-chirurg met een eigen kliniek.",     35000,   60,  0,    "Medicine",         false,            false,    "Junior Ripperdoc,Ripperdoc,Senior Ripperdoc,Legendary Ripperdoc"));
        careerRepository.save(new Career("Fixer",                "Bemiddelaar tussen criminelen en klanten.",    30000,   40,  40,   "Criminal Studies", false,            true,     "Small-time Fixer,Fixer,Big Fixer,Underworld Legend"));
    }

    private void loadItems() {
        if (itemRepository.count() > 0) return;

        // Clothing
        itemRepository.save(new Item("Neon Jacket", "Een jas die oplicht in het donker.", 250, "Clothing", "Jacket").appearance(8).happiness(3));
        itemRepository.save(new Item("LED Shirt", "Shirt met bewegende hologrammen.", 80, "Clothing", "Shirt").appearance(3));
        itemRepository.save(new Item("Kevlar Pants", "Stijlvol én kogelwerend.", 400, "Clothing", "Pants").appearance(4).health(3));
        itemRepository.save(new Item("Mag Boots", "Magnetische laarzen met neonzolen.", 300, "Clothing", "Shoes").appearance(5));

        // Cyberware (kan maar één keer geïnstalleerd worden, en heeft soms bijwerkingen)
        itemRepository.save(new Item("Neural Implant", "Chip in je hersenen die je sneller laat denken.", 4000, "Cyberware", "Neural Implant")
                .intelligence(15).happiness(-5).sideEffect("Je hebt soms last van hoofdpijn (-5 Happiness)."));
        itemRepository.save(new Item("Optical Enhancement", "Cybernetische ogen met zoom en nachtzicht.", 3000, "Cyberware", "Optical Enhancement")
                .appearance(10).intelligence(5));
        itemRepository.save(new Item("Reinforced Skeleton", "Titanium botten. Bijna onbreekbaar.", 6000, "Cyberware", "Reinforced Skeleton")
                .health(20).appearance(-5).sideEffect("Je loopt wat stijf (-5 Appearance)."));
        itemRepository.save(new Item("Reflex Booster", "Snellere reflexen. Handig bij riskante klussen.", 5000, "Cyberware", "Reflex Booster")
                .crime(15).happiness(-5).sideEffect("Je bent vaak gespannen (-5 Happiness). 15% minder kans om gepakt te worden."));

        // Other
        itemRepository.save(new Item("Synth Noodles", "Goedkoop en verrassend lekker.", 20, "Other", "Food").health(3).happiness(2));
        itemRepository.save(new Item("Med Kit", "Lapt je weer op na een zware tijd.", 300, "Other", "Medicine").health(20));
        itemRepository.save(new Item("Holo Phone", "De nieuwste telefoon met hologram-scherm.", 800, "Other", "Electronics").happiness(8));
    }

    private void loadCrimes() {
        if (crimeRepository.count() > 0) return;
        //                            naam               beschrijving                                    min    max    cred  pak-kans  boete  cel  min. int
        crimeRepository.save(new Crime("Theft",           "Steel spullen van rijke toeristen.",          100,   500,   2,    20,       300,   0,   0));
        crimeRepository.save(new Crime("Smuggling",       "Smokkel illegale chips de stad in.",          500,   1500,  4,    25,       1000,  1,   0));
        crimeRepository.save(new Crime("Drug Dealing",    "Verkoop synth-drugs in de clubs.",            300,   1200,  3,    30,       800,   1,   0));
        crimeRepository.save(new Crime("Gang Contract",   "Doe een klus voor een straatbende.",          800,   2500,  8,    35,       1500,  2,   0));
        crimeRepository.save(new Crime("Illegal Hacking", "Hack een bankrekening van een megacorp.",     1500,  5000,  6,    30,       3000,  2,   60));
        crimeRepository.save(new Crime("Fraud",           "Verkoop nep-cyberware via het net.",          1000,  3000,  3,    25,       2000,  1,   50));
    }
}

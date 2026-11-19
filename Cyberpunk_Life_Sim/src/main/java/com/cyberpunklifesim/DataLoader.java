package com.cyberpunklifesim;

import com.cyberpunklifesim.model.Item;
import com.cyberpunklifesim.model.Job;
import com.cyberpunklifesim.repository.ItemRepository;
import com.cyberpunklifesim.repository.JobRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Zet bij het opstarten een paar jobs en items in de database,
 * zodat er meteen iets te doen is in de game.
 *
 * CommandLineRunner = code die één keer draait zodra de app gestart is.
 * We voegen alleen data toe als de tabel nog leeg is, anders krijg je bij
 * iedere herstart dubbele jobs en items.
 */
@Component
public class DataLoader implements CommandLineRunner {

    private final JobRepository jobRepository;
    private final ItemRepository itemRepository;

    public DataLoader(JobRepository jobRepository, ItemRepository itemRepository) {
        this.jobRepository = jobRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public void run(String... args) {
        if (jobRepository.count() == 0) {
            //                         naam               beschrijving                                        geld  cred  health  min.cred  min.smarts
            jobRepository.save(new Job("Noodle Delivery", "Bezorg noodles door de neonstraten.",              50,   1,    2,      0,        0));
            jobRepository.save(new Job("Data Courier",    "Breng een versleutelde datachip naar een klant.",  150,  3,    5,      5,        0));
            jobRepository.save(new Job("Netrunner Hack",  "Hack een corporate server voor een fixer.",        400,  8,    10,     20,       60));
            jobRepository.save(new Job("Corpo Heist",     "Steel prototype-tech uit een megacorp-toren.",     1000, 20,   25,     50,       40));
        }

        if (itemRepository.count() == 0) {
            //                           naam            beschrijving                                    prijs  health  happy  smarts
            itemRepository.save(new Item("Synth Burger", "Goedkoop en verrassend lekker.",                15,    5,      2,     0));
            itemRepository.save(new Item("Neon Jacket",  "Een jas die oplicht in het donker.",            250,   0,      10,    0));
            itemRepository.save(new Item("Med Kit",      "Lapt je weer op na een zware job.",             100,   25,     0,     0));
            itemRepository.save(new Item("Skill Chip",   "Upload kennis direct in je hersenen.",          400,   0,      0,     15));
            itemRepository.save(new Item("Cyber Eye",    "Implantaat waarmee je in het donker ziet.",     1200,  0,      10,    5));
            itemRepository.save(new Item("Hover Bike",   "Snel door de stad, boven het verkeer uit.",     5000,  0,      30,    0));
        }
    }
}

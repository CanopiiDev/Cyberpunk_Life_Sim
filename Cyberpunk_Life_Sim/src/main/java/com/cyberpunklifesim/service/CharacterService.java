package com.cyberpunklifesim.service;

import com.cyberpunklifesim.model.GameCharacter;
import com.cyberpunklifesim.model.Item;
import com.cyberpunklifesim.model.Job;
import com.cyberpunklifesim.repository.GameCharacterRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Hier staan de spelregels voor characters:
 * aanmaken, ophalen, verwijderen, jobs uitvoeren en items kopen.
 */
@Service
public class CharacterService {

    private final GameCharacterRepository characterRepository;
    private final JobService jobService;
    private final ItemService itemService;

    public CharacterService(GameCharacterRepository characterRepository, JobService jobService, ItemService itemService) {
        this.characterRepository = characterRepository;
        this.jobService = jobService;
        this.itemService = itemService;
    }

    public List<GameCharacter> getAllCharacters() {
        return characterRepository.findAll();
    }

    public GameCharacter getCharacter(Long id) {
        return characterRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Character met id " + id + " bestaat niet"));
    }

    public GameCharacter createCharacter(String name) {
        // Een character moet een naam hebben
        if (name == null || name.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Naam mag niet leeg zijn");
        }

        // Nieuwe characters krijgen automatisch 500 geld en 0 Street Cred (zie GameCharacter)
        GameCharacter character = new GameCharacter(name.trim());
        return characterRepository.save(character);
    }

    public void deleteCharacter(Long id) {
        GameCharacter character = getCharacter(id); // geeft 404 als hij niet bestaat
        characterRepository.delete(character);
    }

    public GameCharacter doJob(Long characterId, Long jobId) {
        GameCharacter character = getCharacter(characterId);
        Job job = jobService.getJob(jobId);

        // Spelregel: je hebt genoeg Street Cred nodig voor zwaardere jobs
        if (character.getStreetCred() < job.getRequiredStreetCred()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Niet genoeg Street Cred. Nodig: " + job.getRequiredStreetCred()
                            + ", jij hebt: " + character.getStreetCred());
        }

        // Beloning: geld en Street Cred erbij, en 1 job extra voltooid
        character.setMoney(character.getMoney() + job.getPayout());
        character.setStreetCred(character.getStreetCred() + job.getStreetCredReward());
        character.setJobsCompleted(character.getJobsCompleted() + 1);

        return characterRepository.save(character);
    }

    public GameCharacter buyItem(Long characterId, Long itemId) {
        GameCharacter character = getCharacter(characterId);
        Item item = itemService.getItem(itemId);

        // Spelregel: je kunt alleen kopen als je genoeg geld hebt
        if (character.getMoney() < item.getPrice()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Niet genoeg geld. Prijs: " + item.getPrice() + ", jij hebt: " + character.getMoney());
        }

        // Geld eraf en het item in de inventory
        character.setMoney(character.getMoney() - item.getPrice());
        character.getInventory().add(item);

        return characterRepository.save(character);
    }
}

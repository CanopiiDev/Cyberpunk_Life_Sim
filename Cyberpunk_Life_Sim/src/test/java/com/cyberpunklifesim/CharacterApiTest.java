package com.cyberpunklifesim;

import com.cyberpunklifesim.model.GameCharacter;
import com.cyberpunklifesim.repository.GameCharacterRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test de API van begin tot eind.
 * MockMvc doet "nep" HTTP-requests naar de API, zonder dat de server echt hoeft te draaien.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // iedere test een schone database
class CharacterApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GameCharacterRepository characterRepository;

    @Test
    void nieuwCharacterHeeftStartwaarden() throws Exception {
        mockMvc.perform(post("/api/characters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"V\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("V"))
                .andExpect(jsonPath("$.age").value(18))
                .andExpect(jsonPath("$.alive").value(true))
                .andExpect(jsonPath("$.money").value(500))
                .andExpect(jsonPath("$.streetCred").value(0))
                .andExpect(jsonPath("$.health").value(100));
    }

    @Test
    void characterZonderNaamGeeftFout() throws Exception {
        mockMvc.perform(post("/api/characters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void jobsEnItemsZijnBeschikbaar() throws Exception {
        mockMvc.perform(get("/api/jobs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4));

        mockMvc.perform(get("/api/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(6));
    }

    @Test
    void ouderWordenVerhoogtLeeftijdEnSchrijftInLevensverhaal() throws Exception {
        maakCharacter();

        mockMvc.perform(post("/api/characters/1/age-up"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.age").value(19));

        // 1 regel van het aankomen in de stad + 1 regel van het nieuwe jaar
        mockMvc.perform(get("/api/characters/1/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].age").value(19));
    }

    @Test
    void jobUitvoerenGeeftGeldEnStreetCredMaarKostHealth() throws Exception {
        maakCharacter();

        // Job 1 = Noodle Delivery: +50 geld, +1 Street Cred, -2 health
        mockMvc.perform(post("/api/characters/1/jobs/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.money").value(550))
                .andExpect(jsonPath("$.streetCred").value(1))
                .andExpect(jsonPath("$.health").value(98))
                .andExpect(jsonPath("$.jobsCompleted").value(1));
    }

    @Test
    void jobZonderGenoegStreetCredGeeftFout() throws Exception {
        maakCharacter();

        // Job 4 = Corpo Heist: vereist 50 Street Cred
        mockMvc.perform(post("/api/characters/1/jobs/4"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void itemKopenHaaltGeldErafEnGeeftBonus() throws Exception {
        maakCharacter();

        // Item 2 = Neon Jacket: 250 geld, +10 happiness (70 -> 80)
        mockMvc.perform(post("/api/characters/1/buy/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.money").value(250))
                .andExpect(jsonPath("$.happiness").value(80))
                .andExpect(jsonPath("$.inventory[0].name").value("Neon Jacket"));
    }

    @Test
    void itemTeDuurGeeftFout() throws Exception {
        maakCharacter();

        // Item 6 = Hover Bike: 5000 geld, we hebben er maar 500
        mockMvc.perform(post("/api/characters/1/buy/6"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void doodCharacterKanNietsMeer() throws Exception {
        maakCharacter();

        // We laten het character direct in de database overlijden
        GameCharacter character = characterRepository.findById(1L).orElseThrow();
        character.die("Test");
        characterRepository.save(character);

        mockMvc.perform(post("/api/characters/1/age-up")).andExpect(status().isBadRequest());
        mockMvc.perform(post("/api/characters/1/jobs/1")).andExpect(status().isBadRequest());
        mockMvc.perform(post("/api/characters/1/buy/1")).andExpect(status().isBadRequest());
    }

    @Test
    void characterVerwijderen() throws Exception {
        maakCharacter();

        mockMvc.perform(delete("/api/characters/1"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/characters/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void reactAppMagDeApiAanroepen() throws Exception {
        // De browser stuurt eerst een "preflight" OPTIONS-verzoek om te vragen of het mag
        mockMvc.perform(options("/api/characters")
                        .header("Origin", "http://localhost:5173")
                        .header("Access-Control-Request-Method", "POST"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"));
    }

    private void maakCharacter() throws Exception {
        mockMvc.perform(post("/api/characters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"V\"}"))
                .andExpect(status().isCreated());
    }
}

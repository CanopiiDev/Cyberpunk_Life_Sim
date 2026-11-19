package com.cyberpunklifesim;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test de API van begin tot eind: character maken, job doen, item kopen, verwijderen.
 * MockMvc doet "nep" HTTP-requests naar de API, zonder dat de server echt hoeft te draaien.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // iedere test een schone database
class CharacterApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void nieuwCharacterBegintMet500Geld() throws Exception {
        mockMvc.perform(post("/api/characters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"V\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("V"))
                .andExpect(jsonPath("$.money").value(500))
                .andExpect(jsonPath("$.streetCred").value(0));
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
                .andExpect(jsonPath("$.length()").value(4));
    }

    @Test
    void jobUitvoerenGeeftGeldEnStreetCred() throws Exception {
        maakCharacter();

        // Job 1 = Noodle Delivery: +50 geld, +1 Street Cred
        mockMvc.perform(post("/api/characters/1/jobs/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.money").value(550))
                .andExpect(jsonPath("$.streetCred").value(1))
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
    void itemKopenHaaltGeldEraf() throws Exception {
        maakCharacter();

        // Item 2 = Neon Jacket: 250 geld
        mockMvc.perform(post("/api/characters/1/buy/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.money").value(250))
                .andExpect(jsonPath("$.inventory[0].name").value("Neon Jacket"));
    }

    @Test
    void itemTeDuurGeeftFout() throws Exception {
        maakCharacter();

        // Item 4 = Hover Bike: 5000 geld, we hebben er maar 500
        mockMvc.perform(post("/api/characters/1/buy/4"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void characterVerwijderen() throws Exception {
        maakCharacter();

        mockMvc.perform(delete("/api/characters/1"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/characters/1"))
                .andExpect(status().isNotFound());
    }

    private void maakCharacter() throws Exception {
        mockMvc.perform(post("/api/characters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"V\"}"))
                .andExpect(status().isCreated());
    }
}

package com.cyberpunklifesim;

import com.cyberpunklifesim.model.GameCharacter;
import com.cyberpunklifesim.repository.GameCharacterRepository;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test de API van begin tot eind.
 * MockMvc doet "nep" HTTP-requests, zonder dat de server echt hoeft te draaien.
 * De tests gebruiken een H2 database in het geheugen (zie src/test/resources/application.properties).
 *
 * Id's van de startdata (zie DataLoader): Delivery Runner = carrière 1, Netrunner = carrière 7,
 * Neon Jacket = item 1, Optical Enhancement = item 6, Reflex Booster = item 8, Computer Science = opleiding 1.
 */
@SpringBootTest
@AutoConfigureMockMvc
class CharacterApiTest {

    private static final String NEW_CHARACTER = """
            {
              "firstName": "Kai", "lastName": "Nakamura", "gender": "Male",
              "birthDate": "2001-04-12", "birthPlace": "Neon City", "nationality": "Dutch"
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GameCharacterRepository characterRepository;

    // ---------- UC01: personage aanmaken ----------

    @Test
    void nieuwPersonageKrijgtStartwaarden() throws Exception {
        mockMvc.perform(post("/api/characters").contentType(MediaType.APPLICATION_JSON).content(NEW_CHARACTER))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fullName").value("Kai Nakamura"))
                .andExpect(jsonPath("$.age").value(18))
                .andExpect(jsonPath("$.currentYear").value(2077))
                .andExpect(jsonPath("$.birthDate").value("2059-04-12"))
                .andExpect(jsonPath("$.money").value(500))
                .andExpect(jsonPath("$.health").value(100))
                .andExpect(jsonPath("$.happiness").value(75))
                .andExpect(jsonPath("$.intelligence").value(50))
                .andExpect(jsonPath("$.streetCred").value(0))
                .andExpect(jsonPath("$.appearance").value(50))
                .andExpect(jsonPath("$.criminalRecord").value("Geen"))
                .andExpect(jsonPath("$.alive").value(true));
    }

    @Test
    void personageZonderVoornaamGeeftFout() throws Exception {
        String body = NEW_CHARACTER.replace("\"Kai\"", "\"\"");
        mockMvc.perform(post("/api/characters").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Voornaam is verplicht"));
    }

    @Test
    void nieuwPersonageHeeftOudersEnLevensgeschiedenis() throws Exception {
        long id = createCharacter();
        mockMvc.perform(get("/api/characters/" + id + "/relationships"))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[*].label", hasItem("Moeder")));
        mockMvc.perform(get("/api/characters/" + id + "/life-history"))
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void onbekendPersonageGeeft404() throws Exception {
        mockMvc.perform(get("/api/characters/99999")).andExpect(status().isNotFound());
    }

    // ---------- UC02 + UC03: Next Year en gebeurtenissen ----------

    @Test
    void nextYearGeeftGebeurtenissenDieMaarEenKeerAfgehandeldKunnenWorden() throws Exception {
        long id = createCharacter();

        MvcResult result = mockMvc.perform(post("/api/characters/" + id + "/age"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.character.age").value(19))
                .andExpect(jsonPath("$.character.currentYear").value(2078))
                .andExpect(jsonPath("$.yearSummary.length()").value(greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.events.length()").value(greaterThanOrEqualTo(1)))
                .andReturn();

        // Met openstaande gebeurtenissen kun je niet nog een jaar verder
        mockMvc.perform(post("/api/characters/" + id + "/age")).andExpect(status().isBadRequest());

        // Alle gebeurtenissen afhandelen met de eerste keuze
        String json = result.getResponse().getContentAsString();
        List<Integer> eventIds = JsonPath.read(json, "$.events[*].id");
        List<Integer> choiceIds = JsonPath.read(json, "$.events[*].choices[0].id");
        for (int i = 0; i < eventIds.size(); i++) {
            String url = "/api/characters/" + id + "/events/" + eventIds.get(i) + "/choices";
            String choice = "{\"choiceId\": " + choiceIds.get(i) + "}";
            mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(choice))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").isNotEmpty());

            // Dezelfde gebeurtenis nog een keer afhandelen mag niet
            mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(choice))
                    .andExpect(status().isBadRequest());
        }

        mockMvc.perform(get("/api/characters/" + id + "/events")).andExpect(jsonPath("$.length()").value(0));

        // Huur is betaald en opgeslagen als transactie
        mockMvc.perform(get("/api/characters/" + id + "/transactions"))
                .andExpect(jsonPath("$[*].description", hasItem("Huur en levensonderhoud")));
    }

    @Test
    void keuzeVanEenAndereGebeurtenisGeeftFout() throws Exception {
        long id = createCharacter();
        String json = mockMvc.perform(post("/api/characters/" + id + "/age")).andReturn().getResponse().getContentAsString();
        int eventId = JsonPath.read(json, "$.events[0].id");

        mockMvc.perform(post("/api/characters/" + id + "/events/" + eventId + "/choices")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"choiceId\": 99999}"))
                .andExpect(status().isBadRequest());
    }

    // ---------- UC04: carrière ----------

    @Test
    void solliciterenZonderDiplomaGeeftFout() throws Exception {
        long id = createCharacter();
        mockMvc.perform(post("/api/characters/" + id + "/career/apply/7")) // Netrunner
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Je voldoet niet aan de eisen. Je hebt minimaal 65 Intelligence nodig."));
    }

    @Test
    void solliciterenKostEenActie() throws Exception {
        long id = createCharacter();
        mockMvc.perform(post("/api/characters/" + id + "/career/apply/1")) // Delivery Runner, geen eisen
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.character.actionsLeft").value(2));

        mockMvc.perform(get("/api/characters/" + id + "/career")).andExpect(status().isOk());
    }

    @Test
    void naDrieActiesMoetJeWachtenTotVolgendJaar() throws Exception {
        long id = createCharacter();
        String json = mockMvc.perform(get("/api/characters/" + id + "/relationships")).andReturn().getResponse().getContentAsString();
        int motherId = JsonPath.read(json, "$[0].id");
        String url = "/api/characters/" + id + "/relationships/" + motherId + "/spend-time";

        mockMvc.perform(post(url)).andExpect(status().isOk());
        mockMvc.perform(post(url)).andExpect(status().isOk());
        mockMvc.perform(post(url)).andExpect(status().isOk());
        mockMvc.perform(post(url)).andExpect(status().isBadRequest());
    }

    // ---------- UC05: winkel ----------

    @Test
    void itemKopenHaaltGeldErafEnKomtInInventory() throws Exception {
        long id = createCharacter();
        mockMvc.perform(post("/api/characters/" + id + "/items/1/buy")) // Neon Jacket €$250
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.character.money").value(250))
                .andExpect(jsonPath("$.character.appearance").value(58));

        mockMvc.perform(get("/api/characters/" + id + "/inventory"))
                .andExpect(jsonPath("$[0].item.name").value("Neon Jacket"));
        mockMvc.perform(get("/api/characters/" + id + "/transactions"))
                .andExpect(jsonPath("$[0].amount").value(-250));
    }

    @Test
    void itemTeDuurGeeftFout() throws Exception {
        long id = createCharacter();
        mockMvc.perform(post("/api/characters/" + id + "/items/8/buy")) // Reflex Booster €$5.000
                .andExpect(status().isBadRequest());
    }

    @Test
    void cyberwareKanMaarEenKeerGeinstalleerdWorden() throws Exception {
        long id = createCharacter();
        GameCharacter character = characterRepository.findById(id).orElseThrow();
        character.setMoney(20_000);
        characterRepository.save(character);

        mockMvc.perform(post("/api/characters/" + id + "/items/6/buy")).andExpect(status().isOk());
        mockMvc.perform(post("/api/characters/" + id + "/items/6/buy")).andExpect(status().isBadRequest());
    }

    // ---------- Opleiding ----------

    @Test
    void opleidingStarten() throws Exception {
        long id = createCharacter();
        mockMvc.perform(post("/api/characters/" + id + "/education/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.character.education").value("Computer Science"))
                .andExpect(jsonPath("$.character.educationYearsLeft").value(4));

        mockMvc.perform(post("/api/characters/" + id + "/education/2")).andExpect(status().isBadRequest());
    }

    // ---------- Gevangenis en overlijden ----------

    @Test
    void inDeGevangenisKunJeNietWinkelen() throws Exception {
        long id = createCharacter();
        GameCharacter character = characterRepository.findById(id).orElseThrow();
        character.setPrisonYearsLeft(2);
        characterRepository.save(character);

        mockMvc.perform(post("/api/characters/" + id + "/items/1/buy")).andExpect(status().isBadRequest());
        mockMvc.perform(post("/api/characters/" + id + "/career/apply/1")).andExpect(status().isBadRequest());
    }

    @Test
    void overledenPersonageKanNietsMeerEnHeeftEindoverzicht() throws Exception {
        long id = createCharacter();
        GameCharacter character = characterRepository.findById(id).orElseThrow();
        character.die("Test");
        characterRepository.save(character);

        mockMvc.perform(post("/api/characters/" + id + "/age")).andExpect(status().isBadRequest());
        mockMvc.perform(post("/api/characters/" + id + "/items/1/buy")).andExpect(status().isBadRequest());

        mockMvc.perform(get("/api/characters/" + id + "/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.alive").value(false))
                .andExpect(jsonPath("$.causeOfDeath").value("Test"))
                .andExpect(jsonPath("$.deathDate").value("2077-04-12"))
                .andExpect(jsonPath("$.relationships.length()").value(2));
    }

    @Test
    void personageVerwijderen() throws Exception {
        long id = createCharacter();
        mockMvc.perform(post("/api/characters/" + id + "/items/1/buy"));

        mockMvc.perform(delete("/api/characters/" + id)).andExpect(status().isNoContent());
        mockMvc.perform(get("/api/characters/" + id)).andExpect(status().isNotFound());
    }

    // ---------- Startdata en CORS ----------

    @Test
    void startdataIsGeladen() throws Exception {
        mockMvc.perform(get("/api/careers")).andExpect(jsonPath("$.length()").value(10));
        mockMvc.perform(get("/api/education")).andExpect(jsonPath("$.length()").value(5));
        mockMvc.perform(get("/api/items")).andExpect(jsonPath("$.length()").value(11));
        mockMvc.perform(get("/api/crimes")).andExpect(jsonPath("$.length()").value(6));
    }

    @Test
    void reactAppMagDeApiAanroepen() throws Exception {
        mockMvc.perform(options("/api/characters")
                        .header("Origin", "http://localhost:5173")
                        .header("Access-Control-Request-Method", "POST"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"));
    }

    // ---------- Hulpmethode ----------

    private long createCharacter() throws Exception {
        String json = mockMvc.perform(post("/api/characters").contentType(MediaType.APPLICATION_JSON).content(NEW_CHARACTER))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(json, "$.id")).longValue();
    }
}

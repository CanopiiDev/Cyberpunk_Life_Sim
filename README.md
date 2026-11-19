# Cyberpunk_Life_Sim

Een simpele life simulator in een futuristische cyberpunkstad. Je begint als 18-jarige in Neon City
met €$500, doet jobs, koopt spullen, wordt ieder jaar ouder en maakt willekeurige gebeurtenissen mee,
tot je doodgaat.

- `Cyberpunk_Life_Sim/` – REST API in Java met Spring Boot en MySQL
- `client/` – React frontend (Vite)

## Starten

Je hebt nodig: Java 21, Maven, Node.js en MySQL.

**1. MySQL** moet draaien op `localhost:3306`. Zet je gebruikersnaam en wachtwoord in
`Cyberpunk_Life_Sim/src/main/resources/application.properties`. De database wordt automatisch aangemaakt.

**2. API starten** (terminal 1):

```bash
cd Cyberpunk_Life_Sim
mvn spring-boot:run
```

De API draait op http://localhost:8080.

**3. React starten** (terminal 2):

```bash
cd client
npm install
npm run dev
```

Open http://localhost:5173 in je browser.

Tests van de API draaien (geen MySQL nodig): `cd Cyberpunk_Life_Sim && mvn test`

## Endpoints

| Methode | URL | Wat doet het |
|---|---|---|
| GET | `/api/characters` | Alle characters ophalen |
| GET | `/api/characters/{id}` | Eén character ophalen (geld, leeftijd en stats) |
| GET | `/api/characters/{id}/events` | Het levensverhaal van een character (nieuwste eerst) |
| POST | `/api/characters` | Character aanmaken, body: `{ "name": "V" }` |
| DELETE | `/api/characters/{id}` | Character verwijderen |
| POST | `/api/characters/{id}/age-up` | Een jaar ouder worden (+ willekeurige gebeurtenis) |
| GET | `/api/jobs` | Beschikbare jobs ophalen |
| POST | `/api/characters/{id}/jobs/{jobId}` | Een job uitvoeren |
| GET | `/api/items` | Beschikbare items ophalen |
| POST | `/api/characters/{id}/buy/{itemId}` | Een item kopen |

## Spelregels

- Je begint op 18 jaar met €$500, 100 health, 70 happiness, 50 smarts en 0 Street Cred.
- **+1 jaar**: je wordt ouder en er gebeurt iets willekeurigs (geld vinden, beroofd worden, ziek worden, ...).
  Na je 40e verlies je ieder jaar wat health.
- **Jobs** geven geld en Street Cred, maar kosten health. Zware jobs vereisen Street Cred en/of smarts.
- **Items** kosten geld en geven een bonus op health, happiness of smarts.
- **Doodgaan**: bij 0 health, of na je 60e met een steeds grotere kans van ouderdom.
  Een overleden character kan niets meer doen. Begin dan een nieuw leven.

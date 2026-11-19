# Cyberpunk Life Simulator

Een tekstgebaseerde life simulator in een eigen cyberpunkwereld. Je begint als 18-jarige in Neon City in het jaar 2077
en bouwt je leven op: werk, studie, relaties, cyberware, misdaad... tot je overlijdt.

- `Cyberpunk_Life_Sim/` – de **Java Spring Boot API** (alle game-logica)
- `client/` – de **React frontend** (nog leeg, alleen mappenstructuur)
- `docs/UITLEG.md` – uitleg per onderdeel: hoe React, de API en MySQL samenwerken

## API starten

Je hebt nodig: **Java 21 (JDK)** en **MySQL** (bijvoorbeeld via XAMPP). Maven hoeft niet: het project heeft een Maven Wrapper.

1. Start **MySQL** in het XAMPP Control Panel. De standaard XAMPP-instellingen (gebruiker `root`, geen wachtwoord)
   staan al in `Cyberpunk_Life_Sim/src/main/resources/application.properties`. De database wordt automatisch aangemaakt.
2. Start de API:

   Windows (Command Prompt of PowerShell):

   ```bat
   cd Cyberpunk_Life_Sim
   .\mvnw.cmd spring-boot:run
   ```

   Mac / Linux:

   ```bash
   cd Cyberpunk_Life_Sim
   ./mvnw spring-boot:run
   ```

3. Open **http://localhost:8080** voor de testpagina: een knop voor ieder endpoint, en je ziet direct de JSON.

Tests draaien (geen MySQL nodig): `.\mvnw.cmd test` (Windows) of `./mvnw test` (Mac/Linux).

> Had je een oudere versie van de API al eens gedraaid? Verwijder dan eenmalig de database `cyberpunk_life_sim`
> in phpMyAdmin (http://localhost/phpmyadmin). De tabellen zijn veranderd; de API maakt ze opnieuw aan.

**React koppelen:** de API accepteert requests van `http://localhost:5173` en `http://localhost:3000`
(zie `config/CorsConfig.java`). Draait je React app op een andere poort, voeg die daar toe.

## Endpoints

Alle endpoints geven JSON terug. Een fout ziet er altijd zo uit: `{ "status": 400, "message": "Niet genoeg geld." }`

| Methode | URL | Wat doet het |
|---|---|---|
| **Characters** | | |
| GET | `/api/characters` | Alle personages (Continue / Character History) |
| GET | `/api/characters/{id}` | Eén personage met alles voor het dashboard |
| POST | `/api/characters` | Nieuw personage (body: zie hieronder) |
| DELETE | `/api/characters/{id}` | Personage en al zijn gegevens verwijderen |
| **Gameplay** | | |
| POST | `/api/characters/{id}/age` | **Next Year**: een jaar ouder, salaris, kosten, nieuwe gebeurtenissen |
| GET | `/api/characters/{id}/stats` | Alleen de statistieken |
| GET | `/api/characters/{id}/life-history` | Levensgeschiedenis (chronologisch) |
| GET | `/api/characters/{id}/transactions` | Alle financiële transacties |
| GET | `/api/characters/{id}/summary` | Eindoverzicht (Death Screen) |
| **Events** | | |
| GET | `/api/characters/{id}/events` | Openstaande gebeurtenissen met keuzes |
| POST | `/api/characters/{id}/events/{eventId}/choices` | Keuze maken, body: `{ "choiceId": 3 }` |
| **Careers** | | |
| GET | `/api/careers` | Alle carrières met eisen |
| GET | `/api/characters/{id}/career` | Huidige baan |
| POST | `/api/characters/{id}/career/apply/{careerId}` | Solliciteren (kost 1 actie) |
| POST | `/api/characters/{id}/career/quit` | Ontslag nemen |
| **Education** | | |
| GET | `/api/education` | Alle opleidingen |
| POST | `/api/characters/{id}/education/{educationId}` | Opleiding starten |
| **Shop & Inventory** | | |
| GET | `/api/items` | Alle items |
| GET | `/api/characters/{id}/inventory` | Gekochte items en cyberware |
| POST | `/api/characters/{id}/items/{itemId}/buy` | Item kopen |
| **Relationships** | | |
| GET | `/api/characters/{id}/relationships` | Vrienden, partner, familie en vijanden |
| POST | `/api/characters/{id}/relationships/{relationshipId}/spend-time` | Tijd doorbrengen (kost 1 actie) |
| **Crime** | | |
| GET | `/api/crimes` | Alle misdaden |
| POST | `/api/characters/{id}/crimes/{crimeId}` | Misdaad plegen (kost 1 actie) |

**Nieuw personage** (POST `/api/characters`):

```json
{
  "firstName": "Kai",
  "lastName": "Nakamura",
  "gender": "Male",
  "birthDate": "2059-04-12",
  "birthPlace": "Neon City",
  "nationality": "Dutch"
}
```

Het geboortejaar wordt altijd 2059, want het spel begint in 2077 op je 18e. Dag en maand kies je zelf.

## Spelregels in het kort

- **Start:** 18 jaar, €$500, Health 100, Happiness 75, Intelligence 50, Street Cred 0, Appearance 50, geen strafblad, een moeder en een vader.
- **Next Year:** salaris, studiegeld en €$2.000 huur. Herstel en veroudering. Relaties worden een jaar ouder.
  Daarna 1 of 2 willekeurige gebeurtenissen die passen bij je leeftijd en situatie.
  Je kunt pas verder als je alle gebeurtenissen hebt afgehandeld.
- **Acties:** 3 per jaar (solliciteren, misdaad, tijd doorbrengen). Winkelen en studeren kost geen actie.
- **Geld:** iedere bij- en afschrijving wordt opgeslagen. Je kunt schulden krijgen (studiegeld, huur, boetes), maar in de winkel betaal je alleen met geld dat je hebt.
- **Gevangenis:** je raakt je baan en opleiding kwijt en krijgt alleen gevangenis-gebeurtenissen (vechten, bibliotheek, ontsnappen).
- **Overlijden:** bij 0 Health, of van ouderdom (vanaf 65 steeds grotere kans). Daarna kan het personage niets meer.

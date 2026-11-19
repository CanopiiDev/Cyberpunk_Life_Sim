# Cyberpunk_Life_Sim

Een simpele life simulator in een futuristische cyberpunkstad.

- `client/` – React frontend (nog leeg, alleen mappenstructuur)
- `Cyberpunk_Life_Sim/` – REST API in Java met Spring Boot en MySQL

## API starten

1. Zorg dat MySQL draait op `localhost:3306`.
2. Zet je MySQL gebruikersnaam en wachtwoord in `Cyberpunk_Life_Sim/src/main/resources/application.properties`.
   De database `cyberpunk_life_sim` wordt automatisch aangemaakt.
3. Start de API:

```bash
cd Cyberpunk_Life_Sim
mvn spring-boot:run
```

De API draait dan op http://localhost:8080. Tests draaien (geen MySQL nodig): `mvn test`.

## Endpoints

| Methode | URL | Wat doet het |
|---|---|---|
| GET | `/api/characters` | Alle characters ophalen |
| GET | `/api/characters/{id}` | Eén character ophalen (geld en statistieken) |
| POST | `/api/characters` | Character aanmaken, body: `{ "name": "V" }` |
| DELETE | `/api/characters/{id}` | Character verwijderen |
| GET | `/api/jobs` | Beschikbare jobs ophalen |
| POST | `/api/characters/{id}/jobs/{jobId}` | Een job uitvoeren |
| GET | `/api/items` | Beschikbare items ophalen |
| POST | `/api/characters/{id}/buy/{itemId}` | Een item kopen |

## Spelregels

- Ieder character begint met €$500 en 0 Street Cred.
- Een job geeft geld en Street Cred. Zwaardere jobs vereisen een minimum aan Street Cred.
- Een item kopen kan alleen als je genoeg geld hebt.

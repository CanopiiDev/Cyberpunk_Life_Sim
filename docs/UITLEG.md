# Uitleg: hoe React, de Java API en MySQL samenwerken

## Het grote plaatje

```
 React (browser)            Java Spring Boot API                         MySQL
 ───────────────            ────────────────────────────────────         ─────
 knop "Next Year"  ──HTTP──>  Controller  ──>  Service  ──>  Repository  ──>  tabellen
 scherm verversen  <──JSON──  (URL's)          (spelregels)   (opslaan/ophalen)
```

- **React** toont alleen wat de API teruggeeft en stuurt verzoeken als de speler klikt. React rekent **niets** uit:
  geen geld, geen kansen, geen gevolgen.
- **De API** bepaalt alles: of iets mag, wat de gevolgen zijn, en slaat het op.
- **MySQL** bewaart alles, zodat je later verder kunt spelen.

### De lagen in de Java code

| Map | Wat zit erin | Voorbeeld |
|---|---|---|
| `controller/` | De URL's. Nemen een request aan en geven het door aan een service. Geen logica. | `CharacterController` |
| `service/` | De spelregels. Hier gebeurt het echte werk. | `AgeService`, `EventService` |
| `repository/` | Praten met de database. Spring maakt de SQL automatisch. | `GameCharacterRepository` |
| `model/` | De entities: iedere klasse is een tabel in MySQL. | `GameCharacter` → tabel `game_characters` |
| `dto/` | Wat React stuurt (request) en terugkrijgt (response). | `CreateCharacterRequest`, `CharacterResponse` |
| `data/` | De vaste startdata: carrières, items, misdaden en gebeurtenissen. | `DataLoader`, `EventData` |
| `config/` | Instellingen: CORS (React mag de API aanroepen) en foutmeldingen. | `CorsConfig`, `ErrorHandler` |
| `util/` | Hulpcode: alle willekeur op één plek. | `RandomUtil` |

### Database-tabellen

| Tabel | Wat | Relatie |
|---|---|---|
| `game_characters` | Het personage met alle stats | heeft één `career` en één `education` (ManyToOne) |
| `character_diplomas` | Behaalde diploma's | hoort bij een character |
| `careers` | Alle carrières | |
| `educations` | Alle opleidingen | |
| `items` | Alle winkelitems | |
| `inventory` | Gekochte items | `character_id` + `item_id` |
| `game_events` + `event_choices` | Vooraf bedachte gebeurtenissen en hun keuzes | één event heeft meerdere choices (OneToMany) |
| `character_events` | Welke gebeurtenis een personage kreeg, en of hij al is afgehandeld | `character_id` + `event_id` |
| `relationships` | Vrienden, partner, familie, vijanden | `character_id` |
| `life_history` | De levensgeschiedenis | `character_id` |
| `transactions` | Iedere bij- en afschrijving | `character_id` |
| `crimes` | Alle misdaden | |

De tabellen worden automatisch gemaakt door Hibernate (`spring.jpa.hibernate.ddl-auto=update`).
De startdata wordt geladen als de tabel nog leeg is.

### Een request vanuit React

```js
const API = "http://localhost:8080/api";

// Gegevens ophalen
const character = await fetch(`${API}/characters/1`).then((r) => r.json());

// Iets doen (POST)
const response = await fetch(`${API}/characters/1/age`, { method: "POST" });
const data = await response.json();
if (!response.ok) {
  alert(data.message); // bijvoorbeeld "Maak eerst een keuze bij je openstaande gebeurtenissen."
}
```

Iedere fout ziet er hetzelfde uit: `{ "status": 400, "message": "..." }`. Toon `message` gewoon aan de speler.

| Status | Betekenis |
|---|---|
| 200 OK | Gelukt |
| 201 Created | Personage aangemaakt |
| 204 No Content | Verwijderd |
| 400 Bad Request | Mag niet volgens de spelregels, of ongeldige invoer |
| 404 Not Found | Bestaat niet |

---

## 1. Character Creation (UC01)

**React:** scherm met een formulier → `POST /api/characters` met voornaam, achternaam, geslacht, geboortedatum,
geboorteplaats en nationaliteit. Daarna naar het dashboard met het `id` uit de response.

**API:** `CreateCharacterRequest` controleert de invoer met `@NotBlank`/`@NotNull` (anders 400).
`CharacterService.createCharacter` maakt het personage met de standaardwaarden uit `GameCharacter`
(18 jaar, €$500, Health 100, ...), zet het geboortejaar op 2059, maakt een moeder en vader aan
en schrijft de eerste regel in de levensgeschiedenis.

**MySQL:** een rij in `game_characters`, twee in `relationships` en één in `life_history`.

## 2. Dashboard en statistieken

**React:** `GET /api/characters/{id}` geeft alles voor het dashboard (`CharacterResponse`):
naam, leeftijd, jaar (`currentYear`), locatie, stats, geld, `wealth`, baan, opleiding, strafblad, `actionsLeft`
en `openEvents` (hoeveel gebeurtenissen nog wachten). Alleen de stat-balken? Gebruik `GET /stats`.

**API:** `CharacterResponse.from(...)` zet de entity om naar een DTO. Zo bepalen wij wat React te zien krijgt.
`wealth` wordt berekend uit het geld ("In de schulden", "Blut", "Rondkomen", "Welgesteld", "Rijk").

## 3. Next Year (UC02)

**React:** knop "Next Year" → `POST /api/characters/{id}/age`. De response:

```json
{
  "character": { "...": "het bijgewerkte personage" },
  "yearSummary": ["Je bent 19 geworden. Het is nu 2078.", "Je verdiende €$5000 als Rookie Runner."],
  "events": [ { "id": 12, "title": "Mugged", "description": "...", "category": "Random",
                "age": 19, "choices": [ { "id": 40, "text": "Geld afgeven" }, { "id": 41, "text": "Vechten" } ] } ]
}
```

Toon `yearSummary` als jaaroverzicht en `events` als kaarten met knoppen.

**API:** `AgeService.ageUp` doet in een vaste volgorde:
1. Controleren: leeft het personage, en zijn alle gebeurtenissen afgehandeld? (anders 400)
2. Leeftijd +1, acties weer op 3
3. In de gevangenis: straf aftellen. Anders: salaris (`CareerService`), studie (`EducationService`) en €$2.000 huur
4. Herstel (+5 Health) en veroudering (na je 50e minder Health), stress bij schulden
5. Relaties een jaar ouder (`RelationshipService`)
6. Overlijden? (0 Health, of ouderdom vanaf 65)
7. Nieuwe gebeurtenissen kiezen (`EventService.generateEvents`)

**MySQL:** `game_characters` wordt bijgewerkt, nieuwe regels in `transactions`, `life_history` en `character_events`.

## 4. Events en Choices (UC03)

**React:** toon een gebeurtenis met de keuzes als knoppen. Klik → `POST /api/characters/{id}/events/{eventId}/choices`
met `{ "choiceId": 40 }`. De response is een `ActionResponse`:

```json
{ "success": true, "message": "Je joeg ze weg!", "character": { "...": "bijgewerkt personage" } }
```

**API:** `EventService.makeChoice`
- controleert dat de gebeurtenis bij dit personage hoort, nog niet is afgehandeld (anders 400) en dat de keuze erbij hoort
- gooit "de dobbelsteen" als de keuze een kans heeft (bijvoorbeeld 30% dat je gearresteerd wordt)
- past de gevolgen toe: geld (via `FinanceService`), stats, en speciale acties zoals een nieuwe vriend,
  trouwen, een baan krijgen of de gevangenis in
- zet `resolved = true`, zodat je dezelfde gebeurtenis niet twee keer kunt afhandelen

De gevolgen staan **niet** in de JSON naar React, dus spelers kunnen niet "spieken".

**Gebeurtenissen kiezen:** in `EventService.isPossible` staat welke gebeurtenissen passen bij het personage:
leeftijd (`minAge`/`maxAge`), voorwaarde (`NO_JOB`, `HAS_PARTNER`, `HAS_CYBERWARE`, `IN_PRISON`, ...) en
eenmalige gebeurtenissen (`.once()`, zoals een erfenis).

**Zelf gebeurtenissen toevoegen:** in `data/EventData.java`. Kopieer een bestaande en pas hem aan.
Verwijder daarna de tabellen `event_choices`, `character_events` en `game_events` in phpMyAdmin.
De API laadt de gebeurtenissen dan opnieuw.

**MySQL:** `game_events` en `event_choices` (de templates), `character_events` (wie kreeg wat, en is het afgehandeld).

## 5. Career (UC04)

**React:** scherm Career → `GET /api/careers` voor de lijst met eisen, `GET /api/characters/{id}/career` voor de
huidige baan. Knop "Solliciteren" → `POST /career/apply/{careerId}`. Knop "Ontslag nemen" → `POST /career/quit`.

**API:** `CareerService`
- **Solliciteren:** eerst de eisen (Intelligence, Street Cred, diploma, geen strafblad). Haal je die niet: 400 met de reden.
  Haal je die wel: een kans op aangenomen worden (`success: true`) of afgewezen (`success: false`). Kost 1 actie.
- **Ieder jaar:** salaris, na 2 jaar kans op promotie (+25% salaris en een hogere functie), een kleine kans op ontslag.
  Bij illegale banen (Street Dealer, Fixer) ook kans op arrestatie.

**MySQL:** `game_characters.career_id`, `career_level`, `salary`. De carrières staan in `careers`.

## 6. Education

**React:** `GET /api/education` → knop "Starten" → `POST /api/characters/{id}/education/{educationId}`.

**API:** `EducationService`. Bij Next Year betaal je studiegeld (geen geld = studieschuld), krijg je Intelligence
erbij en gaat er een jaar af. Na het laatste jaar krijg je het diploma. Een diploma geeft toegang tot banen,
bijvoorbeeld Computer Science → Netrunner.

**MySQL:** `game_characters.education_id`, `education_years_left`, en de tabel `character_diplomas`.

## 7. Financial System

**React:** geld staat in het personage (`money`, `wealth`). Een overzicht van alle transacties: `GET /transactions`.

**API:** **alle** geldwijzigingen lopen via `FinanceService.change(...)`. Die past het saldo aan en slaat
een `Transaction` op met het bedrag, de omschrijving en het saldo erna. In de winkel gebruiken we `pay(...)`:
die geeft een 400 als je niet genoeg geld hebt. React kan het saldo nergens zelf aanpassen.

**MySQL:** `transactions`.

## 8. Shop, Inventory en Cyberware (UC05)

**React:** `GET /api/items` (met `category`: Clothing, Cyberware of Other) → knop "Buy" →
`POST /api/characters/{id}/items/{itemId}/buy`. Inventory: `GET /inventory`.

**API:** `ShopService.buyItem` controleert het saldo, schrijft het bedrag af, zet het item in de inventory en past
de bonussen toe. Cyberware kan maar één keer geïnstalleerd worden en heeft soms bijwerkingen (negatieve bonus,
uitgelegd in `sideEffect`). De Reflex Booster verlaagt je kans om gepakt te worden bij misdaden.

**MySQL:** `inventory` koppelt een character aan een item.

## 9. Relationships (UC06)

**React:** `GET /relationships` → lijst met `name`, `age`, `type` (Friend, Partner, Family, Enemy), `label`
(Moeder, Vriend, Echtgeno(o)t(e), ...) en `relationshipLevel` (0-100). Knop "Tijd doorbrengen" → `POST .../spend-time`.

**API:** `RelationshipService`. Nieuwe relaties ontstaan vooral door keuzes bij gebeurtenissen: een vriend maken,
flirten (partner), trouwen, een kind krijgen, uit elkaar gaan of een vijand maken. Ieder jaar worden relaties
een beetje slechter als je niets doet, en ouders kunnen overlijden.

**MySQL:** `relationships`.

## 10. Criminal System

**React:** `GET /api/crimes` → knop → `POST /api/characters/{id}/crimes/{crimeId}`.

**API:** `CrimeService`. Je kans om gepakt te worden is de `arrestChance` van de misdaad, min je Street Cred / 10,
min de bonus van je cyberware. Gepakt: een boete en een strafblad, en soms gevangenisstraf. Niet gepakt: geld en
Street Cred. Kost 1 actie. In de gevangenis (`inPrison: true`) kun je niet winkelen, solliciteren of misdaden plegen,
en krijg je alleen gevangenis-gebeurtenissen.

**MySQL:** `crimes`, en in `game_characters`: `convictions` en `prison_years_left`.

## 11. Death en Life History (UC07)

**React:**
- **Life History:** `GET /life-history` geeft alle regels met `age`, `gameYear` en `text`. Toon ze als tijdlijn.
- **Death Screen:** als `alive` false is, toon `GET /summary`: volledige naam, geboorte- en sterfdatum, leeftijd,
  doodsoorzaak, hoogste functie, totaal verdiend, strafblad, relaties en de belangrijkste gebeurtenissen.
  Daarna terug naar het Main Menu voor een nieuw personage.

**API:** `CharacterService.die` zet `alive` op false, slaat de doodsoorzaak op, schrijft de laatste regel in de
geschiedenis en sluit openstaande gebeurtenissen af. Iedere actie op een overleden personage geeft daarna een 400.
Belangrijke momenten worden opgeslagen met `important = true`: die komen in het eindoverzicht.

**MySQL:** `life_history`.

---

## Welke endpoints heeft ieder React-scherm nodig?

| Scherm | Endpoints |
|---|---|
| Main Menu | `GET /api/characters` (Continue en Character History) |
| Character Creation | `POST /api/characters` |
| Dashboard | `GET /api/characters/{id}`, `POST /api/characters/{id}/age` |
| Events | `GET /api/characters/{id}/events`, `POST .../events/{eventId}/choices` |
| Career | `GET /api/careers`, `GET .../career`, `POST .../career/apply/{careerId}`, `POST .../career/quit`, `GET /api/education`, `POST .../education/{educationId}` |
| Relationships | `GET .../relationships`, `POST .../relationships/{relationshipId}/spend-time` |
| Inventory | `GET .../inventory` |
| Shop | `GET /api/items`, `POST .../items/{itemId}/buy` |
| Crime | `GET /api/crimes`, `POST .../crimes/{crimeId}` |
| Life History | `GET .../life-history`, `GET .../transactions` |
| Death Screen | `GET .../summary` |

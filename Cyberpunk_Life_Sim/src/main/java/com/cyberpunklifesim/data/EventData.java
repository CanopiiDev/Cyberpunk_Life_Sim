package com.cyberpunklifesim.data;

import com.cyberpunklifesim.model.EventChoice;
import com.cyberpunklifesim.model.GameEvent;
import com.cyberpunklifesim.repository.GameEventRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import static com.cyberpunklifesim.model.EventChoice.BREAK_UP;
import static com.cyberpunklifesim.model.EventChoice.ESCAPE_PRISON;
import static com.cyberpunklifesim.model.EventChoice.GET_FIRED;
import static com.cyberpunklifesim.model.EventChoice.HAVE_CHILD;
import static com.cyberpunklifesim.model.EventChoice.JOB_OFFER;
import static com.cyberpunklifesim.model.EventChoice.MAKE_ENEMY;
import static com.cyberpunklifesim.model.EventChoice.MARRY;
import static com.cyberpunklifesim.model.EventChoice.NEW_FRIEND;
import static com.cyberpunklifesim.model.EventChoice.NEW_PARTNER;
import static com.cyberpunklifesim.model.GameEvent.ANYONE;
import static com.cyberpunklifesim.model.GameEvent.HAS_CYBERWARE;
import static com.cyberpunklifesim.model.GameEvent.HAS_JOB;
import static com.cyberpunklifesim.model.GameEvent.HAS_PARTNER;
import static com.cyberpunklifesim.model.GameEvent.IN_PRISON;
import static com.cyberpunklifesim.model.GameEvent.MARRIED;
import static com.cyberpunklifesim.model.GameEvent.NO_JOB;
import static com.cyberpunklifesim.model.GameEvent.NO_PARTNER;
import static com.cyberpunklifesim.model.GameEvent.STUDYING;

/**
 * Alle vooraf bedachte gebeurtenissen.
 *
 * Zo lees je een gebeurtenis:
 *   new GameEvent(titel, beschrijving, categorie, min. leeftijd, max. leeftijd, voorwaarde)
 *       .choice(new EventChoice(knoptekst, resultaat).money(...).health(...)
 *               .chance(30, "tekst als het misgaat").chancePrison(1))
 *
 * Bij .chance(30, ...) gebeurt er in 30% van de gevallen het kans-gevolg IN PLAATS VAN het normale gevolg.
 * Meer gebeurtenissen? Kopieer er een, pas hem aan, en verwijder de tabel game_events zodat hij opnieuw geladen wordt.
 */
@Component
public class EventData implements CommandLineRunner {

    private final GameEventRepository eventRepository;

    public EventData(GameEventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public void run(String... args) {
        if (eventRepository.count() > 0) {
            return;
        }

        // ================= CRIMINAL =================

        add(new GameEvent("An Offer from the Underground",
                "Een onbekende persoon in een achterafstraatje biedt je €$2.000 om een onbekend pakket af te leveren.",
                "Criminal", 18, 70, ANYONE)
                .choice(new EventChoice("Accepteren", "Je leverde het pakket af en kreeg €$2.000.")
                        .money(2000).streetCred(5)
                        .chance(30, "Het pakket zat vol illegale chips. De politie wachtte je op.").chancePrison(1))
                .choice(new EventChoice("Weigeren", "Je liep door. Er gebeurde niets."))
                .choice(new EventChoice("De politie informeren", "De politie bedankte je voor de tip.")
                        .happiness(5).streetCred(-5)
                        .chance(40, "De koerier kwam erachter dat jij hem had verraden.").chanceStreetCred(-10).chanceAction(MAKE_ENEMY)));

        add(new GameEvent("Gang Recruitment",
                "De Neon Serpents, een beruchte straatbende, willen dat je lid wordt.",
                "Criminal", 18, 40, ANYONE)
                .choice(new EventChoice("Lid worden", "Je bent nu een Neon Serpent. Mensen kijken anders naar je.")
                        .streetCred(15).happiness(5)
                        .chance(25, "De inwijding ging mis. Je raakte zwaargewond.").chanceHealth(-30).chanceStreetCred(10))
                .choice(new EventChoice("Beleefd weigeren", "Ze lieten je met rust. Voor nu.").streetCred(-3))
                .choice(new EventChoice("Uitlachen", "Ze vonden het niet grappig.").action(MAKE_ENEMY).health(-10)));

        // ================= CAREER =================

        add(new GameEvent("Job Opportunity",
                "Je krijgt een bericht van een bedrijf dat cybernetische implantaten maakt. Ze zoeken een nieuwe technicus.",
                "Career", 18, 60, NO_JOB)
                .choice(new EventChoice("Solliciteren", "Je stuurde je cv op.").action(JOB_OFFER + "Technician"))
                .choice(new EventChoice("Het aanbod negeren", "Je verwijderde het bericht.")));

        add(new GameEvent("Delivery Gig",
                "Een bezorgdienst zoekt dringend runners. Ze nemen iedereen aan.",
                "Career", 18, 50, NO_JOB)
                .choice(new EventChoice("Aanmelden", "Je meldde je aan.").action(JOB_OFFER + "Delivery Runner"))
                .choice(new EventChoice("Nee bedankt", "Bezorgen is niks voor jou.")));

        add(new GameEvent("Overtime",
                "Je baas vraagt of je dit weekend wilt overwerken.",
                "Career", 18, 70, HAS_JOB)
                .choice(new EventChoice("Overwerken", "Je werkte het hele weekend en kreeg een bonus van €$600.")
                        .money(600).health(-5).happiness(-5))
                .choice(new EventChoice("Weigeren", "Je genoot van je vrije weekend.").happiness(5)
                        .chance(10, "Je baas was woedend.").chanceAction(GET_FIRED)));

        add(new GameEvent("Office Party",
                "Je collega's geven een feest in een rooftop-bar.",
                "Career", 18, 65, HAS_JOB)
                .choice(new EventChoice("Gaan", "Je had een geweldige avond.").happiness(10).money(-100).action(NEW_FRIEND))
                .choice(new EventChoice("Thuisblijven", "Je keek een holo-film op de bank.").happiness(2)));

        // ================= HEALTH & CYBERWARE =================

        add(new GameEvent("Cyberware Malfunction",
                "Je cybernetische implantaat begint plotseling te haperen.",
                "Cyberware", 18, 100, HAS_CYBERWARE)
                .choice(new EventChoice("Dure reparatie (€$1.500)", "Een gecertificeerde ripperdoc repareerde alles perfect.")
                        .money(-1500).health(5))
                .choice(new EventChoice("Goedkope illegale reparatie (€$300)", "Het werkt weer. Voor nu.")
                        .money(-300)
                        .chance(40, "De illegale ripperdoc verknoeide het. Je hebt veel pijn.").chanceMoney(-300).chanceHealth(-30))
                .choice(new EventChoice("Negeren", "Het haperen gaat niet weg.").health(-15).happiness(-5)));

        add(new GameEvent("Black Market Implant",
                "Een louche verkoper biedt je een gebruikte neurale chip aan voor €$800.",
                "Cyberware", 18, 60, ANYONE)
                .choice(new EventChoice("Kopen", "Je voelt je slimmer dan ooit.")
                        .money(-800).intelligence(10)
                        .chance(30, "De chip was defect en gaf je een elektrische schok.").chanceMoney(-800).chanceHealth(-20).chanceHappiness(-10))
                .choice(new EventChoice("Weigeren", "Je vertrouwde het niet. Waarschijnlijk verstandig.")));

        add(new GameEvent("Acid Rain Flu",
                "Na een week zure regen voel je je ziek.",
                "Health", 18, 100, ANYONE)
                .choice(new EventChoice("Naar de dokter (€$200)", "De dokter gaf je medicijnen. Je bent snel beter.").money(-200))
                .choice(new EventChoice("Uitzieken", "Je lag twee weken op bed.").health(-10).happiness(-5)));

        add(new GameEvent("Gym Membership",
                "Er opent een nieuwe sportschool met hologram-trainers in je buurt.",
                "Health", 18, 80, ANYONE)
                .choice(new EventChoice("Lid worden (€$300)", "Je traint drie keer per week. Je voelt je sterk.")
                        .money(-300).health(10).appearance(5).happiness(3))
                .choice(new EventChoice("Nee, de bank is fijner", "Je bleef lekker thuis.")));

        add(new GameEvent("Hovercar Accident",
                "Een hovercar raakt je op het zebrapad.",
                "Health", 18, 100, ANYONE)
                .choice(new EventChoice("Naar het ziekenhuis (€$500)", "Je werd goed verzorgd.").health(-5).money(-500))
                .choice(new EventChoice("Doorlopen", "Je beet op je tanden. Dat was niet slim.").health(-15)));

        // ================= RELATIONSHIP =================

        add(new GameEvent("A Stranger at the Bar",
                "In een neonbar maakt een aantrekkelijk persoon oogcontact met je.",
                "Relationship", 18, 70, NO_PARTNER)
                .choice(new EventChoice("Flirten", "Het klikte meteen!").happiness(10).action(NEW_PARTNER)
                        .chance(30, "Je werd uitgelachen. Au.").chanceHappiness(-10))
                .choice(new EventChoice("Vriendschappelijk praten", "Jullie praatten de hele avond.").happiness(5).action(NEW_FRIEND))
                .choice(new EventChoice("Negeren", "Je dronk je drankje op en ging naar huis.")));

        add(new GameEvent("The Big Question",
                "Je denkt erover om je partner ten huwelijk te vragen.",
                "Relationship", 21, 70, HAS_PARTNER)
                .choice(new EventChoice("Ten huwelijk vragen (€$1.000 ring)", "Ja! Jullie zijn verloofd en trouwen meteen.")
                        .money(-1000).happiness(20).action(MARRY)
                        .chance(25, "Je partner zei nee... en maakte het uit.").chanceMoney(-1000).chanceHappiness(-20).chanceAction(BREAK_UP))
                .choice(new EventChoice("Nog even wachten", "Er is geen haast.")));

        add(new GameEvent("Relationship Trouble",
                "Je partner en jij hebben steeds vaker ruzie.",
                "Relationship", 18, 80, HAS_PARTNER)
                .choice(new EventChoice("Uitpraten", "Jullie hebben het goedgemaakt.").happiness(5))
                .choice(new EventChoice("Uit elkaar gaan", "Het was beter zo.").happiness(-10).action(BREAK_UP)));

        add(new GameEvent("Baby Plans",
                "Je partner en jij praten over kinderen.",
                "Relationship", 21, 50, MARRIED)
                .choice(new EventChoice("Ja, een kind!", "Jullie gezin wordt groter.").happiness(15).money(-1500).action(HAVE_CHILD))
                .choice(new EventChoice("Nog niet", "Jullie wachten nog even.")));

        // ================= FINANCIAL =================

        add(new GameEvent("Lottery Ticket",
                "Een kiosk verkoopt loten voor de Neon Lottery. Een lot kost €$50.",
                "Financial", 18, 100, ANYONE)
                .choice(new EventChoice("Lot kopen", "Helaas, geen prijs.").money(-50)
                        .chance(8, "JACKPOT! Je wint €$10.000!").chanceMoney(9950).chanceHappiness(20))
                .choice(new EventChoice("Niet kopen", "Je houdt je geld liever zelf.")));

        add(new GameEvent("Unexpected Bill",
                "Je krijgt een rekening van €$400 voor 'netwerkonderhoud'.",
                "Financial", 18, 100, ANYONE)
                .choice(new EventChoice("Betalen", "Je betaalde de rekening.").money(-400))
                .choice(new EventChoice("Negeren", "Je hoorde er niets meer van.")
                        .chance(50, "Een incassobureau stond voor de deur. Het werd €$1.000.").chanceMoney(-1000).chanceHappiness(-10)));

        add(new GameEvent("Inheritance",
                "Een verre oom die je nooit hebt ontmoet is overleden. Hij heeft je geld nagelaten.",
                "Financial", 25, 100, ANYONE).once()
                .choice(new EventChoice("Erfenis accepteren", "Je ontving €$3.000.").money(3000)));

        // ================= RANDOM / PERSONAL =================

        add(new GameEvent("Mugged",
                "Twee overvallers met messen houden je tegen in een steeg.",
                "Random", 18, 100, ANYONE)
                .choice(new EventChoice("Geld afgeven", "Ze namen €$200 mee.").money(-200).happiness(-5))
                .choice(new EventChoice("Vechten", "Je joeg ze weg!").streetCred(5)
                        .chance(50, "Je verloor het gevecht.").chanceHealth(-20).chanceMoney(-200)));

        add(new GameEvent("Interesting Stranger",
                "Op een holo-concert raak je aan de praat met iemand die dezelfde muziek leuk vindt.",
                "Personal", 18, 80, ANYONE)
                .choice(new EventChoice("Nummers uitwisselen", "Je hebt er een vriend bij.").happiness(5).action(NEW_FRIEND))
                .choice(new EventChoice("Gewoon genieten van het concert", "Wat een show!").happiness(3)));

        add(new GameEvent("Neon Festival",
                "Het jaarlijkse Neon Festival is in de stad.",
                "Personal", 18, 70, ANYONE)
                .choice(new EventChoice("Gaan (€$150)", "Je danste tot de zon opkwam.").money(-150).happiness(12).health(-3))
                .choice(new EventChoice("Thuisblijven", "Je hoorde de muziek door het raam.")));

        add(new GameEvent("Midlife Crisis",
                "Je kijkt in de spiegel en vraagt je af wat je met je leven hebt gedaan.",
                "Personal", 40, 55, ANYONE).once()
                .choice(new EventChoice("Een hover bike kopen (€$3.000)", "Je voelt je weer twintig!").money(-3000).happiness(15))
                .choice(new EventChoice("Het accepteren", "Je bent wie je bent.").happiness(-5)));

        // ================= EDUCATION =================

        add(new GameEvent("Exam Week",
                "Je hebt deze week belangrijke examens.",
                "Personal", 18, 60, STUDYING)
                .choice(new EventChoice("Hard studeren", "Je haalde hoge cijfers.").intelligence(5).happiness(-5))
                .choice(new EventChoice("Feesten", "Je had een topweek!").happiness(10)
                        .chance(30, "Je zakte voor een vak en moest herkansen.").chanceHappiness(-10)));

        // ================= PRISON =================

        add(new GameEvent("Prison Fight",
                "Een medegevangene daagt je uit op de binnenplaats.",
                "Criminal", 18, 100, IN_PRISON)
                .choice(new EventChoice("Vechten", "Je won. Niemand valt je meer lastig.").streetCred(10).health(-10)
                        .chance(30, "Je verloor en lag weken in de ziekenboeg.").chanceHealth(-35))
                .choice(new EventChoice("Wegkijken", "Ze noemen je nu een lafaard.").happiness(-5).streetCred(-5)));

        add(new GameEvent("Prison Library",
                "De gevangenis heeft een kleine bibliotheek met oude datachips.",
                "Personal", 18, 100, IN_PRISON)
                .choice(new EventChoice("Studeren", "Je leerde veel nieuwe dingen.").intelligence(5))
                .choice(new EventChoice("Slapen", "Je rustte goed uit.").health(5)));

        add(new GameEvent("Escape Plan",
                "Je celgenoot heeft een plan om te ontsnappen via de ventilatieschacht.",
                "Criminal", 18, 100, IN_PRISON)
                .choice(new EventChoice("Meedoen", "Het plan werkte!").streetCred(20).action(ESCAPE_PRISON)
                        .chance(70, "Jullie werden betrapt. Je krijgt extra straf.").chancePrison(2))
                .choice(new EventChoice("Niet meedoen", "Je celgenoot ging alleen. Je hebt hem nooit meer gezien.")));
    }

    private void add(GameEvent event) {
        eventRepository.save(event);
    }
}

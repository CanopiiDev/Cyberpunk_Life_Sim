import { useEffect, useState } from "react";
import * as api from "../services/api.js";
import Header from "../components/layout/Header.jsx";
import StatBar from "../components/game/StatBar.jsx";
import LifeLog from "../components/game/LifeLog.jsx";
import JobList from "../components/game/JobList.jsx";
import ShopList from "../components/game/ShopList.jsx";
import Inventory from "../components/game/Inventory.jsx";

// Het spelscherm van één character: stats, levensverhaal, jobs, winkel en "jaar ouder worden".
export default function CharacterPage({ characterId, onBack }) {
  const [character, setCharacter] = useState(null);
  const [events, setEvents] = useState([]);
  const [jobs, setJobs] = useState([]);
  const [items, setItems] = useState([]);
  const [tab, setTab] = useState("jobs");
  const [error, setError] = useState("");

  // Bij het openen: haal character, levensverhaal, jobs en items op
  useEffect(() => {
    refresh();
    api.getJobs().then(setJobs);
    api.getItems().then(setItems);
  }, [characterId]);

  async function refresh() {
    setCharacter(await api.getCharacter(characterId));
    setEvents(await api.getEvents(characterId));
  }

  // Voert een actie uit (bijv. job doen) en ververst daarna het scherm.
  // Een foutmelding van de API (bijv. "Niet genoeg geld") laten we bovenin zien.
  async function run(action) {
    try {
      await action();
      setError("");
      await refresh();
    } catch (e) {
      setError(e.message);
    }
  }

  if (!character) {
    return <p className="page muted">Laden...</p>;
  }

  return (
    <div className="page game">
      <Header character={character} onBack={onBack} />

      <div className="card stats">
        <StatBar label="Health" value={character.health} color="var(--green)" />
        <StatBar label="Happiness" value={character.happiness} color="var(--yellow)" />
        <StatBar label="Smarts" value={character.smarts} color="var(--cyan)" />
        <div className="stat-row">
          <span>Street Cred</span>
          <strong>{character.streetCred}</strong>
        </div>
        <div className="stat-row">
          <span>Jobs voltooid</span>
          <strong>{character.jobsCompleted}</strong>
        </div>
      </div>

      {error && <p className="error">{error}</p>}

      <LifeLog events={events} />

      {character.alive ? (
        <>
          <button className="primary age-up" onClick={() => run(() => api.ageUp(characterId))}>
            + 1 JAAR
          </button>

          <div className="tabs">
            <button className={tab === "jobs" ? "active" : ""} onClick={() => setTab("jobs")}>
              Jobs
            </button>
            <button className={tab === "shop" ? "active" : ""} onClick={() => setTab("shop")}>
              Winkel
            </button>
            <button className={tab === "inventory" ? "active" : ""} onClick={() => setTab("inventory")}>
              Bezit ({character.inventory.length})
            </button>
          </div>

          {tab === "jobs" && <JobList jobs={jobs} onDoJob={(jobId) => run(() => api.doJob(characterId, jobId))} />}
          {tab === "shop" && <ShopList items={items} onBuy={(itemId) => run(() => api.buyItem(characterId, itemId))} />}
          {tab === "inventory" && <Inventory items={character.inventory} />}
        </>
      ) : (
        <div className="card rip">
          <h2>R.I.P. {character.name}</h2>
          <p>{character.causeOfDeath}</p>
          <p className="muted">
            Geworden: {character.age} jaar · Street Cred: {character.streetCred}
          </p>
          <button className="primary" onClick={onBack}>
            Nieuw leven
          </button>
        </div>
      )}
    </div>
  );
}

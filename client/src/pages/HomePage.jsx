import { useEffect, useState } from "react";
import { createCharacter, deleteCharacter, getCharacters } from "../services/api.js";
import { formatMoney } from "../utils/format.js";

// Startscherm: maak een nieuw character of ga verder met een bestaand leven.
export default function HomePage({ onSelect }) {
  const [characters, setCharacters] = useState([]);
  const [name, setName] = useState("");
  const [error, setError] = useState("");

  // Haal de characters op zodra de pagina laadt
  useEffect(() => {
    loadCharacters();
  }, []);

  async function loadCharacters() {
    try {
      setCharacters(await getCharacters());
      setError("");
    } catch (e) {
      setError("Kan de API niet bereiken. Draait de Java API op poort 8080?");
    }
  }

  async function handleCreate(event) {
    event.preventDefault(); // voorkom dat de pagina herlaadt
    try {
      const character = await createCharacter(name);
      setName("");
      onSelect(character.id); // meteen beginnen met spelen
    } catch (e) {
      setError(e.message);
    }
  }

  async function handleDelete(id) {
    try {
      await deleteCharacter(id);
      loadCharacters();
    } catch (e) {
      setError(e.message);
    }
  }

  return (
    <div className="page">
      <h1 className="logo">
        CYBERPUNK<span>LIFE SIM</span>
      </h1>

      <form className="card" onSubmit={handleCreate}>
        <h2>Nieuw leven</h2>
        <p className="muted">Je komt aan in Neon City met €$500 op zak.</p>
        <div className="row">
          <input value={name} onChange={(e) => setName(e.target.value)} placeholder="Naam van je character" />
          <button className="primary" type="submit">
            Start
          </button>
        </div>
      </form>

      {error && <p className="error">{error}</p>}

      <div className="card">
        <h2>Verder spelen</h2>
        {characters.length === 0 && <p className="muted">Nog geen characters.</p>}
        {characters.map((c) => (
          <div key={c.id} className="list-item">
            <button className="link" onClick={() => onSelect(c.id)}>
              <strong>{c.name}</strong>
              <span className="muted">
                {c.alive ? `${c.age} jaar` : `† ${c.age} jaar`} · {formatMoney(c.money)}
              </span>
            </button>
            <button className="danger small" onClick={() => handleDelete(c.id)}>
              Verwijder
            </button>
          </div>
        ))}
      </div>
    </div>
  );
}

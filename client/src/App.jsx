import { useState } from "react";
import HomePage from "./pages/HomePage.jsx";
import CharacterPage from "./pages/CharacterPage.jsx";

// App bepaalt welke pagina je ziet:
// - geen character gekozen  -> HomePage (kies of maak een character)
// - wel een character       -> CharacterPage (speel het leven van dat character)
export default function App() {
  const [characterId, setCharacterId] = useState(null);

  return (
    <div className="app">
      {characterId === null ? (
        <HomePage onSelect={setCharacterId} />
      ) : (
        <CharacterPage characterId={characterId} onBack={() => setCharacterId(null)} />
      )}
    </div>
  );
}

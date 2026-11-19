import { formatMoney } from "../../utils/format.js";

// Bovenkant van het spelscherm: terugknop, naam, leeftijd en geld.
export default function Header({ character, onBack }) {
  return (
    <header className="header">
      <button className="link back" onClick={onBack}>
        ←
      </button>
      <div>
        <h2>{character.name}</h2>
        <span className="muted">
          {character.alive ? `${character.age} jaar` : `† overleden op ${character.age}`}
        </span>
      </div>
      <div className="money">{formatMoney(character.money)}</div>
    </header>
  );
}

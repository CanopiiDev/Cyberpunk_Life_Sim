// Alles wat je character heeft gekocht.
export default function Inventory({ items }) {
  return (
    <div className="card">
      {items.length === 0 && <p className="muted">Je bezit nog niets.</p>}
      {items.map((item, index) => (
        <div key={index} className="list-item">
          <strong>{item.name}</strong>
          <span className="muted">{item.description}</span>
        </div>
      ))}
    </div>
  );
}

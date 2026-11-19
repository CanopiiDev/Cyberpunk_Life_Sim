// Een balk die een stat (0 - 100) laat zien, bijvoorbeeld Health.
export default function StatBar({ label, value, color }) {
  return (
    <div className="stat-bar">
      <div className="stat-row">
        <span>{label}</span>
        <strong>{value}%</strong>
      </div>
      <div className="bar">
        <div className="bar-fill" style={{ width: `${value}%`, background: color }} />
      </div>
    </div>
  );
}

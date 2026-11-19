import { formatMoney } from "../../utils/format.js";

// De winkel. Items kosten geld maar verbeteren je stats.
export default function ShopList({ items, onBuy }) {
  return (
    <div className="card">
      {items.map((item) => (
        <div key={item.id} className="list-item">
          <div>
            <strong>{item.name}</strong>
            <p className="muted">{item.description}</p>
            <p className="small-text">
              {item.healthBoost > 0 && `+${item.healthBoost} health `}
              {item.happinessBoost > 0 && `+${item.happinessBoost} happiness `}
              {item.smartsBoost > 0 && `+${item.smartsBoost} smarts`}
            </p>
          </div>
          <button className="small" onClick={() => onBuy(item.id)}>
            {formatMoney(item.price)}
          </button>
        </div>
      ))}
    </div>
  );
}

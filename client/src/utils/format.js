// Laat een bedrag zien als eurodollars, bijvoorbeeld 1500 -> "€$1.500"
export function formatMoney(amount) {
  return "€$" + amount.toLocaleString("nl-NL");
}

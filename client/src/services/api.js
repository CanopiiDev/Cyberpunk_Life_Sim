// Alle verzoeken naar de Java API staan in dit ene bestand.
// Draait de API ergens anders? Pas dan alleen deze regel aan.
const API_URL = "http://localhost:8080/api";

// Hulpfunctie: doet een verzoek en geeft de JSON terug.
// Gaat er iets mis (bijv. "Niet genoeg geld"), dan gooit hij een Error met de melding van de API.
async function request(path, options = {}) {
  const response = await fetch(API_URL + path, options);

  if (!response.ok) {
    const error = await response.json().catch(() => ({}));
    throw new Error(error.message || "Er ging iets mis met de API");
  }

  // 204 No Content (bijv. na verwijderen) heeft geen JSON
  if (response.status === 204) {
    return null;
  }
  return response.json();
}

export function getCharacters() {
  return request("/characters");
}

export function getCharacter(id) {
  return request(`/characters/${id}`);
}

export function getEvents(id) {
  return request(`/characters/${id}/events`);
}

export function createCharacter(name) {
  return request("/characters", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ name }),
  });
}

export function deleteCharacter(id) {
  return request(`/characters/${id}`, { method: "DELETE" });
}

export function ageUp(id) {
  return request(`/characters/${id}/age-up`, { method: "POST" });
}

export function getJobs() {
  return request("/jobs");
}

export function doJob(id, jobId) {
  return request(`/characters/${id}/jobs/${jobId}`, { method: "POST" });
}

export function getItems() {
  return request("/items");
}

export function buyItem(id, itemId) {
  return request(`/characters/${id}/buy/${itemId}`, { method: "POST" });
}

import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

// Vite is de tool die de React app start en bouwt.
// De app draait op http://localhost:5173 (die poort staat ook in de CORS-instelling van de API).
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
  },
});

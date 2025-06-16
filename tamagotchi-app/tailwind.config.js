// Contenido para: tailwind.config.js
// Crea un fichero con este nombre en la raíz de tu proyecto React.

/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}", // Esto le dice a Tailwind que busque clases en todos estos ficheros
  ],
  theme: {
    extend: {},
  },
  plugins: [],
}
```javascript
// Contenido para: postcss.config.js
// Crea un fichero con este nombre en la raíz de tu proyecto React.

export default {
  plugins: {
    tailwindcss: {},
    autoprefixer: {},
  },
}
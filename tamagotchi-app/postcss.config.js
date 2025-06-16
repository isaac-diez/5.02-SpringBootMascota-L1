// postcss.config.js (versión corregida)
export default {
  plugins: {
    '@tailwindcss/postcss': {}, // <<< Se usa el nuevo paquete como plugin
    autoprefixer: {},
  },
}
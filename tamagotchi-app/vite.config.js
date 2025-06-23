import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  // THIS IS THE FIX
  server: {
    fs: {
      // Allow access to the project root directory
      allow: ['.'],
    },
  },
  plugins: [react()],
  define: {
    'global': 'window',
  }
})
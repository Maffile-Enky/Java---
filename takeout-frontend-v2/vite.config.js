import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://47.99.34.251:9999',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')
      },
      '/ws/delivery': {
        target: 'ws://47.99.34.251:9999',
        ws: true
      },
      '/ws/notification': {
        target: 'ws://47.99.34.251:9999',
        ws: true
      }
    }
  }
})

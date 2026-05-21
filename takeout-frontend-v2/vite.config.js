import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'

// 从环境变量读取服务器地址，默认为 localhost
const serverIp = process.env.VITE_DEV_SERVER_IP || 'localhost'

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
        target: `http://${serverIp}:9999`,
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')
      },
      '/uploads': {
        target: `http://${serverIp}:8081`,
        changeOrigin: true
      },
      '/ws/delivery': {
        target: `ws://${serverIp}:9999`,
        ws: true
      },
      '/ws/notification': {
        target: `ws://${serverIp}:9999`,
        ws: true
      }
    }
  }
})

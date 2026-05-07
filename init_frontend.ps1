$root = "e:\.Save-User_Enky\Java_IDEA_Project\MeiTuan\Java---\takeout-frontend"

function Write-File {
    param($Path, $Content)
    $dir = Split-Path $Path
    if (!(Test-Path $dir)) { md $dir -Force | Out-Null }
    Set-Content -Path $Path -Value $Content -Encoding UTF8
}

Write-File "$root\package.json" @"
{
  "name": "takeout-frontend",
  "version": "1.0.0",
  "private": true,
  "type": "module",
  "scripts": {
    "dev": "vite",
    "build": "vite build",
    "preview": "vite preview"
  },
  "dependencies": {
    "axios": "^1.6.8",
    "pinia": "^2.1.7",
    "vue": "^3.4.21",
    "vue-router": "^4.3.0"
  },
  "devDependencies": {
    "@vitejs/plugin-vue": "^5.0.4",
    "vite": "^5.2.8"
  }
}
"@

Write-File "$root\vite.config.js" @"
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
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
"@

Write-File "$root\index.html" @"
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <link rel="icon" type="image/svg+xml" href="/vite.svg" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Takeout Platform</title>
  </head>
  <body>
    <div id="app"></div>
    <script type="module" src="/src/main.js"></script>
  </body>
</html>
"@

Write-File "$root\src\main.js" @"
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'

import './assets/main.css'

const app = createApp(App)

app.use(createPinia())
app.use(router)

app.mount('#app')
"@

Write-File "$root\src\App.vue" @"
<template>
  <router-view />
</template>

<script setup>
</script>

<style>
/* Base Premium Styles */
:root {
  --primary-color: #ff6b6b;
  --secondary-color: #4ecdc4;
  --dark-bg: #1a1a2e;
  --light-bg: #f7f9fc;
  --text-main: #2d3436;
}

body {
  margin: 0;
  font-family: 'Inter', 'Roboto', sans-serif;
  background-color: var(--light-bg);
  color: var(--text-main);
  -webkit-font-smoothing: antialiased;
}

/* Glassmorphism utility */
.glass-panel {
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.05);
}
</style>
"@

Write-File "$root\src\assets\main.css" @"
/* Reset and base styles */
*, *::before, *::after {
  box-sizing: border-box;
}
"@

Write-File "$root\src\api\request.js" @"
import axios from 'axios'

const request = axios.create({
  baseURL: '/api',
  timeout: 5000
})

// Request Interceptor
request.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = 'Bearer ' + token
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// Response Interceptor
request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      console.error('API Error:', res.message)
      return Promise.reject(new Error(res.message || 'Error'))
    }
    return res
  },
  error => {
    return Promise.reject(error)
  }
)

export default request
"@

Write-File "$root\src\api\user.js" @"
import request from './request'

export const login = (data) => request.post('/auth/login', data)
export const register = (data) => request.post('/auth/register', data)
export const getUserInfo = () => request.get('/user/info')
"@

Write-File "$root\src\api\merchant.js" @"
import request from './request'

export const getStoreList = (params) => request.get('/merchant/stores', { params })
export const getMenu = (storeId) => request.get(`/merchant/stores/` + storeId + `/menu`)
"@

Write-File "$root\src\api\order.js" @"
import request from './request'

export const createOrder = (data) => request.post('/order/create', data)
export const getOrderList = (params) => request.get('/order/list', { params })
"@

Write-File "$root\src\api\delivery.js" @"
import request from './request'

export const updateLocation = (data) => request.post('/delivery/location', data)
"@

Write-File "$root\src\router\index.js" @"
import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('../views/HomeView.vue')
    },
    {
      path: '/auth/login',
      name: 'login',
      component: () => import('../views/auth/LoginView.vue')
    }
  ]
})

export default router
"@

Write-File "$root\src\views\HomeView.vue" @"
<template>
  <div class="home glass-panel">
    <h1>Takeout Platform</h1>
    <p>Welcome to the ultimate food delivery experience.</p>
    <router-link to="/auth/login" class="btn">Login to Continue</router-link>
  </div>
</template>

<style scoped>
.home {
  max-width: 800px;
  margin: 100px auto;
  padding: 40px;
  text-align: center;
}
.btn {
  display: inline-block;
  margin-top: 20px;
  padding: 12px 24px;
  background-color: var(--primary-color);
  color: white;
  text-decoration: none;
  border-radius: 8px;
  transition: transform 0.2s, box-shadow 0.2s;
}
.btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.4);
}
</style>
"@

Write-File "$root\src\views\auth\LoginView.vue" @"
<template>
  <div class="login-container">
    <div class="login-box glass-panel">
      <h2>Welcome Back</h2>
      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <input type="text" v-model="username" placeholder="Username" required />
        </div>
        <div class="form-group">
          <input type="password" v-model="password" placeholder="Password" required />
        </div>
        <button type="submit" class="submit-btn" :disabled="loading">
          {{ loading ? 'Logging in...' : 'Login' }}
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { login } from '@/api/user'

const router = useRouter()
const username = ref('')
const password = ref('')
const loading = ref(false)

const handleLogin = async () => {
  loading.value = true
  try {
    const res = await login({ username: username.value, password: password.value })
    localStorage.setItem('token', res.data.token)
    router.push('/')
  } catch (error) {
    console.error(error)
    alert('Login failed or dummy endpoint not active')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, var(--light-bg) 0%, #e2e8f0 100%);
}

.login-box {
  width: 100%;
  max-width: 400px;
  padding: 40px;
  text-align: center;
}

h2 {
  margin-bottom: 30px;
  color: var(--text-main);
}

.form-group {
  margin-bottom: 20px;
}

input {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 16px;
  outline: none;
  transition: border-color 0.3s;
}

input:focus {
  border-color: var(--primary-color);
}

.submit-btn {
  width: 100%;
  padding: 14px;
  background-color: var(--primary-color);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  cursor: pointer;
  transition: transform 0.2s, background-color 0.2s;
}

.submit-btn:hover {
  background-color: #ff5252;
  transform: translateY(-2px);
}

.submit-btn:disabled {
  background-color: #fab1b1;
  cursor: not-allowed;
  transform: none;
}
</style>
"@

echo "Frontend generation complete."

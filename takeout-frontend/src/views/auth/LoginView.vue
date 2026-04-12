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

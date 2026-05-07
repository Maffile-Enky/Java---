<template>
  <div class="login-container">
    <div class="login-box glass-panel">
      <h2>注册账号</h2>
      <form @submit.prevent="handleRegister">
        <div class="form-group">
          <input type="text" v-model="form.username" placeholder="用户名" required />
        </div>
        <div class="form-group">
          <input type="tel" v-model="form.phone" placeholder="手机号（选填）" maxlength="11" />
        </div>
        <div class="form-group">
          <input type="password" v-model="form.password" placeholder="密码" required />
        </div>
        <div class="form-group">
          <input type="password" v-model="confirmPassword" placeholder="确认密码" required />
        </div>
        <p v-if="errorMsg" class="error-msg">{{ errorMsg }}</p>
        <button type="submit" class="submit-btn" :disabled="loading">
          {{ loading ? '注册中...' : '注册' }}
        </button>
      </form>
      <div class="links">
        已有账号？<router-link to="/auth/login">去登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const form = ref({ username: '', password: '', phone: '' })
const confirmPassword = ref('')
const loading = ref(false)
const errorMsg = ref('')

const handleRegister = async () => {
  errorMsg.value = ''
  if (form.value.password.length < 6) {
    errorMsg.value = '密码长度不能少于6位'
    return
  }
  if (form.value.password !== confirmPassword.value) {
    errorMsg.value = '两次密码输入不一致'
    return
  }
  loading.value = true
  try {
    await authStore.register(form.value)
    router.push('/')
  } catch (error) {
    errorMsg.value = error?.response?.data?.message || '注册失败'
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
.login-box { width: 100%; max-width: 400px; padding: 40px; text-align: center; }
h2 { margin-bottom: 30px; color: var(--text-main); }
.form-group { margin-bottom: 16px; }
input {
  width: 100%; padding: 12px 16px; border: 1px solid #ddd; border-radius: 8px;
  font-size: 16px; outline: none; transition: border-color 0.3s; box-sizing: border-box;
}
input:focus { border-color: var(--primary-color); }
.error-msg { color: #ff4757; font-size: 14px; margin: -4px 0 8px 0; }
.submit-btn {
  width: 100%; padding: 14px; background-color: var(--primary-color); color: white;
  border: none; border-radius: 8px; font-size: 16px; cursor: pointer;
  transition: transform 0.2s, background-color 0.2s;
}
.submit-btn:hover { background-color: #ff5252; transform: translateY(-2px); }
.submit-btn:disabled { background-color: #fab1b1; cursor: not-allowed; transform: none; }
.links { margin-top: 20px; font-size: 14px; color: #666; }
.links a { color: var(--primary-color); text-decoration: none; }
</style>

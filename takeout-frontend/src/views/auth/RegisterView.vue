<template>
  <div class="auth-page">
    <div class="auth-card">
      <div class="auth-header">
        <img src="/images/logo/logo-icon.svg" alt="logo" class="auth-logo" />
        <h1>注册美团外卖</h1>
      </div>

      <form @submit.prevent="handleRegister" class="auth-form">
        <div class="form-group">
          <input type="text" v-model="form.username" placeholder="请输入用户名" required />
        </div>
        <div class="form-group">
          <input type="tel" v-model="form.phone" placeholder="手机号（选填）" maxlength="11" />
        </div>
        <div class="form-group">
          <input type="password" v-model="form.password" placeholder="请输入密码（至少6位）" required />
        </div>
        <div class="form-group">
          <input type="password" v-model="confirmPassword" placeholder="请确认密码" required />
        </div>
        <p v-if="errorMsg" class="error-msg">{{ errorMsg }}</p>
        <button type="submit" class="submit-btn" :disabled="loading">
          {{ loading ? '注册中...' : '注册' }}
        </button>
      </form>

      <div class="auth-footer">
        <router-link to="/auth/login" class="link">已有账号？立即登录</router-link>
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
.auth-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-bg-page);
  padding: var(--spacing-xl);
}

.auth-card {
  width: 100%;
  max-width: 400px;
  background: var(--color-bg-card);
  border-radius: var(--radius-xl);
  padding: 40px 32px;
  box-shadow: var(--shadow-lg);
}

.auth-header {
  text-align: center;
  margin-bottom: 32px;
}

.auth-logo {
  width: 48px;
  height: 48px;
  margin-bottom: 12px;
}

.auth-header h1 {
  font-size: var(--font-size-xl);
  font-weight: 700;
  margin: 0;
  color: var(--color-text-primary);
}

.auth-form {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.form-group input {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  font-size: var(--font-size-base);
  transition: border-color 0.2s;
  box-sizing: border-box;
}

.form-group input:focus {
  border-color: var(--color-primary);
}

.error-msg {
  color: var(--color-error);
  font-size: var(--font-size-sm);
  margin: -4px 0 0;
}

.submit-btn {
  width: 100%;
  padding: 14px;
  background: var(--color-primary);
  color: var(--color-text-primary);
  border: none;
  border-radius: var(--radius-xl);
  font-size: var(--font-size-md);
  font-weight: 700;
  cursor: pointer;
  transition: background 0.2s;
}

.submit-btn:hover {
  background: var(--color-primary-dark);
}

.submit-btn:disabled {
  background: var(--color-border);
  cursor: not-allowed;
}

.auth-footer {
  text-align: center;
  margin-top: 24px;
}

.link {
  font-size: var(--font-size-sm);
  color: var(--color-accent);
  text-decoration: none;
}

.link:hover {
  text-decoration: underline;
}
</style>

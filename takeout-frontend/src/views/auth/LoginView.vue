<template>
  <div class="auth-page">
    <div class="auth-card">
      <div class="auth-header">
        <img src="/images/logo/logo-icon.svg" alt="logo" class="auth-logo" />
        <h1>登录美团外卖</h1>
      </div>

      <div class="auth-tabs">
        <span :class="{ active: tab === 'password' }" @click="tab = 'password'">密码登录</span>
        <span :class="{ active: tab === 'phone' }" @click="tab = 'phone'">验证码登录</span>
      </div>

      <form v-if="tab === 'password'" @submit.prevent="handleLogin" class="auth-form">
        <div class="form-group">
          <input type="text" v-model="username" placeholder="请输入用户名" required />
        </div>
        <div class="form-group">
          <input type="password" v-model="password" placeholder="请输入密码" required />
        </div>
        <p v-if="errorMsg" class="error-msg">{{ errorMsg }}</p>
        <button type="submit" class="submit-btn" :disabled="loading">
          {{ loading ? '登录中...' : '登录' }}
        </button>
      </form>

      <form v-else @submit.prevent="handlePhoneLogin" class="auth-form">
        <div class="form-group">
          <input type="tel" v-model="phone" placeholder="请输入手机号" maxlength="11" required />
        </div>
        <div class="form-group sms-group">
          <input type="text" v-model="smsCode" placeholder="验证码" maxlength="6" required />
          <button type="button" class="sms-btn" :disabled="countdown > 0" @click="handleSendSms">
            {{ countdown > 0 ? `${countdown}s` : '获取验证码' }}
          </button>
        </div>
        <p v-if="errorMsg" class="error-msg">{{ errorMsg }}</p>
        <button type="submit" class="submit-btn" :disabled="loading">
          {{ loading ? '登录中...' : '登录' }}
        </button>
      </form>

      <div class="auth-footer">
        <router-link to="/auth/register" class="link">还没有账号？立即注册</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { sendSmsCode } from '@/api/user'

const router = useRouter()
const authStore = useAuthStore()

const tab = ref('password')
const username = ref('')
const password = ref('')
const phone = ref('')
const smsCode = ref('')
const loading = ref(false)
const errorMsg = ref('')
const countdown = ref(0)

const handleLogin = async () => {
  loading.value = true
  errorMsg.value = ''
  try {
    await authStore.login({ username: username.value, password: password.value })
    router.push('/')
  } catch (error) {
    errorMsg.value = error?.response?.data?.message || '登录失败，请检查用户名和密码'
  } finally {
    loading.value = false
  }
}

const handlePhoneLogin = async () => {
  loading.value = true
  errorMsg.value = ''
  try {
    await authStore.loginByPhone({ phone: phone.value, code: smsCode.value })
    router.push('/')
  } catch (error) {
    errorMsg.value = error?.response?.data?.message || '登录失败，请检查手机号和验证码'
  } finally {
    loading.value = false
  }
}

const handleSendSms = async () => {
  if (!phone.value || !/^1[3-9]\d{9}$/.test(phone.value)) {
    errorMsg.value = '请输入正确的手机号'
    return
  }
  try {
    await sendSmsCode({ phone: phone.value })
    countdown.value = 60
    const timer = setInterval(() => {
      countdown.value--
      if (countdown.value <= 0) clearInterval(timer)
    }, 1000)
  } catch (error) {
    errorMsg.value = error?.response?.data?.message || '验证码发送失败'
  }
}
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, var(--color-bg-page) 0%, var(--color-surface-warm) 100%);
  padding: var(--spacing-xl);
}

.auth-card {
  width: 100%;
  max-width: 400px;
  background: var(--color-bg-card);
  border-radius: var(--radius-xl);
  padding: 40px 32px;
  box-shadow: var(--shadow-lg);
  border: 1px solid var(--color-divider);
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
  font-family: var(--font-heading);
  font-size: var(--font-size-xl);
  font-weight: 700;
  margin: 0;
  color: var(--color-text-primary);
}

.auth-tabs {
  display: flex;
  border-bottom: 2px solid var(--color-divider);
  margin-bottom: 24px;
}

.auth-tabs span {
  flex: 1;
  text-align: center;
  padding: 12px 0;
  cursor: pointer;
  font-size: var(--font-size-base);
  color: var(--color-text-hint);
  border-bottom: 2px solid transparent;
  margin-bottom: -2px;
  transition: all 0.2s;
}

.auth-tabs span.active {
  color: var(--color-text-primary);
  font-weight: 600;
  border-bottom-color: var(--color-primary);
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

.sms-group {
  display: flex;
  gap: var(--spacing-sm);
}

.sms-group input {
  flex: 1;
}

.sms-btn {
  padding: 12px 16px;
  border: 1px solid var(--color-primary);
  border-radius: var(--radius-md);
  background: var(--color-bg-card);
  color: var(--color-text-primary);
  font-size: var(--font-size-sm);
  cursor: pointer;
  white-space: nowrap;
  transition: all 0.2s;
}

.sms-btn:disabled {
  border-color: var(--color-border);
  color: var(--color-text-hint);
  cursor: not-allowed;
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
  color: #fff;
  border: none;
  border-radius: var(--radius-xl);
  font-size: var(--font-size-md);
  font-weight: 700;
  cursor: pointer;
  transition: background var(--transition-smooth), box-shadow var(--transition-smooth);
}

.submit-btn:hover {
  background: var(--color-primary-dark);
  box-shadow: 0 4px 12px rgba(200, 75, 49, 0.25);
}

.submit-btn:disabled {
  background: var(--color-border);
  cursor: not-allowed;
  box-shadow: none;
}

.auth-footer {
  text-align: center;
  margin-top: 24px;
}

.link {
  font-size: var(--font-size-sm);
  color: var(--color-primary);
  text-decoration: none;
}

.link:hover {
  text-decoration: underline;
}
</style>

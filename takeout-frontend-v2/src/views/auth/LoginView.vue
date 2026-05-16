<template>
  <div class="auth-page">
    <div class="auth-card glass-panel">
      <div class="auth-header">
        <router-link to="/" class="auth-logo">
          <span class="logo-text">味觉星球</span>
        </router-link>
        <h1 class="auth-title">欢迎回来</h1>
        <p class="auth-subtitle">登录你的账号继续</p>
      </div>

      <!-- Tab switch -->
      <div class="auth-tabs">
        <button
          class="tab-btn"
          :class="{ active: tab === 'password' }"
          @click="tab = 'password'"
        >密码登录</button>
        <button
          class="tab-btn"
          :class="{ active: tab === 'sms' }"
          @click="tab = 'sms'"
        >短信登录</button>
      </div>

      <!-- Password form -->
      <form v-if="tab === 'password'" class="auth-form" @submit.prevent="handlePasswordLogin">
        <GlassInput
          v-model="form.phone"
          type="tel"
          placeholder="手机号"
          label="手机号"
          :error="errors.phone"
        />
        <GlassInput
          v-model="form.password"
          type="password"
          placeholder="密码"
          label="密码"
          :error="errors.password"
        />
        <GlassButton type="submit" block :loading="loading" variant="primary">
          登录
        </GlassButton>
      </form>

      <!-- SMS form -->
      <form v-else class="auth-form" @submit.prevent="handleSmsLogin">
        <GlassInput
          v-model="smsForm.phone"
          type="tel"
          placeholder="手机号"
          label="手机号"
          :error="errors.phone"
        />
        <div class="sms-row">
          <GlassInput
            v-model="smsForm.code"
            type="text"
            placeholder="验证码"
            label="验证码"
            :error="errors.code"
          />
          <GlassButton
            type="button"
            variant="ghost"
            size="sm"
            :disabled="countdown > 0"
            @click="sendCode"
          >
            {{ countdown > 0 ? `${countdown}s` : '获取验证码' }}
          </GlassButton>
        </div>
        <GlassButton type="submit" block :loading="loading" variant="primary">
          登录
        </GlassButton>
      </form>

      <div class="auth-footer">
        <span class="footer-text">还没有账号？</span>
        <router-link to="/auth/register" class="footer-link">立即注册</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { login, sendSmsCode } from '@/api/user'
import GlassInput from '@/components/ui/GlassInput.vue'
import GlassButton from '@/components/ui/GlassButton.vue'

const router = useRouter()
const auth = useAuthStore()

const tab = ref('password')
const loading = ref(false)
const countdown = ref(0)

const form = reactive({ phone: '', password: '' })
const smsForm = reactive({ phone: '', code: '' })
const errors = reactive({ phone: '', password: '', code: '' })

function clearErrors() {
  errors.phone = ''
  errors.password = ''
  errors.code = ''
}

async function handlePasswordLogin() {
  clearErrors()
  if (!form.phone) { errors.phone = '请输入手机号'; return }
  if (!form.password) { errors.password = '请输入密码'; return }

  loading.value = true
  try {
    const res = await login({ phone: form.phone, password: form.password })
    auth.setToken(res.data.token)
    auth.setUserInfo(res.data)
    router.push('/')
  } catch (e) {
    errors.phone = e.response?.data?.message || '登录失败'
  } finally {
    loading.value = false
  }
}

async function handleSmsLogin() {
  clearErrors()
  if (!smsForm.phone) { errors.phone = '请输入手机号'; return }
  if (!smsForm.code) { errors.code = '请输入验证码'; return }

  loading.value = true
  try {
    const res = await login({ phone: smsForm.phone, code: smsForm.code })
    auth.setToken(res.data.token)
    auth.setUserInfo(res.data)
    router.push('/')
  } catch (e) {
    errors.phone = e.response?.data?.message || '登录失败'
  } finally {
    loading.value = false
  }
}

async function sendCode() {
  if (!smsForm.phone) { errors.phone = '请输入手机号'; return }
  try {
    await sendSmsCode(smsForm.phone)
    countdown.value = 60
    const timer = setInterval(() => {
      countdown.value--
      if (countdown.value <= 0) clearInterval(timer)
    }, 1000)
  } catch (e) {
    errors.phone = e.response?.data?.message || '发送失败'
  }
}
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: var(--space-6);
}

.auth-card {
  width: 100%;
  max-width: 400px;
  padding: var(--space-8);
}

.auth-header {
  text-align: center;
  margin-bottom: var(--space-8);
}

.auth-logo {
  text-decoration: none;
}

.logo-text {
  font-family: var(--font-serif);
  font-size: 1.5rem;
  font-weight: 900;
  background: var(--gradient-green);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.auth-title {
  font-size: 1.3rem;
  font-weight: 800;
  color: var(--text-primary);
  margin: var(--space-4) 0 var(--space-1);
}

.auth-subtitle {
  font-size: var(--text-sm);
  color: var(--text-muted);
  margin: 0;
}

.auth-tabs {
  display: flex;
  gap: var(--space-1);
  margin-bottom: var(--space-6);
  padding: 3px;
  background: var(--glass);
  border-radius: var(--radius-md);
}

.tab-btn {
  flex: 1;
  padding: var(--space-2) var(--space-4);
  border: none;
  background: none;
  color: var(--text-muted);
  font-size: var(--text-sm);
  font-weight: 500;
  cursor: pointer;
  border-radius: var(--radius-sm);
  transition: all var(--duration-fast);
  font-family: var(--font-sans);
}

.tab-btn.active {
  background: var(--gradient-green);
  color: var(--text-inverse);
  font-weight: 600;
}

.auth-form {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
}

.sms-row {
  display: flex;
  gap: var(--space-2);
  align-items: flex-end;
}

.sms-row :deep(.glass-input-wrapper) {
  flex: 1;
}

.auth-footer {
  text-align: center;
  margin-top: var(--space-6);
}

.footer-text {
  font-size: var(--text-sm);
  color: var(--text-muted);
}

.footer-link {
  font-size: var(--text-sm);
  color: var(--accent);
  text-decoration: none;
  font-weight: 600;
  margin-left: var(--space-1);
}

.footer-link:hover {
  text-decoration: underline;
}
</style>

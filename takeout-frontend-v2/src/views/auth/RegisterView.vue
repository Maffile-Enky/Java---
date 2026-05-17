<template>
  <div class="auth-page">
    <div class="auth-card glass-panel">
      <div class="auth-header">
        <router-link to="/" class="auth-logo">
          <span class="logo-text">味觉星球</span>
        </router-link>
        <h1 class="auth-title">创建账号</h1>
        <p class="auth-subtitle">注册成为味觉星球用户</p>
      </div>

      <form class="auth-form" @submit.prevent="handleRegister">
        <GlassInput
          v-model="form.phone"
          type="tel"
          placeholder="手机号"
          label="手机号"
          :error="errors.phone"
        />
        <GlassInput
          v-model="form.nickname"
          type="text"
          placeholder="昵称"
          label="昵称"
          :error="errors.nickname"
        />
        <GlassInput
          v-model="form.password"
          type="password"
          placeholder="密码（至少6位）"
          label="密码"
          :error="errors.password"
        />
        <GlassInput
          v-model="form.confirmPassword"
          type="password"
          placeholder="确认密码"
          label="确认密码"
          :error="errors.confirmPassword"
        />
        <GlassButton type="submit" block :loading="loading" variant="primary">
          注册
        </GlassButton>
      </form>

      <div class="auth-footer">
        <span class="footer-text">已有账号？</span>
        <router-link to="/auth/login" class="footer-link">去登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import GlassInput from '@/components/ui/GlassInput.vue'
import GlassButton from '@/components/ui/GlassButton.vue'

const router = useRouter()
const auth = useAuthStore()
const loading = ref(false)

const form = reactive({
  phone: '',
  nickname: '',
  password: '',
  confirmPassword: ''
})

const errors = reactive({
  phone: '',
  nickname: '',
  password: '',
  confirmPassword: ''
})

function clearErrors() {
  Object.keys(errors).forEach(k => errors[k] = '')
}

async function handleRegister() {
  clearErrors()
  let valid = true

  if (!form.phone) { errors.phone = '请输入手机号'; valid = false }
  if (!form.nickname) { errors.nickname = '请输入昵称'; valid = false }
  if (!form.password || form.password.length < 6) { errors.password = '密码至少6位'; valid = false }
  if (form.password !== form.confirmPassword) { errors.confirmPassword = '两次密码不一致'; valid = false }
  if (!valid) return

  loading.value = true
  try {
    await auth.register({
      phone: form.phone,
      nickname: form.nickname,
      password: form.password
    })
    router.push('/')
  } catch (e) {
    errors.phone = e.response?.data?.message || e.message || '注册失败'
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

.auth-form {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
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

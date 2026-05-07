<template>
  <div class="login-container">
    <div class="login-box glass-panel">
      <h2>欢迎回来</h2>

      <!-- Tab切换 -->
      <div class="tab-bar">
        <span :class="{ active: tab === 'password' }" @click="tab = 'password'">密码登录</span>
        <span :class="{ active: tab === 'phone' }" @click="tab = 'phone'">验证码登录</span>
      </div>

      <!-- 密码登录 -->
      <form v-if="tab === 'password'" @submit.prevent="handleLogin">
        <div class="form-group">
          <input type="text" v-model="username" placeholder="用户名" required />
        </div>
        <div class="form-group">
          <input type="password" v-model="password" placeholder="密码" required />
        </div>
        <p v-if="errorMsg" class="error-msg">{{ errorMsg }}</p>
        <button type="submit" class="submit-btn" :disabled="loading">
          {{ loading ? '登录中...' : '登录' }}
        </button>
      </form>

      <!-- 手机号登录 -->
      <form v-else @submit.prevent="handlePhoneLogin">
        <div class="form-group">
          <input type="tel" v-model="phone" placeholder="手机号" maxlength="11" required />
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

      <div class="links">
        <router-link to="/auth/register">注册账号</router-link>
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
h2 { margin-bottom: 24px; color: var(--text-main); }
.tab-bar {
  display: flex;
  margin-bottom: 24px;
  border-bottom: 2px solid #eee;
}
.tab-bar span {
  flex: 1;
  padding: 10px 0;
  cursor: pointer;
  font-size: 15px;
  color: #999;
  transition: all 0.2s;
}
.tab-bar span.active {
  color: var(--primary-color);
  border-bottom: 2px solid var(--primary-color);
  margin-bottom: -2px;
  font-weight: 600;
}
.form-group { margin-bottom: 16px; }
input {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 16px;
  outline: none;
  transition: border-color 0.3s;
  box-sizing: border-box;
}
input:focus { border-color: var(--primary-color); }
.sms-group { display: flex; gap: 10px; }
.sms-group input { flex: 1; }
.sms-btn {
  padding: 12px 16px;
  border: 1px solid var(--primary-color);
  border-radius: 8px;
  background: #fff;
  color: var(--primary-color);
  font-size: 14px;
  cursor: pointer;
  white-space: nowrap;
}
.sms-btn:disabled { border-color: #ccc; color: #ccc; cursor: not-allowed; }
.error-msg { color: #ff4757; font-size: 14px; margin: -4px 0 8px 0; text-align: left; }
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
.submit-btn:hover { background-color: #ff5252; transform: translateY(-2px); }
.submit-btn:disabled { background-color: #fab1b1; cursor: not-allowed; transform: none; }
.links {
  margin-top: 20px;
  font-size: 14px;
}
.links a { color: var(--primary-color); text-decoration: none; }
</style>

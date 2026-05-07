import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, loginByPhone as phoneLoginApi, register as registerApi } from '@/api/user'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))

  const isLoggedIn = computed(() => !!token.value)
  const userRole = computed(() => userInfo.value?.role || 'USER')
  const username = computed(() => userInfo.value?.username || '')
  const nickname = computed(() => userInfo.value?.nickname || userInfo.value?.username || '')

  function setAuth(tokenVal, user) {
    token.value = tokenVal
    userInfo.value = user
    localStorage.setItem('token', tokenVal)
    localStorage.setItem('userInfo', JSON.stringify(user))
  }

  function updateUserInfo(user) {
    userInfo.value = { ...userInfo.value, ...user }
    localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
  }

  async function login(credentials) {
    const res = await loginApi(credentials)
    const tokenVal = res.data.token
    const user = res.data.userInfo || res.data.user || { username: credentials.username, role: 'USER' }
    setAuth(tokenVal, user)
    return res
  }

  async function loginByPhone(data) {
    const res = await phoneLoginApi(data)
    const tokenVal = res.data.token
    const user = res.data.userInfo || res.data.user || { role: 'USER' }
    setAuth(tokenVal, user)
    return res
  }

  async function register(data) {
    const res = await registerApi(data)
    const tokenVal = res.data.token
    const user = res.data.userInfo || res.data.user || { username: data.username, role: 'USER' }
    setAuth(tokenVal, user)
    return res
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  function loadFromStorage() {
    token.value = localStorage.getItem('token') || ''
    userInfo.value = JSON.parse(localStorage.getItem('userInfo') || 'null')
  }

  return {
    token, userInfo, isLoggedIn, userRole, username, nickname,
    setAuth, updateUserInfo, login, loginByPhone, register, logout, loadFromStorage
  }
})

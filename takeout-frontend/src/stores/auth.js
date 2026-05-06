import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi } from '@/api/user'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))

  const isLoggedIn = computed(() => !!token.value)
  const userRole = computed(() => userInfo.value?.role || 'USER')
  const username = computed(() => userInfo.value?.username || '')

  function setAuth(tokenVal, user) {
    token.value = tokenVal
    userInfo.value = user
    localStorage.setItem('token', tokenVal)
    localStorage.setItem('userInfo', JSON.stringify(user))
  }

  async function login(credentials) {
    const res = await loginApi(credentials)
    const tokenVal = res.data.token
    const user = res.data.userInfo || res.data.user || { username: credentials.username, role: 'USER' }
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
    token, userInfo, isLoggedIn, userRole, username,
    setAuth, login, logout, loadFromStorage
  }
})

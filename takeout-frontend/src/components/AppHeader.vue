<template>
  <header class="app-header">
    <div class="header-inner">
      <div class="header-left">
        <div class="logo" @click="router.push('/')">
          <img src="/images/logo/logo.svg" alt="美团外卖" class="logo-img" />
        </div>
      </div>

      <div class="header-center">
        <div class="search-bar">
          <svg class="search-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="11" cy="11" r="8"/><path d="m21 21-4.35-4.35"/>
          </svg>
          <input
            v-model="searchText"
            type="text"
            placeholder="搜索商家或菜品"
            @keyup.enter="handleSearch"
          />
        </div>
      </div>

      <div class="header-right">
        <router-link v-if="authStore.userRole === 'MERCHANT' || authStore.userRole === 'ADMIN'" to="/merchant" class="header-link">
          商家中心
        </router-link>
        <router-link v-if="authStore.userRole === 'ADMIN'" to="/admin" class="header-link">
          管理后台
        </router-link>
        <template v-if="authStore.isLoggedIn">
          <div class="user-dropdown" @click="showUserMenu = !showUserMenu">
            <img :src="avatarSrc" class="user-avatar" @error="avatarError = true" />
            <span class="user-name">{{ authStore.username }}</span>
            <svg class="arrow-down" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="m6 9 6 6 6-6"/></svg>
            <div v-if="showUserMenu" class="dropdown-menu">
              <router-link to="/user/profile" class="dropdown-item" @click="showUserMenu = false">个人中心</router-link>
              <router-link to="/user/orders" class="dropdown-item" @click="showUserMenu = false">我的订单</router-link>
              <div class="dropdown-divider"></div>
              <div class="dropdown-item danger" @click="handleLogout">退出登录</div>
            </div>
          </div>
        </template>
        <template v-else>
          <router-link to="/auth/login" class="btn-login">登录</router-link>
          <router-link to="/auth/register" class="btn-register">注册</router-link>
        </template>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useCartStore } from '@/stores/cart'

const router = useRouter()
const authStore = useAuthStore()
const cartStore = useCartStore()

const searchText = ref('')
const showUserMenu = ref(false)
const avatarError = ref(false)

const avatarSrc = computed(() => {
  if (avatarError.value) return '/images/placeholders/avatar-default.png'
  return '/images/placeholders/avatar-default.png'
})

function handleSearch() {
  if (searchText.value.trim()) {
    router.push(`/user/restaurants?keyword=${searchText.value}`)
  }
}

function handleLogout() {
  showUserMenu.value = false
  authStore.logout()
  cartStore.clearCart()
  router.push('/auth/login')
}
</script>

<style scoped>
.app-header {
  background: var(--color-primary);
  height: var(--header-height);
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: var(--shadow-sm);
}

.header-inner {
  max-width: var(--content-max-width);
  margin: 0 auto;
  height: 100%;
  display: flex;
  align-items: center;
  padding: 0 var(--spacing-xl);
  gap: var(--spacing-xl);
}

.header-left {
  flex-shrink: 0;
}

.logo {
  cursor: pointer;
  display: flex;
  align-items: center;
}

.logo-img {
  height: 36px;
  width: auto;
}

.header-center {
  flex: 1;
  max-width: 480px;
}

.search-bar {
  display: flex;
  align-items: center;
  background: rgba(0,0,0,0.06);
  border-radius: var(--radius-xl);
  padding: 0 var(--spacing-lg);
  height: 38px;
  transition: background 0.2s;
}

.search-bar:focus-within {
  background: rgba(0,0,0,0.1);
}

.search-icon {
  width: 18px;
  height: 18px;
  color: var(--color-text-hint);
  flex-shrink: 0;
  margin-right: var(--spacing-sm);
}

.search-bar input {
  flex: 1;
  border: none;
  background: transparent;
  height: 100%;
  font-size: var(--font-size-base);
  color: var(--color-text-primary);
}

.search-bar input::placeholder {
  color: var(--color-text-hint);
}

.header-right {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  flex-shrink: 0;
}

.header-link {
  padding: 6px 14px;
  border-radius: var(--radius-md);
  font-size: var(--font-size-sm);
  font-weight: 500;
  color: var(--color-text-primary);
  transition: background 0.2s;
}

.header-link:hover {
  background: rgba(0,0,0,0.06);
}

.user-dropdown {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: var(--radius-md);
  position: relative;
  transition: background 0.2s;
}

.user-dropdown:hover {
  background: rgba(0,0,0,0.06);
}

.user-avatar {
  width: 30px;
  height: 30px;
  border-radius: var(--radius-full);
  object-fit: cover;
  border: 2px solid rgba(255,255,255,0.6);
}

.user-name {
  font-size: var(--font-size-sm);
  font-weight: 500;
  max-width: 80px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.arrow-down {
  width: 14px;
  height: 14px;
  color: var(--color-text-secondary);
}

.dropdown-menu {
  position: absolute;
  top: 100%;
  right: 0;
  margin-top: 8px;
  background: var(--color-bg-card);
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-lg);
  min-width: 140px;
  padding: 6px 0;
  z-index: 200;
}

.dropdown-item {
  display: block;
  padding: 10px 16px;
  font-size: var(--font-size-base);
  color: var(--color-text-primary);
  transition: background 0.15s;
  text-decoration: none;
}

.dropdown-item:hover {
  background: var(--color-bg-hover);
}

.dropdown-item.danger {
  color: var(--color-error);
}

.dropdown-divider {
  height: 1px;
  background: var(--color-divider);
  margin: 4px 0;
}

.btn-login, .btn-register {
  padding: 6px 16px;
  border-radius: var(--radius-md);
  font-size: var(--font-size-sm);
  font-weight: 500;
  text-decoration: none;
  transition: all 0.2s;
}

.btn-login {
  color: var(--color-text-primary);
}

.btn-login:hover {
  background: rgba(0,0,0,0.06);
}

.btn-register {
  background: var(--color-text-primary);
  color: var(--color-primary);
}

.btn-register:hover {
  opacity: 0.85;
}
</style>

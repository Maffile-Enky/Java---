<template>
  <header class="app-header" :class="{ scrolled }">
    <div class="header-inner">
      <!-- Logo -->
      <router-link to="/" class="header-logo">
        <span class="logo-text">味觉星球</span>
        <span class="logo-sub">FLAVOR</span>
      </router-link>

      <!-- Search bar -->
      <div class="header-search" v-if="showSearch">
        <input
          v-model="keyword"
          type="text"
          placeholder="搜索美食、商家..."
          class="search-input"
          @keyup.enter="doSearch"
        />
      </div>

      <!-- Right actions -->
      <div class="header-actions">
        <router-link v-if="isLoggedIn" to="/user/notifications" class="action-btn">
          🔔
        </router-link>
        <router-link v-if="!isLoggedIn" to="/auth/login" class="btn btn-primary btn-sm">
          登录
        </router-link>
        <div v-else class="user-menu" @click="showMenu = !showMenu">
          <img v-if="auth.userInfo?.avatar" :src="auth.userInfo.avatar" class="user-avatar-img" alt="头像" />
          <span v-else class="user-avatar">{{ nickname.charAt(0) }}</span>
          <!-- Dropdown -->
          <div v-if="showMenu" class="menu-dropdown glass-panel">
            <div class="menu-header">
              <span class="menu-name">{{ nickname }}</span>
              <span class="menu-role tag tag-green" style="font-size:10px;">{{ userRole }}</span>
            </div>
            <div class="menu-divider"></div>
            <router-link to="/user/profile" class="menu-item" @click="showMenu = false">个人中心</router-link>
            <router-link to="/user/orders" class="menu-item" @click="showMenu = false">我的订单</router-link>
            <router-link v-if="userRole === 'MERCHANT' || userRole === 'ADMIN'" to="/merchant" class="menu-item" @click="showMenu = false">商家后台</router-link>
            <router-link v-if="userRole === 'ADMIN'" to="/admin" class="menu-item" @click="showMenu = false">管理后台</router-link>
            <router-link v-if="userRole === 'RIDER' || userRole === 'ADMIN'" to="/rider" class="menu-item" @click="showMenu = false">骑手中心</router-link>
            <div class="menu-divider"></div>
            <button class="menu-item menu-item--danger" @click="handleLogout">退出登录</button>
          </div>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { storeToRefs } from 'pinia'

const props = defineProps({
  showSearch: { type: Boolean, default: true }
})

const router = useRouter()
const auth = useAuthStore()
const { isLoggedIn, nickname, userRole } = storeToRefs(auth)

const keyword = ref('')
const showMenu = ref(false)
const scrolled = ref(false)

function doSearch() {
  if (keyword.value.trim()) {
    router.push({ path: '/user/restaurants', query: { keyword: keyword.value.trim() } })
  }
}

function handleLogout() {
  auth.logout()
  showMenu.value = false
  router.push('/')
}

function onScroll() {
  scrolled.value = window.scrollY > 20
}

function onClickOutside(e) {
  if (!e.target.closest('.user-menu')) {
    showMenu.value = false
  }
}

onMounted(() => {
  window.addEventListener('scroll', onScroll)
  document.addEventListener('click', onClickOutside)
})

onUnmounted(() => {
  window.removeEventListener('scroll', onScroll)
  document.removeEventListener('click', onClickOutside)
})
</script>

<style scoped>
.app-header {
  position: fixed;
  top: 0; left: 0; right: 0;
  z-index: 100;
  height: var(--header-height);
  transition: all var(--duration-normal) var(--ease-out);
}

.app-header.scrolled {
  background: rgba(14, 22, 18, 0.88);
  backdrop-filter: blur(24px) saturate(1.4);
  -webkit-backdrop-filter: blur(24px) saturate(1.4);
  border-bottom: 1px solid var(--glass-border);
}

.header-inner {
  max-width: var(--content-max);
  margin: 0 auto;
  padding: 0 var(--space-6);
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--space-6);
}

.header-logo {
  display: flex;
  align-items: baseline;
  gap: var(--space-2);
  text-decoration: none;
  flex-shrink: 0;
}

.logo-text {
  font-family: var(--font-serif);
  font-size: 1.3rem;
  font-weight: 900;
  background: var(--gradient-green);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.logo-sub {
  font-size: var(--text-xs);
  color: var(--text-muted);
  letter-spacing: 0.1em;
}

.header-search {
  flex: 1;
  max-width: 400px;
}

.search-input {
  width: 100%;
  padding: var(--space-2) var(--space-5);
  background: rgba(255,255,255,0.05);
  border: 1px solid var(--glass-border);
  border-radius: var(--radius-full);
  color: var(--text-primary);
  font-size: var(--text-sm);
  transition: border-color var(--duration-fast);
}

.search-input::placeholder { color: var(--text-muted); }
.search-input:focus { border-color: var(--accent); }

.header-actions {
  display: flex;
  align-items: center;
  gap: var(--space-4);
}

.action-btn {
  font-size: 1.1rem;
  text-decoration: none;
  transition: transform var(--duration-fast);
}

.action-btn:hover { transform: scale(1.1); }

.user-menu {
  position: relative;
}

.user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: var(--gradient-green);
  color: var(--text-inverse);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: var(--text-sm);
  font-weight: 700;
  cursor: pointer;
}

.user-avatar-img {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
  cursor: pointer;
  border: 2px solid var(--glass-border);
  transition: border-color var(--duration-fast);
}

.user-avatar-img:hover {
  border-color: var(--accent);
}

.menu-dropdown {
  position: absolute;
  top: calc(100% + var(--space-2));
  right: 0;
  min-width: 180px;
  padding: var(--space-2);
  animation: fadeSlideDown 0.2s var(--ease-out);
}

.menu-header {
  padding: var(--space-3) var(--space-4);
  display: flex;
  align-items: center;
  gap: var(--space-2);
}

.menu-name {
  font-weight: 600;
  font-size: var(--text-sm);
}

.menu-divider {
  height: 1px;
  background: var(--glass-border);
  margin: var(--space-1) 0;
}

.menu-item {
  display: block;
  width: 100%;
  padding: var(--space-2) var(--space-4);
  border-radius: var(--radius-sm);
  font-size: var(--text-sm);
  color: var(--text-secondary);
  text-decoration: none;
  transition: all var(--duration-fast);
  text-align: left;
  background: none;
  border: none;
  cursor: pointer;
  font-family: var(--font-sans);
}

.menu-item:hover {
  background: var(--glass);
  color: var(--text-primary);
}

.menu-item--danger:hover {
  color: var(--color-danger);
}

@keyframes fadeSlideDown {
  from { opacity: 0; transform: translateY(-8px); }
  to { opacity: 1; transform: translateY(0); }
}

@media (max-width: 640px) {
  .header-search { display: none; }
}
</style>

<template>
  <header class="app-header" :class="{ scrolled, 'menu-open': showMobileMenu }">
    <div class="header-inner">
      <!-- Logo -->
      <router-link to="/" class="header-logo">
        <img src="@/assets/pic/enk.png" alt="味觉星球" class="logo-icon" />
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
        <router-link v-if="isLoggedIn" to="/user/notifications" class="action-btn notification-btn">
          <span class="notification-icon">🔔</span>
        </router-link>
        <router-link v-if="!isLoggedIn" to="/auth/login" class="btn btn-primary btn-sm">
          登录
        </router-link>
        <div v-else class="user-menu" @click="showMenu = !showMenu">
          <img v-if="auth.userInfo?.avatar" :src="auth.userInfo.avatar" class="user-avatar-img" alt="头像" />
          <span v-else class="user-avatar">{{ nickname.charAt(0) }}</span>
          <!-- Dropdown -->
          <Transition name="dropdown">
            <div v-if="showMenu" class="menu-dropdown glass-panel">
              <div class="menu-header">
                <span class="menu-name">{{ nickname }}</span>
                <span class="menu-role tag tag-green" style="font-size:10px;">{{ userRole }}</span>
              </div>
              <div class="menu-divider"></div>
              <router-link to="/user/profile" class="menu-item" @click="showMenu = false">
                <span class="menu-icon">👤</span> 个人中心
              </router-link>
              <router-link to="/user/orders" class="menu-item" @click="showMenu = false">
                <span class="menu-icon">📦</span> 我的订单
              </router-link>
              <router-link v-if="userRole === 'MERCHANT' || userRole === 'ADMIN'" to="/merchant" class="menu-item" @click="showMenu = false">
                <span class="menu-icon">🏪</span> 商家后台
              </router-link>
              <router-link v-if="userRole === 'ADMIN'" to="/admin" class="menu-item" @click="showMenu = false">
                <span class="menu-icon">⚙️</span> 管理后台
              </router-link>
              <router-link v-if="userRole === 'RIDER' || userRole === 'ADMIN'" to="/rider" class="menu-item" @click="showMenu = false">
                <span class="menu-icon">🚴</span> 骑手中心
              </router-link>
              <div class="menu-divider"></div>
              <button class="menu-item menu-item--danger" @click="handleLogout">
                <span class="menu-icon">🚪</span> 退出登录
              </button>
            </div>
          </Transition>
        </div>

        <!-- Mobile menu button -->
        <button class="mobile-menu-btn" @click="showMobileMenu = !showMobileMenu">
          <span class="hamburger" :class="{ active: showMobileMenu }">
            <span></span>
            <span></span>
            <span></span>
          </span>
        </button>
      </div>
    </div>

    <!-- Mobile menu -->
    <Transition name="mobile-menu">
      <div v-if="showMobileMenu" class="mobile-menu">
        <div class="mobile-menu-content">
          <div class="mobile-search" v-if="showSearch">
            <input
              v-model="keyword"
              type="text"
              placeholder="搜索美食、商家..."
              class="search-input"
              @keyup.enter="doSearchMobile"
            />
          </div>
          <nav class="mobile-nav">
            <router-link to="/" class="mobile-nav-item" @click="closeMobileMenu">
              <span class="nav-icon">🏠</span> 首页
            </router-link>
            <router-link to="/user/restaurants" class="mobile-nav-item" @click="closeMobileMenu">
              <span class="nav-icon">🍽️</span> 点餐
            </router-link>
            <router-link to="/user/cart" class="mobile-nav-item" @click="closeMobileMenu">
              <span class="nav-icon">🛒</span> 购物车
            </router-link>
            <router-link to="/user/orders" class="mobile-nav-item" @click="closeMobileMenu">
              <span class="nav-icon">📦</span> 订单
            </router-link>
            <router-link v-if="isLoggedIn" to="/user/notifications" class="mobile-nav-item" @click="closeMobileMenu">
              <span class="nav-icon">🔔</span> 通知
            </router-link>
            <router-link v-if="isLoggedIn" to="/user/profile" class="mobile-nav-item" @click="closeMobileMenu">
              <span class="nav-icon">👤</span> 我的
            </router-link>
          </nav>
          <div class="mobile-menu-footer">
            <router-link v-if="!isLoggedIn" to="/auth/login" class="btn btn-primary btn-block" @click="closeMobileMenu">
              登录
            </router-link>
            <button v-else class="btn btn-danger btn-block" @click="handleLogout">
              退出登录
            </button>
          </div>
        </div>
      </div>
    </Transition>
  </header>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { storeToRefs } from 'pinia'

const props = defineProps({
  showSearch: { type: Boolean, default: true }
})

const router = useRouter()
const route = useRoute()
const auth = useAuthStore()
const { isLoggedIn, nickname, userRole } = storeToRefs(auth)

const keyword = ref('')
const showMenu = ref(false)
const showMobileMenu = ref(false)
const scrolled = ref(false)

function doSearch() {
  if (keyword.value.trim()) {
    router.push({ path: '/user/restaurants', query: { keyword: keyword.value.trim() } })
  }
}

function doSearchMobile() {
  doSearch()
  closeMobileMenu()
}

function closeMobileMenu() {
  showMobileMenu.value = false
}

function handleLogout() {
  auth.logout()
  showMenu.value = false
  showMobileMenu.value = false
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

// Close mobile menu on route change
watch(() => route.path, () => {
  showMobileMenu.value = false
})

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

.app-header.menu-open {
  background: rgba(14, 22, 18, 0.95);
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
  align-items: center;
  gap: var(--space-2);
  text-decoration: none;
  flex-shrink: 0;
}

.logo-icon {
  width: 28px;
  height: 28px;
  object-fit: contain;
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
.search-input:focus { border-color: var(--accent); outline: none; }

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

.notification-btn {
  position: relative;
}

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
  min-width: 200px;
  padding: var(--space-2);
  z-index: 1000;
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
  display: flex;
  align-items: center;
  gap: var(--space-3);
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

.menu-icon {
  font-size: 1rem;
}

/* Mobile menu button */
.mobile-menu-btn {
  display: none;
  background: none;
  border: none;
  cursor: pointer;
  padding: var(--space-2);
}

.hamburger {
  display: flex;
  flex-direction: column;
  gap: 5px;
  width: 20px;
}

.hamburger span {
  display: block;
  height: 2px;
  background: var(--text-primary);
  border-radius: 2px;
  transition: all var(--duration-fast);
}

.hamburger.active span:nth-child(1) {
  transform: rotate(45deg) translate(5px, 5px);
}

.hamburger.active span:nth-child(2) {
  opacity: 0;
}

.hamburger.active span:nth-child(3) {
  transform: rotate(-45deg) translate(5px, -5px);
}

/* Mobile menu */
.mobile-menu {
  position: fixed;
  top: var(--header-height);
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(14, 22, 18, 0.98);
  backdrop-filter: blur(24px);
  z-index: 99;
  overflow-y: auto;
}

.mobile-menu-content {
  padding: var(--space-6);
  display: flex;
  flex-direction: column;
  gap: var(--space-6);
  min-height: calc(100vh - var(--header-height));
}

.mobile-search {
  margin-bottom: var(--space-4);
}

.mobile-nav {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

.mobile-nav-item {
  display: flex;
  align-items: center;
  gap: var(--space-4);
  padding: var(--space-4);
  color: var(--text-primary);
  text-decoration: none;
  font-size: var(--text-lg);
  border-radius: var(--radius-md);
  transition: background var(--duration-fast);
}

.mobile-nav-item:hover {
  background: var(--glass);
}

.nav-icon {
  font-size: 1.3rem;
}

.mobile-menu-footer {
  margin-top: auto;
  padding-top: var(--space-6);
  border-top: 1px solid var(--glass-border);
}

.btn-block {
  width: 100%;
  justify-content: center;
}

.btn-danger {
  background: rgba(248, 113, 113, 0.15);
  border: 1px solid rgba(248, 113, 113, 0.3);
  color: #f87171;
}

.btn-danger:hover {
  background: rgba(248, 113, 113, 0.25);
}

/* Dropdown animation */
.dropdown-enter-active {
  animation: dropdownIn 0.2s var(--ease-out);
}

.dropdown-leave-active {
  animation: dropdownOut 0.15s var(--ease-out);
}

@keyframes dropdownIn {
  from { opacity: 0; transform: translateY(-8px) scale(0.95); }
  to { opacity: 1; transform: translateY(0) scale(1); }
}

@keyframes dropdownOut {
  from { opacity: 1; transform: translateY(0) scale(1); }
  to { opacity: 0; transform: translateY(-8px) scale(0.95); }
}

/* Mobile menu animation */
.mobile-menu-enter-active {
  animation: mobileMenuIn 0.3s var(--ease-out);
}

.mobile-menu-leave-active {
  animation: mobileMenuOut 0.2s var(--ease-out);
}

@keyframes mobileMenuIn {
  from { opacity: 0; transform: translateY(-20px); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes mobileMenuOut {
  from { opacity: 1; transform: translateY(0); }
  to { opacity: 0; transform: translateY(-20px); }
}

/* Responsive */
@media (max-width: 768px) {
  .header-search { display: none; }
  .user-menu { display: none; }
  .notification-btn { display: none; }
  .mobile-menu-btn { display: block; }
}

@media (min-width: 769px) {
  .mobile-menu { display: none; }
}
</style>

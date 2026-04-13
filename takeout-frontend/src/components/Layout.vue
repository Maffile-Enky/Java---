<template>
  <div class="layout">
    <!-- Top Navigation -->
    <header class="header">
      <div class="header-left">
        <div class="logo" @click="goHome">
          <span class="logo-icon">🍔</span>
          <span class="logo-text">Takeout Platform</span>
        </div>
      </div>

      <nav class="nav-menu">
        <router-link to="/" class="nav-item" :class="{ active: $route.path === '/' }">
          <span class="nav-icon">🏠</span>
          <span>首页</span>
        </router-link>
        <router-link to="/user/restaurants" class="nav-item" :class="{ active: $route.path.startsWith('/user/restaurants') }">
          <span class="nav-icon">🏪</span>
          <span>商家</span>
        </router-link>
        <router-link to="/user/cart" class="nav-item" :class="{ active: $route.path.startsWith('/user/cart') }">
          <span class="nav-icon">🛒</span>
          <span>购物车</span>
          <span v-if="cartCount > 0" class="cart-badge">{{ cartCount }}</span>
        </router-link>
        <router-link to="/user/orders" class="nav-item" :class="{ active: $route.path.startsWith('/user/orders') }">
          <span class="nav-icon">📋</span>
          <span>订单</span>
        </router-link>
      </nav>

      <div class="header-right">
        <div class="user-info" v-if="userInfo">
          <span class="user-role" :class="userInfo.role">{{ getRoleName(userInfo.role) }}</span>
          <span class="user-name">{{ userInfo.username }}</span>
        </div>
        <button class="logout-btn" @click="handleLogout">退出</button>
      </div>
    </header>

    <!-- Main Content -->
    <main class="main-content">
      <div class="content-container">
        <slot></slot>
      </div>
    </main>

    <!-- Footer -->
    <footer class="footer">
      <p>&copy; 2026 Takeout Platform. All rights reserved.</p>
    </footer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const userInfo = ref(null)
const cartCount = ref(0)

onMounted(() => {
  // 从 localStorage 获取用户信息
  const user = localStorage.getItem('userInfo')
  if (user) {
    userInfo.value = JSON.parse(user)
  }
  // 获取购物车数量
  updateCartCount()
})

const getRoleName = (role) => {
  const roleMap = {
    'USER': '用户',
    'MERCHANT': '商家',
    'RIDER': '骑手',
    'ADMIN': '管理员'
  }
  return roleMap[role] || role
}

const updateCartCount = () => {
  const cart = JSON.parse(localStorage.getItem('cart') || '[]')
  cartCount.value = cart.reduce((sum, item) => sum + item.quantity, 0)
}

const goHome = () => {
  router.push('/')
}

const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('userInfo')
  localStorage.removeItem('cart')
  router.push('/auth/login')
}
</script>

<style scoped>
.layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 0 24px;
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-left {
  display: flex;
  align-items: center;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  font-size: 20px;
  font-weight: bold;
}

.logo-icon {
  font-size: 28px;
}

.nav-menu {
  display: flex;
  gap: 8px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  color: rgba(255, 255, 255, 0.8);
  text-decoration: none;
  border-radius: 8px;
  transition: all 0.3s;
  position: relative;
}

.nav-item:hover {
  background: rgba(255, 255, 255, 0.1);
  color: white;
}

.nav-item.active {
  background: rgba(255, 255, 255, 0.2);
  color: white;
}

.nav-icon {
  font-size: 18px;
}

.cart-badge {
  position: absolute;
  top: -4px;
  right: -4px;
  background: #ff4757;
  color: white;
  font-size: 12px;
  padding: 2px 6px;
  border-radius: 10px;
  min-width: 18px;
  text-align: center;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-role {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  background: rgba(255, 255, 255, 0.2);
}

.user-role.MERCHANT {
  background: #ffa502;
}

.user-role.RIDER {
  background: #2ed573;
}

.user-role.ADMIN {
  background: #ff4757;
}

.user-name {
  font-weight: 500;
}

.logout-btn {
  padding: 8px 16px;
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.logout-btn:hover {
  background: rgba(255, 255, 255, 0.3);
}

.main-content {
  flex: 1;
  background: #f5f7fa;
  padding: 24px;
}

.content-container {
  max-width: 1200px;
  margin: 0 auto;
}

.footer {
  background: #2d3436;
  color: rgba(255, 255, 255, 0.6);
  text-align: center;
  padding: 16px;
  font-size: 14px;
}
</style>

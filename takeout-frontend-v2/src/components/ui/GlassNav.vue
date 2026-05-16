<template>
  <nav class="glass-nav">
    <router-link
      v-for="item in navItems"
      :key="item.path"
      :to="item.path"
      class="nav-item"
      :class="{ active: isActive(item.path) }"
    >
      <span class="nav-icon">{{ item.icon }}</span>
      <span class="nav-label">{{ item.label }}</span>
      <span v-if="item.badge" class="nav-badge">{{ item.badge }}</span>
    </router-link>
  </nav>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useCartStore } from '@/stores/cart'

const route = useRoute()
const cart = useCartStore()

const navItems = computed(() => [
  { path: '/user/restaurants', icon: '🏠', label: '首页' },
  { path: '/user/orders', icon: '📋', label: '订单' },
  { path: '/user/cart', icon: '🛒', label: '购物车', badge: cart.totalCount > 0 ? cart.totalCount : null },
  { path: '/user/profile', icon: '👤', label: '我的' }
])

function isActive(path) {
  return route.path.startsWith(path)
}
</script>

<style scoped>
.glass-nav {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 100;
  display: flex;
  justify-content: space-around;
  padding: var(--space-2) 0;
  padding-bottom: max(var(--space-2), env(safe-area-inset-bottom));
  background: var(--glass-solid);
  border-top: 1px solid var(--glass-border);
  backdrop-filter: blur(24px);
  -webkit-backdrop-filter: blur(24px);
}

.nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
  padding: var(--space-2) var(--space-4);
  border-radius: var(--radius-md);
  text-decoration: none;
  color: var(--text-muted);
  transition: all var(--duration-fast);
  position: relative;
}

.nav-item.active {
  color: var(--accent);
}

.nav-item.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 20px;
  height: 2px;
  background: var(--gradient-green);
  border-radius: 1px;
}

.nav-icon {
  font-size: 1.2rem;
  line-height: 1;
}

.nav-label {
  font-size: var(--text-xs);
  font-weight: 500;
}

.nav-badge {
  position: absolute;
  top: 0;
  right: 4px;
  min-width: 16px;
  height: 16px;
  padding: 0 4px;
  background: var(--accent);
  color: var(--text-inverse);
  font-size: 10px;
  font-weight: 700;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  animation: pulse 2s ease-in-out infinite;
}
</style>

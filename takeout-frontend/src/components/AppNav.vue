<template>
  <nav class="app-nav">
    <router-link to="/" class="nav-item" :class="{ active: $route.path === '/' }">
      <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <path d="m3 9 9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/>
        <polyline points="9,22 9,12 15,12 15,22"/>
      </svg>
      <span class="nav-label">首页</span>
    </router-link>
    <router-link to="/user/restaurants" class="nav-item" :class="{ active: $route.path.startsWith('/user/restaurants') }">
      <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <path d="M3 3h18v18H3z"/><path d="M3 9h18"/><path d="M9 21V9"/>
      </svg>
      <span class="nav-label">商家</span>
    </router-link>
    <router-link to="/user/cart" class="nav-item" :class="{ active: $route.path.startsWith('/user/cart') }">
      <div class="icon-wrapper">
        <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <circle cx="9" cy="21" r="1"/><circle cx="20" cy="21" r="1"/>
          <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"/>
        </svg>
        <span v-if="cartStore.totalCount > 0" class="cart-badge">{{ cartStore.totalCount > 99 ? '99+' : cartStore.totalCount }}</span>
      </div>
      <span class="nav-label">购物车</span>
    </router-link>
    <router-link to="/user/orders" class="nav-item" :class="{ active: $route.path.startsWith('/user/orders') }">
      <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
        <polyline points="14,2 14,8 20,8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/>
      </svg>
      <span class="nav-label">订单</span>
    </router-link>
    <router-link to="/user/profile" class="nav-item" :class="{ active: $route.path.startsWith('/user/profile') }">
      <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/>
      </svg>
      <span class="nav-label">我的</span>
    </router-link>
  </nav>
</template>

<script setup>
import { useCartStore } from '@/stores/cart'
const cartStore = useCartStore()
</script>

<style scoped>
.app-nav {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: var(--nav-height);
  background: rgba(255, 253, 249, 0.95);
  backdrop-filter: blur(12px);
  display: flex;
  align-items: center;
  justify-content: space-around;
  box-shadow: 0 -1px 8px rgba(80, 60, 30, 0.06);
  z-index: 100;
  padding-bottom: env(safe-area-inset-bottom, 0);
}

.nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 2px;
  text-decoration: none;
  color: var(--color-text-hint);
  padding: 4px 12px;
  transition: color 0.2s;
  position: relative;
}

.nav-item.active {
  color: var(--color-primary);
}

.nav-item.active .nav-icon {
  stroke: var(--color-primary);
}

.nav-item.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 4px;
  height: 4px;
  border-radius: 50%;
  background: var(--color-primary);
}

.icon-wrapper {
  position: relative;
}

.nav-icon {
  width: 22px;
  height: 22px;
}

.nav-label {
  font-size: var(--font-size-xs);
  font-weight: 500;
}

.cart-badge {
  position: absolute;
  top: -6px;
  right: -10px;
  background: var(--color-error);
  color: #fff;
  font-size: 10px;
  font-weight: 600;
  padding: 1px 5px;
  border-radius: 10px;
  min-width: 16px;
  text-align: center;
  line-height: 1.4;
  animation: badgePulse 2s ease-in-out infinite;
}

@keyframes badgePulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.1); }
}

@media (min-width: 768px) {
  .app-nav {
    display: none;
  }
}
</style>

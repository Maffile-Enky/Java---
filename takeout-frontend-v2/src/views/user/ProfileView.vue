<template>
  <div class="profile-page">
    <div class="container">
      <h1 class="page-title">个人中心</h1>

      <!-- User Card -->
      <div class="user-card glass-panel">
        <div class="user-avatar">{{ (auth.nickname || '用户').charAt(0) }}</div>
        <div class="user-info">
          <h2 class="user-name">{{ auth.nickname || '未登录' }}</h2>
          <span class="user-role tag tag-green">{{ roleLabel }}</span>
        </div>
      </div>

      <!-- Menu -->
      <div class="profile-menu">
        <router-link to="/user/orders" class="menu-link glass-panel">
          <span class="menu-icon">📋</span>
          <span class="menu-text">我的订单</span>
          <span class="menu-arrow">→</span>
        </router-link>
        <router-link to="/user/address" class="menu-link glass-panel">
          <span class="menu-icon">📍</span>
          <span class="menu-text">收货地址</span>
          <span class="menu-arrow">→</span>
        </router-link>
        <router-link to="/user/notifications" class="menu-link glass-panel">
          <span class="menu-icon">🔔</span>
          <span class="menu-text">消息通知</span>
          <span class="menu-arrow">→</span>
        </router-link>
        <a v-if="auth.userInfo?.role === 'MERCHANT' || auth.userInfo?.role === 'ADMIN'" href="#" class="menu-link glass-panel" @click.prevent="$router.push('/merchant')">
          <span class="menu-icon">🏪</span>
          <span class="menu-text">商家后台</span>
          <span class="menu-arrow">→</span>
        </a>
        <a v-if="auth.userInfo?.role === 'ADMIN'" href="#" class="menu-link glass-panel" @click.prevent="$router.push('/admin')">
          <span class="menu-icon">⚙️</span>
          <span class="menu-text">管理后台</span>
          <span class="menu-arrow">→</span>
        </a>
        <a v-if="auth.userInfo?.role === 'RIDER' || auth.userInfo?.role === 'ADMIN'" href="#" class="menu-link glass-panel" @click.prevent="$router.push('/rider')">
          <span class="menu-icon">📦</span>
          <span class="menu-text">骑手中心</span>
          <span class="menu-arrow">→</span>
        </a>
      </div>

      <GlassButton variant="ghost" block @click="handleLogout" style="margin-top:24px;">
        退出登录
      </GlassButton>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import GlassButton from '@/components/ui/GlassButton.vue'

const router = useRouter()
const auth = useAuthStore()

const roleLabel = computed(() => {
  const map = { USER: '用户', MERCHANT: '商家', ADMIN: '管理员', RIDER: '骑手' }
  return map[auth.userInfo?.role] || '用户'
})

function handleLogout() {
  auth.logout()
  router.push('/')
}
</script>

<style scoped>
.profile-page {
  padding: var(--space-6) 0;
}

.page-title {
  font-family: var(--font-serif);
  font-size: 1.8rem;
  font-weight: 900;
  margin-bottom: var(--space-6);
}

.user-card {
  display: flex;
  align-items: center;
  gap: var(--space-4);
  padding: var(--space-6);
  margin-bottom: var(--space-6);
}

.user-avatar {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: var(--gradient-green);
  color: var(--text-inverse);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
  font-weight: 800;
  flex-shrink: 0;
}

.user-name {
  font-size: 1.2rem;
  font-weight: 700;
  margin-bottom: var(--space-1);
}

.profile-menu {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

.menu-link {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-4);
  text-decoration: none;
  color: var(--text-primary);
  transition: all var(--duration-fast);
}

.menu-link:hover {
  border-color: rgba(110, 231, 160, 0.2);
}

.menu-icon { font-size: 1.2rem; }
.menu-text { flex: 1; font-size: var(--text-sm); font-weight: 500; }
.menu-arrow { color: var(--text-muted); }
</style>

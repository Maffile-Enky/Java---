<template>
  <aside class="app-sidebar">
    <div class="sidebar-header">
      <router-link to="/" class="sidebar-logo">
        <span class="logo-text">味觉星球</span>
      </router-link>
      <span class="sidebar-badge tag tag-green">{{ roleLabel }}</span>
    </div>

    <nav class="sidebar-nav">
      <router-link
        v-for="item in navItems"
        :key="item.path"
        :to="item.path"
        class="sidebar-link"
        :class="{ active: isActive(item.path) }"
      >
        <span class="sidebar-icon">{{ item.icon }}</span>
        <span class="sidebar-label">{{ item.label }}</span>
      </router-link>
    </nav>

    <div class="sidebar-footer">
      <router-link to="/" class="sidebar-link">
        <span class="sidebar-icon">🏠</span>
        <span class="sidebar-label">返回首页</span>
      </router-link>
      <button class="sidebar-link" @click="handleLogout">
        <span class="sidebar-icon">🚪</span>
        <span class="sidebar-label">退出登录</span>
      </button>
    </div>
  </aside>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const props = defineProps({
  role: { type: String, default: 'merchant' } // merchant | admin | rider
})

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

const roleLabel = computed(() => {
  const map = { merchant: '商家', admin: '管理员', rider: '骑手' }
  return map[props.role] || props.role
})

const navMap = {
  merchant: [
    { path: '/merchant', icon: '📊', label: '仪表盘' },
    { path: '/merchant/dishes', icon: '🍜', label: '菜品管理' },
    { path: '/merchant/orders', icon: '📋', label: '订单管理' },
    { path: '/merchant/settings', icon: '⚙️', label: '店铺设置' }
  ],
  admin: [
    { path: '/admin', icon: '📊', label: '数据概览' },
    { path: '/admin/users', icon: '👥', label: '用户管理' },
    { path: '/admin/applications', icon: '📝', label: '入驻审核' },
    { path: '/admin/merchants', icon: '🏪', label: '商家管理' }
  ],
  rider: [
    { path: '/rider', icon: '📦', label: '任务大厅' },
    { path: '/rider/tasks', icon: '📋', label: '我的任务' }
  ]
}

const navItems = computed(() => navMap[props.role] || [])

function isActive(path) {
  if (path === `/admin` || path === `/merchant` || path === `/rider`) {
    return route.path === path
  }
  return route.path.startsWith(path)
}

function handleLogout() {
  auth.logout()
  router.push('/')
}
</script>

<style scoped>
.app-sidebar {
  position: fixed;
  top: 0;
  left: 0;
  bottom: 0;
  width: var(--sidebar-width);
  background: var(--glass-solid);
  border-right: 1px solid var(--glass-border);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  display: flex;
  flex-direction: column;
  z-index: 50;
  overflow-y: auto;
}

.sidebar-header {
  padding: var(--space-6);
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid var(--glass-border);
}

.sidebar-logo {
  text-decoration: none;
}

.sidebar-logo .logo-text {
  font-family: var(--font-serif);
  font-size: 1.1rem;
  font-weight: 900;
  background: var(--gradient-green);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.sidebar-nav {
  flex: 1;
  padding: var(--space-4);
  display: flex;
  flex-direction: column;
  gap: var(--space-1);
}

.sidebar-link {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-3) var(--space-4);
  border-radius: var(--radius-md);
  color: var(--text-secondary);
  text-decoration: none;
  font-size: var(--text-sm);
  transition: all var(--duration-fast);
  background: none;
  border: none;
  cursor: pointer;
  font-family: var(--font-sans);
  width: 100%;
  text-align: left;
}

.sidebar-link:hover {
  background: var(--glass);
  color: var(--text-primary);
}

.sidebar-link.active {
  background: rgba(110, 231, 160, 0.1);
  color: var(--accent);
  font-weight: 600;
}

.sidebar-icon {
  font-size: 1rem;
  line-height: 1;
  width: 20px;
  text-align: center;
}

.sidebar-footer {
  padding: var(--space-4);
  border-top: 1px solid var(--glass-border);
  display: flex;
  flex-direction: column;
  gap: var(--space-1);
}

@media (max-width: 768px) {
  .app-sidebar {
    width: 60px;
  }
  .sidebar-header { padding: var(--space-3); justify-content: center; }
  .sidebar-logo .logo-text, .sidebar-badge, .sidebar-label { display: none; }
  .sidebar-link { justify-content: center; padding: var(--space-3); }
  .sidebar-footer { padding: var(--space-2); }
}
</style>

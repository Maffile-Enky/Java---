<template>
  <div class="profile-view">
    <!-- 用户信息 -->
    <div class="user-info-section">
      <div class="user-avatar">
        <span class="avatar-emoji">👤</span>
      </div>
      <div class="user-details">
        <h2>{{ authStore.username || '未登录' }}</h2>
        <p class="user-role">{{ getRoleName(authStore.userRole) }}</p>
      </div>
    </div>

    <!-- 订单管理 -->
    <div class="order-management">
      <div class="section-header">
        <h3>订单管理</h3>
        <router-link to="/user/orders" class="view-all">查看全部</router-link>
      </div>
      <div class="order-types">
        <div class="order-type-item" @click="navigateToOrders('PENDING')">
          <div class="order-icon">⏳</div>
          <span>待支付</span>
        </div>
        <div class="order-type-item" @click="navigateToOrders('DELIVERING')">
          <div class="order-icon">🚚</div>
          <span>待配送</span>
        </div>
        <div class="order-type-item" @click="navigateToOrders('COMPLETED')">
          <div class="order-icon">✅</div>
          <span>已完成</span>
        </div>
        <div class="order-type-item" @click="navigateToOrders('CANCELLED')">
          <div class="order-icon">❌</div>
          <span>已取消</span>
        </div>
      </div>
    </div>

    <!-- 功能列表 -->
    <div class="feature-list">
      <div class="feature-item" v-for="item in featureItems" :key="item.label">
        <div class="feature-icon">{{ item.icon }}</div>
        <span>{{ item.label }}</span>
        <div class="feature-arrow">›</div>
      </div>
    </div>

    <!-- 退出登录按钮 -->
    <button class="logout-btn" @click="handleLogout">退出登录</button>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useCartStore } from '@/stores/cart'

const router = useRouter()
const authStore = useAuthStore()
const cartStore = useCartStore()

const featureItems = [
  { icon: '📍', label: '收货地址' },
  { icon: '🎁', label: '我的优惠' },
  { icon: '⭐', label: '我的收藏' },
  { icon: '💬', label: '客服中心' },
  { icon: '⚙️', label: '设置' }
]

function getRoleName(role) {
  const map = { USER: '用户', MERCHANT: '商家', RIDER: '骑手', ADMIN: '管理员' }
  return map[role] || role
}

function navigateToOrders(status) {
  router.push(`/user/orders?status=${status}`)
}

function handleLogout() {
  authStore.logout()
  cartStore.clearCart()
  router.push('/auth/login')
}
</script>

<style scoped>
.profile-view { padding-bottom: 20px; }
.user-info-section { display: flex; align-items: center; padding: 30px 20px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: #fff; border-radius: 12px; }
.user-avatar { width: 70px; height: 70px; border-radius: 50%; background: rgba(255,255,255,0.2); display: flex; align-items: center; justify-content: center; margin-right: 20px; }
.avatar-emoji { font-size: 36px; }
.user-details h2 { margin: 0 0 5px 0; font-size: 20px; font-weight: bold; }
.user-role { margin: 0; font-size: 14px; opacity: 0.8; }
.order-management { margin-top: 10px; padding: 15px 20px; background-color: #fff; border-radius: 12px; }
.section-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 15px; }
.section-header h3 { margin: 0; font-size: 16px; font-weight: bold; }
.view-all { font-size: 14px; color: #667eea; text-decoration: none; }
.order-types { display: flex; justify-content: space-around; }
.order-type-item { display: flex; flex-direction: column; align-items: center; cursor: pointer; transition: transform 0.2s; }
.order-type-item:hover { transform: scale(1.05); }
.order-icon { width: 50px; height: 50px; border-radius: 50%; background-color: #f5f5f5; display: flex; align-items: center; justify-content: center; font-size: 22px; margin-bottom: 5px; }
.order-type-item span { font-size: 12px; color: #666; }
.feature-list { margin-top: 10px; background-color: #fff; border-radius: 12px; overflow: hidden; }
.feature-item { display: flex; align-items: center; padding: 15px 20px; border-bottom: 1px solid #f0f0f0; cursor: pointer; transition: background 0.2s; }
.feature-item:hover { background: #f9f9f9; }
.feature-item:last-child { border-bottom: none; }
.feature-icon { font-size: 20px; margin-right: 15px; }
.feature-item span { flex: 1; font-size: 16px; }
.feature-arrow { font-size: 20px; color: #999; }
.logout-btn { width: 90%; margin: 20px auto; display: block; padding: 12px; border: 1px solid #ff6b00; border-radius: 25px; background-color: #fff; color: #ff6b00; font-size: 16px; font-weight: bold; cursor: pointer; transition: all 0.2s; }
.logout-btn:hover { background-color: #fff3e6; }
</style>

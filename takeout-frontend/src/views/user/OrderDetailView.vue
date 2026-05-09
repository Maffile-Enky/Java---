<template>
  <div class="order-detail-view">
    <div v-if="loading" class="loading">
      <div class="loading-spinner"></div>
    </div>

    <template v-else>
      <!-- Status banner -->
      <div class="status-banner" :class="'banner-' + order.status">
        <div class="status-icon">{{ statusIcon(order.status) }}</div>
        <div class="status-text">
          <h2>{{ statusText(order.status) }}</h2>
          <p>订单号：{{ order.id }}</p>
        </div>
      </div>

      <!-- Delivery info -->
      <div class="info-card card" v-if="order.deliveryAddress">
        <h3 class="card-title">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="18" height="18"><path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/><circle cx="12" cy="10" r="3"/></svg>
          配送信息
        </h3>
        <div class="info-row">
          <span class="info-label">收货人</span>
          <span class="info-value">{{ order.deliveryName || '-' }}</span>
        </div>
        <div class="info-row">
          <span class="info-label">联系电话</span>
          <span class="info-value">{{ order.deliveryPhone || '-' }}</span>
        </div>
        <div class="info-row">
          <span class="info-label">收货地址</span>
          <span class="info-value">{{ order.deliveryAddress }}</span>
        </div>
      </div>

      <!-- Merchant -->
      <div class="info-card card">
        <h3 class="card-title">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="18" height="18"><path d="M3 3h18v18H3z"/><path d="M3 9h18"/><path d="M9 21V9"/></svg>
          {{ order.merchantName }}
        </h3>
      </div>

      <!-- Items -->
      <div class="info-card card">
        <h3 class="card-title">订单商品</h3>
        <div v-for="item in order.items" :key="item.id" class="item-row">
          <span class="item-name">{{ item.name || item.dishName }}</span>
          <span class="item-qty">x{{ item.quantity }}</span>
          <span class="item-price">¥{{ (item.price * item.quantity).toFixed(2) }}</span>
        </div>
      </div>

      <!-- Price breakdown -->
      <div class="info-card card">
        <h3 class="card-title">订单金额</h3>
        <div class="info-row">
          <span class="info-label">商品金额</span>
          <span class="info-value">¥{{ order.subtotal || order.totalPrice }}</span>
        </div>
        <div class="info-row" v-if="order.deliveryFee">
          <span class="info-label">配送费</span>
          <span class="info-value">¥{{ order.deliveryFee }}</span>
        </div>
        <div class="info-row total-row">
          <span class="info-label">实付金额</span>
          <span class="info-value total-price">¥{{ order.totalPrice }}</span>
        </div>
      </div>

      <!-- Order info -->
      <div class="info-card card">
        <h3 class="card-title">订单信息</h3>
        <div class="info-row">
          <span class="info-label">订单编号</span>
          <span class="info-value">{{ order.id }}</span>
        </div>
        <div class="info-row">
          <span class="info-label">下单时间</span>
          <span class="info-value">{{ order.createTime }}</span>
        </div>
      </div>

      <!-- Actions -->
      <div class="action-bar">
        <button class="btn-outline" @click="router.push('/user/orders')">返回列表</button>
        <button v-if="order.status === 'PENDING'" class="btn-primary" @click="goPayment">去支付</button>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getOrderDetail } from '@/api/order'

const router = useRouter()
const route = useRoute()
const loading = ref(true)
const order = ref({})

function statusText(status) {
  const map = { PENDING: '待支付', PAID: '已支付', CONFIRMED: '已确认', DELIVERING: '配送中', COMPLETED: '已完成', CANCELLED: '已取消' }
  return map[status] || status || '未知'
}

function statusIcon(status) {
  const map = { PENDING: '⏳', PAID: '💳', CONFIRMED: '✓', DELIVERING: '🚚', COMPLETED: '✓', CANCELLED: '✕' }
  return map[status] || '?'
}

function goPayment() {
  router.push({
    path: '/user/payment',
    query: {
      orderNo: order.value.orderNo,
      amount: order.value.totalPrice,
      subject: order.value.merchantName + ' - 外卖订单'
    }
  })
}

onMounted(async () => {
  const id = route.params.id
  try {
    const res = await getOrderDetail(id)
    order.value = res.data || res || {}
  } catch {
    order.value = {}
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.order-detail-view {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
  padding-bottom: 80px;
}

.loading {
  display: flex;
  justify-content: center;
  padding: 80px;
}

.loading-spinner {
  width: 32px;
  height: 32px;
  border: 3px solid var(--color-border);
  border-top-color: var(--color-primary);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin { to { transform: rotate(360deg); } }

/* Status banner */
.status-banner {
  display: flex;
  align-items: center;
  gap: var(--spacing-lg);
  padding: var(--spacing-xl);
  border-radius: var(--radius-lg);
  color: #fff;
}

.banner-PENDING { background: linear-gradient(135deg, #f093fb, #f5576c); }
.banner-PAID { background: linear-gradient(135deg, #a18cd1, #fbc2eb); }
.banner-CONFIRMED { background: linear-gradient(135deg, #a18cd1, #fbc2eb); }
.banner-DELIVERING { background: linear-gradient(135deg, #4facfe, #00f2fe); }
.banner-COMPLETED { background: linear-gradient(135deg, #43e97b, #38f9d7); }
.banner-CANCELLED { background: linear-gradient(135deg, #a8a8a8, #d0d0d0); }

.status-icon {
  width: 48px;
  height: 48px;
  border-radius: var(--radius-full);
  background: rgba(255,255,255,0.25);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  flex-shrink: 0;
}

.status-text h2 {
  margin: 0 0 4px 0;
  font-size: var(--font-size-lg);
}

.status-text p {
  margin: 0;
  font-size: var(--font-size-sm);
  opacity: 0.85;
}

/* Info cards */
.info-card {
  padding: var(--spacing-lg);
}

.card-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: var(--font-size-md);
  font-weight: 600;
  margin: 0 0 var(--spacing-md) 0;
  padding-bottom: var(--spacing-md);
  border-bottom: 1px solid var(--color-divider);
}

.card-title svg {
  color: var(--color-text-hint);
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 6px 0;
  font-size: var(--font-size-sm);
}

.info-label {
  color: var(--color-text-hint);
}

.info-value {
  color: var(--color-text-primary);
}

.total-row {
  padding-top: var(--spacing-md);
  margin-top: var(--spacing-sm);
  border-top: 1px solid var(--color-divider);
}

.total-price {
  color: var(--color-accent);
  font-size: var(--font-size-md);
  font-weight: 700;
}

.item-row {
  display: flex;
  align-items: center;
  padding: 6px 0;
  font-size: var(--font-size-sm);
}

.item-name {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.item-qty {
  margin: 0 var(--spacing-md);
  color: var(--color-text-hint);
}

.item-price {
  color: var(--color-accent);
  font-weight: 500;
}

/* Actions */
.action-bar {
  display: flex;
  gap: var(--spacing-md);
  padding: var(--spacing-lg);
  background: var(--color-bg-card);
  border-radius: var(--radius-lg);
  justify-content: flex-end;
}

.btn-primary {
  padding: 8px 24px;
  background: var(--color-accent);
  color: #fff;
  border: none;
  border-radius: var(--radius-xl);
  font-size: var(--font-size-sm);
  font-weight: 600;
  cursor: pointer;
}
</style>

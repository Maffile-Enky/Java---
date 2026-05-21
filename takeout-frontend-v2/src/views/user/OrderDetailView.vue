<template>
  <div class="order-detail-page">
    <div class="container">
      <LoadingSpinner v-if="loading" text="加载中..." />

      <template v-else-if="order">
        <div class="detail-header">
          <button class="back-btn" @click="$router.back()">← 返回</button>
          <h1 class="page-title">订单详情</h1>
        </div>

        <!-- Status -->
        <div class="status-card glass-panel">
          <OrderStatusBadge :status="order.status" />
          <p class="status-desc">{{ statusDesc }}</p>
        </div>

        <!-- Items -->
        <div class="section glass-panel">
          <h2 class="section-title">订单商品</h2>
          <div v-for="item in order.items" :key="item.id" class="detail-item">
            <span class="di-name">{{ item.dishName || item.name }}</span>
            <span class="di-qty">x{{ item.quantity }}</span>
            <span class="di-price">¥{{ Number(item.price * item.quantity).toFixed(2) }}</span>
          </div>
          <div class="detail-divider"></div>
          <div class="detail-row">
            <span>配送费</span>
            <span>¥5.00</span>
          </div>
          <div class="detail-row total-row">
            <span>合计</span>
            <span class="total-price">¥{{ Number(order.totalPrice || 0).toFixed(2) }}</span>
          </div>
        </div>

        <!-- Address -->
        <div class="section glass-panel">
          <h2 class="section-title">配送信息</h2>
          <p class="info-text">{{ order.deliveryAddress || '暂无地址信息' }}</p>
          <p class="info-text">{{ order.deliveryPhone }}</p>
        </div>

        <!-- Actions -->
        <div class="detail-actions">
          <GlassButton v-if="order.status === 'PENDING'" variant="primary" block @click="payOrder">
            去支付
          </GlassButton>
          <GlassButton v-if="order.status === 'PENDING'" variant="ghost" block @click="cancelOrder">
            取消订单
          </GlassButton>
          <GlassButton v-if="order.status === 'DELIVERED'" variant="primary" block @click="confirmOrder">
            确认收货
          </GlassButton>
          <GlassButton v-if="['DELIVERING', 'DELIVERED'].includes(order.status)" variant="ghost" block @click="$router.push(`/user/orders/${order.id}/track`)">
            查看配送
          </GlassButton>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getOrderDetail, cancelOrder as cancelApi, updateOrderStatus } from '@/api/order'
import OrderStatusBadge from '@/components/common/OrderStatusBadge.vue'
import LoadingSpinner from '@/components/common/LoadingSpinner.vue'
import GlassButton from '@/components/ui/GlassButton.vue'

const route = useRoute()
const router = useRouter()
const order = ref(null)
const loading = ref(true)

const statusDescMap = {
  PENDING: '请尽快完成支付',
  PAID: '商家正在准备中',
  PREPARING: '厨师正在精心烹制',
  READY: '骑手即将取餐',
  DELIVERING: '骑手正在飞速赶来',
  DELIVERED: '请确认收货',
  COMPLETED: '感谢您的订购',
  CANCELLED: '订单已取消',
  REFUNDED: '退款已完成'
}

const statusDesc = computed(() => statusDescMap[order.value?.status] || '')

async function fetchOrder() {
  try {
    const res = await getOrderDetail(route.params.id)
    order.value = res.data
  } catch { order.value = null }
  finally { loading.value = false }
}

async function cancelOrder() {
  if (!confirm('确定取消订单？')) return
  try {
    await cancelApi(route.params.id)
    fetchOrder()
  } catch {}
}

async function confirmOrder() {
  try {
    await updateOrderStatus(route.params.id, 'COMPLETED')
    fetchOrder()
  } catch {}
}

function payOrder() {
  router.push(`/user/payment/${route.params.id}`)
}

onMounted(fetchOrder)
</script>

<style scoped>
.order-detail-page {
  padding: var(--space-6) 0;
}

.detail-header {
  display: flex;
  align-items: center;
  gap: var(--space-4);
  margin-bottom: var(--space-6);
}

.back-btn {
  background: none;
  border: none;
  color: var(--text-secondary);
  font-size: var(--text-sm);
  cursor: pointer;
  font-family: var(--font-sans);
}

.back-btn:hover { color: var(--accent); }

.page-title {
  font-family: var(--font-serif);
  font-size: 1.8rem;
  font-weight: 900;
}

.status-card {
  padding: var(--space-6);
  margin-bottom: var(--space-4);
  display: flex;
  align-items: center;
  gap: var(--space-4);
}

.status-desc {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  margin: 0;
}

.section {
  padding: var(--space-6);
  margin-bottom: var(--space-4);
}

.section-title {
  font-size: var(--text-base);
  font-weight: 700;
  margin-bottom: var(--space-4);
}

.detail-item {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-2) 0;
  font-size: var(--text-sm);
}

.di-name { flex: 1; }
.di-qty { color: var(--text-muted); }
.di-price { font-weight: 600; color: var(--text-primary); }

.detail-divider {
  height: 1px;
  background: var(--glass-border);
  margin: var(--space-3) 0;
}

.detail-row {
  display: flex;
  justify-content: space-between;
  font-size: var(--text-sm);
  color: var(--text-secondary);
  padding: var(--space-1) 0;
}

.total-row {
  font-weight: 700;
  color: var(--text-primary);
  font-size: 1rem;
}

.total-price {
  color: var(--accent);
  font-size: 1.2rem;
  font-weight: 800;
}

.info-text {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  margin: 0 0 var(--space-1);
}

.detail-actions {
  display: flex;
  flex-direction: column;
  gap: var(--space-3);
  margin-top: var(--space-6);
}
</style>

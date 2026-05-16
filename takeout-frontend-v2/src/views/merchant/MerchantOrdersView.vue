<template>
  <div class="merchant-orders">
    <h1 class="page-title">订单管理</h1>

    <div class="order-tabs">
      <button
        v-for="tab in tabs"
        :key="tab.value"
        class="tab-btn"
        :class="{ active: activeTab === tab.value }"
        @click="activeTab = tab.value"
      >{{ tab.label }}</button>
    </div>

    <LoadingSpinner v-if="loading" text="加载订单..." />

    <EmptyState v-else-if="!filteredOrders.length" icon="📋" text="暂无订单" />

    <div v-else class="order-list">
      <div v-for="order in filteredOrders" :key="order.id" class="order-card glass-panel">
        <div class="order-header">
          <span class="order-id">#{{ order.id }}</span>
          <OrderStatusBadge :status="order.status" />
        </div>
        <div class="order-items">
          <div v-for="item in (order.items || [])" :key="item.id" class="item-row">
            <span>{{ item.dishName || item.name }}</span>
            <span class="item-qty">x{{ item.quantity }}</span>
          </div>
        </div>
        <div class="order-footer">
          <span class="order-total">¥{{ Number(order.totalAmount || 0).toFixed(2) }}</span>
          <span class="order-time">{{ formatDate(order.createTime) }}</span>
        </div>
        <div class="order-actions" v-if="order.status === 'PAID'">
          <GlassButton variant="primary" size="sm" @click="updateStatus(order.id, 'PREPARING')">
            开始备餐
          </GlassButton>
        </div>
        <div class="order-actions" v-if="order.status === 'PREPARING'">
          <GlassButton variant="primary" size="sm" @click="updateStatus(order.id, 'READY')">
            备餐完成
          </GlassButton>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getMerchantOrderList } from '@/api/order'
import OrderStatusBadge from '@/components/common/OrderStatusBadge.vue'
import LoadingSpinner from '@/components/common/LoadingSpinner.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import GlassButton from '@/components/ui/GlassButton.vue'
import dayjs from 'dayjs'

const orders = ref([])
const loading = ref(true)
const activeTab = ref('ALL')

const tabs = [
  { label: '全部', value: 'ALL' },
  { label: '新订单', value: 'PAID' },
  { label: '备餐中', value: 'PREPARING' },
  { label: '待取餐', value: 'READY' },
  { label: '已完成', value: 'COMPLETED' }
]

const filteredOrders = computed(() => {
  if (activeTab.value === 'ALL') return orders.value
  return orders.value.filter(o => o.status === activeTab.value)
})

function formatDate(val) {
  return val ? dayjs(val).format('MM-DD HH:mm') : ''
}

async function updateStatus(orderId, status) {
  try {
    const { updateOrderStatus } = await import('@/api/order')
    await updateOrderStatus(orderId, status)
    fetchOrders()
  } catch {}
}

async function fetchOrders() {
  try {
    const res = await getMerchantOrderList()
    orders.value = res.data || []
  } catch { orders.value = [] }
  finally { loading.value = false }
}

onMounted(fetchOrders)
</script>

<style scoped>
.merchant-orders {
  max-width: 1200px;
}

.page-title {
  font-family: var(--font-serif);
  font-size: 1.8rem;
  font-weight: 900;
  margin-bottom: var(--space-6);
}

.order-tabs {
  display: flex;
  gap: var(--space-2);
  margin-bottom: var(--space-6);
  overflow-x: auto;
}

.tab-btn {
  padding: var(--space-2) var(--space-4);
  background: var(--glass);
  border: 1px solid var(--glass-border);
  border-radius: var(--radius-full);
  color: var(--text-secondary);
  font-size: var(--text-sm);
  cursor: pointer;
  white-space: nowrap;
  transition: all var(--duration-fast);
  font-family: var(--font-sans);
}

.tab-btn:hover { border-color: var(--accent); color: var(--text-primary); }

.tab-btn.active {
  background: var(--gradient-green);
  color: var(--text-inverse);
  border-color: transparent;
  font-weight: 600;
}

.order-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-3);
}

.order-card {
  padding: var(--space-5);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-3);
}

.order-id {
  font-weight: 600;
  font-size: var(--text-xs);
  color: var(--text-muted);
}

.order-items {
  margin-bottom: var(--space-3);
}

.item-row {
  display: flex;
  justify-content: space-between;
  font-size: var(--text-sm);
  color: var(--text-secondary);
  padding: 2px 0;
}

.item-qty { color: var(--text-muted); }

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: var(--space-3);
  border-top: 1px solid var(--glass-border);
  margin-bottom: var(--space-3);
}

.order-total {
  font-weight: 700;
  color: var(--accent);
  font-size: 1.1rem;
}

.order-time {
  font-size: var(--text-xs);
  color: var(--text-muted);
}

.order-actions {
  display: flex;
  gap: var(--space-2);
  justify-content: flex-end;
}
</style>

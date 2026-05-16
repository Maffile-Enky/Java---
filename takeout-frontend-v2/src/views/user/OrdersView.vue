<template>
  <div class="orders-page">
    <div class="container">
      <h1 class="page-title">我的订单</h1>

      <!-- Tab filters -->
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
        <div
          v-for="order in filteredOrders"
          :key="order.id"
          class="order-card glass-panel"
          @click="$router.push(`/user/orders/${order.id}`)"
        >
          <div class="order-header">
            <span class="order-merchant">🏪 {{ order.merchantName || '商家' }}</span>
            <OrderStatusBadge :status="order.status" />
          </div>
          <div class="order-items">
            <div v-for="item in (order.items || []).slice(0, 3)" :key="item.id" class="order-item-row">
              <span class="item-name">{{ item.dishName || item.name }}</span>
              <span class="item-qty">x{{ item.quantity }}</span>
            </div>
            <span v-if="(order.items || []).length > 3" class="more-items">
              等{{ order.items.length }}件商品
            </span>
          </div>
          <div class="order-footer">
            <span class="order-total">
              合计: <strong>¥{{ Number(order.totalAmount || 0).toFixed(2) }}</strong>
            </span>
            <span class="order-time">{{ formatDate(order.createTime) }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getOrderList } from '@/api/order'
import OrderStatusBadge from '@/components/common/OrderStatusBadge.vue'
import LoadingSpinner from '@/components/common/LoadingSpinner.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import dayjs from 'dayjs'

const orders = ref([])
const loading = ref(true)
const activeTab = ref('ALL')

const tabs = [
  { label: '全部', value: 'ALL' },
  { label: '待支付', value: 'PENDING' },
  { label: '进行中', value: 'ACTIVE' },
  { label: '已完成', value: 'COMPLETED' }
]

const filteredOrders = computed(() => {
  if (activeTab.value === 'ALL') return orders.value
  if (activeTab.value === 'ACTIVE') {
    return orders.value.filter(o => ['PAID', 'PREPARING', 'READY', 'DELIVERING'].includes(o.status))
  }
  if (activeTab.value === 'COMPLETED') {
    return orders.value.filter(o => ['COMPLETED', 'CANCELLED', 'REFUNDED'].includes(o.status))
  }
  return orders.value.filter(o => o.status === activeTab.value)
})

function formatDate(val) {
  return val ? dayjs(val).format('MM-DD HH:mm') : ''
}

onMounted(async () => {
  try {
    const res = await getOrderList()
    orders.value = res.data || []
  } catch { orders.value = [] }
  finally { loading.value = false }
})
</script>

<style scoped>
.orders-page {
  padding: var(--space-6) 0;
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
  gap: var(--space-4);
}

.order-card {
  padding: var(--space-5);
  cursor: pointer;
  transition: all var(--duration-fast);
}

.order-card:hover {
  border-color: rgba(110, 231, 160, 0.2);
  transform: translateY(-1px);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-3);
}

.order-merchant {
  font-weight: 600;
  font-size: var(--text-base);
}

.order-items {
  margin-bottom: var(--space-3);
}

.order-item-row {
  display: flex;
  justify-content: space-between;
  font-size: var(--text-sm);
  color: var(--text-secondary);
  padding: 2px 0;
}

.item-qty { color: var(--text-muted); }

.more-items {
  font-size: var(--text-xs);
  color: var(--text-muted);
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: var(--space-3);
  border-top: 1px solid var(--glass-border);
}

.order-total {
  font-size: var(--text-sm);
  color: var(--text-secondary);
}

.order-total strong {
  color: var(--accent);
  font-weight: 800;
  font-size: 1.1rem;
}

.order-time {
  font-size: var(--text-xs);
  color: var(--text-muted);
}
</style>

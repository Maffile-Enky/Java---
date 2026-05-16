<template>
  <div class="dashboard">
    <h1 class="page-title">商家仪表盘</h1>

    <!-- Stats -->
    <div class="stat-grid">
      <div v-for="stat in stats" :key="stat.label" class="stat-card glass-panel">
        <span class="stat-icon">{{ stat.icon }}</span>
        <div class="stat-info">
          <span class="stat-value">{{ stat.value }}</span>
          <span class="stat-label">{{ stat.label }}</span>
        </div>
      </div>
    </div>

    <!-- Recent Orders -->
    <div class="section">
      <h2 class="section-title">最近订单</h2>
      <LoadingSpinner v-if="loading" size="sm" />
      <EmptyState v-else-if="!recentOrders.length" icon="📋" text="暂无订单" />
      <div v-else class="order-table">
        <div v-for="order in recentOrders" :key="order.id" class="table-row glass-panel">
          <span class="row-id">#{{ order.id }}</span>
          <span class="row-items">{{ orderItemCount(order) }}件</span>
          <span class="row-price">¥{{ Number(order.totalAmount || 0).toFixed(2) }}</span>
          <OrderStatusBadge :status="order.status" />
          <span class="row-time">{{ formatDate(order.createTime) }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMerchantOrderList } from '@/api/order'
import OrderStatusBadge from '@/components/common/OrderStatusBadge.vue'
import LoadingSpinner from '@/components/common/LoadingSpinner.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import dayjs from 'dayjs'

const recentOrders = ref([])
const loading = ref(true)

const stats = [
  { icon: '📦', label: '今日订单', value: '—' },
  { icon: '💰', label: '今日收入', value: '—' },
  { icon: '⭐', label: '店铺评分', value: '—' },
  { icon: '📊', label: '月销量', value: '—' }
]

function orderItemCount(order) {
  return (order.items || []).reduce((sum, item) => sum + (item.quantity || 1), 0)
}

function formatDate(val) {
  return val ? dayjs(val).format('MM-DD HH:mm') : ''
}

onMounted(async () => {
  try {
    const res = await getMerchantOrderList()
    recentOrders.value = (res.data || []).slice(0, 10)
  } catch { recentOrders.value = [] }
  finally { loading.value = false }
})
</script>

<style scoped>
.dashboard {
  max-width: 1200px;
}

.page-title {
  font-family: var(--font-serif);
  font-size: 1.8rem;
  font-weight: 900;
  margin-bottom: var(--space-8);
}

.stat-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: var(--space-4);
  margin-bottom: var(--space-8);
}

.stat-card {
  display: flex;
  align-items: center;
  gap: var(--space-4);
  padding: var(--space-5);
}

.stat-icon { font-size: 2rem; }
.stat-value { font-size: 1.5rem; font-weight: 800; color: var(--accent); display: block; }
.stat-label { font-size: var(--text-xs); color: var(--text-muted); }

.section {
  margin-bottom: var(--space-8);
}

.section-title {
  font-size: 1.2rem;
  font-weight: 700;
  margin-bottom: var(--space-4);
}

.order-table {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

.table-row {
  display: grid;
  grid-template-columns: 80px 60px 100px auto 120px;
  align-items: center;
  gap: var(--space-4);
  padding: var(--space-3) var(--space-4);
  font-size: var(--text-sm);
}

.row-id { font-weight: 600; color: var(--text-muted); font-size: var(--text-xs); }
.row-items { color: var(--text-secondary); }
.row-price { font-weight: 700; color: var(--accent); }
.row-time { color: var(--text-muted); font-size: var(--text-xs); text-align: right; }
</style>

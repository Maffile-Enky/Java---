<template>
  <div class="orders-view">
    <!-- Tab bar -->
    <div class="tabs card">
      <button
        v-for="f in filters"
        :key="f.value"
        class="tab"
        :class="{ active: activeFilter === f.value }"
        @click="activeFilter = f.value"
      >{{ f.label }}</button>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="loading">
      <div class="loading-spinner"></div>
    </div>

    <!-- Order list -->
    <div v-else-if="filteredOrders.length > 0" class="order-list">
      <div v-for="order in filteredOrders" :key="order.id" class="order-card card">
        <div class="order-header">
          <span class="order-merchant">{{ order.merchantName }}</span>
          <span class="order-status" :class="'status-' + order.status">{{ statusText(order.status) }}</span>
        </div>
        <div class="order-items">
          <div v-for="item in order.items" :key="item.id" class="order-item-row">
            <span class="oi-name">{{ item.name || item.dishName }}</span>
            <span class="oi-qty">x{{ item.quantity }}</span>
            <span class="oi-price">¥{{ item.price }}</span>
          </div>
        </div>
        <div class="order-footer">
          <span class="order-total">合计：¥{{ order.totalPrice }}</span>
          <div class="order-actions">
            <button v-if="order.status === 'PENDING'" class="btn-sm btn-primary" @click="handlePay(order.id)">立即支付</button>
            <button v-if="order.status === 'PENDING'" class="btn-sm btn-outline" @click="handleCancel(order.id)">取消订单</button>
            <button class="btn-sm btn-outline" @click="goDetail(order.id)">查看详情</button>
          </div>
        </div>
      </div>
    </div>

    <!-- Empty -->
    <div v-else class="empty-state">
      <img src="/images/empty-states/empty-orders.svg" alt="暂无订单" class="empty-img" />
      <p>暂无订单</p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getOrderList, cancelOrder } from '@/api/order'
import { createPayment } from '@/api/payment'

const router = useRouter()
const route = useRoute()
const loading = ref(true)
const activeFilter = ref(route.query.status || 'all')
const orders = ref([])

const filters = [
  { label: '全部', value: 'all' },
  { label: '待支付', value: 'PENDING' },
  { label: '已支付', value: 'PAID' },
  { label: '待配送', value: 'DELIVERING' },
  { label: '已完成', value: 'COMPLETED' },
  { label: '已取消', value: 'CANCELLED' }
]

const filteredOrders = computed(() => {
  if (activeFilter.value === 'all') return orders.value
  return orders.value.filter(o => o.status === activeFilter.value)
})

function statusText(status) {
  const map = { PENDING: '待支付', PAID: '已支付', CONFIRMED: '已确认', DELIVERING: '待配送', COMPLETED: '已完成', CANCELLED: '已取消' }
  return map[status] || status
}

function goDetail(id) {
  router.push(`/user/orders/${id}`)
}

async function handlePay(id) {
  const order = orders.value.find(o => o.id === id)
  if (!order) return
  router.push({
    path: '/user/payment',
    query: {
      orderNo: order.orderNo,
      amount: order.totalPrice,
      subject: order.merchantName + ' - 外卖订单'
    }
  })
}

async function handleCancel(id) {
  if (!confirm('确定取消该订单？')) return
  try {
    await cancelOrder(id)
    const order = orders.value.find(o => o.id === id)
    if (order) order.status = 'CANCELLED'
  } catch {
    alert('取消失败')
  }
}

onMounted(async () => {
  try {
    const res = await getOrderList()
    const page = res.data || res
    orders.value = page.records || page || []
  } catch {
    orders.value = []
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.orders-view {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

/* Tabs */
.tabs {
  display: flex;
  overflow-x: auto;
  padding: 0;
}

.tab {
  flex: 1;
  padding: 14px 0;
  border: none;
  background: none;
  font-size: var(--font-size-base);
  color: var(--color-text-hint);
  cursor: pointer;
  border-bottom: 2px solid transparent;
  transition: all 0.2s;
  white-space: nowrap;
}

.tab.active {
  color: var(--color-primary);
  font-weight: 600;
  border-bottom-color: var(--color-primary);
}

/* Loading */
.loading {
  display: flex;
  justify-content: center;
  padding: 60px;
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

/* Order list */
.order-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.order-card {
  padding: var(--spacing-lg);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-md);
}

.order-merchant {
  font-family: var(--font-heading);
  font-size: var(--font-size-md);
  font-weight: 600;
}

.order-status {
  font-size: var(--font-size-xs);
  padding: 3px 10px;
  border-radius: 12px;
  font-weight: 500;
}

.status-PENDING { background: #FFF8EC; color: #C88A2A; }
.status-PAID { background: #EDF3F8; color: #5B8DB8; }
.status-CONFIRMED { background: #EDF3F8; color: #5B8DB8; }
.status-DELIVERING { background: #EDF3F8; color: #4A7A9E; }
.status-COMPLETED { background: #F0F5EC; color: #4A8C5C; }
.status-CANCELLED { background: #F5EDEB; color: #B85050; }

.order-items {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: var(--spacing-md);
}

.order-item-row {
  display: flex;
  align-items: center;
  font-size: var(--font-size-sm);
}

.oi-name {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.oi-qty {
  margin: 0 var(--spacing-md);
  color: var(--color-text-hint);
}

.oi-price {
  color: var(--color-text-secondary);
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: var(--spacing-md);
  border-top: 1px solid var(--color-divider);
}

.order-total {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
}

.order-actions {
  display: flex;
  gap: var(--spacing-sm);
}

.btn-sm {
  padding: 5px 14px;
  border-radius: var(--radius-xl);
  font-size: var(--font-size-xs);
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-sm.btn-primary {
  background: var(--color-primary);
  color: #fff;
  border: none;
}

.btn-sm.btn-outline {
  background: var(--color-bg-card);
  color: var(--color-text-secondary);
  border: 1px solid var(--color-border);
}

.btn-sm:hover {
  opacity: 0.85;
}

/* Empty */
.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: var(--color-text-hint);
}

.empty-img {
  width: 200px;
  margin: 0 auto 16px;
  opacity: 0.6;
}
</style>

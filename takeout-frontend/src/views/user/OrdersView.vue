<template>
  <div class="orders-view">
    <h1>我的订单</h1>

    <!-- 订单状态筛选 -->
    <div class="order-filters">
      <button
        v-for="filter in filters"
        :key="filter.value"
        class="filter-btn"
        :class="{ active: activeFilter === filter.value }"
        @click="activeFilter = filter.value"
      >
        {{ filter.label }}
      </button>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading">加载中...</div>

    <!-- 订单列表 -->
    <div v-else-if="filteredOrders.length > 0" class="order-list">
      <div v-for="order in filteredOrders" :key="order.id" class="order-item">
        <div class="order-header">
          <span class="order-merchant">{{ order.merchantName }}</span>
          <span class="order-status" :class="statusClass(order.status)">{{ statusText(order.status) }}</span>
        </div>
        <div class="order-content">
          <div v-for="item in order.items" :key="item.id" class="order-item-detail">
            <span class="item-name">{{ item.name || item.dishName }}</span>
            <span class="item-quantity">x{{ item.quantity }}</span>
            <span class="item-price">¥{{ item.price }}</span>
          </div>
        </div>
        <div class="order-footer">
          <span class="order-total">合计：¥{{ order.totalPrice }}</span>
          <div class="order-actions">
            <button
              v-if="order.status === 'PENDING'"
              class="action-btn primary"
              @click="handlePay(order.id)"
            >立即支付</button>
            <button
              v-if="order.status === 'PENDING'"
              class="action-btn secondary"
              @click="handleCancel(order.id)"
            >取消订单</button>
            <button
              class="action-btn secondary"
              @click="goDetail(order.id)"
            >查看详情</button>
          </div>
        </div>
        <div class="order-divider"></div>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-else class="empty-state">
      <span class="empty-emoji">📋</span>
      <p>暂无订单</p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getOrderList, cancelOrder } from '@/api/order'

const router = useRouter()
const route = useRoute()
const loading = ref(true)
const activeFilter = ref(route.query.status || 'all')
const orders = ref([])

const filters = [
  { label: '全部', value: 'all' },
  { label: '待支付', value: 'PENDING' },
  { label: '待配送', value: 'DELIVERING' },
  { label: '已完成', value: 'COMPLETED' },
  { label: '已取消', value: 'CANCELLED' }
]

const filteredOrders = computed(() => {
  if (activeFilter.value === 'all') return orders.value
  return orders.value.filter(o => o.status === activeFilter.value)
})

function statusText(status) {
  const map = { PENDING: '待支付', DELIVERING: '待配送', COMPLETED: '已完成', CANCELLED: '已取消' }
  return map[status] || status
}

function statusClass(status) {
  const map = { PENDING: 'status-pending', DELIVERING: 'status-processing', COMPLETED: 'status-completed', CANCELLED: 'status-cancelled' }
  return map[status] || ''
}

function goDetail(id) {
  router.push(`/user/orders/${id}`)
}

async function handlePay(id) {
  // TODO: integrate with payment-service
  alert('支付功能暂未开放')
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
    orders.value = res.data || []
  } catch {
    // Fallback mock data
    orders.value = [
      {
        id: '100001', merchantName: '示例餐厅', status: 'COMPLETED', totalPrice: 66,
        items: [
          { id: 1, name: '宫保鸡丁', quantity: 2, price: 28 },
          { id: 2, name: '麻婆豆腐', quantity: 1, price: 22 }
        ]
      },
      {
        id: '100002', merchantName: '示例餐厅', status: 'PENDING', totalPrice: 36,
        items: [
          { id: 1, name: '宫保鸡丁', quantity: 1, price: 28 },
          { id: 4, name: '可乐', quantity: 1, price: 8 }
        ]
      }
    ]
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.orders-view { padding-bottom: 20px; }
h1 { padding: 20px; margin: 0; font-size: 24px; font-weight: bold; background-color: #fff; border-bottom: 1px solid #f0f0f0; border-radius: 12px; }
.order-filters { display: flex; background-color: #fff; margin-top: 10px; border-bottom: 1px solid #f0f0f0; overflow-x: auto; border-radius: 12px; }
.filter-btn { flex: 1; padding: 15px 0; border: none; background: none; font-size: 14px; color: #666; cursor: pointer; border-bottom: 2px solid transparent; transition: all 0.2s; }
.filter-btn.active { color: #ff6b00; border-bottom-color: #ff6b00; font-weight: bold; }
.loading { text-align: center; padding: 40px; color: #999; }
.order-list { margin-top: 10px; background-color: #fff; border-radius: 12px; overflow: hidden; }
.order-item { padding: 15px 20px; }
.order-header { display: flex; justify-content: space-between; margin-bottom: 10px; }
.order-merchant { font-size: 16px; font-weight: bold; }
.order-status { font-size: 14px; padding: 2px 8px; border-radius: 10px; }
.status-pending { background-color: #fff3cd; color: #856404; }
.status-processing { background-color: #cce7ff; color: #004085; }
.status-completed { background-color: #d4edda; color: #155724; }
.status-cancelled { background-color: #f8d7da; color: #721c24; }
.order-content { margin-bottom: 10px; }
.order-item-detail { display: flex; justify-content: space-between; margin-bottom: 5px; font-size: 14px; }
.item-name { flex: 1; }
.item-quantity { margin: 0 10px; color: #999; }
.order-footer { display: flex; justify-content: space-between; align-items: center; margin-top: 10px; }
.order-total { font-size: 14px; color: #666; }
.order-actions { display: flex; gap: 10px; }
.action-btn { padding: 5px 12px; border: 1px solid #e0e0e0; border-radius: 15px; background-color: #fff; font-size: 12px; cursor: pointer; transition: all 0.2s; }
.action-btn.primary { background-color: #ff6b00; color: #fff; border-color: #ff6b00; }
.action-btn.secondary { color: #666; }
.action-btn:hover { opacity: 0.8; }
.order-divider { height: 10px; background-color: #f5f5f5; margin: 15px -20px 0 -20px; }
.empty-state { text-align: center; padding: 60px 20px; color: #b2bec3; }
.empty-emoji { font-size: 64px; display: block; margin-bottom: 16px; }
</style>

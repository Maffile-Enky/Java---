<template>
  <div class="merchant-orders">
    <h1 class="page-title">订单管理</h1>

    <div class="tab-bar">
      <button
        v-for="tab in tabs"
        :key="tab.value"
        class="tab-btn"
        :class="{ active: activeStatus === tab.value }"
        @click="switchTab(tab.value)"
      >{{ tab.label }}</button>
    </div>

    <div v-if="loading" class="loading">
      <div class="loading-spinner"></div>
    </div>

    <div v-else-if="orders.length === 0" class="empty-state">
      <p>暂无订单</p>
    </div>

    <div v-else class="order-list">
      <div v-for="order in orders" :key="order.id" class="order-card card">
        <div class="order-header">
          <span class="order-no">订单号: {{ order.orderNo }}</span>
          <span :class="['order-status', 'status-' + order.status]">{{ statusMap[order.status] || order.status }}</span>
        </div>
        <div class="order-info">
          <div class="info-row">
            <span class="label">收货人:</span>
            <span>{{ order.deliveryName || '-' }}</span>
          </div>
          <div class="info-row">
            <span class="label">电话:</span>
            <span>{{ order.deliveryPhone || '-' }}</span>
          </div>
          <div class="info-row">
            <span class="label">地址:</span>
            <span>{{ order.deliveryAddress || '-' }}</span>
          </div>
        </div>
        <div class="order-items" v-if="order.items && order.items.length">
          <div v-for="item in order.items" :key="item.id" class="order-item">
            <span class="item-name">{{ item.dishName }}</span>
            <span class="item-qty">x{{ item.quantity }}</span>
            <span class="item-price">¥{{ item.price }}</span>
          </div>
        </div>
        <div class="order-footer">
          <span class="order-total">合计: ¥{{ order.totalPrice }}</span>
          <span class="order-time">{{ order.createdAt }}</span>
        </div>
        <div class="order-actions" v-if="order.status === 'PAID' || order.status === 'CONFIRMED'">
          <button v-if="order.status === 'PAID'" class="btn-sm btn-primary" @click="handleConfirm(order)">确认接单</button>
          <button v-if="order.status === 'CONFIRMED'" class="btn-sm btn-accent" @click="handleDeliver(order)">标记配送</button>
        </div>
      </div>
    </div>

    <div v-if="totalPages > 1" class="pagination">
      <button class="btn-sm btn-outline" :disabled="page <= 1" @click="goPage(page - 1)">上一页</button>
      <span class="page-info">第 {{ page }} / {{ totalPages }} 页</span>
      <button class="btn-sm btn-outline" :disabled="page >= totalPages" @click="goPage(page + 1)">下一页</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMerchantOrderList, updateOrderStatus } from '@/api/order'

const tabs = [
  { label: '全部', value: '' },
  { label: '待付款', value: 'PENDING' },
  { label: '已付款', value: 'PAID' },
  { label: '已确认', value: 'CONFIRMED' },
  { label: '配送中', value: 'DELIVERING' },
  { label: '已完成', value: 'COMPLETED' }
]

const statusMap = {
  PENDING: '待付款',
  PAID: '已付款',
  CONFIRMED: '已确认',
  DELIVERING: '配送中',
  COMPLETED: '已完成',
  CANCELLED: '已取消'
}

const activeStatus = ref('')
const orders = ref([])
const loading = ref(false)
const page = ref(1)
const size = ref(10)
const total = ref(0)
const totalPages = ref(0)

async function fetchOrders() {
  loading.value = true
  try {
    const params = { page: page.value, size: size.value }
    if (activeStatus.value) params.status = activeStatus.value
    const res = await getMerchantOrderList(params)
    const data = res.data || res
    orders.value = data.records || []
    total.value = data.total || 0
    totalPages.value = data.pages || 0
  } catch {
    orders.value = []
  } finally {
    loading.value = false
  }
}

function switchTab(status) {
  activeStatus.value = status
  page.value = 1
  fetchOrders()
}

function goPage(p) {
  page.value = p
  fetchOrders()
}

async function handleConfirm(order) {
  try {
    await updateOrderStatus(order.id, 'CONFIRMED')
    order.status = 'CONFIRMED'
  } catch {
    alert('操作失败')
  }
}

async function handleDeliver(order) {
  try {
    await updateOrderStatus(order.id, 'DELIVERING')
    order.status = 'DELIVERING'
  } catch {
    alert('操作失败')
  }
}

onMounted(() => fetchOrders())
</script>

<style scoped>
.merchant-orders {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xl);
}

.page-title {
  font-family: var(--font-heading);
  font-size: var(--font-size-xl);
  font-weight: 700;
  margin: 0;
}

.tab-bar {
  display: flex;
  gap: var(--spacing-sm);
  flex-wrap: wrap;
}

.tab-btn {
  padding: 8px 16px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-xl);
  background: var(--color-bg-card);
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  cursor: pointer;
  transition: all 0.2s;
}

.tab-btn.active {
  background: var(--color-primary);
  color: #fff;
  border-color: var(--color-primary);
  font-weight: 600;
}

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

.empty-state {
  text-align: center;
  padding: 60px;
  color: var(--color-text-hint);
}

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
  padding-bottom: var(--spacing-md);
  border-bottom: 1px solid var(--color-divider);
}

.order-no {
  font-size: var(--font-size-sm);
  color: var(--color-text-hint);
}

.order-status {
  font-size: var(--font-size-sm);
  font-weight: 600;
  padding: 2px 10px;
  border-radius: 12px;
}

.status-PENDING { background: #FFF8EC; color: #C88A2A; }
.status-PAID { background: #EDF3F8; color: #5B8DB8; }
.status-CONFIRMED { background: #F0F5EC; color: #4A8C5C; }
.status-DELIVERING { background: #FFF8EC; color: #C88A2A; }
.status-COMPLETED { background: #F0F5EC; color: #4A8C5C; }
.status-CANCELLED { background: #F5EDEB; color: #C84B31; }

.order-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
  margin-bottom: var(--spacing-md);
}

.info-row {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
}

.info-row .label {
  color: var(--color-text-hint);
  margin-right: 8px;
}

.order-items {
  background: var(--color-bg-page);
  border-radius: var(--radius-md);
  padding: var(--spacing-md);
  margin-bottom: var(--spacing-md);
}

.order-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  padding: 4px 0;
  font-size: var(--font-size-sm);
}

.item-name {
  flex: 1;
}

.item-qty {
  color: var(--color-text-hint);
}

.item-price {
  font-weight: 600;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: var(--font-size-sm);
}

.order-total {
  font-weight: 700;
  color: var(--color-accent);
  font-size: var(--font-size-md);
}

.order-time {
  color: var(--color-text-hint);
}

.order-actions {
  display: flex;
  gap: var(--spacing-sm);
  margin-top: var(--spacing-md);
  padding-top: var(--spacing-md);
  border-top: 1px solid var(--color-divider);
}

.btn-sm {
  padding: 6px 16px;
  border-radius: var(--radius-md);
  font-size: var(--font-size-sm);
  font-weight: 500;
  cursor: pointer;
  border: none;
  transition: all 0.2s;
}

.btn-primary {
  background: var(--color-primary);
  color: #fff;
}

.btn-accent {
  background: var(--color-accent);
  color: #fff;
}

.btn-outline {
  background: transparent;
  border: 1px solid var(--color-border);
  color: var(--color-text-secondary);
}

.pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-lg);
}

.page-info {
  font-size: var(--font-size-sm);
  color: var(--color-text-hint);
}
</style>

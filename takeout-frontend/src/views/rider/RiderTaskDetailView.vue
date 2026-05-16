<template>
  <div class="task-detail">
    <div v-if="loading" class="loading">
      <div class="loading-spinner"></div>
    </div>

    <template v-else-if="task">
      <h1 class="page-title">任务详情</h1>

      <div class="card detail-card">
        <div class="detail-header">
          <span class="task-no">{{ task.taskNo }}</span>
          <span :class="['badge', statusBadgeClass(task.status)]">{{ statusMap[task.status] || task.status }}</span>
        </div>

        <div class="detail-section">
          <h3>取餐信息</h3>
          <p>{{ task.merchantName || '商家' }}</p>
          <p class="addr">{{ task.merchantAddress || task.pickupAddress }}</p>
        </div>

        <div class="detail-section">
          <h3>配送信息</h3>
          <p>{{ task.deliveryName || '收货人' }} {{ task.deliveryPhone }}</p>
          <p class="addr">{{ task.deliveryAddress }}</p>
        </div>

        <div class="detail-section" v-if="task.orderNo">
          <h3>订单信息</h3>
          <p>订单号: {{ task.orderNo }}</p>
          <p v-if="task.deliveryFee">配送费: ¥{{ task.deliveryFee }}</p>
        </div>

        <div class="detail-actions">
          <button
            v-if="task.status === 'ASSIGNED'"
            class="btn-primary"
            @click="handlePickup"
          >确认取餐</button>
          <button
            v-if="task.status === 'PICKED_UP'"
            class="btn-accent"
            @click="handleDeliver"
          >确认送达</button>
          <button class="btn-outline" @click="router.back()">返回</button>
        </div>
      </div>
    </template>

    <div v-else class="empty-state">
      <p>任务不存在</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getTaskDetail, pickupOrder, deliverOrder } from '@/api/delivery'

const router = useRouter()
const route = useRoute()

const statusMap = {
  PENDING: '待抢单',
  ASSIGNED: '待取餐',
  PICKED_UP: '配送中',
  DELIVERED: '已完成'
}

function statusBadgeClass(status) {
  if (status === 'PENDING') return 'badge-warning'
  if (status === 'ASSIGNED') return 'badge-warning'
  if (status === 'PICKED_UP') return 'badge-info'
  return 'badge-success'
}

const task = ref(null)
const loading = ref(true)

async function loadTask() {
  loading.value = true
  try {
    const res = await getTaskDetail(route.params.taskNo)
    task.value = res.data || res
  } catch {
    task.value = null
  } finally {
    loading.value = false
  }
}

async function handlePickup() {
  try {
    await pickupOrder(task.value.taskNo)
    task.value.status = 'PICKED_UP'
  } catch (e) {
    alert('操作失败: ' + (e.message || '请重试'))
  }
}

async function handleDeliver() {
  try {
    await deliverOrder(task.value.taskNo)
    task.value.status = 'DELIVERED'
  } catch (e) {
    alert('操作失败: ' + (e.message || '请重试'))
  }
}

onMounted(() => loadTask())
</script>

<style scoped>
.task-detail {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xl);
  max-width: 640px;
}

.page-title {
  font-family: var(--font-heading);
  font-size: var(--font-size-xl);
  font-weight: 700;
  margin: 0;
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

.empty-state {
  text-align: center;
  padding: 60px;
  color: var(--color-text-hint);
}

.detail-card {
  padding: 24px;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-xl);
  padding-bottom: var(--spacing-lg);
  border-bottom: 1px solid var(--color-divider);
}

.task-no {
  font-size: var(--font-size-sm);
  color: var(--color-text-hint);
}

.detail-section {
  margin-bottom: var(--spacing-xl);
}

.detail-section h3 {
  font-family: var(--font-heading);
  font-size: var(--font-size-base);
  font-weight: 600;
  color: var(--color-text-secondary);
  margin: 0 0 8px 0;
}

.detail-section p {
  font-size: var(--font-size-base);
  margin: 4px 0;
}

.detail-section .addr {
  color: var(--color-text-hint);
  font-size: var(--font-size-sm);
}

.detail-actions {
  display: flex;
  gap: var(--spacing-md);
  padding-top: var(--spacing-lg);
  border-top: 1px solid var(--color-divider);
}

.btn-primary {
  background: var(--color-primary);
  color: #fff;
  padding: 10px 24px;
  border-radius: var(--radius-xl);
  font-weight: 600;
  cursor: pointer;
  border: none;
}

.btn-accent {
  background: var(--color-accent);
  color: #fff;
  padding: 10px 24px;
  border-radius: var(--radius-xl);
  font-weight: 600;
  cursor: pointer;
  border: none;
}

.btn-outline {
  background: transparent;
  border: 1px solid var(--color-border);
  color: var(--color-text-secondary);
  padding: 10px 24px;
  border-radius: var(--radius-xl);
  cursor: pointer;
}
</style>

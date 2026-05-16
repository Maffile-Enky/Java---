<template>
  <div class="rider-tasks">
    <h1 class="page-title">我的任务</h1>

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

    <div v-else-if="tasks.length === 0" class="empty-state">
      <p>暂无任务</p>
    </div>

    <div v-else class="task-list">
      <div
        v-for="task in tasks"
        :key="task.id"
        class="task-card card"
        @click="router.push(`/rider/tasks/${task.taskNo}`)"
      >
        <div class="task-header">
          <span class="task-no">{{ task.taskNo }}</span>
          <span :class="['task-status', 'badge', statusBadgeClass(task.status)]">{{ statusMap[task.status] || task.status }}</span>
        </div>
        <div class="task-info">
          <p class="address">{{ task.merchantAddress || task.pickupAddress }}</p>
          <p class="address">{{ task.deliveryAddress }}</p>
        </div>
        <div class="task-footer">
          <span class="task-amount">¥{{ task.deliveryFee || 5 }}</span>
          <span class="task-time">{{ task.assignedAt || task.createdAt }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getRiderTasks } from '@/api/delivery'

const router = useRouter()

const tabs = [
  { label: '全部', value: '' },
  { label: '待取餐', value: 'ASSIGNED' },
  { label: '配送中', value: 'PICKED_UP' },
  { label: '已完成', value: 'DELIVERED' }
]

const statusMap = {
  ASSIGNED: '待取餐',
  PICKED_UP: '配送中',
  DELIVERED: '已完成'
}

function statusBadgeClass(status) {
  if (status === 'ASSIGNED') return 'badge-warning'
  if (status === 'PICKED_UP') return 'badge-info'
  return 'badge-success'
}

const activeStatus = ref('')
const tasks = ref([])
const loading = ref(true)

async function loadTasks() {
  loading.value = true
  try {
    const params = {}
    if (activeStatus.value) params.status = activeStatus.value
    const res = await getRiderTasks(params)
    tasks.value = res.data || []
  } catch {
    tasks.value = []
  } finally {
    loading.value = false
  }
}

function switchTab(status) {
  activeStatus.value = status
  loadTasks()
}

onMounted(() => loadTasks())
</script>

<style scoped>
.rider-tasks {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xl);
  max-width: 800px;
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

.task-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.task-card {
  padding: var(--spacing-lg);
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.task-card:hover {
  transform: translateY(-1px);
  box-shadow: var(--shadow-md);
}

.task-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-sm);
}

.task-no {
  font-size: var(--font-size-sm);
  color: var(--color-text-hint);
}

.task-info .address {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  margin: 2px 0;
}

.task-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: var(--spacing-sm);
  padding-top: var(--spacing-sm);
  border-top: 1px solid var(--color-divider);
}

.task-amount {
  font-weight: 700;
  color: var(--color-accent);
}

.task-time {
  font-size: var(--font-size-xs);
  color: var(--color-text-hint);
}
</style>

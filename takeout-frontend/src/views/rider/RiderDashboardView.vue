<template>
  <div class="rider-dashboard">
    <div class="status-bar card">
      <div class="rider-info">
        <h2>{{ riderStore.isRider ? '欢迎回来' : '成为骑手' }}</h2>
        <p v-if="riderStore.rider">{{ riderStore.rider.name }} · {{ riderStore.rider.phone }}</p>
        <p v-else>注册成为骑手，开始接单配送</p>
      </div>
      <div class="status-actions">
        <button
          v-if="riderStore.isRider"
          :class="['toggle-btn', riderStore.isOnline ? 'online' : 'offline']"
          @click="riderStore.toggleOnline()"
        >
          {{ riderStore.isOnline ? '在线中' : '已离线' }}
        </button>
        <router-link v-else to="/rider/register" class="btn-primary">立即注册</router-link>
      </div>
    </div>

    <h3 class="section-title">可抢订单</h3>

    <div v-if="loading" class="loading">
      <div class="loading-spinner"></div>
    </div>

    <div v-else-if="tasks.length === 0" class="empty-state">
      <p>暂无可抢订单</p>
    </div>

    <div v-else class="task-list">
      <div v-for="task in tasks" :key="task.id" class="task-card card">
        <div class="task-header">
          <span class="task-no">{{ task.taskNo }}</span>
          <span class="task-status badge badge-warning">待抢单</span>
        </div>
        <div class="task-info">
          <div class="info-row">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="16" height="16"><path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/><circle cx="12" cy="10" r="3"/></svg>
            <span>{{ task.merchantAddress || task.pickupAddress || '商家地址' }}</span>
          </div>
          <div class="info-row">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="16" height="16"><path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><polyline points="9,22 9,12 15,12 15,22"/></svg>
            <span>{{ task.deliveryAddress || '收货地址' }}</span>
          </div>
        </div>
        <div class="task-footer">
          <span class="task-amount">配送费: ¥{{ task.deliveryFee || 5 }}</span>
          <button class="btn-sm btn-accent" @click="handleGrab(task)">抢单</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useRiderStore } from '@/stores/rider'
import { getAvailableTasks, grabOrder } from '@/api/delivery'

const router = useRouter()
const riderStore = useRiderStore()
const tasks = ref([])
const loading = ref(true)

async function loadTasks() {
  loading.value = true
  try {
    const res = await getAvailableTasks()
    tasks.value = res.data || []
  } catch {
    tasks.value = []
  } finally {
    loading.value = false
  }
}

async function handleGrab(task) {
  try {
    await grabOrder(task.taskNo)
    alert('抢单成功!')
    tasks.value = tasks.value.filter(t => t.id !== task.id)
    router.push(`/rider/tasks/${task.taskNo}`)
  } catch (e) {
    alert('抢单失败: ' + (e.message || '请重试'))
  }
}

onMounted(async () => {
  await riderStore.loadRiderInfo()
  if (riderStore.isRider) {
    loadTasks()
  } else {
    loading.value = false
  }
})
</script>

<style scoped>
.rider-dashboard {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xl);
  max-width: 800px;
}

.status-bar {
  padding: 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.rider-info h2 {
  font-family: var(--font-heading);
  font-size: var(--font-size-lg);
  margin: 0 0 4px 0;
}

.rider-info p {
  font-size: var(--font-size-sm);
  color: var(--color-text-hint);
  margin: 0;
}

.toggle-btn {
  padding: 10px 24px;
  border-radius: var(--radius-xl);
  font-size: var(--font-size-base);
  font-weight: 600;
  cursor: pointer;
  border: none;
  transition: all 0.2s;
}

.toggle-btn.online {
  background: #4A8C5C;
  color: #fff;
}

.toggle-btn.offline {
  background: var(--color-text-hint);
  color: #fff;
}

.section-title {
  font-family: var(--font-heading);
  font-size: var(--font-size-lg);
  font-weight: 700;
  margin: 0;
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
}

.task-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-md);
}

.task-no {
  font-size: var(--font-size-sm);
  color: var(--color-text-hint);
}

.task-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: var(--spacing-md);
}

.info-row {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
}

.info-row svg {
  color: var(--color-accent);
  flex-shrink: 0;
}

.task-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: var(--spacing-md);
  border-top: 1px solid var(--color-divider);
}

.task-amount {
  font-weight: 700;
  color: var(--color-accent);
}

.btn-sm {
  padding: 6px 16px;
  border-radius: var(--radius-md);
  font-size: var(--font-size-sm);
  font-weight: 500;
  cursor: pointer;
  border: none;
}

.btn-accent {
  background: var(--color-accent);
  color: #fff;
}

.btn-primary {
  background: var(--color-primary);
  color: #fff;
  padding: 10px 24px;
  border-radius: var(--radius-xl);
  font-weight: 600;
  text-decoration: none;
  display: inline-block;
}
</style>

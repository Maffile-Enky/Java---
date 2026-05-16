<template>
  <div class="rider-dashboard">
    <h1 class="page-title">骑手中心</h1>

    <!-- Status Card -->
    <div class="status-card glass-panel">
      <div class="status-info">
        <span class="status-label">当前状态</span>
        <span class="status-value" :class="{ online: riderStore.isOnline }">
          {{ riderStore.isOnline ? '接单中' : '休息中' }}
        </span>
      </div>
      <GlassButton
        :variant="riderStore.isOnline ? 'ghost' : 'primary'"
        @click="toggleOnline"
      >
        {{ riderStore.isOnline ? '暂停接单' : '开始接单' }}
      </GlassButton>
    </div>

    <!-- Stats -->
    <div class="stat-grid">
      <div class="stat-card glass-panel">
        <span class="stat-icon">📦</span>
        <div>
          <span class="stat-value">{{ stats.todayDeliveries }}</span>
          <span class="stat-label">今日配送</span>
        </div>
      </div>
      <div class="stat-card glass-panel">
        <span class="stat-icon">💰</span>
        <div>
          <span class="stat-value">¥{{ stats.todayEarnings }}</span>
          <span class="stat-label">今日收入</span>
        </div>
      </div>
      <div class="stat-card glass-panel">
        <span class="stat-icon">⭐</span>
        <div>
          <span class="stat-value">{{ stats.rating }}</span>
          <span class="stat-label">评分</span>
        </div>
      </div>
    </div>

    <!-- Recent Tasks -->
    <div class="section">
      <h2 class="section-title">最近任务</h2>
      <LoadingSpinner v-if="loading" size="sm" />
      <EmptyState v-else-if="!tasks.length" icon="📦" text="暂无任务" />
      <div v-else class="task-list">
        <div v-for="task in tasks" :key="task.id" class="task-card glass-panel" @click="$router.push(`/rider/tasks/${task.id}`)">
          <div class="task-header">
            <span class="task-id">#{{ task.orderId || task.id }}</span>
            <span class="task-status tag" :class="taskStatusClass(task.status)">{{ taskStatusLabel(task.status) }}</span>
          </div>
          <p class="task-addr">{{ task.deliveryAddress || task.address }}</p>
          <span class="task-time">{{ formatDate(task.createTime) }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRiderStore } from '@/stores/rider'
import { getRiderTasks, updateRiderStatus } from '@/api/delivery'
import GlassButton from '@/components/ui/GlassButton.vue'
import LoadingSpinner from '@/components/common/LoadingSpinner.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import dayjs from 'dayjs'

const riderStore = useRiderStore()
const tasks = ref([])
const loading = ref(true)

const stats = reactive({
  todayDeliveries: '—',
  todayEarnings: '—',
  rating: '—'
})

const statusMap = {
  ACCEPTED: '已接单', PICKED_UP: '已取餐', DELIVERING: '配送中', COMPLETED: '已完成'
}

function taskStatusLabel(s) { return statusMap[s] || s }
function taskStatusClass(s) {
  if (s === 'COMPLETED') return 'tag-green'
  if (s === 'DELIVERING') return 'tag-active'
  return 'tag-gold'
}

function formatDate(val) { return val ? dayjs(val).format('MM-DD HH:mm') : '' }

async function toggleOnline() {
  try {
    await updateRiderStatus(!riderStore.isOnline)
    riderStore.setOnline(!riderStore.isOnline)
  } catch {}
}

onMounted(async () => {
  try {
    const res = await getRiderTasks()
    tasks.value = (res.data || []).slice(0, 10)
  } catch { tasks.value = [] }
  finally { loading.value = false }
})
</script>

<style scoped>
.rider-dashboard { max-width: 1000px; }

.page-title {
  font-family: var(--font-serif);
  font-size: 1.8rem;
  font-weight: 900;
  margin-bottom: var(--space-8);
}

.status-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-6);
  margin-bottom: var(--space-6);
}

.status-label { font-size: var(--text-sm); color: var(--text-muted); display: block; }
.status-value { font-size: 1.3rem; font-weight: 800; color: var(--text-muted); }
.status-value.online { color: var(--accent); }

.stat-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
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
.stat-value { font-size: 1.3rem; font-weight: 800; color: var(--accent); display: block; }
.stat-label { font-size: var(--text-xs); color: var(--text-muted); }

.section { margin-bottom: var(--space-8); }
.section-title { font-size: 1.2rem; font-weight: 700; margin-bottom: var(--space-4); }

.task-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

.task-card {
  padding: var(--space-4);
  cursor: pointer;
  transition: all var(--duration-fast);
}

.task-card:hover { border-color: rgba(110, 231, 160, 0.2); }

.task-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-2);
}

.task-id { font-size: var(--text-xs); font-weight: 600; color: var(--text-muted); }
.task-addr { font-size: var(--text-sm); color: var(--text-secondary); margin: 0 0 var(--space-1); }
.task-time { font-size: var(--text-xs); color: var(--text-muted); }

.tag-green { background: rgba(110, 231, 160, 0.15); color: var(--accent); padding: 2px 10px; border-radius: var(--radius-full); font-size: var(--text-xs); font-weight: 600; }
.tag-gold { background: rgba(240, 197, 90, 0.15); color: var(--accent-secondary); padding: 2px 10px; border-radius: var(--radius-full); font-size: var(--text-xs); font-weight: 600; }
.tag-active { background: rgba(110, 231, 160, 0.15); color: var(--accent); padding: 2px 10px; border-radius: var(--radius-full); font-size: var(--text-xs); font-weight: 600; animation: pulse 2s ease-in-out infinite; }
</style>

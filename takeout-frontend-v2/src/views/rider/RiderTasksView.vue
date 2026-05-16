<template>
  <div class="rider-tasks">
    <h1 class="page-title">我的任务</h1>

    <div class="task-tabs">
      <button
        v-for="tab in tabs"
        :key="tab.value"
        class="tab-btn"
        :class="{ active: activeTab === tab.value }"
        @click="activeTab = tab.value"
      >{{ tab.label }}</button>
    </div>

    <LoadingSpinner v-if="loading" text="加载中..." />
    <EmptyState v-else-if="!filteredTasks.length" icon="📋" text="暂无任务" />

    <div v-else class="task-list">
      <div
        v-for="task in filteredTasks"
        :key="task.id"
        class="task-card glass-panel"
        @click="$router.push(`/rider/tasks/${task.id}`)"
      >
        <div class="task-header">
          <span class="task-id">#{{ task.orderId || task.id }}</span>
          <span class="task-status tag" :class="statusClass(task.status)">{{ statusLabel(task.status) }}</span>
        </div>
        <p class="task-addr">📍 {{ task.deliveryAddress || task.address }}</p>
        <p v-if="task.merchantName" class="task-merchant">🏪 {{ task.merchantName }}</p>
        <span class="task-time">{{ formatDate(task.createTime) }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getRiderTasks } from '@/api/delivery'
import LoadingSpinner from '@/components/common/LoadingSpinner.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import dayjs from 'dayjs'

const tasks = ref([])
const loading = ref(true)
const activeTab = ref('ALL')

const tabs = [
  { label: '全部', value: 'ALL' },
  { label: '进行中', value: 'ACTIVE' },
  { label: '已完成', value: 'COMPLETED' }
]

const statusMap = {
  ACCEPTED: '已接单', PICKED_UP: '已取餐', DELIVERING: '配送中', COMPLETED: '已完成'
}

function statusLabel(s) { return statusMap[s] || s }
function statusClass(s) {
  if (s === 'COMPLETED') return 'tag-green'
  if (s === 'DELIVERING') return 'tag-active'
  return 'tag-gold'
}

function formatDate(val) { return val ? dayjs(val).format('MM-DD HH:mm') : '' }

const filteredTasks = computed(() => {
  if (activeTab.value === 'ALL') return tasks.value
  if (activeTab.value === 'ACTIVE') return tasks.value.filter(t => t.status !== 'COMPLETED')
  return tasks.value.filter(t => t.status === 'COMPLETED')
})

onMounted(async () => {
  try {
    const res = await getRiderTasks()
    tasks.value = res.data || []
  } catch { tasks.value = [] }
  finally { loading.value = false }
})
</script>

<style scoped>
.rider-tasks { max-width: 1000px; }

.page-title {
  font-family: var(--font-serif);
  font-size: 1.8rem;
  font-weight: 900;
  margin-bottom: var(--space-6);
}

.task-tabs {
  display: flex;
  gap: var(--space-2);
  margin-bottom: var(--space-6);
}

.tab-btn {
  padding: var(--space-2) var(--space-4);
  background: var(--glass);
  border: 1px solid var(--glass-border);
  border-radius: var(--radius-full);
  color: var(--text-secondary);
  font-size: var(--text-sm);
  cursor: pointer;
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

.task-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-3);
}

.task-card {
  padding: var(--space-5);
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

.task-addr {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  margin: 0 0 var(--space-1);
}

.task-merchant {
  font-size: var(--text-xs);
  color: var(--text-muted);
  margin: 0 0 var(--space-2);
}

.task-time { font-size: var(--text-xs); color: var(--text-muted); }

.tag-green { background: rgba(110, 231, 160, 0.15); color: var(--accent); padding: 2px 10px; border-radius: var(--radius-full); font-size: var(--text-xs); font-weight: 600; }
.tag-gold { background: rgba(240, 197, 90, 0.15); color: var(--accent-secondary); padding: 2px 10px; border-radius: var(--radius-full); font-size: var(--text-xs); font-weight: 600; }
.tag-active { background: rgba(110, 231, 160, 0.15); color: var(--accent); padding: 2px 10px; border-radius: var(--radius-full); font-size: var(--text-xs); font-weight: 600; animation: pulse 2s ease-in-out infinite; }
</style>

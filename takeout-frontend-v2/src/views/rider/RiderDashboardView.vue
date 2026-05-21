<template>
  <div class="rider-dashboard">
    <h1 class="page-title">骑手中心</h1>

    <!-- Status Card -->
    <div class="status-card glass-panel">
      <div class="status-info">
        <span class="status-label">当前状态</span>
        <span class="status-value" :class="{ online: riderStore.isOnline }">
          {{ riderStore.isOnline ? '接单中' : riderStore.isBusy ? '配送中' : '休息中' }}
        </span>
      </div>
      <GlassButton
        :variant="riderStore.isOnline ? 'ghost' : 'primary'"
        @click="toggleOnline"
        :loading="toggling"
      >
        {{ riderStore.isOnline ? '暂停接单' : '开始接单' }}
      </GlassButton>
    </div>

    <!-- Auto dispatch notification -->
    <Transition name="slide">
      <div v-if="autoDispatchTask" class="auto-dispatch-card glass-panel">
        <div class="dispatch-icon">🎉</div>
        <div class="dispatch-info">
          <h3>自动接单成功！</h3>
          <p>订单号: {{ autoDispatchTask.orderNo }}</p>
          <p>配送地址: {{ autoDispatchTask.deliveryAddress }}</p>
        </div>
        <GlassButton variant="primary" size="sm" @click="viewTask(autoDispatchTask.taskNo)">
          查看详情
        </GlassButton>
      </div>
    </Transition>

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
      <div v-if="!riderStore.isRider && !loading" class="register-prompt glass-panel">
        <p>您还未注册为骑手</p>
        <GlassButton variant="primary" @click="$router.push('/rider/register')">立即注册</GlassButton>
      </div>
      <LoadingSpinner v-else-if="loading" size="sm" />
      <EmptyState v-else-if="!tasks.length" icon="📦" text="暂无任务" />
      <div v-else class="task-list">
        <div v-for="task in tasks" :key="task.id" class="task-card glass-panel" @click="viewTask(task.taskNo)">
          <div class="task-header">
            <span class="task-id">#{{ task.taskNo || task.orderNo }}</span>
            <span class="task-status tag" :class="taskStatusClass(task.status)">{{ taskStatusLabel(task.status) }}</span>
          </div>
          <p class="task-addr">{{ task.deliveryAddress || task.address }}</p>
          <span class="task-time">{{ formatDate(task.createdAt) }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useRiderStore } from '@/stores/rider'
import { getRiderTasks, updateRiderStatus } from '@/api/delivery'
import GlassButton from '@/components/ui/GlassButton.vue'
import LoadingSpinner from '@/components/common/LoadingSpinner.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import dayjs from 'dayjs'

const router = useRouter()
const riderStore = useRiderStore()
const tasks = ref([])
const loading = ref(true)
const toggling = ref(false)
const autoDispatchTask = ref(null)

const stats = reactive({
  todayDeliveries: '—',
  todayEarnings: '—',
  rating: '—'
})

const statusMap = {
  ASSIGNED: '已接单',
  ACCEPTED: '已接单',
  PICKED_UP: '已取餐',
  DELIVERING: '配送中',
  COMPLETED: '已完成'
}

function taskStatusLabel(s) { return statusMap[s] || s }
function taskStatusClass(s) {
  if (s === 'COMPLETED') return 'tag-green'
  if (s === 'DELIVERING' || s === 'PICKED_UP') return 'tag-active'
  return 'tag-gold'
}

function formatDate(val) { return val ? dayjs(val).format('MM-DD HH:mm') : '' }

function viewTask(taskNo) {
  router.push(`/rider/tasks/${taskNo}`)
}

async function toggleOnline() {
  toggling.value = true
  autoDispatchTask.value = null

  try {
    const newStatus = riderStore.isOnline ? 'OFFLINE' : 'ONLINE'
    const res = await updateRiderStatus(newStatus)
    riderStore.rider = res.data || { ...riderStore.rider, status: newStatus }

    // 如果上线成功，检查是否自动接到了订单
    if (newStatus === 'ONLINE') {
      // 重新加载任务列表
      await loadTasks()

      // 检查是否有新分配的任务
      const assignedTask = tasks.value.find(t => t.status === 'ASSIGNED')
      if (assignedTask) {
        autoDispatchTask.value = assignedTask
        // 5秒后自动隐藏提示
        setTimeout(() => {
          autoDispatchTask.value = null
        }, 5000)
      }
    }
  } catch (e) {
    alert('操作失败: ' + (e.message || '未知错误'))
  } finally {
    toggling.value = false
  }
}

async function loadTasks() {
  try {
    const res = await getRiderTasks()
    tasks.value = (res.data || []).slice(0, 10)
    stats.todayDeliveries = tasks.value.filter(t => t.status === 'COMPLETED').length || '—'
  } catch (e) {
    tasks.value = []
    if (!e.message?.includes('404')) {
      console.error('加载任务失败:', e)
    }
  }
}

onMounted(async () => {
  try {
    const rider = await riderStore.loadRiderInfo()
    if (!rider) {
      loading.value = false
      return
    }
    stats.rating = rider.rating || '—'
    await loadTasks()
  } catch (e) {
    tasks.value = []
    if (!e.message?.includes('404')) {
      alert('加载失败: ' + (e.message || '未知错误'))
    }
  } finally {
    loading.value = false
  }
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

/* Auto dispatch notification */
.auto-dispatch-card {
  display: flex;
  align-items: center;
  gap: var(--space-4);
  padding: var(--space-5);
  margin-bottom: var(--space-6);
  background: rgba(110, 231, 160, 0.1);
  border-color: rgba(110, 231, 160, 0.3);
  animation: slideIn 0.3s var(--ease-out);
}

.dispatch-icon {
  font-size: 2rem;
}

.dispatch-info {
  flex: 1;
}

.dispatch-info h3 {
  font-size: var(--text-base);
  font-weight: 700;
  color: var(--accent);
  margin-bottom: var(--space-1);
}

.dispatch-info p {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  margin: 0;
}

.slide-enter-active {
  animation: slideIn 0.3s var(--ease-out);
}

.slide-leave-active {
  animation: slideOut 0.2s var(--ease-out);
}

@keyframes slideIn {
  from { opacity: 0; transform: translateY(-10px); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes slideOut {
  from { opacity: 1; transform: translateY(0); }
  to { opacity: 0; transform: translateY(-10px); }
}

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

.register-prompt {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--space-6);
  margin-bottom: var(--space-6);
}
.register-prompt p { font-size: var(--text-sm); color: var(--text-secondary); }
</style>

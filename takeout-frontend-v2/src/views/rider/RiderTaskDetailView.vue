<template>
  <div class="task-detail">
    <div class="detail-header">
      <button class="back-btn" @click="$router.back()">← 返回</button>
      <h1 class="page-title">任务详情</h1>
    </div>

    <LoadingSpinner v-if="loading" text="加载中..." />

    <template v-else-if="task">
      <!-- Status -->
      <div class="status-card glass-panel">
        <span class="task-status tag" :class="statusClass(task.status)">{{ statusLabel(task.status) }}</span>
        <span class="task-id">#{{ task.orderId || task.id }}</span>
      </div>

      <!-- Addresses -->
      <div class="section glass-panel">
        <h2 class="section-title">配送信息</h2>
        <div class="addr-row">
          <span class="addr-icon">🏪</span>
          <div>
            <span class="addr-label">取餐地址</span>
            <p class="addr-text">{{ task.pickupAddress || task.merchantAddress || '商家地址' }}</p>
          </div>
        </div>
        <div class="addr-divider"></div>
        <div class="addr-row">
          <span class="addr-icon">📍</span>
          <div>
            <span class="addr-label">送达地址</span>
            <p class="addr-text">{{ task.deliveryAddress || task.address }}</p>
          </div>
        </div>
      </div>

      <!-- Contact -->
      <div v-if="task.customerPhone" class="section glass-panel">
        <h2 class="section-title">联系信息</h2>
        <p class="info-text">客户: {{ task.customerName || '—' }}</p>
        <a :href="`tel:${task.customerPhone}`" class="call-link">📞 {{ task.customerPhone }}</a>
      </div>

      <!-- Actions -->
      <div class="task-actions">
        <GlassButton v-if="task.status === 'ACCEPTED'" variant="primary" block @click="updateStatus('PICKED_UP')">
          确认取餐
        </GlassButton>
        <GlassButton v-if="task.status === 'PICKED_UP'" variant="primary" block @click="updateStatus('DELIVERING')">
          开始配送
        </GlassButton>
        <GlassButton v-if="task.status === 'DELIVERING'" variant="primary" block @click="updateStatus('COMPLETED')">
          确认送达
        </GlassButton>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getTaskDetail, pickupOrder, deliverOrder } from '@/api/delivery'
import GlassButton from '@/components/ui/GlassButton.vue'
import LoadingSpinner from '@/components/common/LoadingSpinner.vue'

const route = useRoute()
const task = ref(null)
const loading = ref(true)

const statusMap = {
  ACCEPTED: '已接单', PICKED_UP: '已取餐', DELIVERING: '配送中', COMPLETED: '已完成'
}

function statusLabel(s) { return statusMap[s] || s }
function statusClass(s) {
  if (s === 'COMPLETED') return 'tag-green'
  if (s === 'DELIVERING') return 'tag-active'
  return 'tag-gold'
}

async function fetchTask() {
  try {
    const res = await getTaskDetail(route.params.taskNo)
    task.value = res.data
  } catch { task.value = null }
  finally { loading.value = false }
}

async function updateStatus(status) {
  try {
    if (status === 'PICKED_UP') await pickupOrder(route.params.taskNo)
    else if (status === 'DELIVERING') await deliverOrder(route.params.taskNo)
    else if (status === 'COMPLETED') await deliverOrder(route.params.taskNo)
    fetchTask()
  } catch {}
}

onMounted(fetchTask)
</script>

<style scoped>
.task-detail { max-width: 800px; }

.detail-header {
  display: flex;
  align-items: center;
  gap: var(--space-4);
  margin-bottom: var(--space-6);
}

.back-btn {
  background: none;
  border: none;
  color: var(--text-secondary);
  font-size: var(--text-sm);
  cursor: pointer;
  font-family: var(--font-sans);
}

.back-btn:hover { color: var(--accent); }

.page-title {
  font-family: var(--font-serif);
  font-size: 1.8rem;
  font-weight: 900;
}

.status-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-5);
  margin-bottom: var(--space-4);
}

.task-id { font-size: var(--text-xs); color: var(--text-muted); }

.section {
  padding: var(--space-6);
  margin-bottom: var(--space-4);
}

.section-title {
  font-size: var(--text-base);
  font-weight: 700;
  margin-bottom: var(--space-4);
}

.addr-row {
  display: flex;
  gap: var(--space-3);
  align-items: flex-start;
}

.addr-icon { font-size: 1.3rem; margin-top: 2px; }
.addr-label { font-size: var(--text-xs); color: var(--text-muted); display: block; margin-bottom: 2px; }
.addr-text { font-size: var(--text-sm); color: var(--text-primary); margin: 0; }

.addr-divider {
  height: 1px;
  background: var(--glass-border);
  margin: var(--space-4) 0;
}

.info-text { font-size: var(--text-sm); color: var(--text-secondary); margin: 0 0 var(--space-2); }

.call-link {
  color: var(--accent);
  text-decoration: none;
  font-size: var(--text-sm);
  font-weight: 600;
}

.task-actions {
  display: flex;
  flex-direction: column;
  gap: var(--space-3);
  margin-top: var(--space-6);
}

.tag-green { background: rgba(110, 231, 160, 0.15); color: var(--accent); padding: 4px 12px; border-radius: var(--radius-full); font-size: var(--text-xs); font-weight: 600; }
.tag-gold { background: rgba(240, 197, 90, 0.15); color: var(--accent-secondary); padding: 4px 12px; border-radius: var(--radius-full); font-size: var(--text-xs); font-weight: 600; }
.tag-active { background: rgba(110, 231, 160, 0.15); color: var(--accent); padding: 4px 12px; border-radius: var(--radius-full); font-size: var(--text-xs); font-weight: 600; animation: pulse 2s ease-in-out infinite; }
</style>

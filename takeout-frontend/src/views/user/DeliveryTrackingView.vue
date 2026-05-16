<template>
  <div class="tracking-page">
    <h1 class="page-title">配送追踪</h1>

    <div v-if="loading" class="loading">
      <div class="loading-spinner"></div>
    </div>

    <template v-else-if="task">
      <div class="card status-card">
        <div class="status-header">
          <span :class="['status-badge', statusClass]">{{ statusText }}</span>
          <span class="order-no">订单号: {{ task.orderNo }}</span>
        </div>

        <div class="rider-info" v-if="task.riderName">
          <div class="rider-avatar">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="32" height="32"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
          </div>
          <div class="rider-detail">
            <h3>{{ task.riderName }}</h3>
            <p>{{ task.riderPhone }}</p>
          </div>
          <a v-if="task.riderPhone" :href="'tel:' + task.riderPhone" class="call-btn">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="20" height="20"><path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z"/></svg>
          </a>
        </div>

        <div class="addresses">
          <div class="addr-row">
            <span class="addr-dot pickup"></span>
            <span>{{ task.merchantAddress || task.pickupAddress }}</span>
          </div>
          <div class="addr-line"></div>
          <div class="addr-row">
            <span class="addr-dot delivery"></span>
            <span>{{ task.deliveryAddress }}</span>
          </div>
        </div>
      </div>

      <div class="card location-card" v-if="lastLocation">
        <h3>骑手位置</h3>
        <p class="location-text">经度: {{ lastLocation.longitude }}, 纬度: {{ lastLocation.latitude }}</p>
        <p class="location-time">更新时间: {{ lastLocation.time }}</p>
      </div>
    </template>

    <div v-else class="empty-state">
      <p>暂无配送信息</p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'
import { trackDelivery } from '@/api/delivery'

const route = useRoute()

const task = ref(null)
const loading = ref(true)
const lastLocation = ref(null)
let pollTimer = null

const statusText = computed(() => {
  const map = {
    PENDING: '等待骑手接单',
    ASSIGNED: '骑手已接单，等待取餐',
    PICKED_UP: '骑手正在配送中',
    DELIVERED: '已送达'
  }
  return map[task.value?.status] || task.value?.status || ''
})

const statusClass = computed(() => {
  const s = task.value?.status
  if (s === 'PICKED_UP') return 'delivering'
  if (s === 'DELIVERED') return 'done'
  return 'pending'
})

async function loadTracking() {
  try {
    const res = await trackDelivery(route.params.id)
    task.value = res.data || res
    if (task.value?.longitude && task.value?.latitude) {
      lastLocation.value = {
        longitude: task.value.longitude,
        latitude: task.value.latitude,
        time: new Date().toLocaleTimeString()
      }
    }
  } catch {
    // task stays null
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadTracking()
  pollTimer = setInterval(loadTracking, 10000)
})

onUnmounted(() => {
  if (pollTimer) clearInterval(pollTimer)
})
</script>

<style scoped>
.tracking-page {
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

.status-card {
  padding: 24px;
}

.status-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-xl);
}

.status-badge {
  padding: 4px 14px;
  border-radius: 20px;
  font-size: var(--font-size-sm);
  font-weight: 600;
}

.status-badge.pending { background: #FFF8EC; color: #C88A2A; }
.status-badge.delivering { background: #EDF3F8; color: #5B8DB8; }
.status-badge.done { background: #F0F5EC; color: #4A8C5C; }

.order-no {
  font-size: var(--font-size-sm);
  color: var(--color-text-hint);
}

.rider-info {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-xl);
  padding: var(--spacing-lg);
  background: var(--color-bg-page);
  border-radius: var(--radius-md);
}

.rider-avatar {
  color: var(--color-accent);
}

.rider-detail {
  flex: 1;
}

.rider-detail h3 {
  font-size: var(--font-size-base);
  margin: 0;
}

.rider-detail p {
  font-size: var(--font-size-sm);
  color: var(--color-text-hint);
  margin: 0;
}

.call-btn {
  width: 40px;
  height: 40px;
  border-radius: var(--radius-full);
  background: var(--color-primary);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background var(--transition-smooth);
}

.call-btn:hover {
  background: var(--color-primary-dark);
}

.addresses {
  display: flex;
  flex-direction: column;
  gap: 0;
}

.addr-row {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 0;
  font-size: var(--font-size-sm);
}

.addr-dot {
  width: 10px;
  height: 10px;
  border-radius: var(--radius-full);
  flex-shrink: 0;
}

.addr-dot.pickup { background: var(--color-primary); }
.addr-dot.delivery { background: var(--color-success); }

.addr-line {
  width: 2px;
  height: 20px;
  background: var(--color-border);
  margin-left: 4px;
}

.location-card {
  padding: 24px;
}

.location-card h3 {
  font-size: var(--font-size-base);
  font-weight: 600;
  margin: 0 0 8px 0;
}

.location-text {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  margin: 0;
}

.location-time {
  font-size: var(--font-size-xs);
  color: var(--color-text-hint);
  margin: 4px 0 0 0;
}
</style>

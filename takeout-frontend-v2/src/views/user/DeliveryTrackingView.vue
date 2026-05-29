<template>
  <div class="tracking-page">
    <div class="container">
      <div class="detail-header">
        <button class="back-btn" @click="$router.back()">← 返回</button>
        <h1 class="page-title">配送追踪</h1>
      </div>

      <LoadingSpinner v-if="loading" text="加载中..." />

      <template v-else>
        <!-- Map placeholder -->
        <div class="map-placeholder glass-panel">
          <span class="map-icon">🗺️</span>
          <p class="map-text">地图加载中...</p>
        </div>

        <!-- Rider Info -->
        <div v-if="delivery" class="rider-card glass-panel">
          <div class="rider-avatar">🏍️</div>
          <div class="rider-info">
            <h3 class="rider-name">{{ delivery.riderName || '骑手' }}</h3>
            <p class="rider-phone">{{ delivery.riderPhone }}</p>
          </div>
          <a v-if="delivery.riderPhone" :href="`tel:${delivery.riderPhone}`" class="call-btn">
            📞 联系骑手
          </a>
        </div>

        <!-- Status Timeline -->
        <div class="timeline glass-panel">
          <div
            v-for="(step, i) in timeline"
            :key="i"
            class="timeline-item"
            :class="{ active: step.active, current: i === 0 }"
          >
            <div class="timeline-dot"></div>
            <div class="timeline-content">
              <span class="timeline-label">{{ step.label }}</span>
              <span class="timeline-time">{{ step.time }}</span>
            </div>
          </div>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'
import { trackDelivery } from '@/api/delivery'
import { getOrderDetail } from '@/api/order'
import LoadingSpinner from '@/components/common/LoadingSpinner.vue'
import dayjs from 'dayjs'

const route = useRoute()
const delivery = ref(null)
const loading = ref(true)
let ws = null

const timeline = computed(() => {
  if (!delivery.value) return []
  const d = delivery.value
  const steps = [
    { label: '骑手已接单', time: d.acceptTime, active: !!d.acceptTime },
    { label: '骑手已取餐', time: d.pickupTime, active: !!d.pickupTime },
    { label: '配送中', time: d.deliverTime, active: d.status === 'DELIVERING' },
    { label: '已送达', time: d.completeTime, active: d.status === 'COMPLETED' }
  ]
  return steps.filter(s => s.active).reverse()
})

function formatTime(val) {
  return val ? dayjs(val).format('HH:mm') : ''
}

async function fetchDelivery() {
  try {
    // First get the order to find its orderNo
    const orderRes = await getOrderDetail(route.params.id)
    const orderNo = orderRes.data?.orderNo
    if (!orderNo) {
      delivery.value = null
      return
    }
    const res = await trackDelivery(orderNo)
    delivery.value = res.data
  } catch { delivery.value = null }
  finally { loading.value = false }
}

onMounted(() => {
  fetchDelivery()
  // WebSocket for real-time updates
  try {
    ws = new WebSocket(`ws://${location.host}/ws/delivery/${route.params.id}`)
    ws.onmessage = (e) => {
      try {
        const data = JSON.parse(e.data)
        if (delivery.value) Object.assign(delivery.value, data)
      } catch {}
    }
  } catch {}
})

onUnmounted(() => {
  ws?.close()
})
</script>

<style scoped>
.tracking-page {
  padding: var(--space-6) 0;
}

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

.map-placeholder {
  height: 250px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: var(--space-2);
  margin-bottom: var(--space-4);
  background: linear-gradient(145deg, rgba(110, 231, 160, 0.03), rgba(167, 243, 208, 0.02));
}

.map-icon { font-size: 3rem; opacity: 0.3; }
.map-text { font-size: var(--text-sm); color: var(--text-muted); }

.rider-card {
  display: flex;
  align-items: center;
  gap: var(--space-4);
  padding: var(--space-5);
  margin-bottom: var(--space-4);
}

.rider-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: var(--glass);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
}

.rider-info { flex: 1; }
.rider-name { font-size: var(--text-base); font-weight: 700; margin-bottom: 2px; }
.rider-phone { font-size: var(--text-sm); color: var(--text-muted); margin: 0; }

.call-btn {
  padding: var(--space-2) var(--space-4);
  background: var(--gradient-green);
  color: var(--text-inverse);
  border-radius: var(--radius-full);
  text-decoration: none;
  font-size: var(--text-sm);
  font-weight: 600;
}

.timeline {
  padding: var(--space-6);
}

.timeline-item {
  display: flex;
  gap: var(--space-4);
  padding-bottom: var(--space-6);
  position: relative;
}

.timeline-item:last-child { padding-bottom: 0; }

.timeline-item::before {
  content: '';
  position: absolute;
  left: 7px;
  top: 20px;
  bottom: 0;
  width: 2px;
  background: var(--glass-border);
}

.timeline-item:last-child::before { display: none; }

.timeline-dot {
  width: 16px;
  height: 16px;
  border-radius: 50%;
  background: var(--glass-border);
  flex-shrink: 0;
  position: relative;
  z-index: 1;
}

.timeline-item.current .timeline-dot {
  background: var(--accent);
  box-shadow: 0 0 10px var(--accent-glow);
}

.timeline-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex: 1;
}

.timeline-label { font-size: var(--text-sm); font-weight: 500; }
.timeline-time { font-size: var(--text-xs); color: var(--text-muted); }
</style>

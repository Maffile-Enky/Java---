<template>
  <span class="status-badge" :class="`status--${statusClass}`">
    {{ statusText }}
  </span>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  status: { type: String, required: true }
})

const statusMap = {
  PENDING: { text: '待支付', cls: 'warning' },
  PAID: { text: '已支付', cls: 'info' },
  PREPARING: { text: '备餐中', cls: 'info' },
  READY: { text: '待取餐', cls: 'info' },
  DELIVERING: { text: '配送中', cls: 'active' },
  DELIVERED: { text: '已送达', cls: 'success' },
  COMPLETED: { text: '已完成', cls: 'success' },
  CANCELLED: { text: '已取消', cls: 'muted' },
  REFUNDED: { text: '已退款', cls: 'muted' }
}

const statusText = computed(() => statusMap[props.status]?.text || props.status)
const statusClass = computed(() => statusMap[props.status]?.cls || 'muted')
</script>

<style scoped>
.status-badge {
  display: inline-flex;
  align-items: center;
  padding: 2px var(--space-3);
  border-radius: var(--radius-full);
  font-size: var(--text-xs);
  font-weight: 600;
  white-space: nowrap;
}

.status--success { background: rgba(110, 231, 160, 0.15); color: var(--accent); }
.status--info { background: rgba(96, 165, 250, 0.15); color: #60a5fa; }
.status--warning { background: rgba(240, 197, 90, 0.15); color: var(--accent-secondary); }
.status--active { background: rgba(110, 231, 160, 0.15); color: var(--accent); animation: pulse 2s ease-in-out infinite; }
.status--muted { background: var(--glass); color: var(--text-muted); }
</style>

<template>
  <div class="notification-page">
    <div class="container">
      <div class="detail-header">
        <button class="back-btn" @click="$router.back()">← 返回</button>
        <h1 class="page-title">消息通知</h1>
      </div>

      <LoadingSpinner v-if="loading" text="加载中..." />

      <EmptyState v-else-if="!notifications.length" icon="🔔" text="暂无通知" />

      <div v-else class="notif-list">
        <div
          v-for="notif in notifications"
          :key="notif.id"
          class="notif-card glass-panel"
          :class="{ unread: !notif.read }"
        >
          <div class="notif-icon">{{ notif.type === 'ORDER' ? '📋' : '📢' }}</div>
          <div class="notif-content">
            <h3 class="notif-title">{{ notif.title }}</h3>
            <p class="notif-text">{{ notif.content }}</p>
            <span class="notif-time">{{ formatDate(notif.createTime) }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getNotifications } from '@/api/notification'
import LoadingSpinner from '@/components/common/LoadingSpinner.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import dayjs from 'dayjs'

const notifications = ref([])
const loading = ref(true)

function formatDate(val) {
  return val ? dayjs(val).format('MM-DD HH:mm') : ''
}

onMounted(async () => {
  try {
    const res = await getNotifications()
    notifications.value = res.data || []
  } catch { notifications.value = [] }
  finally { loading.value = false }
})
</script>

<style scoped>
.notification-page {
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

.notif-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-3);
}

.notif-card {
  display: flex;
  gap: var(--space-4);
  padding: var(--space-5);
  transition: all var(--duration-fast);
}

.notif-card.unread {
  border-left: 3px solid var(--accent);
}

.notif-icon {
  font-size: 1.5rem;
  flex-shrink: 0;
}

.notif-content { flex: 1; }

.notif-title {
  font-size: var(--text-base);
  font-weight: 600;
  margin-bottom: var(--space-1);
}

.notif-text {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  margin: 0 0 var(--space-2);
  line-height: 1.6;
}

.notif-time {
  font-size: var(--text-xs);
  color: var(--text-muted);
}
</style>

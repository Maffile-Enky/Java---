<template>
  <div class="notification-page">
    <h1 class="page-title">消息通知</h1>

    <div v-if="loading" class="loading">
      <div class="loading-spinner"></div>
    </div>

    <div v-else-if="notifications.length === 0" class="empty-state">
      <p>暂无通知</p>
    </div>

    <div v-else class="notification-list">
      <div
        v-for="item in notifications"
        :key="item.id"
        class="notification-card card"
        :class="{ unread: !item.read }"
      >
        <div class="notification-header">
          <span class="notification-title">{{ item.title || '系统通知' }}</span>
          <span class="notification-time">{{ item.createdAt }}</span>
        </div>
        <p class="notification-content">{{ item.content }}</p>
      </div>
    </div>

    <div v-if="totalPages > 1" class="pagination">
      <button class="btn-sm btn-outline" :disabled="page <= 1" @click="goPage(page - 1)">上一页</button>
      <span class="page-info">第 {{ page }} / {{ totalPages }} 页</span>
      <button class="btn-sm btn-outline" :disabled="page >= totalPages" @click="goPage(page + 1)">下一页</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getNotifications } from '@/api/notification'

const notifications = ref([])
const loading = ref(true)
const page = ref(1)
const size = ref(20)
const totalPages = ref(0)

async function loadNotifications() {
  loading.value = true
  try {
    const res = await getNotifications({ page: page.value, size: size.value })
    const data = res.data || res
    notifications.value = data.records || []
    totalPages.value = data.pages || 0
  } catch {
    notifications.value = []
  } finally {
    loading.value = false
  }
}

function goPage(p) {
  page.value = p
  loadNotifications()
}

onMounted(() => loadNotifications())
</script>

<style scoped>
.notification-page {
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

.notification-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.notification-card {
  padding: var(--spacing-lg);
  transition: background 0.2s;
}

.notification-card.unread {
  border-left: 3px solid var(--color-primary);
}

.notification-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.notification-title {
  font-family: var(--font-heading);
  font-size: var(--font-size-base);
  font-weight: 600;
}

.notification-time {
  font-size: var(--font-size-xs);
  color: var(--color-text-hint);
}

.notification-content {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  margin: 0;
  line-height: 1.6;
}

.pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-lg);
}

.btn-sm {
  padding: 6px 16px;
  border-radius: var(--radius-md);
  font-size: var(--font-size-sm);
  cursor: pointer;
  border: 1px solid var(--color-border);
  background: transparent;
  color: var(--color-text-secondary);
}

.btn-sm:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  font-size: var(--font-size-sm);
  color: var(--color-text-hint);
}
</style>

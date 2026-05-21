<template>
  <div class="application-manage">
    <h1 class="page-title">入驻审核</h1>

    <LoadingSpinner v-if="loading" text="加载中..." />
    <EmptyState v-else-if="!applications.length" icon="📝" text="暂无待审核申请" />

    <div v-else class="app-list">
      <div v-for="app in applications" :key="app.id" class="app-card glass-panel">
        <div class="app-header">
          <h3 class="app-name">{{ app.merchantName || app.name }}</h3>
          <span class="app-status tag" :class="statusClass(app.status)">{{ statusLabel(app.status) }}</span>
        </div>
        <p class="app-info">申请人: {{ app.applicantName || app.phone }}</p>
        <p class="app-info">类型: {{ app.category || '未分类' }}</p>
        <p class="app-desc">{{ app.description }}</p>
        <div class="app-actions" v-if="app.status === 'PENDING'">
          <GlassButton variant="primary" size="sm" @click="handleReview(app.id, 'APPROVED')">通过</GlassButton>
          <GlassButton variant="ghost" size="sm" @click="handleReview(app.id, 'REJECTED')">拒绝</GlassButton>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getApplicationList, approveApplication, rejectApplication } from '@/api/admin'
import GlassButton from '@/components/ui/GlassButton.vue'
import LoadingSpinner from '@/components/common/LoadingSpinner.vue'
import EmptyState from '@/components/common/EmptyState.vue'

const applications = ref([])
const loading = ref(true)

const statusMap = {
  PENDING: '待审核', APPROVED: '已通过', REJECTED: '已拒绝'
}

function statusLabel(s) { return statusMap[s] || s }
function statusClass(s) {
  if (s === 'APPROVED') return 'tag-green'
  if (s === 'REJECTED') return 'tag-danger'
  return 'tag-gold'
}

async function fetchApplications() {
  try {
    const res = await getApplicationList()
    applications.value = res.data || []
  } catch { applications.value = [] }
  finally { loading.value = false }
}

async function handleReview(id, status) {
  try {
    if (status === 'APPROVED') {
      await approveApplication(id)
    } else {
      await rejectApplication(id, '不符合入驻条件')
    }
    fetchApplications()
  } catch {}
}

onMounted(fetchApplications)
</script>

<style scoped>
.application-manage { max-width: 1000px; }

.page-title {
  font-family: var(--font-serif);
  font-size: 1.8rem;
  font-weight: 900;
  margin-bottom: var(--space-8);
}

.app-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
}

.app-card {
  padding: var(--space-6);
}

.app-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-3);
}

.app-name { font-size: var(--text-base); font-weight: 700; }

.app-info {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  margin: 0 0 var(--space-1);
}

.app-desc {
  font-size: var(--text-sm);
  color: var(--text-muted);
  margin: var(--space-2) 0 var(--space-4);
  line-height: 1.6;
}

.app-actions {
  display: flex;
  gap: var(--space-2);
}

.tag-green { background: rgba(110, 231, 160, 0.15); color: var(--accent); padding: 2px 10px; border-radius: var(--radius-full); font-size: var(--text-xs); font-weight: 600; }
.tag-gold { background: rgba(240, 197, 90, 0.15); color: var(--accent-secondary); padding: 2px 10px; border-radius: var(--radius-full); font-size: var(--text-xs); font-weight: 600; }
.tag-danger { background: rgba(239, 68, 68, 0.15); color: #ef4444; padding: 2px 10px; border-radius: var(--radius-full); font-size: var(--text-xs); font-weight: 600; }
</style>

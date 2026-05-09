<template>
  <div class="dashboard">
    <h1 class="page-title">数据概览</h1>
    <div class="stat-cards">
      <div class="stat-card card">
        <div class="stat-icon users-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="28" height="28"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/></svg>
        </div>
        <div class="stat-info">
          <div class="stat-number">{{ stats.totalUsers ?? '--' }}</div>
          <div class="stat-label">用户总数</div>
        </div>
      </div>
      <div class="stat-card card">
        <div class="stat-icon merchants-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="28" height="28"><path d="M3 3h18v18H3z"/><path d="M3 9h18"/><path d="M9 21V9"/></svg>
        </div>
        <div class="stat-info">
          <div class="stat-number">{{ stats.totalMerchants ?? '--' }}</div>
          <div class="stat-label">商家总数</div>
        </div>
      </div>
      <div class="stat-card card">
        <div class="stat-icon pending-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="28" height="28"><circle cx="12" cy="12" r="10"/><polyline points="12,6 12,12 16,14"/></svg>
        </div>
        <div class="stat-info">
          <div class="stat-number">{{ stats.pendingApplications ?? '--' }}</div>
          <div class="stat-label">待审核申请</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAdminStats } from '@/api/admin'

const stats = ref({
  totalUsers: null,
  totalMerchants: null,
  pendingApplications: null
})

onMounted(async () => {
  try {
    const res = await getAdminStats()
    stats.value = res.data || res
  } catch (e) {
    console.error('Failed to load admin stats', e)
  }
})
</script>

<style scoped>
.dashboard {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xl);
}

.page-title {
  font-size: var(--font-size-xl);
  font-weight: 700;
  margin: 0;
}

.stat-cards {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--spacing-xl);
}

.stat-card {
  padding: 28px;
  display: flex;
  align-items: center;
  gap: 20px;
  transition: transform 0.2s, box-shadow 0.2s;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.users-icon {
  background: #e6f7ff;
  color: #1890ff;
}

.merchants-icon {
  background: #fff7e6;
  color: #fa8c16;
}

.pending-icon {
  background: #fffbe6;
  color: #faad14;
}

.stat-number {
  font-size: 32px;
  font-weight: 800;
  color: var(--color-text-primary);
  line-height: 1.2;
}

.stat-label {
  font-size: var(--font-size-sm);
  color: var(--color-text-hint);
  margin-top: 4px;
}

@media (max-width: 768px) {
  .stat-cards {
    grid-template-columns: 1fr;
  }
}
</style>

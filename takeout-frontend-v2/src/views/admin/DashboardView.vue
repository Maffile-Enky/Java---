<template>
  <div class="admin-dashboard">
    <h1 class="page-title">管理后台</h1>

    <div class="stat-grid">
      <div v-for="stat in stats" :key="stat.label" class="stat-card glass-panel">
        <span class="stat-icon">{{ stat.icon }}</span>
        <div>
          <span class="stat-value">{{ stat.value }}</span>
          <span class="stat-label">{{ stat.label }}</span>
        </div>
      </div>
    </div>

    <div class="section">
      <h2 class="section-title">系统概况</h2>
      <p class="section-desc">管理后台概览数据。</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAdminStats } from '@/api/admin'

const stats = ref([
  { icon: '👥', label: '用户总数', value: '—' },
  { icon: '🏪', label: '商家总数', value: '—' },
  { icon: '📦', label: '今日订单', value: '—' },
  { icon: '💰', label: '今日收入', value: '—' }
])

onMounted(async () => {
  try {
    const res = await getAdminStats()
    const data = res.data || {}
    stats.value = [
      { icon: '👥', label: '用户总数', value: data.userCount || '—' },
      { icon: '🏪', label: '商家总数', value: data.merchantCount || '—' },
      { icon: '📦', label: '今日订单', value: data.todayOrders || '—' },
      { icon: '💰', label: '今日收入', value: data.todayRevenue ? `¥${data.todayRevenue}` : '—' }
    ]
  } catch {}
})
</script>

<style scoped>
.admin-dashboard {
  max-width: 1200px;
}

.page-title {
  font-family: var(--font-serif);
  font-size: 1.8rem;
  font-weight: 900;
  margin-bottom: var(--space-8);
}

.stat-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
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
.stat-value { font-size: 1.5rem; font-weight: 800; color: var(--accent); display: block; }
.stat-label { font-size: var(--text-xs); color: var(--text-muted); }

.section { margin-bottom: var(--space-8); }
.section-title { font-size: 1.2rem; font-weight: 700; margin-bottom: var(--space-2); }
.section-desc { font-size: var(--text-sm); color: var(--text-secondary); }
</style>

<template>
  <div class="dashboard">
    <h1 class="page-title">数据概览</h1>
    <div class="stat-cards">
      <div class="stat-card card-users">
        <div class="stat-icon">👥</div>
        <div class="stat-info">
          <div class="stat-number">{{ stats.totalUsers ?? '--' }}</div>
          <div class="stat-label">用户总数</div>
        </div>
      </div>
      <div class="stat-card card-merchants">
        <div class="stat-icon">🏪</div>
        <div class="stat-info">
          <div class="stat-number">{{ stats.totalMerchants ?? '--' }}</div>
          <div class="stat-label">商家总数</div>
        </div>
      </div>
      <div class="stat-card card-pending">
        <div class="stat-icon">📋</div>
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
  animation: fadeIn 0.4s ease;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(16px); }
  to { opacity: 1; transform: translateY(0); }
}

.page-title {
  font-size: 26px;
  font-weight: 700;
  color: #2d3436;
  margin: 0 0 28px 0;
}

.stat-cards {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}

.stat-card {
  background: #fff;
  border-radius: 12px;
  padding: 32px 28px;
  display: flex;
  align-items: center;
  gap: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  transition: transform 0.3s, box-shadow 0.3s;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.card-users {
  border-left: 4px solid #667eea;
}

.card-merchants {
  border-left: 4px solid #f093fb;
}

.card-pending {
  border-left: 4px solid #ffa502;
}

.stat-icon {
  font-size: 48px;
  width: 72px;
  height: 72px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 16px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8ecf1 100%);
}

.stat-number {
  font-size: 36px;
  font-weight: 800;
  color: #2d3436;
  line-height: 1.2;
}

.stat-label {
  font-size: 15px;
  color: #636e72;
  margin-top: 4px;
  font-weight: 500;
}
</style>

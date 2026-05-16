<template>
  <div class="application-manage">
    <h1 class="page-title">入驻审核</h1>

    <div class="tab-bar">
      <button
        v-for="tab in tabs"
        :key="tab.value"
        class="tab-btn"
        :class="{ active: activeStatus === tab.value }"
        @click="switchTab(tab.value)"
      >{{ tab.label }}</button>
    </div>

    <div class="table-card card">
      <table class="data-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>申请人ID</th>
            <th>店铺名称</th>
            <th>地址</th>
            <th>联系电话</th>
            <th>状态</th>
            <th>申请时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading">
            <td colspan="8" class="empty-cell">加载中...</td>
          </tr>
          <tr v-else-if="applications.length === 0">
            <td colspan="8" class="empty-cell">暂无数据</td>
          </tr>
          <tr v-for="app in applications" :key="app.id">
            <td>{{ app.id }}</td>
            <td>{{ app.userId }}</td>
            <td>{{ app.shopName || '-' }}</td>
            <td>{{ app.shopAddress || app.address || '-' }}</td>
            <td>{{ app.contactPhone || app.phone || '-' }}</td>
            <td>
              <span class="badge" :class="'badge-status-' + app.status">
                {{ statusMap[app.status] || '未知' }}
              </span>
            </td>
            <td>{{ app.createdAt || '-' }}</td>
            <td>
              <div class="action-group" v-if="app.status === 0">
                <button class="btn-sm btn-success" @click="handleApprove(app)">通过</button>
                <button class="btn-sm btn-danger" @click="handleReject(app)">拒绝</button>
              </div>
              <span v-else class="text-muted">-</span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="pagination">
      <button class="btn-sm btn-outline" :disabled="page <= 1" @click="goPage(page - 1)">上一页</button>
      <span class="page-info">第 {{ page }} / {{ totalPages }} 页，共 {{ total }} 条</span>
      <button class="btn-sm btn-outline" :disabled="page >= totalPages" @click="goPage(page + 1)">下一页</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getApplicationList, approveApplication, rejectApplication } from '@/api/admin'

const tabs = [
  { label: '全部', value: null },
  { label: '待审核', value: 0 },
  { label: '已通过', value: 1 },
  { label: '已拒绝', value: 2 }
]

const statusMap = { 0: '待审核', 1: '已通过', 2: '已拒绝' }

const activeStatus = ref(null)
const applications = ref([])
const loading = ref(false)
const page = ref(1)
const size = ref(10)
const total = ref(0)
const totalPages = ref(0)

const fetchApplications = async () => {
  loading.value = true
  try {
    const params = { page: page.value, size: size.value }
    if (activeStatus.value !== null) params.status = activeStatus.value
    const res = await getApplicationList(params)
    const data = res.data || res
    applications.value = data.records || []
    total.value = data.total || 0
    totalPages.value = data.pages || 0
  } catch {
    alert('加载申请列表失败')
  } finally {
    loading.value = false
  }
}

const switchTab = (status) => {
  activeStatus.value = status
  page.value = 1
  fetchApplications()
}

const goPage = (p) => {
  page.value = p
  fetchApplications()
}

const handleApprove = async (app) => {
  if (!confirm(`确认通过「${app.shopName}」的入驻申请？`)) return
  try {
    await approveApplication(app.id)
    fetchApplications()
  } catch {
    alert('操作失败')
  }
}

const handleReject = async (app) => {
  const adminNote = prompt('请输入拒绝原因：')
  if (adminNote === null) return
  try {
    await rejectApplication(app.id, adminNote)
    fetchApplications()
  } catch {
    alert('操作失败')
  }
}

onMounted(() => { fetchApplications() })
</script>

<style scoped>
.application-manage {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xl);
}

.page-title {
  font-family: var(--font-heading);
  font-size: var(--font-size-xl);
  font-weight: 700;
  margin: 0;
}

.tab-bar {
  display: flex;
  gap: var(--spacing-sm);
}

.tab-btn {
  padding: 8px 20px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-xl);
  background: var(--color-bg-card);
  font-size: var(--font-size-sm);
  font-weight: 500;
  color: var(--color-text-secondary);
  cursor: pointer;
  transition: all 0.2s;
}

.tab-btn:hover {
  border-color: var(--color-primary);
  color: var(--color-text-primary);
}

.tab-btn.active {
  background: var(--color-primary);
  color: #fff;
  border-color: var(--color-primary);
  font-weight: 600;
}

.table-card {
  overflow: hidden;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
}

.data-table th {
  background: var(--color-bg-page);
  padding: 12px 16px;
  text-align: left;
  font-size: var(--font-size-sm);
  font-weight: 600;
  color: var(--color-text-hint);
  border-bottom: 1px solid var(--color-divider);
}

.data-table td {
  padding: 12px 16px;
  font-size: var(--font-size-base);
  color: var(--color-text-primary);
  border-bottom: 1px solid var(--color-divider);
}

.data-table tbody tr:hover {
  background: var(--color-bg-page);
}

.empty-cell {
  text-align: center;
  color: var(--color-text-hint);
  padding: 40px 16px !important;
}

.badge {
  display: inline-block;
  padding: 3px 10px;
  border-radius: 12px;
  font-size: var(--font-size-xs);
  font-weight: 600;
}

.badge-status-0 { background: #FFF8EC; color: #C88A2A; }
.badge-status-1 { background: #F0F5EC; color: #4A8C5C; }
.badge-status-2 { background: #F5EDEB; color: #C84B31; }

.action-group {
  display: flex;
  align-items: center;
  gap: 8px;
}

.text-muted {
  color: var(--color-text-hint);
}

.pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-lg);
}

.page-info {
  font-size: var(--font-size-sm);
  color: var(--color-text-hint);
}
</style>

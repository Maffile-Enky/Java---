<template>
  <div class="application-manage">
    <h1 class="page-title">入驻审核</h1>

    <!-- Tab Filter -->
    <div class="tab-bar">
      <button
        v-for="tab in tabs"
        :key="tab.value"
        class="tab-btn"
        :class="{ active: activeStatus === tab.value }"
        @click="switchTab(tab.value)"
      >
        {{ tab.label }}
      </button>
    </div>

    <!-- Table -->
    <div class="table-card">
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
            <td>{{ app.address || '-' }}</td>
            <td>{{ app.phone || '-' }}</td>
            <td>
              <span class="badge" :class="'badge-status-' + app.status">
                {{ statusMap[app.status] || '未知' }}
              </span>
            </td>
            <td>{{ app.createdAt || '-' }}</td>
            <td>
              <div class="action-group" v-if="app.status === 0">
                <button class="btn btn-sm btn-success" @click="handleApprove(app)">通过</button>
                <button class="btn btn-sm btn-danger" @click="handleReject(app)">拒绝</button>
              </div>
              <span v-else class="text-muted">-</span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Pagination -->
    <div class="pagination">
      <button class="btn btn-sm" :disabled="page <= 1" @click="goPage(page - 1)">上一页</button>
      <span class="page-info">第 {{ page }} / {{ totalPages }} 页，共 {{ total }} 条</span>
      <button class="btn btn-sm" :disabled="page >= totalPages" @click="goPage(page + 1)">下一页</button>
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

const statusMap = {
  0: '待审核',
  1: '已通过',
  2: '已拒绝'
}

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
    if (activeStatus.value !== null) {
      params.status = activeStatus.value
    }
    const res = await getApplicationList(params)
    const data = res.data || res
    applications.value = data.records || []
    total.value = data.total || 0
    totalPages.value = data.pages || 0
  } catch (e) {
    console.error('Failed to load applications', e)
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
    alert('已通过')
    fetchApplications()
  } catch (e) {
    console.error('Failed to approve', e)
    alert('操作失败')
  }
}

const handleReject = async (app) => {
  const adminNote = prompt('请输入拒绝原因：')
  if (adminNote === null) return
  try {
    await rejectApplication(app.id, adminNote)
    alert('已拒绝')
    fetchApplications()
  } catch (e) {
    console.error('Failed to reject', e)
    alert('操作失败')
  }
}

onMounted(() => {
  fetchApplications()
})
</script>

<style scoped>
.application-manage {
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
  margin: 0 0 24px 0;
}

.tab-bar {
  display: flex;
  gap: 8px;
  margin-bottom: 20px;
}

.tab-btn {
  padding: 8px 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
  background: #fff;
  font-size: 14px;
  font-weight: 500;
  color: #636e72;
  cursor: pointer;
  transition: all 0.25s;
}

.tab-btn:hover {
  border-color: #667eea;
  color: #667eea;
}

.tab-btn.active {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: #fff;
  border-color: transparent;
}

.btn {
  padding: 10px 20px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.25s;
}

.btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn-success {
  background: #00b894;
  color: #fff;
}

.btn-success:hover {
  background: #00a381;
}

.btn-danger {
  background: #ff6b6b;
  color: #fff;
}

.btn-danger:hover {
  background: #ee5a5a;
}

.btn-sm {
  padding: 6px 14px;
  font-size: 13px;
  border-radius: 6px;
}

.table-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  overflow: hidden;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
}

.data-table th {
  background: #f8f9fb;
  padding: 14px 16px;
  text-align: left;
  font-size: 13px;
  font-weight: 600;
  color: #636e72;
  border-bottom: 1px solid #eee;
}

.data-table td {
  padding: 14px 16px;
  font-size: 14px;
  color: #2d3436;
  border-bottom: 1px solid #f0f0f0;
}

.data-table tbody tr:hover {
  background: #f8f9ff;
}

.empty-cell {
  text-align: center;
  color: #b2bec3;
  padding: 40px 16px !important;
}

.badge {
  display: inline-block;
  padding: 3px 10px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
}

.badge-status-0 {
  background: #fff3e0;
  color: #e67e22;
}

.badge-status-1 {
  background: #e8f5e9;
  color: #27ae60;
}

.badge-status-2 {
  background: #fde8e8;
  color: #e74c3c;
}

.action-group {
  display: flex;
  align-items: center;
  gap: 8px;
}

.text-muted {
  color: #b2bec3;
  font-size: 14px;
}

.pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  margin-top: 24px;
}

.page-info {
  font-size: 14px;
  color: #636e72;
}
</style>

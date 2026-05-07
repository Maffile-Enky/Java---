<template>
  <div class="user-manage">
    <h1 class="page-title">用户管理</h1>

    <!-- Search Bar -->
    <div class="search-bar">
      <input
        v-model="keyword"
        type="text"
        placeholder="搜索用户名/昵称/手机号..."
        @keyup.enter="handleSearch"
      />
      <button class="btn btn-primary" @click="handleSearch">搜索</button>
    </div>

    <!-- Table -->
    <div class="table-card">
      <table class="data-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>用户名</th>
            <th>昵称</th>
            <th>手机号</th>
            <th>角色</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading">
            <td colspan="7" class="empty-cell">加载中...</td>
          </tr>
          <tr v-else-if="users.length === 0">
            <td colspan="7" class="empty-cell">暂无数据</td>
          </tr>
          <tr v-for="user in users" :key="user.id">
            <td>{{ user.id }}</td>
            <td>{{ user.username }}</td>
            <td>{{ user.nickname || '-' }}</td>
            <td>{{ user.phone || '-' }}</td>
            <td>
              <span class="badge" :class="'badge-role-' + (user.role || 'USER').toLowerCase()">
                {{ user.role || 'USER' }}
              </span>
            </td>
            <td>
              <span class="badge" :class="user.status === 1 ? 'badge-active' : 'badge-disabled'">
                {{ user.status === 1 ? '正常' : '禁用' }}
              </span>
            </td>
            <td>
              <div class="action-group">
                <button
                  class="btn btn-sm"
                  :class="user.status === 1 ? 'btn-danger' : 'btn-success'"
                  @click="toggleStatus(user)"
                >
                  {{ user.status === 1 ? '禁用' : '启用' }}
                </button>
                <select
                  class="role-select"
                  :value="user.role"
                  @change="handleChangeRole(user, $event.target.value)"
                >
                  <option value="USER">USER</option>
                  <option value="MERCHANT">MERCHANT</option>
                  <option value="RIDER">RIDER</option>
                  <option value="ADMIN">ADMIN</option>
                </select>
              </div>
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
import { getAdminUserList, updateUserStatus, updateUserRole } from '@/api/admin'

const keyword = ref('')
const users = ref([])
const loading = ref(false)
const page = ref(1)
const size = ref(10)
const total = ref(0)
const totalPages = ref(0)

const fetchUsers = async () => {
  loading.value = true
  try {
    const res = await getAdminUserList({ page: page.value, size: size.value, keyword: keyword.value })
    const data = res.data || res
    users.value = data.records || []
    total.value = data.total || 0
    totalPages.value = data.pages || 0
  } catch (e) {
    console.error('Failed to load users', e)
    alert('加载用户列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  page.value = 1
  fetchUsers()
}

const goPage = (p) => {
  page.value = p
  fetchUsers()
}

const toggleStatus = async (user) => {
  const newStatus = user.status === 1 ? 0 : 1
  try {
    await updateUserStatus(user.id, newStatus)
    user.status = newStatus
    alert(newStatus === 1 ? '已启用' : '已禁用')
  } catch (e) {
    console.error('Failed to update status', e)
    alert('操作失败')
  }
}

const handleChangeRole = async (user, newRole) => {
  try {
    await updateUserRole(user.id, newRole)
    user.role = newRole
    alert('角色已更新为 ' + newRole)
  } catch (e) {
    console.error('Failed to update role', e)
    alert('操作失败')
  }
}

onMounted(() => {
  fetchUsers()
})
</script>

<style scoped>
.user-manage {
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

.search-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.search-bar input {
  flex: 1;
  max-width: 360px;
  padding: 10px 16px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 14px;
  outline: none;
  transition: border-color 0.3s;
}

.search-bar input:focus {
  border-color: #667eea;
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

.btn-primary {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: #fff;
}

.btn-primary:hover {
  opacity: 0.9;
  transform: translateY(-1px);
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

.badge-role-user {
  background: #e8f0fe;
  color: #1a73e8;
}

.badge-role-merchant {
  background: #fff3e0;
  color: #e67e22;
}

.badge-role-rider {
  background: #e8f5e9;
  color: #27ae60;
}

.badge-role-admin {
  background: #fde8e8;
  color: #e74c3c;
}

.badge-active {
  background: #e8f5e9;
  color: #27ae60;
}

.badge-disabled {
  background: #fde8e8;
  color: #e74c3c;
}

.action-group {
  display: flex;
  align-items: center;
  gap: 8px;
}

.role-select {
  padding: 6px 10px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 13px;
  outline: none;
  background: #fff;
  cursor: pointer;
}

.role-select:focus {
  border-color: #667eea;
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

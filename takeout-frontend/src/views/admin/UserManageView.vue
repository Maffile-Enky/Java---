<template>
  <div class="user-manage">
    <h1 class="page-title">用户管理</h1>

    <div class="search-bar">
      <div class="search-input">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="18" height="18"><circle cx="11" cy="11" r="8"/><path d="m21 21-4.35-4.35"/></svg>
        <input v-model="keyword" type="text" placeholder="搜索用户名/昵称/手机号..." @keyup.enter="handleSearch" />
      </div>
      <button class="btn-primary" @click="handleSearch">搜索</button>
    </div>

    <div class="table-card card">
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
                  class="btn-sm"
                  :class="user.status === 1 ? 'btn-danger' : 'btn-success'"
                  @click="toggleStatus(user)"
                >{{ user.status === 1 ? '禁用' : '启用' }}</button>
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

    <div class="pagination">
      <button class="btn-sm btn-outline" :disabled="page <= 1" @click="goPage(page - 1)">上一页</button>
      <span class="page-info">第 {{ page }} / {{ totalPages }} 页，共 {{ total }} 条</span>
      <button class="btn-sm btn-outline" :disabled="page >= totalPages" @click="goPage(page + 1)">下一页</button>
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
  } catch {
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
  } catch {
    alert('操作失败')
  }
}

const handleChangeRole = async (user, newRole) => {
  try {
    await updateUserRole(user.id, newRole)
    user.role = newRole
  } catch {
    alert('操作失败')
  }
}

onMounted(() => { fetchUsers() })
</script>

<style scoped>
.user-manage {
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

.search-bar {
  display: flex;
  gap: var(--spacing-sm);
}

.search-input {
  display: flex;
  align-items: center;
  gap: 8px;
  background: var(--color-bg-card);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  padding: 0 var(--spacing-md);
  height: 40px;
  width: 320px;
}

.search-input input {
  flex: 1;
  border: none;
  background: transparent;
  height: 100%;
  font-size: var(--font-size-base);
}

.search-input svg {
  color: var(--color-text-hint);
  flex-shrink: 0;
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

.badge-role-user { background: #EDF3F8; color: #5B8DB8; }
.badge-role-merchant { background: #FFF8EC; color: #C88A2A; }
.badge-role-rider { background: #F0F5EC; color: #4A8C5C; }
.badge-role-admin { background: #F5EDEB; color: #C84B31; }
.badge-active { background: #F0F5EC; color: #4A8C5C; }
.badge-disabled { background: #F5EDEB; color: #C84B31; }

.action-group {
  display: flex;
  align-items: center;
  gap: 8px;
}

.role-select {
  padding: 5px 8px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
  font-size: var(--font-size-sm);
  outline: none;
  background: var(--color-bg-card);
  cursor: pointer;
}

.role-select:focus {
  border-color: var(--color-primary);
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

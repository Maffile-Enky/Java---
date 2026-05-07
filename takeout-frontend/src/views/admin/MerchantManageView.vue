<template>
  <div class="merchant-manage">
    <h1 class="page-title">商家管理</h1>

    <!-- Table -->
    <div class="table-card">
      <table class="data-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>商家名称</th>
            <th>地址</th>
            <th>联系电话</th>
            <th>状态</th>
            <th>排序权重</th>
            <th>用户ID</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading">
            <td colspan="8" class="empty-cell">加载中...</td>
          </tr>
          <tr v-else-if="merchants.length === 0">
            <td colspan="8" class="empty-cell">暂无数据</td>
          </tr>
          <tr v-for="merchant in merchants" :key="merchant.id">
            <td>{{ merchant.id }}</td>
            <td>{{ merchant.name }}</td>
            <td>{{ merchant.address || '-' }}</td>
            <td>{{ merchant.phone || '-' }}</td>
            <td>
              <span class="badge" :class="merchant.status === 1 ? 'badge-active' : 'badge-disabled'">
                {{ merchant.status === 1 ? '营业中' : '已关闭' }}
              </span>
            </td>
            <td>
              <div class="sort-weight-group">
                <input
                  type="number"
                  class="sort-input"
                  v-model.number="merchant._editWeight"
                  min="0"
                />
                <button class="btn btn-sm btn-primary" @click="handleSaveWeight(merchant)">保存</button>
              </div>
            </td>
            <td>{{ merchant.userId || '-' }}</td>
            <td>
              <button
                class="btn btn-sm"
                :class="merchant.status === 1 ? 'btn-danger' : 'btn-success'"
                @click="toggleStatus(merchant)"
              >
                {{ merchant.status === 1 ? '禁用' : '启用' }}
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAdminMerchantList, updateMerchantStatus, updateMerchantSortWeight } from '@/api/admin'

const merchants = ref([])
const loading = ref(false)

const fetchMerchants = async () => {
  loading.value = true
  try {
    const res = await getAdminMerchantList()
    const list = res.data || res
    merchants.value = (Array.isArray(list) ? list : []).map(m => ({
      ...m,
      _editWeight: m.sortWeight ?? 0
    }))
  } catch (e) {
    console.error('Failed to load merchants', e)
    alert('加载商家列表失败')
  } finally {
    loading.value = false
  }
}

const toggleStatus = async (merchant) => {
  const newStatus = merchant.status === 1 ? 0 : 1
  try {
    await updateMerchantStatus(merchant.id, newStatus)
    merchant.status = newStatus
    alert(newStatus === 1 ? '已启用' : '已禁用')
  } catch (e) {
    console.error('Failed to update status', e)
    alert('操作失败')
  }
}

const handleSaveWeight = async (merchant) => {
  try {
    await updateMerchantSortWeight(merchant.id, merchant._editWeight)
    merchant.sortWeight = merchant._editWeight
    alert('排序权重已更新')
  } catch (e) {
    console.error('Failed to update sort weight', e)
    alert('操作失败')
  }
}

onMounted(() => {
  fetchMerchants()
})
</script>

<style scoped>
.merchant-manage {
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

.badge-active {
  background: #e8f5e9;
  color: #27ae60;
}

.badge-disabled {
  background: #fde8e8;
  color: #e74c3c;
}

.sort-weight-group {
  display: flex;
  align-items: center;
  gap: 8px;
}

.sort-input {
  width: 64px;
  padding: 6px 8px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 13px;
  text-align: center;
  outline: none;
  transition: border-color 0.3s;
}

.sort-input:focus {
  border-color: #667eea;
}
</style>

<template>
  <div class="merchant-manage">
    <h1 class="page-title">商家管理</h1>

    <div class="table-card card">
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
                <input type="number" class="sort-input" v-model.number="merchant._editWeight" min="0" />
                <button class="btn-sm btn-primary" @click="handleSaveWeight(merchant)">保存</button>
              </div>
            </td>
            <td>{{ merchant.userId || '-' }}</td>
            <td>
              <button
                class="btn-sm"
                :class="merchant.status === 1 ? 'btn-danger' : 'btn-success'"
                @click="toggleStatus(merchant)"
              >{{ merchant.status === 1 ? '禁用' : '启用' }}</button>
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
  } catch {
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
  } catch {
    alert('操作失败')
  }
}

const handleSaveWeight = async (merchant) => {
  try {
    await updateMerchantSortWeight(merchant.id, merchant._editWeight)
    merchant.sortWeight = merchant._editWeight
  } catch {
    alert('操作失败')
  }
}

onMounted(() => { fetchMerchants() })
</script>

<style scoped>
.merchant-manage {
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

.badge-active { background: #F0F5EC; color: #4A8C5C; }
.badge-disabled { background: #F5EDEB; color: #C84B31; }

.sort-weight-group {
  display: flex;
  align-items: center;
  gap: 8px;
}

.sort-input {
  width: 60px;
  padding: 5px 8px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
  font-size: var(--font-size-sm);
  text-align: center;
  outline: none;
}

.sort-input:focus {
  border-color: var(--color-primary);
}
</style>

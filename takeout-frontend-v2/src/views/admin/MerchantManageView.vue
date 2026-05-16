<template>
  <div class="merchant-manage">
    <h1 class="page-title">商家管理</h1>

    <LoadingSpinner v-if="loading" text="加载中..." />
    <EmptyState v-else-if="!merchants.length" icon="🏪" text="暂无商家" />

    <div v-else class="merchant-table">
      <div v-for="m in merchants" :key="m.id" class="merchant-row glass-panel">
        <div class="m-info">
          <h3 class="m-name">{{ m.name }}</h3>
          <span class="m-addr">{{ m.address }}</span>
        </div>
        <span class="m-status tag" :class="m.status === 'APPROVED' ? 'tag-green' : 'tag-muted'">
          {{ m.status === 'APPROVED' ? '营业中' : '未审核' }}
        </span>
        <span class="m-phone">{{ m.phone }}</span>
        <div class="m-actions">
          <button class="action-btn" @click="toggleStatus(m)">
            {{ m.status === 'APPROVED' ? '暂停' : '启用' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAdminMerchantList, updateMerchantStatus } from '@/api/admin'
import LoadingSpinner from '@/components/common/LoadingSpinner.vue'
import EmptyState from '@/components/common/EmptyState.vue'

const merchants = ref([])
const loading = ref(true)

async function fetchMerchants() {
  try {
    const res = await getAdminMerchantList()
    merchants.value = res.data || []
  } catch { merchants.value = [] }
  finally { loading.value = false }
}

async function toggleStatus(m) {
  try {
    const newStatus = m.status === 'APPROVED' ? 'SUSPENDED' : 'APPROVED'
    await updateMerchantStatus(m.id, newStatus)
    fetchMerchants()
  } catch {}
}

onMounted(fetchMerchants)
</script>

<style scoped>
.merchant-manage { max-width: 1200px; }

.page-title {
  font-family: var(--font-serif);
  font-size: 1.8rem;
  font-weight: 900;
  margin-bottom: var(--space-8);
}

.merchant-table {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

.merchant-row {
  display: grid;
  grid-template-columns: 1fr auto 140px auto;
  align-items: center;
  gap: var(--space-4);
  padding: var(--space-4);
}

.m-name { font-size: var(--text-sm); font-weight: 600; margin-bottom: 2px; }
.m-addr { font-size: var(--text-xs); color: var(--text-muted); }
.m-phone { font-size: var(--text-sm); color: var(--text-secondary); }

.action-btn {
  padding: var(--space-1) var(--space-3);
  background: var(--glass);
  border: 1px solid var(--glass-border);
  border-radius: var(--radius-sm);
  color: var(--text-secondary);
  font-size: var(--text-xs);
  cursor: pointer;
  transition: all var(--duration-fast);
  font-family: var(--font-sans);
}

.action-btn:hover { border-color: var(--accent); color: var(--text-primary); }

.tag-green { background: rgba(110, 231, 160, 0.15); color: var(--accent); padding: 2px 10px; border-radius: var(--radius-full); font-size: var(--text-xs); font-weight: 600; }
.tag-muted { background: var(--glass); color: var(--text-muted); padding: 2px 10px; border-radius: var(--radius-full); font-size: var(--text-xs); }
</style>

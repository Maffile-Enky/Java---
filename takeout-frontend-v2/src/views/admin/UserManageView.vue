<template>
  <div class="user-manage">
    <h1 class="page-title">用户管理</h1>

    <LoadingSpinner v-if="loading" text="加载中..." />
    <EmptyState v-else-if="!users.length" icon="👥" text="暂无用户" />

    <div v-else class="user-table">
      <div v-for="user in users" :key="user.id" class="user-row glass-panel">
        <div class="user-avatar">{{ (user.nickname || 'U').charAt(0) }}</div>
        <div class="user-meta">
          <h3 class="user-name">{{ user.nickname || '未设置' }}</h3>
          <span class="user-phone">{{ user.phone }}</span>
        </div>
        <span class="user-role tag" :class="roleClass(user.role)">{{ roleLabel(user.role) }}</span>
        <span class="user-date">{{ formatDate(user.createTime) }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAdminUserList } from '@/api/admin'
import LoadingSpinner from '@/components/common/LoadingSpinner.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import dayjs from 'dayjs'

const users = ref([])
const loading = ref(true)

const roleMap = { USER: '用户', MERCHANT: '商家', ADMIN: '管理员', RIDER: '骑手' }

function roleLabel(role) { return roleMap[role] || role }
function roleClass(role) {
  if (role === 'ADMIN') return 'tag-gold'
  if (role === 'MERCHANT') return 'tag-green'
  return 'tag-muted'
}

function formatDate(val) { return val ? dayjs(val).format('YYYY-MM-DD') : '' }

onMounted(async () => {
  try {
    const res = await getAdminUserList()
    users.value = res.data || []
  } catch { users.value = [] }
  finally { loading.value = false }
})
</script>

<style scoped>
.user-manage { max-width: 1200px; }

.page-title {
  font-family: var(--font-serif);
  font-size: 1.8rem;
  font-weight: 900;
  margin-bottom: var(--space-8);
}

.user-table {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

.user-row {
  display: grid;
  grid-template-columns: 44px 1fr auto 120px;
  align-items: center;
  gap: var(--space-4);
  padding: var(--space-3) var(--space-4);
}

.user-avatar {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: var(--gradient-green);
  color: var(--text-inverse);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
}

.user-name { font-size: var(--text-sm); font-weight: 600; margin-bottom: 2px; }
.user-phone { font-size: var(--text-xs); color: var(--text-muted); }
.user-date { font-size: var(--text-xs); color: var(--text-muted); text-align: right; }

.tag-green { background: rgba(110, 231, 160, 0.15); color: var(--accent); padding: 2px 10px; border-radius: var(--radius-full); font-size: var(--text-xs); font-weight: 600; }
.tag-gold { background: rgba(240, 197, 90, 0.15); color: var(--accent-secondary); padding: 2px 10px; border-radius: var(--radius-full); font-size: var(--text-xs); font-weight: 600; }
.tag-muted { background: var(--glass); color: var(--text-muted); padding: 2px 10px; border-radius: var(--radius-full); font-size: var(--text-xs); }
</style>

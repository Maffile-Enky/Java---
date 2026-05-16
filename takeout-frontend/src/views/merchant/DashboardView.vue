<template>
  <div class="dashboard-view">
    <h1 class="page-title">店铺概览</h1>

    <div v-if="loading" class="loading">
      <div class="loading-spinner"></div>
    </div>

    <div v-else-if="!merchant" class="create-section">
      <div class="card create-card">
        <h2>创建您的店铺</h2>
        <p class="card-desc">您还没有店铺，请填写以下信息创建店铺。</p>
        <form @submit.prevent="handleCreate" class="merchant-form">
          <div class="form-group">
            <label>店铺名称</label>
            <input v-model="form.name" type="text" placeholder="请输入店铺名称" required />
          </div>
          <div class="form-group">
            <label>店铺地址</label>
            <input v-model="form.address" type="text" placeholder="请输入店铺地址" required />
          </div>
          <div class="form-group">
            <label>联系电话</label>
            <input v-model="form.phone" type="text" placeholder="请输入联系电话" required />
          </div>
          <div class="form-group">
            <label>店铺描述</label>
            <textarea v-model="form.description" placeholder="请输入店铺描述" rows="3"></textarea>
          </div>
          <button type="submit" class="btn-primary" :disabled="submitting">
            {{ submitting ? '创建中...' : '创建店铺' }}
          </button>
        </form>
      </div>
    </div>

    <div v-else class="info-section">
      <!-- Shop info card -->
      <div class="card info-card">
        <div class="info-header">
          <h2 class="shop-name">{{ merchant.name }}</h2>
          <span :class="['status-badge', merchant.status === 1 ? 'status-open' : 'status-closed']">
            {{ merchant.status === 1 ? '营业中' : '已关闭' }}
          </span>
        </div>
        <div class="info-grid">
          <div class="info-item">
            <span class="info-label">地址</span>
            <span class="info-value">{{ merchant.address || '-' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">电话</span>
            <span class="info-value">{{ merchant.phone || '-' }}</span>
          </div>
          <div class="info-item full-width">
            <span class="info-label">描述</span>
            <span class="info-value">{{ merchant.description || '暂无描述' }}</span>
          </div>
        </div>
      </div>

      <!-- Quick actions -->
      <div class="quick-grid">
        <router-link to="/merchant/dishes" class="quick-card card">
          <div class="quick-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="28" height="28"><path d="M18 8h1a4 4 0 0 1 0 8h-1"/><path d="M2 8h16v9a4 4 0 0 1-4 4H6a4 4 0 0 1-4-4V8z"/><line x1="6" y1="1" x2="6" y2="4"/><line x1="10" y1="1" x2="10" y2="4"/><line x1="14" y1="1" x2="14" y2="4"/></svg>
          </div>
          <h3>菜品管理</h3>
          <p>管理您的菜品信息</p>
        </router-link>
        <router-link to="/merchant/settings" class="quick-card card">
          <div class="quick-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="28" height="28"><circle cx="12" cy="12" r="3"/><path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06A1.65 1.65 0 0 0 4.68 15a1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06A1.65 1.65 0 0 0 9 4.68a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z"/></svg>
          </div>
          <h3>店铺设置</h3>
          <p>编辑店铺资料</p>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMyMerchant, createMyMerchant } from '@/api/merchant'

const merchant = ref(null)
const loading = ref(true)
const submitting = ref(false)

const form = ref({
  name: '',
  address: '',
  phone: '',
  description: ''
})

onMounted(async () => {
  try {
    merchant.value = await getMyMerchant()
  } catch {
    merchant.value = null
  } finally {
    loading.value = false
  }
})

async function handleCreate() {
  submitting.value = true
  try {
    await createMyMerchant(form.value)
    alert('店铺创建成功!')
    merchant.value = await getMyMerchant()
  } catch (e) {
    alert('创建失败: ' + (e.message || '请重试'))
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.dashboard-view {
  max-width: 800px;
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

.loading {
  display: flex;
  justify-content: center;
  padding: 80px;
}

.loading-spinner {
  width: 32px;
  height: 32px;
  border: 3px solid var(--color-border);
  border-top-color: var(--color-primary);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin { to { transform: rotate(360deg); } }

/* Create card */
.create-card {
  padding: 32px;
}

.create-card h2 {
  font-family: var(--font-heading);
  font-size: var(--font-size-lg);
  font-weight: 700;
  margin: 0 0 8px 0;
}

.card-desc {
  color: var(--color-text-hint);
  font-size: var(--font-size-sm);
  margin: 0 0 24px 0;
}

.merchant-form {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-group label {
  font-size: var(--font-size-sm);
  font-weight: 600;
  color: var(--color-text-secondary);
}

.form-group input,
.form-group textarea {
  padding: 10px 14px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  font-size: var(--font-size-base);
  transition: border-color 0.2s;
}

.form-group input:focus,
.form-group textarea:focus {
  border-color: var(--color-primary);
}

.form-group textarea {
  resize: vertical;
  font-family: inherit;
}

/* Info card */
.info-card {
  padding: 24px;
}

.info-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--color-divider);
}

.shop-name {
  font-family: var(--font-heading);
  font-size: var(--font-size-lg);
  font-weight: 700;
  margin: 0;
}

.status-badge {
  padding: 4px 14px;
  border-radius: 20px;
  font-size: var(--font-size-xs);
  font-weight: 600;
}

.status-open { background: #F0F5EC; color: #4A8C5C; }
.status-closed { background: #F5EDEB; color: #C84B31; }

.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-item.full-width {
  grid-column: 1 / -1;
}

.info-label {
  font-size: var(--font-size-xs);
  color: var(--color-text-hint);
}

.info-value {
  font-size: var(--font-size-base);
  color: var(--color-text-primary);
}

/* Quick links */
.quick-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--spacing-lg);
}

.quick-card {
  text-align: center;
  padding: 28px 20px;
  text-decoration: none;
  transition: transform 0.2s, box-shadow 0.2s;
}

.quick-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.quick-icon {
  margin-bottom: 12px;
  color: var(--color-primary);
}

.quick-card h3 {
  font-family: var(--font-heading);
  font-size: var(--font-size-md);
  font-weight: 600;
  margin: 0 0 6px 0;
  color: var(--color-text-primary);
}

.quick-card p {
  font-size: var(--font-size-sm);
  color: var(--color-text-hint);
  margin: 0;
}
</style>

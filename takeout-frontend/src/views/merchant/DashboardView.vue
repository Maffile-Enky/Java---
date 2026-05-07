<template>
  <div class="dashboard-view">
    <h1 class="page-title">店铺概览</h1>

    <div v-if="loading" class="loading-state">加载中...</div>

    <div v-else-if="!merchant" class="create-section">
      <div class="card create-card">
        <h2 class="card-title">创建您的店铺</h2>
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

      <div class="quick-links">
        <router-link to="/merchant/dishes" class="quick-card">
          <div class="quick-icon">&#9638;</div>
          <h3>前往菜品管理</h3>
          <p>管理您的菜品信息</p>
        </router-link>
        <router-link to="/merchant/settings" class="quick-card">
          <div class="quick-icon">&#9881;</div>
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
}

.page-title {
  font-size: 24px;
  font-weight: 700;
  color: #333;
  margin: 0 0 24px 0;
}

.loading-state {
  text-align: center;
  padding: 60px 0;
  color: #999;
  font-size: 16px;
}

.card {
  background: #fff;
  border-radius: 12px;
  padding: 32px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.card-title {
  font-size: 20px;
  font-weight: 700;
  color: #333;
  margin: 0 0 8px 0;
}

.card-desc {
  color: #888;
  font-size: 14px;
  margin: 0 0 24px 0;
}

.merchant-form {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-group label {
  font-size: 14px;
  font-weight: 600;
  color: #555;
}

.form-group input,
.form-group textarea {
  padding: 10px 14px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 14px;
  outline: none;
  transition: border-color 0.2s;
}

.form-group input:focus,
.form-group textarea:focus {
  border-color: #ff6b00;
}

.form-group textarea {
  resize: vertical;
  font-family: inherit;
}

.btn-primary {
  padding: 12px 0;
  background: #ff6b00;
  color: #fff;
  border: none;
  border-radius: 8px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s;
  align-self: flex-start;
  min-width: 140px;
}

.btn-primary:hover {
  background: #e66000;
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.info-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
}

.shop-name {
  font-size: 22px;
  font-weight: 700;
  color: #333;
  margin: 0;
}

.status-badge {
  display: inline-block;
  padding: 4px 14px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
}

.status-open {
  background: #e6f9ee;
  color: #1db954;
}

.status-closed {
  background: #fde8e8;
  color: #e74c3c;
}

.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
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
  font-size: 13px;
  color: #999;
}

.info-value {
  font-size: 15px;
  color: #333;
}

.quick-links {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-top: 24px;
}

.quick-card {
  background: #fff;
  border-radius: 12px;
  padding: 28px 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  text-decoration: none;
  text-align: center;
  transition: transform 0.2s, box-shadow 0.2s;
}

.quick-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.quick-icon {
  font-size: 32px;
  margin-bottom: 12px;
}

.quick-card h3 {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 6px 0;
}

.quick-card p {
  font-size: 13px;
  color: #999;
  margin: 0;
}
</style>

<template>
  <div class="settings-view">
    <h1 class="page-title">店铺设置</h1>

    <div v-if="loading" class="loading">
      <div class="loading-spinner"></div>
    </div>

    <div v-else class="card settings-card">
      <div class="status-row">
        <span class="status-label">店铺状态</span>
        <span :class="['status-badge', form.status === 1 ? 'status-open' : 'status-closed']">
          {{ form.status === 1 ? '营业中' : '已关闭' }}
        </span>
      </div>

      <form @submit.prevent="handleSave" class="settings-form">
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
          <textarea v-model="form.description" placeholder="请输入店铺描述" rows="4"></textarea>
        </div>
        <div class="form-group">
          <label>店铺图片 URL</label>
          <input v-model="form.imageUrl" type="url" placeholder="https://example.com/image.jpg" />
        </div>
        <div class="form-actions">
          <button type="submit" class="btn-primary" :disabled="saving">
            {{ saving ? '保存中...' : '保存' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getMyMerchant, updateMyMerchant } from '@/api/merchant'

const router = useRouter()
const loading = ref(true)
const saving = ref(false)

const form = ref({
  id: null,
  name: '',
  address: '',
  phone: '',
  description: '',
  imageUrl: '',
  status: 1
})

onMounted(async () => {
  try {
    const merchant = await getMyMerchant()
    if (!merchant) {
      alert('请先创建店铺')
      router.push('/merchant')
      return
    }
    form.value = {
      id: merchant.id,
      name: merchant.name || '',
      address: merchant.address || '',
      phone: merchant.phone || '',
      description: merchant.description || '',
      imageUrl: merchant.imageUrl || '',
      status: merchant.status ?? 1
    }
  } catch {
    alert('请先创建店铺')
    router.push('/merchant')
  } finally {
    loading.value = false
  }
})

async function handleSave() {
  saving.value = true
  try {
    await updateMyMerchant({ ...form.value })
    alert('保存成功!')
  } catch (e) {
    alert('保存失败: ' + (e.message || '请重试'))
  } finally {
    saving.value = false
  }
}
</script>

<style scoped>
.settings-view {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xl);
  max-width: 640px;
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

.settings-card {
  padding: 24px;
}

.status-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--color-divider);
}

.status-label {
  font-size: var(--font-size-base);
  font-weight: 600;
  color: var(--color-text-secondary);
}

.status-badge {
  padding: 4px 14px;
  border-radius: 20px;
  font-size: var(--font-size-xs);
  font-weight: 600;
}

.status-open { background: #F0F5EC; color: #4A8C5C; }
.status-closed { background: #F5EDEB; color: #C84B31; }

.settings-form {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xl);
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

.form-actions {
  padding-top: 8px;
}
</style>

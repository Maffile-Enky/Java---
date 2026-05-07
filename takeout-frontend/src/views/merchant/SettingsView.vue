<template>
  <div class="settings-view">
    <h1 class="page-title">店铺设置</h1>

    <div v-if="loading" class="loading-state">加载中...</div>

    <div v-else class="card">
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
  max-width: 640px;
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

.status-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 28px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.status-label {
  font-size: 14px;
  font-weight: 600;
  color: #555;
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

.settings-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
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

.form-actions {
  padding-top: 8px;
}

.btn-primary {
  padding: 12px 32px;
  background: #ff6b00;
  color: #fff;
  border: none;
  border-radius: 8px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s;
}

.btn-primary:hover {
  background: #e66000;
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>

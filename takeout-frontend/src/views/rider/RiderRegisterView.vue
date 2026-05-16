<template>
  <Layout>
    <div class="register-page">
      <h1 class="page-title">成为骑手</h1>
      <div class="card form-card">
        <form @submit.prevent="handleRegister">
          <div class="form-group">
            <label>姓名</label>
            <input v-model="form.name" type="text" placeholder="请输入真实姓名" required />
          </div>
          <div class="form-group">
            <label>手机号</label>
            <input v-model="form.phone" type="tel" placeholder="请输入手机号" required />
          </div>
          <div class="form-group">
            <label>身份证号</label>
            <input v-model="form.idCard" type="text" placeholder="请输入身份证号" required />
          </div>
          <div class="form-group">
            <label>交通工具</label>
            <select v-model="form.vehicleType">
              <option value="电动车">电动车</option>
              <option value="摩托车">摩托车</option>
              <option value="自行车">自行车</option>
              <option value="步行">步行</option>
            </select>
          </div>
          <button type="submit" class="btn-primary" :disabled="submitting">
            {{ submitting ? '提交中...' : '提交注册' }}
          </button>
        </form>
      </div>
    </div>
  </Layout>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import Layout from '@/components/Layout.vue'
import { registerRider } from '@/api/delivery'

const router = useRouter()
const submitting = ref(false)

const form = ref({
  name: '',
  phone: '',
  idCard: '',
  vehicleType: '电动车'
})

async function handleRegister() {
  submitting.value = true
  try {
    await registerRider(form.value)
    alert('注册成功!')
    router.push('/rider')
  } catch (e) {
    alert('注册失败: ' + (e.message || '请重试'))
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.register-page {
  max-width: 480px;
  margin: 0 auto;
}

.page-title {
  font-family: var(--font-heading);
  font-size: var(--font-size-xl);
  font-weight: 700;
  margin: 0 0 var(--spacing-xl) 0;
}

.form-card {
  padding: 24px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: var(--spacing-xl);
}

.form-group label {
  font-size: var(--font-size-sm);
  font-weight: 600;
  color: var(--color-text-secondary);
}

.form-group input,
.form-group select {
  padding: 10px 14px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  font-size: var(--font-size-base);
  transition: border-color 0.2s;
}

.form-group input:focus,
.form-group select:focus {
  border-color: var(--color-primary);
}

.btn-primary {
  width: 100%;
  background: var(--color-primary);
  color: #fff;
  padding: 12px;
  border-radius: var(--radius-xl);
  font-size: var(--font-size-base);
  font-weight: 600;
  cursor: pointer;
  border: none;
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>

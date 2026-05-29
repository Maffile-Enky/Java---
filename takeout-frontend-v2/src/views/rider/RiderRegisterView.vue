<template>
  <div class="rider-register">
    <div class="register-card glass-panel">
      <h1 class="page-title">注册成为骑手</h1>
      <p class="page-subtitle">加入味觉星球骑手团队，灵活接单赚取收入</p>

      <form class="register-form" @submit.prevent="handleRegister">
        <GlassInput v-model="form.realName" placeholder="真实姓名" label="姓名" />
        <GlassInput v-model="form.phone" type="tel" placeholder="手机号" label="手机号" />
        <GlassInput v-model="form.idCard" placeholder="身份证号" label="身份证号" />
        <GlassInput v-model="form.vehicleType" placeholder="如: 电动车、汽车" label="交通工具" />
        <GlassInput v-model="form.licensePlate" placeholder="车牌号（选填）" label="车牌号" />
        <GlassButton type="submit" variant="primary" block :loading="loading">
          提交申请
        </GlassButton>
      </form>

      <div class="register-footer">
        <span>已有账号？</span>
        <router-link to="/auth/login" class="footer-link">去登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { registerRider } from '@/api/delivery'
import GlassInput from '@/components/ui/GlassInput.vue'
import GlassButton from '@/components/ui/GlassButton.vue'

const router = useRouter()
const loading = ref(false)

const form = reactive({
  realName: '', phone: '', idCard: '', vehicleType: '', licensePlate: ''
})

async function handleRegister() {
  loading.value = true
  try {
    await registerRider({
      name: form.realName,
      phone: form.phone,
      idCardNo: form.idCard,
      vehicleType: form.vehicleType
    })
    alert('注册成功')
    router.push('/rider')
  } catch (e) {
    alert('注册失败: ' + (e.message || '未知错误'))
  }
  finally { loading.value = false }
}
</script>

<style scoped>
.rider-register {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: var(--space-6);
}

.register-card {
  width: 100%;
  max-width: 450px;
  padding: var(--space-8);
}

.page-title {
  font-family: var(--font-serif);
  font-size: 1.5rem;
  font-weight: 900;
  margin-bottom: var(--space-2);
  text-align: center;
}

.page-subtitle {
  font-size: var(--text-sm);
  color: var(--text-muted);
  text-align: center;
  margin-bottom: var(--space-8);
}

.register-form {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
}

.register-footer {
  text-align: center;
  margin-top: var(--space-6);
  font-size: var(--text-sm);
  color: var(--text-muted);
}

.footer-link {
  color: var(--accent);
  text-decoration: none;
  font-weight: 600;
  margin-left: var(--space-1);
}
</style>

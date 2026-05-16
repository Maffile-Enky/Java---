<template>
  <div class="settings-page">
    <h1 class="page-title">店铺设置</h1>

    <LoadingSpinner v-if="loading" text="加载中..." />

    <template v-else>
      <form class="settings-form" @submit.prevent="saveSettings">
        <div class="section glass-panel">
          <h2 class="section-title">基本信息</h2>
          <GlassInput v-model="form.name" placeholder="店铺名称" label="店铺名称" />
          <GlassInput v-model="form.address" placeholder="店铺地址" label="地址" />
          <GlassInput v-model="form.phone" type="tel" placeholder="联系电话" label="电话" />
          <GlassInput v-model="form.description" placeholder="店铺简介" label="简介" />
        </div>

        <div class="section glass-panel">
          <h2 class="section-title">营业设置</h2>
          <GlassInput v-model="form.coverImage" placeholder="封面图URL" label="封面图" />
          <GlassInput v-model="form.openingHours" placeholder="如: 09:00-22:00" label="营业时间" />
        </div>

        <GlassButton type="submit" variant="primary" :loading="saving">
          保存设置
        </GlassButton>
      </form>
    </template>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getMyMerchant, updateMyMerchant } from '@/api/merchant'
import GlassInput from '@/components/ui/GlassInput.vue'
import GlassButton from '@/components/ui/GlassButton.vue'
import LoadingSpinner from '@/components/common/LoadingSpinner.vue'

const loading = ref(true)
const saving = ref(false)
const merchantId = ref(null)

const form = reactive({
  name: '', address: '', phone: '', description: '',
  coverImage: '', openingHours: ''
})

async function fetchMerchant() {
  try {
    const res = await getMyMerchant()
    const data = res.data || res
    merchantId.value = data.id
    Object.assign(form, {
      name: data.name || '', address: data.address || '',
      phone: data.phone || '', description: data.description || '',
      coverImage: data.coverImage || '', openingHours: data.openingHours || ''
    })
  } catch {}
  finally { loading.value = false }
}

async function saveSettings() {
  saving.value = true
  try {
    await updateMyMerchant(form)
  } catch {}
  finally { saving.value = false }
}

onMounted(fetchMerchant)
</script>

<style scoped>
.settings-page {
  max-width: 800px;
}

.page-title {
  font-family: var(--font-serif);
  font-size: 1.8rem;
  font-weight: 900;
  margin-bottom: var(--space-8);
}

.settings-form {
  display: flex;
  flex-direction: column;
  gap: var(--space-6);
}

.section {
  padding: var(--space-6);
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
}

.section-title {
  font-size: 1.1rem;
  font-weight: 700;
  margin-bottom: var(--space-2);
}
</style>

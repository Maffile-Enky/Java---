<template>
  <div class="address-page">
    <div class="container">
      <div class="detail-header">
        <button class="back-btn" @click="$router.back()">← 返回</button>
        <h1 class="page-title">收货地址</h1>
      </div>

      <LoadingSpinner v-if="loading" text="加载中..." />

      <EmptyState v-else-if="!addresses.length" icon="📍" text="暂无地址">
        <GlassButton variant="primary" size="sm" style="margin-top:16px;" @click="showAdd = true">
          添加地址
        </GlassButton>
      </EmptyState>

      <div v-else class="address-list">
        <div v-for="addr in addresses" :key="addr.id" class="address-card glass-panel">
          <div class="addr-info">
            <div class="addr-top">
              <span class="addr-name">{{ addr.name }}</span>
              <span class="addr-phone">{{ addr.phone }}</span>
              <span v-if="addr.isDefault" class="addr-default tag tag-green">默认</span>
            </div>
            <p class="addr-detail">{{ addr.province }}{{ addr.city }}{{ addr.district }}{{ addr.detail }}</p>
          </div>
          <div class="addr-actions">
            <button class="action-btn" @click="editAddress(addr)">编辑</button>
            <button class="action-btn action-btn--danger" @click="deleteAddress(addr.id)">删除</button>
          </div>
        </div>
        <GlassButton variant="primary" block @click="showAdd = true">
          添加新地址
        </GlassButton>
      </div>

      <!-- Add/Edit Modal -->
      <GlassModal v-model="showAdd" :title="editingId ? '编辑地址' : '添加地址'">
        <form class="addr-form" @submit.prevent="saveAddress">
          <GlassInput v-model="form.name" placeholder="收货人姓名" label="姓名" />
          <GlassInput v-model="form.phone" type="tel" placeholder="手机号" label="手机号" />
          <GlassInput v-model="form.province" placeholder="省份" label="省份" />
          <GlassInput v-model="form.city" placeholder="城市" label="城市" />
          <GlassInput v-model="form.district" placeholder="区/县" label="区/县" />
          <GlassInput v-model="form.detail" placeholder="详细地址" label="详细地址" />
          <label class="checkbox-label">
            <input type="checkbox" v-model="form.isDefault" />
            <span>设为默认地址</span>
          </label>
          <GlassButton type="submit" variant="primary" block :loading="saving">
            保存
          </GlassButton>
        </form>
      </GlassModal>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getAddressList, saveAddress as saveAddressApi, updateAddress, deleteAddress as deleteApi } from '@/api/user'
import GlassButton from '@/components/ui/GlassButton.vue'
import GlassInput from '@/components/ui/GlassInput.vue'
import GlassModal from '@/components/ui/GlassModal.vue'
import LoadingSpinner from '@/components/common/LoadingSpinner.vue'
import EmptyState from '@/components/common/EmptyState.vue'

const addresses = ref([])
const loading = ref(true)
const showAdd = ref(false)
const saving = ref(false)
const editingId = ref(null)

const form = reactive({
  name: '', phone: '', province: '', city: '', district: '', detail: '', isDefault: false
})

function resetForm() {
  Object.assign(form, { name: '', phone: '', province: '', city: '', district: '', detail: '', isDefault: false })
  editingId.value = null
}

function editAddress(addr) {
  editingId.value = addr.id
  Object.assign(form, {
    name: addr.name, phone: addr.phone, province: addr.province || '',
    city: addr.city || '', district: addr.district || '',
    detail: addr.detail, isDefault: addr.isDefault
  })
  showAdd.value = true
}

async function fetchAddresses() {
  try {
    const res = await getAddressList()
    addresses.value = res.data || []
  } catch { addresses.value = [] }
  finally { loading.value = false }
}

async function saveAddress() {
  saving.value = true
  try {
    if (editingId.value) {
      await updateAddress(editingId.value, form)
    } else {
      await saveAddressApi(form)
    }
    showAdd.value = false
    resetForm()
    fetchAddresses()
  } catch {}
  finally { saving.value = false }
}

async function deleteAddress(id) {
  if (!confirm('确定删除该地址？')) return
  try {
    await deleteApi(id)
    fetchAddresses()
  } catch {}
}

onMounted(fetchAddresses)
</script>

<style scoped>
.address-page {
  padding: var(--space-6) 0;
}

.detail-header {
  display: flex;
  align-items: center;
  gap: var(--space-4);
  margin-bottom: var(--space-6);
}

.back-btn {
  background: none;
  border: none;
  color: var(--text-secondary);
  font-size: var(--text-sm);
  cursor: pointer;
  font-family: var(--font-sans);
}

.back-btn:hover { color: var(--accent); }

.page-title {
  font-family: var(--font-serif);
  font-size: 1.8rem;
  font-weight: 900;
}

.address-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-3);
}

.address-card {
  padding: var(--space-5);
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: var(--space-4);
}

.addr-info { flex: 1; }

.addr-top {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  margin-bottom: var(--space-1);
}

.addr-name { font-weight: 700; }
.addr-phone { color: var(--text-secondary); font-size: var(--text-sm); }
.addr-detail { font-size: var(--text-sm); color: var(--text-muted); margin: 0; }

.addr-actions {
  display: flex;
  gap: var(--space-2);
  flex-shrink: 0;
}

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
.action-btn--danger:hover { border-color: #ef4444; color: #ef4444; }

.addr-form {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  font-size: var(--text-sm);
  color: var(--text-secondary);
  cursor: pointer;
}

.checkbox-label input[type="checkbox"] {
  accent-color: var(--accent);
}
</style>

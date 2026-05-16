<template>
  <div class="address-view">
    <div class="page-header">
      <button class="back-btn" @click="$router.back()">←</button>
      <h3>收货地址</h3>
      <button class="add-btn" @click="openForm()">+ 新增</button>
    </div>

    <!-- 地址列表 -->
    <div v-if="addresses.length === 0" class="empty">
      <p>📍 暂无收货地址</p>
      <button @click="openForm()">添加地址</button>
    </div>

    <div v-else class="address-list">
      <div v-for="addr in addresses" :key="addr.id" class="address-card glass-panel">
        <div class="addr-header">
          <span class="addr-name">{{ addr.name }}</span>
          <span class="addr-phone">{{ addr.phone }}</span>
          <span v-if="addr.isDefault" class="default-tag">默认</span>
        </div>
        <div class="addr-detail">
          {{ addr.province }}{{ addr.city }}{{ addr.district }} {{ addr.detail }}
        </div>
        <div class="addr-actions">
          <button v-if="!addr.isDefault" @click="handleSetDefault(addr.id)">设为默认</button>
          <button @click="openForm(addr)">编辑</button>
          <button class="delete" @click="handleDelete(addr.id)">删除</button>
        </div>
      </div>
    </div>

    <!-- 新增/编辑弹窗 -->
    <div v-if="showForm" class="modal-overlay" @click.self="showForm = false">
      <div class="modal glass-panel">
        <h3>{{ editingId ? '编辑地址' : '新增地址' }}</h3>
        <form @submit.prevent="handleSave">
          <input v-model="form.name" placeholder="联系人姓名" required />
          <input v-model="form.phone" placeholder="联系电话" required />
          <div class="row">
            <input v-model="form.province" placeholder="省" />
            <input v-model="form.city" placeholder="市" />
            <input v-model="form.district" placeholder="区" />
          </div>
          <input v-model="form.detail" placeholder="详细地址" required />
          <div class="form-actions">
            <button type="button" @click="showForm = false">取消</button>
            <button type="submit" class="primary">保存</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAddressList, saveAddress, updateAddress, deleteAddress, setDefaultAddress } from '@/api/user'

const addresses = ref([])
const showForm = ref(false)
const editingId = ref(null)
const form = ref({ name: '', phone: '', province: '', city: '', district: '', detail: '' })

const loadAddresses = async () => {
  try {
    const res = await getAddressList()
    addresses.value = res.data || []
  } catch {
    addresses.value = []
  }
}

const openForm = (addr) => {
  if (addr) {
    editingId.value = addr.id
    form.value = { ...addr }
  } else {
    editingId.value = null
    form.value = { name: '', phone: '', province: '', city: '', district: '', detail: '' }
  }
  showForm.value = true
}

const handleSave = async () => {
  try {
    if (editingId.value) {
      await updateAddress(editingId.value, form.value)
    } else {
      await saveAddress(form.value)
    }
    showForm.value = false
    loadAddresses()
  } catch (e) {
    alert(e?.response?.data?.message || '保存失败')
  }
}

const handleDelete = async (id) => {
  if (!confirm('确定删除该地址？')) return
  try {
    await deleteAddress(id)
    loadAddresses()
  } catch (e) {
    alert('删除失败')
  }
}

const handleSetDefault = async (id) => {
  try {
    await setDefaultAddress(id)
    loadAddresses()
  } catch {
    alert('设置失败')
  }
}

onMounted(loadAddresses)
</script>

<style scoped>
.address-view { padding-bottom: 20px; }
.page-header {
  display: flex; align-items: center; justify-content: space-between;
  padding: 16px 0; margin-bottom: 10px;
}
.page-header h3 { margin: 0; font-family: var(--font-heading); font-size: 18px; }
.back-btn {
  background: none; border: none; font-size: 20px; cursor: pointer;
  padding: 4px 8px; border-radius: 6px;
}
.back-btn:hover { background: var(--color-bg-hover); }
.add-btn {
  background: var(--color-primary); color: #fff; border: none;
  padding: 8px 16px; border-radius: 8px; font-size: 14px; cursor: pointer;
}
.empty { text-align: center; padding: 60px 0; color: #999; }
.empty button {
  margin-top: 12px; padding: 10px 24px; background: var(--color-primary);
  color: #fff; border: none; border-radius: 8px; cursor: pointer;
}
.address-list { display: flex; flex-direction: column; gap: 10px; }
.address-card { padding: 16px; }
.addr-header { display: flex; align-items: center; gap: 10px; margin-bottom: 8px; }
.addr-name { font-weight: 600; font-size: 16px; }
.addr-phone { color: #666; }
.default-tag {
  background: var(--color-primary); color: #fff; font-size: 12px;
  padding: 2px 8px; border-radius: 10px;
}
.addr-detail { color: #666; font-size: 14px; margin-bottom: 12px; }
.addr-actions { display: flex; gap: 10px; }
.addr-actions button {
  padding: 6px 12px; border: 1px solid #ddd; border-radius: 6px;
  background: #fff; font-size: 13px; cursor: pointer;
}
.addr-actions button:hover { border-color: var(--color-primary); color: var(--color-primary); background: var(--color-bg-hover); }
.addr-actions button.delete { color: var(--color-error); border-color: var(--color-error); }
.addr-actions button.delete:hover { background: #F5EDEB; }

/* Modal */
.modal-overlay {
  position: fixed; top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(45, 35, 25, 0.5); display: flex; align-items: center;
  justify-content: center; z-index: 1000;
}
.modal {
  width: 90%; max-width: 450px; padding: 24px;
}
.modal h3 { margin: 0 0 20px 0; font-family: var(--font-heading); }
.modal input {
  width: 100%; padding: 10px 12px; border: 1px solid #ddd;
  border-radius: 8px; font-size: 14px; margin-bottom: 12px;
  box-sizing: border-box; outline: none;
}
.modal input:focus { border-color: var(--color-primary); box-shadow: 0 0 0 3px rgba(200, 75, 49, 0.1); }
.row { display: flex; gap: 8px; }
.row input { flex: 1; }
.form-actions { display: flex; gap: 10px; justify-content: flex-end; margin-top: 8px; }
.form-actions button {
  padding: 10px 20px; border: 1px solid #ddd; border-radius: 8px;
  background: #fff; cursor: pointer;
}
.form-actions button.primary {
  background: var(--color-primary); color: #fff; border-color: var(--color-primary);
}
</style>

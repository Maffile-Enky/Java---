<template>
  <div class="dish-manage">
    <div class="page-header">
      <h1 class="page-title">菜品管理</h1>
      <button v-if="merchant" class="btn-primary" @click="openAddModal">添加菜品</button>
    </div>

    <div v-if="loading" class="loading-state">加载中...</div>

    <div v-else-if="!merchant" class="empty-state">
      <div class="empty-icon">&#9888;</div>
      <h2>请先创建店铺</h2>
      <p>您需要先创建店铺才能管理菜品。</p>
      <router-link to="/merchant" class="btn-primary">前往创建店铺</router-link>
    </div>

    <div v-else-if="dishes.length === 0" class="empty-state">
      <div class="empty-icon">&#9638;</div>
      <h2>暂无菜品</h2>
      <p>点击上方"添加菜品"按钮添加您的第一个菜品。</p>
    </div>

    <div v-else class="dish-grid">
      <div v-for="dish in dishes" :key="dish.id" class="dish-card">
        <div class="dish-header">
          <h3 class="dish-name">{{ dish.name }}</h3>
          <span :class="['status-badge', dish.status === 1 ? 'status-on' : 'status-off']">
            {{ dish.status === 1 ? '上架' : '下架' }}
          </span>
        </div>
        <div class="dish-price">&yen;{{ dish.price }}</div>
        <p class="dish-desc">{{ dish.description || '暂无描述' }}</p>
        <div class="dish-stock">库存: {{ dish.stock }}</div>
        <div class="dish-actions">
          <button class="btn-edit" @click="openEditModal(dish)">编辑</button>
          <button class="btn-delete" @click="handleDelete(dish)">删除</button>
        </div>
      </div>
    </div>

    <div v-if="showModal" class="modal-overlay" @click.self="closeModal">
      <div class="modal">
        <h2 class="modal-title">{{ isEditing ? '编辑菜品' : '添加菜品' }}</h2>
        <form @submit.prevent="handleSubmit" class="dish-form">
          <div class="form-group">
            <label>菜品名称</label>
            <input v-model="dishForm.name" type="text" placeholder="请输入菜品名称" required />
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>价格</label>
              <input v-model.number="dishForm.price" type="number" step="0.01" min="0" placeholder="0.00" required />
            </div>
            <div class="form-group">
              <label>库存</label>
              <input v-model.number="dishForm.stock" type="number" min="0" placeholder="0" required />
            </div>
          </div>
          <div class="form-group">
            <label>描述</label>
            <textarea v-model="dishForm.description" placeholder="请输入菜品描述" rows="3"></textarea>
          </div>
          <div class="form-group">
            <label>状态</label>
            <div class="status-toggle">
              <button type="button" :class="['toggle-btn', dishForm.status === 1 ? 'active-on' : '']"
                @click="dishForm.status = 1">上架</button>
              <button type="button" :class="['toggle-btn', dishForm.status === 0 ? 'active-off' : '']"
                @click="dishForm.status = 0">下架</button>
            </div>
          </div>
          <div class="modal-actions">
            <button type="button" class="btn-cancel" @click="closeModal">取消</button>
            <button type="submit" class="btn-primary" :disabled="submitting">
              {{ submitting ? '保存中...' : '保存' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMyMerchant, getDishList, createDish, updateDish, deleteDish } from '@/api/merchant'

const merchant = ref(null)
const dishes = ref([])
const loading = ref(true)
const showModal = ref(false)
const isEditing = ref(false)
const submitting = ref(false)
const editingId = ref(null)

const dishForm = ref({
  name: '',
  price: '',
  description: '',
  stock: '',
  status: 1
})

onMounted(async () => {
  try {
    merchant.value = await getMyMerchant()
    if (merchant.value) {
      await loadDishes()
    }
  } catch {
    merchant.value = null
  } finally {
    loading.value = false
  }
})

async function loadDishes() {
  try {
    const res = await getDishList(merchant.value.id)
    dishes.value = res.data || res || []
  } catch {
    dishes.value = []
  }
}

function openAddModal() {
  isEditing.value = false
  editingId.value = null
  dishForm.value = { name: '', price: '', description: '', stock: '', status: 1 }
  showModal.value = true
}

function openEditModal(dish) {
  isEditing.value = true
  editingId.value = dish.id
  dishForm.value = {
    name: dish.name,
    price: dish.price,
    description: dish.description || '',
    stock: dish.stock,
    status: dish.status
  }
  showModal.value = true
}

function closeModal() {
  showModal.value = false
  submitting.value = false
}

async function handleSubmit() {
  submitting.value = true
  try {
    if (isEditing.value) {
      await updateDish({ id: editingId.value, merchantId: merchant.value.id, ...dishForm.value })
      alert('菜品更新成功!')
    } else {
      await createDish({ merchantId: merchant.value.id, ...dishForm.value })
      alert('菜品添加成功!')
    }
    closeModal()
    await loadDishes()
  } catch (e) {
    alert('操作失败: ' + (e.message || '请重试'))
    submitting.value = false
  }
}

async function handleDelete(dish) {
  if (!confirm(`确定要删除菜品"${dish.name}"吗?`)) return
  try {
    await deleteDish(dish.id)
    alert('删除成功!')
    await loadDishes()
  } catch (e) {
    alert('删除失败: ' + (e.message || '请重试'))
  }
}
</script>

<style scoped>
.dish-manage {
  max-width: 960px;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
}

.page-title {
  font-size: 24px;
  font-weight: 700;
  color: #333;
  margin: 0;
}

.loading-state {
  text-align: center;
  padding: 60px 0;
  color: #999;
  font-size: 16px;
}

.empty-state {
  background: #fff;
  border-radius: 12px;
  padding: 60px 32px;
  text-align: center;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.empty-state h2 {
  font-size: 20px;
  color: #333;
  margin: 0 0 8px 0;
}

.empty-state p {
  color: #999;
  font-size: 14px;
  margin: 0 0 24px 0;
}

.dish-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.dish-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.dish-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
}

.dish-name {
  font-size: 17px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.status-badge {
  display: inline-block;
  padding: 2px 10px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
}

.status-on {
  background: #e6f9ee;
  color: #1db954;
}

.status-off {
  background: #fde8e8;
  color: #e74c3c;
}

.dish-price {
  font-size: 20px;
  font-weight: 700;
  color: #ff6b00;
  margin-bottom: 8px;
}

.dish-desc {
  font-size: 13px;
  color: #888;
  margin: 0 0 12px 0;
  line-height: 1.5;
}

.dish-stock {
  font-size: 13px;
  color: #999;
  margin-bottom: 16px;
}

.dish-actions {
  display: flex;
  gap: 10px;
}

.btn-primary {
  padding: 10px 20px;
  background: #ff6b00;
  color: #fff;
  border: none;
  border-radius: 8px;
  font-size: 14px;
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

.btn-edit {
  flex: 1;
  padding: 8px 0;
  background: #fff;
  color: #ff6b00;
  border: 1px solid #ff6b00;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s, color 0.2s;
}

.btn-edit:hover {
  background: #fff5ee;
}

.btn-delete {
  flex: 1;
  padding: 8px 0;
  background: #fff;
  color: #e74c3c;
  border: 1px solid #e74c3c;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s;
}

.btn-delete:hover {
  background: #fef2f2;
}

/* Modal */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.45);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal {
  background: #fff;
  border-radius: 12px;
  padding: 32px;
  width: 480px;
  max-width: 90vw;
  max-height: 85vh;
  overflow-y: auto;
  box-shadow: 0 8px 40px rgba(0, 0, 0, 0.15);
}

.modal-title {
  font-size: 20px;
  font-weight: 700;
  color: #333;
  margin: 0 0 24px 0;
}

.dish-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
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

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.status-toggle {
  display: flex;
  gap: 8px;
}

.toggle-btn {
  padding: 8px 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
  background: #fff;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
}

.toggle-btn.active-on {
  border-color: #1db954;
  background: #e6f9ee;
  color: #1db954;
  font-weight: 600;
}

.toggle-btn.active-off {
  border-color: #e74c3c;
  background: #fde8e8;
  color: #e74c3c;
  font-weight: 600;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 8px;
}

.btn-cancel {
  padding: 10px 20px;
  background: #f5f5f5;
  color: #666;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  transition: background 0.2s;
}

.btn-cancel:hover {
  background: #eee;
}
</style>

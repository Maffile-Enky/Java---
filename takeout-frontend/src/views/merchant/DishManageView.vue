<template>
  <div class="dish-manage">
    <div class="page-header">
      <h1 class="page-title">菜品管理</h1>
      <button v-if="merchant" class="btn-primary" @click="openAddModal">添加菜品</button>
    </div>

    <div v-if="loading" class="loading">
      <div class="loading-spinner"></div>
    </div>

    <div v-else-if="!merchant" class="empty-state card">
      <h2>请先创建店铺</h2>
      <p>您需要先创建店铺才能管理菜品。</p>
      <router-link to="/merchant" class="btn-primary">前往创建店铺</router-link>
    </div>

    <div v-else-if="dishes.length === 0" class="empty-state card">
      <h2>暂无菜品</h2>
      <p>点击上方"添加菜品"按钮添加您的第一个菜品。</p>
    </div>

    <div v-else class="dish-list">
      <div v-for="dish in dishes" :key="dish.id" class="dish-card card">
        <div class="dish-img">
          <img :src="dish.imageUrl || '/images/placeholders/dish-default.png'" :alt="dish.name" />
        </div>
        <div class="dish-info">
          <div class="dish-header">
            <h3 class="dish-name">{{ dish.name }}</h3>
            <span :class="['status-tag', dish.status === 1 ? 'tag-on' : 'tag-off']">
              {{ dish.status === 1 ? '上架' : '下架' }}
            </span>
          </div>
          <p class="dish-desc">{{ dish.description || '暂无描述' }}</p>
          <div class="dish-meta">
            <span class="dish-price">¥{{ dish.price }}</span>
            <span class="dish-stock">库存: {{ dish.stock }}</span>
          </div>
          <div class="dish-actions">
            <button class="btn-sm btn-edit" @click="openEditModal(dish)">编辑</button>
            <button class="btn-sm btn-delete" @click="handleDelete(dish)">删除</button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal -->
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
    await loadDishes()
  } catch (e) {
    alert('删除失败: ' + (e.message || '请重试'))
  }
}
</script>

<style scoped>
.dish-manage {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xl);
  max-width: 960px;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.page-title {
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

.empty-state {
  text-align: center;
  padding: 60px 32px;
}

.empty-state h2 {
  font-size: var(--font-size-lg);
  margin: 0 0 8px 0;
}

.empty-state p {
  color: var(--color-text-hint);
  margin: 0 0 20px 0;
}

/* Dish list */
.dish-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.dish-card {
  display: flex;
  gap: var(--spacing-lg);
  padding: var(--spacing-lg);
}

.dish-img {
  width: 90px;
  height: 90px;
  border-radius: var(--radius-md);
  overflow: hidden;
  flex-shrink: 0;
  background: var(--color-bg-page);
}

.dish-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.dish-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.dish-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 4px;
}

.dish-name {
  font-size: var(--font-size-md);
  font-weight: 600;
  margin: 0;
}

.status-tag {
  padding: 2px 10px;
  border-radius: 12px;
  font-size: var(--font-size-xs);
  font-weight: 500;
}

.tag-on { background: #f0fff0; color: var(--color-success); }
.tag-off { background: #fff2f0; color: var(--color-error); }

.dish-desc {
  font-size: var(--font-size-sm);
  color: var(--color-text-hint);
  margin: 0 0 8px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.dish-meta {
  display: flex;
  gap: var(--spacing-lg);
  margin-bottom: 8px;
}

.dish-price {
  font-size: var(--font-size-md);
  font-weight: 700;
  color: var(--color-accent);
}

.dish-stock {
  font-size: var(--font-size-sm);
  color: var(--color-text-hint);
}

.dish-actions {
  display: flex;
  gap: var(--spacing-sm);
}

.btn-sm {
  padding: 5px 14px;
  border-radius: var(--radius-md);
  font-size: var(--font-size-sm);
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-edit {
  background: var(--color-bg-card);
  color: var(--color-accent);
  border: 1px solid var(--color-accent);
}

.btn-edit:hover { background: #fff5ee; }

.btn-delete {
  background: var(--color-bg-card);
  color: var(--color-error);
  border: 1px solid var(--color-error);
}

.btn-delete:hover { background: #fff2f0; }

/* Modal */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.45);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal {
  background: var(--color-bg-card);
  border-radius: var(--radius-xl);
  padding: 28px;
  width: 480px;
  max-width: 90vw;
  max-height: 85vh;
  overflow-y: auto;
  box-shadow: var(--shadow-lg);
}

.modal-title {
  font-size: var(--font-size-lg);
  font-weight: 700;
  margin: 0 0 20px 0;
}

.dish-form {
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

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--spacing-lg);
}

.status-toggle {
  display: flex;
  gap: var(--spacing-sm);
}

.toggle-btn {
  padding: 8px 20px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  background: var(--color-bg-card);
  font-size: var(--font-size-base);
  cursor: pointer;
  transition: all 0.2s;
}

.toggle-btn.active-on {
  border-color: var(--color-success);
  background: #f0fff0;
  color: var(--color-success);
  font-weight: 600;
}

.toggle-btn.active-off {
  border-color: var(--color-error);
  background: #fff2f0;
  color: var(--color-error);
  font-weight: 600;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: var(--spacing-md);
}

.btn-cancel {
  padding: 10px 20px;
  background: var(--color-bg-page);
  color: var(--color-text-secondary);
  border: none;
  border-radius: var(--radius-md);
  font-size: var(--font-size-base);
  cursor: pointer;
}
</style>

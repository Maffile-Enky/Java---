<template>
  <div class="dish-manage">
    <div class="page-header">
      <h1 class="page-title">菜品管理</h1>
      <GlassButton variant="primary" size="sm" @click="openAdd">+ 添加菜品</GlassButton>
    </div>

    <LoadingSpinner v-if="loading" text="加载中..." />

    <EmptyState v-else-if="!dishes.length" icon="🍜" text="暂无菜品" />

    <div v-else class="dish-table">
      <div v-for="dish in dishes" :key="dish.id" class="dish-row glass-panel">
        <div class="dish-img-tiny">
          <img v-if="dish.image" :src="dish.image" :alt="dish.name" />
          <span v-else>🍜</span>
        </div>
        <div class="dish-meta">
          <h3 class="dish-name">{{ dish.name }}</h3>
          <span class="dish-cat">{{ dish.category || '未分类' }}</span>
        </div>
        <span class="dish-price">¥{{ Number(dish.price).toFixed(2) }}</span>
        <span class="dish-status" :class="{ active: dish.status === 'ON' }">
          {{ dish.status === 'ON' ? '在售' : '下架' }}
        </span>
        <div class="dish-actions">
          <button class="action-btn" @click="openEdit(dish)">编辑</button>
          <button class="action-btn action-btn--danger" @click="toggleStatus(dish)">
            {{ dish.status === 'ON' ? '下架' : '上架' }}
          </button>
        </div>
      </div>
    </div>

    <!-- Add/Edit Modal -->
    <GlassModal v-model="showModal" :title="editingId ? '编辑菜品' : '添加菜品'">
      <form class="dish-form" @submit.prevent="saveDish">
        <GlassInput v-model="form.name" placeholder="菜品名称" label="名称" />
        <GlassInput v-model="form.description" placeholder="菜品描述" label="描述" />
        <GlassInput v-model="form.price" type="number" placeholder="价格" label="价格" />
        <GlassInput v-model="form.category" placeholder="分类" label="分类" />
        <GlassInput v-model="form.image" placeholder="图片URL" label="图片" />
        <GlassButton type="submit" variant="primary" block :loading="saving">保存</GlassButton>
      </form>
    </GlassModal>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getMyMerchant } from '@/api/merchant'
import { createDish, updateDish, getDishList } from '@/api/merchant'
import { useAuthStore } from '@/stores/auth'
import GlassButton from '@/components/ui/GlassButton.vue'
import GlassInput from '@/components/ui/GlassInput.vue'
import GlassModal from '@/components/ui/GlassModal.vue'
import LoadingSpinner from '@/components/common/LoadingSpinner.vue'
import EmptyState from '@/components/common/EmptyState.vue'

const auth = useAuthStore()
const dishes = ref([])
const loading = ref(true)
const showModal = ref(false)
const saving = ref(false)
const editingId = ref(null)
const merchantId = ref(null)

const form = reactive({ name: '', description: '', price: '', category: '', image: '' })

function resetForm() {
  Object.assign(form, { name: '', description: '', price: '', category: '', image: '' })
  editingId.value = null
}

function openAdd() {
  resetForm()
  showModal.value = true
}

function openEdit(dish) {
  editingId.value = dish.id
  Object.assign(form, {
    name: dish.name, description: dish.description || '',
    price: dish.price, category: dish.category || '', image: dish.image || ''
  })
  showModal.value = true
}

async function fetchDishes() {
  loading.value = true
  try {
    if (!merchantId.value) {
      const mRes = await getMyMerchant()
      merchantId.value = mRes.data?.id || mRes.id
    }
    const res = await getDishList(merchantId.value)
    dishes.value = res.data || res || []
  } catch { dishes.value = [] }
  finally { loading.value = false }
}

async function saveDish() {
  saving.value = true
  try {
    if (editingId.value) {
      await updateDish(editingId.value, form)
    } else {
      await createDish({ ...form, merchantId: merchantId.value })
    }
    showModal.value = false
    resetForm()
    fetchDishes()
  } catch {}
  finally { saving.value = false }
}

async function toggleStatus(dish) {
  try {
    await updateDish(dish.id, { status: dish.status === 'ON' ? 'OFF' : 'ON' })
    fetchDishes()
  } catch {}
}

onMounted(fetchDishes)
</script>

<style scoped>
.dish-manage {
  max-width: 1200px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-8);
}

.page-title {
  font-family: var(--font-serif);
  font-size: 1.8rem;
  font-weight: 900;
}

.dish-table {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

.dish-row {
  display: grid;
  grid-template-columns: 50px 1fr 100px 60px auto;
  align-items: center;
  gap: var(--space-4);
  padding: var(--space-3) var(--space-4);
}

.dish-img-tiny {
  width: 50px;
  height: 50px;
  border-radius: var(--radius-md);
  overflow: hidden;
  background: var(--bg-elevated);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
}

.dish-img-tiny img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.dish-name {
  font-size: var(--text-sm);
  font-weight: 600;
  margin-bottom: 2px;
}

.dish-cat {
  font-size: var(--text-xs);
  color: var(--text-muted);
}

.dish-price {
  font-weight: 700;
  color: var(--accent);
}

.dish-status {
  font-size: var(--text-xs);
  font-weight: 600;
  color: var(--text-muted);
}

.dish-status.active { color: var(--accent); }

.dish-actions {
  display: flex;
  gap: var(--space-2);
  justify-content: flex-end;
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

.dish-form {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
}

@media (max-width: 768px) {
  .dish-row { grid-template-columns: 40px 1fr auto; }
  .dish-cat, .dish-status { display: none; }
  .dish-actions { flex-direction: column; }
}
</style>

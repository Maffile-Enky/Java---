<template>
  <div class="restaurant-detail">
    <div v-if="loading" class="loading">
      <div class="loading-spinner"></div>
      <span>加载中...</span>
    </div>

    <template v-else-if="restaurant">
      <!-- Header -->
      <div class="detail-header card">
        <div class="header-cover">
          <img :src="restaurant.imageUrl || '/images/placeholders/merchant-default.png'" :alt="restaurant.name" />
        </div>
        <div class="header-info">
          <h1 class="shop-name">{{ restaurant.name }}</h1>
          <div class="shop-meta">
            <span class="rating">
              <svg viewBox="0 0 24 24" fill="currentColor" width="14" height="14"><path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/></svg>
              {{ restaurant.rating || '4.5' }}
            </span>
            <span class="meta-item">月售{{ restaurant.monthlySales || 100 }}+</span>
            <span class="meta-item">{{ restaurant.deliveryTime || 30 }}分钟送达</span>
          </div>
          <div class="shop-addr">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="14" height="14"><path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/><circle cx="12" cy="10" r="3"/></svg>
            {{ restaurant.address || '暂无地址' }}
          </div>
        </div>
      </div>

      <!-- Menu -->
      <div class="menu-container">
        <!-- Category sidebar -->
        <div class="category-sidebar">
          <div
            v-for="cat in categories"
            :key="cat.id"
            class="cat-item"
            :class="{ active: activeCategoryId === cat.id }"
            @click="activeCategoryId = cat.id"
          >
            {{ cat.name }}
          </div>
        </div>

        <!-- Dish list -->
        <div class="dish-list">
          <div v-for="food in filteredFoods" :key="food.id" class="dish-card">
            <div class="dish-img">
              <img :src="food.imageUrl || '/images/placeholders/dish-default.png'" :alt="food.name" />
            </div>
            <div class="dish-info">
              <h3 class="dish-name">{{ food.name }}</h3>
              <p class="dish-desc">{{ food.description || '暂无描述' }}</p>
              <div class="dish-bottom">
                <span class="dish-price">¥{{ food.price }}</span>
                <div class="dish-actions">
                  <button
                    v-if="cartStore.getQuantity(food.id) > 0"
                    class="btn-circle btn-minus"
                    @click="decreaseFood(food)"
                  >-</button>
                  <span v-if="cartStore.getQuantity(food.id) > 0" class="qty">{{ cartStore.getQuantity(food.id) }}</span>
                  <button class="btn-circle btn-plus" @click="increaseFood(food)">+</button>
                </div>
              </div>
            </div>
          </div>
          <div v-if="filteredFoods.length === 0" class="empty-dishes">
            <p>该分类暂无菜品</p>
          </div>
        </div>
      </div>

      <!-- Floating cart bar -->
      <div class="cart-bar" v-if="!cartStore.isEmpty">
        <div class="cart-bar-left">
          <div class="cart-icon-wrap">
            <svg viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="2" width="24" height="24">
              <circle cx="9" cy="21" r="1"/><circle cx="20" cy="21" r="1"/>
              <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"/>
            </svg>
            <span class="cart-badge">{{ cartStore.totalCount }}</span>
          </div>
          <span class="cart-total">¥{{ cartStore.totalPrice.toFixed(2) }}</span>
        </div>
        <button class="checkout-btn" @click="goToCart">去结算</button>
      </div>
    </template>

    <div v-else class="empty-state">
      <p>商家不存在或已关闭</p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getMerchantById, getDishList } from '@/api/merchant'
import { useCartStore } from '@/stores/cart'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()

const loading = ref(true)
const restaurant = ref({})
const categories = ref([])
const foods = ref([])
const activeCategoryId = ref(null)

const filteredFoods = computed(() => {
  if (!activeCategoryId.value) return foods.value
  return foods.value.filter(f => f.categoryId === activeCategoryId.value)
})

function increaseFood(food) {
  cartStore.addItem(food, restaurant.value.id, restaurant.value.name)
}

function decreaseFood(food) {
  const qty = cartStore.getQuantity(food.id)
  if (qty > 0) {
    cartStore.updateQuantity(food.id, qty - 1)
  }
}

function goToCart() {
  router.push('/user/cart')
}

onMounted(async () => {
  const id = route.params.id
  try {
    const [merchantRes, dishRes] = await Promise.all([
      getMerchantById(id),
      getDishList(id)
    ])
    restaurant.value = merchantRes.data || {}
    const dishList = dishRes.data || []
    foods.value = dishList.map(d => ({
      id: d.id,
      name: d.name,
      price: d.price,
      description: d.description,
      stock: d.stock,
      imageUrl: d.imageUrl,
      categoryId: d.categoryId || 1
    }))
    const catMap = new Map()
    dishList.forEach(d => {
      const cid = d.categoryId || 1
      if (!catMap.has(cid)) {
        catMap.set(cid, { id: cid, name: d.categoryName || '推荐' })
      }
    })
    categories.value = catMap.size > 0 ? [...catMap.values()] : [{ id: 1, name: '推荐' }]
    activeCategoryId.value = categories.value[0]?.id
  } catch {
    restaurant.value = null
    foods.value = []
    categories.value = []
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.restaurant-detail {
  padding-bottom: 90px;
}

.loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 80px 20px;
  color: var(--color-text-hint);
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

/* Header */
.detail-header {
  overflow: hidden;
  margin-bottom: var(--spacing-md);
}

.header-cover {
  height: 160px;
  overflow: hidden;
}

.header-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.header-info {
  padding: var(--spacing-lg);
}

.shop-name {
  font-family: var(--font-heading);
  font-size: var(--font-size-xl);
  font-weight: 700;
  margin: 0 0 8px 0;
}

.shop-meta {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  font-size: var(--font-size-sm);
  margin-bottom: 8px;
}

.rating {
  display: flex;
  align-items: center;
  gap: 3px;
  color: var(--color-accent);
  font-weight: 600;
}

.rating svg { color: var(--color-accent); }

.meta-item {
  color: var(--color-text-hint);
}

.shop-addr {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: var(--font-size-sm);
  color: var(--color-text-hint);
  margin: 0;
}

/* Menu container */
.menu-container {
  display: flex;
  background: var(--color-bg-card);
  border-radius: var(--radius-lg);
  overflow: hidden;
  min-height: 400px;
}

.category-sidebar {
  width: 90px;
  background: var(--color-bg-page);
  overflow-y: auto;
  flex-shrink: 0;
}

.cat-item {
  padding: 14px 8px;
  text-align: center;
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  cursor: pointer;
  border-left: 3px solid transparent;
  transition: all 0.2s;
}

.cat-item.active {
  background: var(--color-bg-card);
  color: var(--color-primary);
  font-weight: 600;
  border-left-color: var(--color-primary);
}

/* Dish list */
.dish-list {
  flex: 1;
  padding: var(--spacing-md);
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.dish-card {
  display: flex;
  gap: var(--spacing-md);
  padding: var(--spacing-md);
  border-radius: var(--radius-md);
  transition: background 0.15s;
}

.dish-card:hover {
  background: var(--color-bg-page);
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

.dish-name {
  font-size: var(--font-size-base);
  font-weight: 600;
  margin: 0 0 4px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.dish-desc {
  font-size: var(--font-size-xs);
  color: var(--color-text-hint);
  margin: 0 0 auto 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.dish-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 6px;
}

.dish-price {
  font-size: var(--font-size-md);
  font-weight: 700;
  color: var(--color-accent);
}

.dish-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.btn-circle {
  width: 28px;
  height: 28px;
  border-radius: var(--radius-full);
  border: 1px solid var(--color-border);
  background: var(--color-bg-card);
  font-size: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-plus {
  background: var(--color-primary);
  border-color: var(--color-primary);
  color: #fff;
}

.btn-plus:hover {
  background: var(--color-primary-dark);
}

.btn-minus:hover {
  border-color: var(--color-accent);
  color: var(--color-accent);
}

.qty {
  min-width: 24px;
  text-align: center;
  font-weight: 600;
  font-size: var(--font-size-base);
}

.empty-dishes {
  text-align: center;
  padding: 40px;
  color: var(--color-text-hint);
}

/* Cart bar */
.cart-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 60px;
  background: linear-gradient(135deg, #2D2D2D 0%, #3D3028 100%);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 var(--spacing-xl);
  z-index: 50;
  box-shadow: 0 -2px 10px rgba(0,0,0,0.15);
}

.cart-bar-left {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.cart-icon-wrap {
  position: relative;
  width: 44px;
  height: 44px;
  background: var(--color-primary);
  border-radius: var(--radius-full);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: -10px;
}

.cart-badge {
  position: absolute;
  top: -4px;
  right: -4px;
  background: var(--color-error);
  color: #fff;
  font-size: 10px;
  font-weight: 700;
  padding: 1px 5px;
  border-radius: 10px;
  min-width: 16px;
  text-align: center;
}

.cart-total {
  font-size: var(--font-size-lg);
  font-weight: 700;
  color: #fff;
}

.checkout-btn {
  background: var(--color-primary);
  color: #fff;
  border: none;
  border-radius: var(--radius-xl);
  padding: 10px 28px;
  font-size: var(--font-size-base);
  font-weight: 700;
  cursor: pointer;
  transition: background 0.2s;
}

.checkout-btn:hover {
  background: var(--color-primary-dark);
}

.empty-state {
  text-align: center;
  padding: 80px 20px;
  color: var(--color-text-hint);
}

@media (min-width: 768px) {
  .cart-bar {
    bottom: 0;
  }
}
</style>

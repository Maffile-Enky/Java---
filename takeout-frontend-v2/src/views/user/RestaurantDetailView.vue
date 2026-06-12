<template>
  <div class="detail-page">
    <div class="container">
      <LoadingSpinner v-if="loading" text="加载中..." />

      <template v-else-if="merchant">
        <!-- Merchant Header -->
        <div class="merchant-header glass-panel reveal">
          <div class="merchant-cover">
            <img v-if="merchant.coverImage" :src="merchant.coverImage" :alt="merchant.name" @error="handleImageError" />
            <div v-else class="cover-placeholder">
              <img src="@/assets/pic/enk.png" alt="默认封面" class="placeholder-img" />
            </div>
          </div>
          <div class="merchant-info">
            <h1 class="merchant-name">{{ merchant.name }}</h1>
            <p class="merchant-addr">{{ merchant.address }}</p>
            <div class="merchant-meta">
              <RatingStars v-if="merchant.rating != null" :value="merchant.rating" />
              <span v-if="merchant.rating != null" class="meta-num">{{ merchant.rating.toFixed(1) }}</span>
              <span class="meta-sep">·</span>
              <span>月售 {{ merchant.monthlySales || 0 }}</span>
            </div>
          </div>
        </div>

        <!-- Dish Categories -->
        <div class="dish-section">
          <div class="category-tabs">
            <button
              v-for="cat in dishCategories"
              :key="cat"
              class="cat-tab"
              :class="{ active: activeCat === cat }"
              @click="activeCat = cat"
            >{{ cat }}</button>
          </div>

          <div class="dish-list">
            <div
              v-for="dish in filteredDishes"
              :key="dish.id"
              class="dish-row glass-panel"
            >
              <div class="dish-img-small">
                <img v-if="dish.image" :src="dish.image" :alt="dish.name" @error="handleImageError" />
                <span v-else class="img-placeholder">
                  <img src="@/assets/pic/enk.png" alt="默认图片" class="placeholder-img-small" />
                </span>
              </div>
              <div class="dish-info">
                <h3 class="dish-name">{{ dish.name }}</h3>
                <p class="dish-desc">{{ dish.description }}</p>
                <div class="dish-bottom">
                  <span class="dish-price">
                    <span class="price-sym">¥</span>{{ Number(dish.price).toFixed(2) }}
                  </span>
                  <button class="add-btn" @click="addToCart(dish)">+</button>
                </div>
              </div>
            </div>
          </div>

          <EmptyState v-if="!filteredDishes.length" icon="🍜" text="暂无菜品" />
        </div>
      </template>

      <!-- Cart floating button -->
      <router-link v-if="cart.totalCount > 0" to="/user/cart" class="cart-fab">
        <span class="cart-fab-icon">🛒</span>
        <span class="cart-fab-count">{{ cart.totalCount }}</span>
        <span class="cart-fab-price">¥{{ cart.totalPrice.toFixed(2) }}</span>
      </router-link>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getMerchantById, getDishList } from '@/api/merchant'
import { useCartStore } from '@/stores/cart'
import { useAuthStore } from '@/stores/auth'
import RatingStars from '@/components/common/RatingStars.vue'
import LoadingSpinner from '@/components/common/LoadingSpinner.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import { useScrollReveal } from '@/composables/useScrollReveal'
import enkImg from '@/assets/pic/enk.png'

const route = useRoute()
const cart = useCartStore()
const auth = useAuthStore()

const merchant = ref(null)
const dishes = ref([])
const loading = ref(true)
const activeCat = ref('全部')

const { reobserve } = useScrollReveal()

function handleImageError(e) {
  e.target.src = enkImg
  e.target.alt = '加载失败'
}

const dishCategories = computed(() => {
  const cats = new Set(dishes.value.map(d => d.category || '其他'))
  return ['全部', ...cats]
})

const filteredDishes = computed(() => {
  if (activeCat.value === '全部') return dishes.value
  return dishes.value.filter(d => (d.category || '其他') === activeCat.value)
})

async function fetchData() {
  loading.value = true
  const id = route.params.id
  try {
    const [mRes, dRes] = await Promise.all([
      getMerchantById(id),
      getDishList(id)
    ])
    merchant.value = mRes.data || mRes
    dishes.value = dRes.data || dRes || []
  } catch {
    merchant.value = null
    dishes.value = []
  } finally {
    loading.value = false
    reobserve()
  }
}

function addToCart(dish) {
  cart.addItem(dish, merchant.value.id, merchant.value.name)
}

onMounted(fetchData)
</script>

<style scoped>
.detail-page {
  padding: var(--space-6) 0;
}

.merchant-header {
  display: flex;
  gap: var(--space-6);
  padding: 0;
  overflow: hidden;
  margin-bottom: var(--space-8);
}

.merchant-cover {
  width: 200px;
  height: 150px;
  flex-shrink: 0;
  overflow: hidden;
  border-radius: var(--radius-lg) 0 0 var(--radius-lg);
}

.merchant-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cover-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--bg-elevated);
}

.placeholder-img {
  width: 60%;
  height: 60%;
  object-fit: contain;
  opacity: 0.6;
}

.merchant-info {
  padding: var(--space-6) var(--space-6) var(--space-6) 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.merchant-name {
  font-family: var(--font-serif);
  font-size: 1.5rem;
  font-weight: 900;
  margin-bottom: var(--space-1);
}

.merchant-addr {
  font-size: var(--text-sm);
  color: var(--text-muted);
  margin-bottom: var(--space-2);
}

.merchant-meta {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  font-size: var(--text-sm);
  color: var(--text-secondary);
}

.meta-num {
  color: var(--accent-secondary);
  font-weight: 600;
}

.meta-sep { opacity: 0.3; }

.dish-section {
  margin-bottom: var(--space-8);
}

.category-tabs {
  display: flex;
  gap: var(--space-2);
  margin-bottom: var(--space-6);
  overflow-x: auto;
  padding-bottom: var(--space-2);
}

.cat-tab {
  padding: var(--space-2) var(--space-4);
  background: var(--glass);
  border: 1px solid var(--glass-border);
  border-radius: var(--radius-full);
  color: var(--text-secondary);
  font-size: var(--text-sm);
  cursor: pointer;
  white-space: nowrap;
  transition: all var(--duration-fast);
  font-family: var(--font-sans);
}

.cat-tab:hover { border-color: var(--accent); color: var(--text-primary); }

.cat-tab.active {
  background: var(--gradient-green);
  color: var(--text-inverse);
  border-color: transparent;
  font-weight: 600;
}

.dish-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-3);
}

.dish-row {
  display: flex;
  gap: var(--space-4);
  padding: var(--space-3);
  transition: all var(--duration-fast);
}

.dish-row:hover {
  border-color: rgba(110, 231, 160, 0.2);
}

.dish-img-small {
  width: 90px;
  height: 90px;
  border-radius: var(--radius-md);
  overflow: hidden;
  flex-shrink: 0;
  background: var(--bg-elevated);
}

.dish-img-small img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.img-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.placeholder-img-small {
  width: 60%;
  height: 60%;
  object-fit: contain;
  opacity: 0.6;
}

.dish-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  min-width: 0;
}

.dish-name {
  font-size: var(--text-base);
  font-weight: 700;
  margin-bottom: var(--space-1);
}

.dish-desc {
  font-size: var(--text-xs);
  color: var(--text-muted);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-bottom: var(--space-2);
}

.dish-bottom {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.dish-price {
  font-size: 1.1rem;
  font-weight: 800;
  color: var(--accent);
}

.price-sym {
  font-size: var(--text-xs);
  margin-right: 1px;
}

.add-btn {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: var(--gradient-green);
  color: var(--text-inverse);
  border: none;
  font-size: 1.2rem;
  font-weight: 700;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all var(--duration-fast);
  box-shadow: 0 2px 8px rgba(110, 231, 160, 0.3);
}

.add-btn:hover {
  transform: scale(1.1);
  box-shadow: 0 4px 16px rgba(110, 231, 160, 0.5);
}

.cart-fab {
  position: fixed;
  bottom: calc(var(--nav-height) + var(--space-4));
  right: var(--space-6);
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-3) var(--space-5);
  background: var(--gradient-green);
  color: var(--text-inverse);
  border-radius: var(--radius-full);
  text-decoration: none;
  font-weight: 700;
  box-shadow: 0 8px 24px rgba(110, 231, 160, 0.4);
  z-index: 50;
  animation: fadeSlideUp 0.3s var(--ease-out);
}

.cart-fab-icon { font-size: 1.2rem; }
.cart-fab-count {
  background: var(--accent-secondary);
  color: var(--text-inverse);
  min-width: 20px;
  height: 20px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 11px;
}

@media (max-width: 640px) {
  .merchant-header { flex-direction: column; }
  .merchant-cover { width: 100%; height: 180px; border-radius: var(--radius-lg) var(--radius-lg) 0 0; }
  .dish-img-small { width: 70px; height: 70px; }
}
</style>

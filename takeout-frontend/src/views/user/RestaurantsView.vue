<template>
  <div class="restaurants-page">
    <!-- Search & Filter -->
    <div class="filter-bar card">
      <div class="search-row">
        <div class="search-input">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="18" height="18"><circle cx="11" cy="11" r="8"/><path d="m21 21-4.35-4.35"/></svg>
          <input
            v-model="searchKeyword"
            type="text"
            placeholder="搜索商家或菜品"
            @keyup.enter="handleSearch"
          />
        </div>
        <button class="search-btn" @click="handleSearch">搜索</button>
      </div>
      <div class="filter-chips">
        <button
          v-for="f in sortFilters"
          :key="f.value"
          class="chip"
          :class="{ active: sortBy === f.value }"
          @click="sortBy = f.value"
        >{{ f.label }}</button>
      </div>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="loading-grid">
      <div v-for="i in 4" :key="i" class="skeleton-card card">
        <div class="skeleton-img"></div>
        <div class="skeleton-text">
          <div class="skeleton-line w60"></div>
          <div class="skeleton-line w40"></div>
          <div class="skeleton-line w80"></div>
        </div>
      </div>
    </div>

    <!-- Restaurant List -->
    <div v-else-if="filteredShops.length > 0" class="restaurant-list">
      <div
        v-for="shop in filteredShops"
        :key="shop.id"
        class="restaurant-card card"
        @click="goToRestaurant(shop.id)"
      >
        <div class="shop-cover">
          <img :src="shop.imageUrl || '/images/placeholders/merchant-default.png'" :alt="shop.name" />
        </div>
        <div class="shop-info">
          <h3 class="shop-name">{{ shop.name }}</h3>
          <div class="shop-rating">
            <svg viewBox="0 0 24 24" fill="currentColor" width="14" height="14"><path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/></svg>
            <span>{{ shop.rating || '4.5' }}</span>
            <span class="monthly">月售{{ shop.monthlySales || 100 }}+</span>
          </div>
          <div class="shop-delivery">
            <span>{{ shop.deliveryTime || 30 }}分钟</span>
            <span class="sep">·</span>
            <span>起送¥{{ shop.minOrder || 20 }}</span>
            <span class="sep">·</span>
            <span>配送¥{{ shop.deliveryFee || 5 }}</span>
          </div>
          <p class="shop-addr">{{ shop.address }}</p>
        </div>
      </div>
    </div>

    <!-- Empty -->
    <div v-else class="empty-state">
      <img src="/images/empty-states/empty-restaurants.svg" alt="暂无商家" class="empty-img" />
      <p>暂无符合条件的商家</p>
      <button class="btn-outline" @click="searchKeyword = ''; sortBy = 'default'">清除筛选</button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getStoreList } from '@/api/merchant'

const router = useRouter()
const route = useRoute()

const loading = ref(true)
const searchKeyword = ref(route.query.keyword || '')
const sortBy = ref('default')
const shops = ref([])

const sortFilters = [
  { label: '默认排序', value: 'default' },
  { label: '评分优先', value: 'rating' },
  { label: '配送最快', value: 'speed' },
]

const filteredShops = computed(() => {
  let result = [...shops.value]

  if (searchKeyword.value.trim()) {
    const kw = searchKeyword.value.toLowerCase()
    result = result.filter(s =>
      s.name.toLowerCase().includes(kw) ||
      (s.address && s.address.toLowerCase().includes(kw))
    )
  }

  if (sortBy.value === 'rating') {
    result.sort((a, b) => (b.rating || 0) - (a.rating || 0))
  } else if (sortBy.value === 'speed') {
    result.sort((a, b) => (a.deliveryTime || 30) - (b.deliveryTime || 30))
  }

  return result
})

function handleSearch() {}

function goToRestaurant(id) {
  router.push(`/user/restaurants/${id}`)
}

onMounted(async () => {
  try {
    const res = await getStoreList()
    shops.value = res.data || []
  } catch {
    shops.value = []
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.restaurants-page {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

/* Filter bar */
.filter-bar {
  padding: var(--spacing-lg);
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.search-row {
  display: flex;
  gap: var(--spacing-sm);
}

.search-input {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 8px;
  background: var(--color-bg-page);
  border-radius: var(--radius-md);
  padding: 0 var(--spacing-md);
  height: 40px;
}

.search-input input {
  flex: 1;
  border: none;
  background: transparent;
  height: 100%;
  font-size: var(--font-size-base);
}

.search-input svg {
  color: var(--color-text-hint);
  flex-shrink: 0;
}

.search-btn {
  background: var(--color-accent);
  color: #fff;
  border: none;
  border-radius: var(--radius-md);
  padding: 0 20px;
  font-size: var(--font-size-base);
  font-weight: 500;
  cursor: pointer;
}

.filter-chips {
  display: flex;
  gap: var(--spacing-sm);
}

.chip {
  padding: 6px 14px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-xl);
  background: var(--color-bg-card);
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  cursor: pointer;
  transition: all 0.2s;
}

.chip.active {
  background: var(--color-primary-light);
  border-color: var(--color-primary);
  color: var(--color-text-primary);
  font-weight: 600;
}

/* Loading skeleton */
.loading-grid {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.skeleton-card {
  display: flex;
  padding: var(--spacing-md);
  gap: var(--spacing-md);
}

.skeleton-img {
  width: 120px;
  height: 90px;
  border-radius: var(--radius-md);
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
  flex-shrink: 0;
}

.skeleton-text {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 4px 0;
}

.skeleton-line {
  height: 14px;
  border-radius: 4px;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
}

.w60 { width: 60%; }
.w40 { width: 40%; }
.w80 { width: 80%; }

@keyframes shimmer {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}

/* Restaurant list */
.restaurant-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.restaurant-card {
  display: flex;
  cursor: pointer;
  overflow: hidden;
  transition: transform 0.2s, box-shadow 0.2s;
}

.restaurant-card:hover {
  transform: translateY(-1px);
  box-shadow: var(--shadow-md);
}

.shop-cover {
  width: 130px;
  height: 100px;
  flex-shrink: 0;
  overflow: hidden;
  border-radius: var(--radius-md);
  margin: var(--spacing-md);
}

.shop-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.shop-info {
  flex: 1;
  padding: var(--spacing-md) var(--spacing-md) var(--spacing-md) 0;
  display: flex;
  flex-direction: column;
  gap: 4px;
  min-width: 0;
}

.shop-name {
  font-size: var(--font-size-md);
  font-weight: 600;
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.shop-rating {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: var(--font-size-sm);
  color: var(--color-accent);
  font-weight: 600;
}

.shop-rating svg {
  color: var(--color-accent);
}

.monthly {
  color: var(--color-text-hint);
  font-weight: 400;
  margin-left: 4px;
}

.shop-delivery {
  font-size: var(--font-size-xs);
  color: var(--color-text-hint);
}

.sep {
  margin: 0 4px;
}

.shop-addr {
  font-size: var(--font-size-xs);
  color: var(--color-text-hint);
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* Empty */
.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: var(--color-text-hint);
}

.empty-img {
  width: 200px;
  margin: 0 auto 16px;
  opacity: 0.6;
}

.empty-state p {
  margin-bottom: 16px;
}
</style>

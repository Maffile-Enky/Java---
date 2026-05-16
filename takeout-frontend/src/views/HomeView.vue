<template>
  <Layout>
    <div class="home-page">
      <!-- Hero: Search -->
      <section class="hero">
        <div class="hero-inner">
          <h1 class="hero-title">美团外卖</h1>
          <p class="hero-subtitle">送啥都快</p>
          <div class="hero-search">
            <svg class="search-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="11" cy="11" r="8"/><path d="m21 21-4.35-4.35"/>
            </svg>
            <input
              v-model="searchKeyword"
              type="text"
              placeholder="搜索商家或菜品"
              @keyup.enter="handleSearch"
            />
            <button class="search-btn" @click="handleSearch">搜索</button>
          </div>
        </div>
      </section>

      <!-- Categories -->
      <section class="categories card">
        <div class="category-grid">
          <div
            v-for="cat in categories"
            :key="cat.id"
            class="category-item"
            @click="filterByCategory(cat.id)"
          >
            <div class="category-icon" :style="{ background: cat.bg }">
              <img :src="`/images/categories/${cat.icon}.svg`" :alt="cat.name" class="cat-img" />
            </div>
            <span class="category-name">{{ cat.name }}</span>
          </div>
        </div>
      </section>

      <!-- Banner Carousel -->
      <section class="banner-section">
        <div class="banner-track" :style="{ transform: `translateX(-${currentBanner * 100}%)` }">
          <div class="banner-slide" v-for="i in 3" :key="i">
            <img :src="`/images/banners/banner-${i}.png`" :alt="`banner-${i}`" class="banner-img" />
          </div>
        </div>
        <div class="banner-dots">
          <span
            v-for="i in 3"
            :key="i"
            class="dot"
            :class="{ active: currentBanner === i - 1 }"
            @click="currentBanner = i - 1"
          ></span>
        </div>
      </section>

      <!-- Recommended Merchants -->
      <section class="section">
        <div class="section-header">
          <h2 class="section-title">推荐商家</h2>
          <router-link to="/user/restaurants" class="section-link">
            查看全部
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="16" height="16"><path d="m9 18 6-6-6-6"/></svg>
          </router-link>
        </div>
        <div class="merchant-grid">
          <div
            v-for="shop in popularShops"
            :key="shop.id"
            class="merchant-card card"
            @click="goToRestaurant(shop.id)"
          >
            <div class="merchant-cover">
              <img :src="shop.imageUrl || '/images/placeholders/merchant-default.png'" :alt="shop.name" />
            </div>
            <div class="merchant-info">
              <h3 class="merchant-name">{{ shop.name }}</h3>
              <div class="merchant-meta">
                <span class="rating">
                  <svg viewBox="0 0 24 24" fill="currentColor" width="14" height="14"><path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/></svg>
                  {{ shop.rating || '4.5' }}
                </span>
                <span class="monthly-sales">月售{{ shop.monthlySales || 100 }}+</span>
              </div>
              <div class="merchant-delivery">
                <span class="delivery-time">{{ shop.deliveryTime || 30 }}分钟</span>
                <span class="min-order">起送¥{{ shop.minOrder || 20 }}</span>
              </div>
            </div>
          </div>
        </div>
        <div v-if="popularShops.length === 0 && !loading" class="empty-merchants">
          <img src="/images/empty-states/empty-restaurants.svg" alt="暂无商家" class="empty-img" />
          <p>暂无商家，敬请期待</p>
        </div>
      </section>
    </div>
  </Layout>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import Layout from '@/components/Layout.vue'
import { getHotMerchants } from '@/api/search'

const router = useRouter()
const searchKeyword = ref('')
const popularShops = ref([])
const loading = ref(true)
const currentBanner = ref(0)
let bannerTimer = null

const categories = ref([
  { id: 1, name: '中餐', icon: 'chinese', bg: '#FFF3E0' },
  { id: 2, name: '快餐', icon: 'fastfood', bg: '#E3F2FD' },
  { id: 3, name: '火锅', icon: 'hotpot', bg: '#FCE4EC' },
  { id: 4, name: '饮品', icon: 'drinks', bg: '#E8F5E9' },
  { id: 5, name: '烧烤', icon: 'bbq', bg: '#FFF8E1' },
  { id: 6, name: '甜点', icon: 'dessert', bg: '#F3E5F5' },
  { id: 7, name: '日料', icon: 'japanese', bg: '#E0F7FA' },
  { id: 8, name: '西餐', icon: 'western', bg: '#FBE9E7' },
  { id: 9, name: '水果', icon: 'fruit', bg: '#F1F8E9' },
  { id: 10, name: '超市', icon: 'grocery', bg: '#ECEFF1' },
  { id: 11, name: '早餐', icon: 'breakfast', bg: '#FFF3E0' },
  { id: 12, name: '全部', icon: 'all', bg: '#F5F5F5' },
])

function handleSearch() {
  if (searchKeyword.value.trim()) {
    router.push(`/user/restaurants?keyword=${searchKeyword.value}`)
  }
}

function goToRestaurant(id) {
  router.push(`/user/restaurants/${id}`)
}

function filterByCategory(catId) {
  if (catId === 12) {
    router.push('/user/restaurants')
  } else {
    router.push(`/user/restaurants?category=${catId}`)
  }
}

function startBanner() {
  bannerTimer = setInterval(() => {
    currentBanner.value = (currentBanner.value + 1) % 3
  }, 4000)
}

onMounted(async () => {
  startBanner()
  try {
    const res = await getHotMerchants(6)
    popularShops.value = res.data || []
  } catch {
    popularShops.value = []
  } finally {
    loading.value = false
  }
})

onUnmounted(() => {
  if (bannerTimer) clearInterval(bannerTimer)
})
</script>

<style scoped>
.home-page {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

/* Hero */
.hero {
  background: linear-gradient(135deg, #C84B31 0%, #E8A838 50%, #C84B31 100%);
  background-size: 200% 200%;
  animation: gradientShift 8s ease infinite;
  border-radius: var(--radius-card);
  padding: 48px 32px;
  text-align: center;
  position: relative;
  overflow: hidden;
}

@keyframes gradientShift {
  0%, 100% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
}

.hero-title {
  font-family: var(--font-heading);
  font-size: var(--font-size-2xl);
  font-weight: 800;
  color: #fff;
  margin: 0 0 4px 0;
}

.hero-subtitle {
  font-size: var(--font-size-base);
  color: rgba(255,255,255,0.7);
  margin: 0 0 20px 0;
}

.hero-search {
  display: flex;
  align-items: center;
  background: var(--color-bg-card);
  border-radius: var(--radius-xl);
  padding: 4px 4px 4px 16px;
  max-width: 520px;
  margin: 0 auto;
  box-shadow: var(--shadow-md);
}

.hero-search .search-icon {
  width: 20px;
  height: 20px;
  color: var(--color-text-hint);
  flex-shrink: 0;
  margin-right: 8px;
}

.hero-search input {
  flex: 1;
  border: none;
  background: transparent;
  height: 40px;
  font-size: var(--font-size-base);
}

.hero-search input::placeholder {
  color: var(--color-text-hint);
}

.search-btn {
  background: var(--color-primary);
  color: #fff;
  border: none;
  border-radius: var(--radius-xl);
  padding: 8px 24px;
  font-size: var(--font-size-base);
  font-weight: 600;
  cursor: pointer;
  transition: background var(--transition-smooth);
}

.search-btn:hover {
  background: var(--color-primary-dark);
}

/* Categories */
.categories {
  padding: var(--spacing-xl);
  background: var(--color-surface-warm);
  border: 1px solid var(--color-divider);
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: var(--spacing-lg) var(--spacing-sm);
}

.category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  transition: transform 0.2s;
}

.category-item:hover {
  transform: translateY(-3px) scale(1.05);
}

.category-icon {
  width: 48px;
  height: 48px;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.cat-img {
  width: 32px;
  height: 32px;
  object-fit: contain;
}

.category-name {
  font-size: var(--font-size-sm);
  color: var(--color-text-primary);
  font-weight: 500;
}

/* Banner */
.banner-section {
  position: relative;
  border-radius: var(--radius-lg);
  overflow: hidden;
  background: var(--color-bg-card);
}

.banner-track {
  display: flex;
  transition: transform 0.5s ease;
}

.banner-slide {
  min-width: 100%;
}

.banner-img {
  width: 100%;
  height: 160px;
  object-fit: cover;
  display: block;
}

.banner-dots {
  position: absolute;
  bottom: 10px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 6px;
}

.dot {
  width: 8px;
  height: 8px;
  border-radius: var(--radius-full);
  background: rgba(255,255,255,0.5);
  cursor: pointer;
  transition: all 0.3s;
}

.dot.active {
  background: var(--color-primary);
  width: 20px;
  border-radius: 4px;
}

/* Section */
.section {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.section-title {
  font-family: var(--font-heading);
  font-size: var(--font-size-lg);
  font-weight: 700;
  margin: 0;
  color: var(--color-text-primary);
}

.section-link {
  display: flex;
  align-items: center;
  gap: 2px;
  font-size: var(--font-size-sm);
  color: var(--color-text-hint);
  text-decoration: none;
  transition: color 0.2s;
}

.section-link:hover {
  color: var(--color-accent);
}

/* Merchant Grid */
.merchant-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: var(--spacing-lg);
}

.merchant-card {
  cursor: pointer;
  overflow: hidden;
  border: 1px solid rgba(237, 229, 219, 0.5);
  transition: transform var(--transition-smooth), box-shadow var(--transition-smooth);
}

.merchant-card:hover {
  transform: translateY(-3px) rotate(-0.5deg);
  box-shadow: var(--shadow-md);
}

.merchant-cover {
  height: 140px;
  overflow: hidden;
  background: var(--color-bg-page);
}

.merchant-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.merchant-info {
  padding: var(--spacing-md);
}

.merchant-name {
  font-family: var(--font-heading);
  font-size: var(--font-size-md);
  font-weight: 600;
  margin: 0 0 6px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.merchant-meta {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  font-size: var(--font-size-sm);
  margin-bottom: 6px;
}

.rating {
  display: flex;
  align-items: center;
  gap: 3px;
  color: var(--color-accent);
  font-weight: 600;
}

.rating svg {
  color: var(--color-accent);
}

.monthly-sales {
  color: var(--color-text-hint);
}

.merchant-delivery {
  display: flex;
  gap: var(--spacing-md);
  font-size: var(--font-size-xs);
  color: var(--color-text-hint);
}

/* Empty */
.empty-merchants {
  text-align: center;
  padding: 40px 20px;
  color: var(--color-text-hint);
}

.empty-img {
  width: 180px;
  margin: 0 auto 16px;
  opacity: 0.6;
}

/* Responsive */
@media (max-width: 640px) {
  .category-grid {
    grid-template-columns: repeat(4, 1fr);
  }
  .merchant-grid {
    grid-template-columns: 1fr;
  }
}
</style>

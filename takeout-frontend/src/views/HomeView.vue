<template>
  <Layout>
    <div class="home-page">
      <!-- Hero Section -->
      <section class="hero">
        <div class="hero-content">
          <h1 class="hero-title">美食外卖，即刻送达</h1>
          <p class="hero-subtitle">海量商家，万千美食，一键下单，快速配送</p>
          <div class="search-box">
            <input
              type="text"
              v-model="searchKeyword"
              placeholder="搜索商家或菜品..."
              @keyup.enter="handleSearch"
            />
            <button class="search-btn" @click="handleSearch">搜索</button>
          </div>
        </div>
      </section>

      <!-- Features -->
      <section class="features">
        <div class="feature-card">
          <span class="feature-icon">⚡</span>
          <h3>快速配送</h3>
          <p>平均 30 分钟送达</p>
        </div>
        <div class="feature-card">
          <span class="feature-icon">🏪</span>
          <h3>优质商家</h3>
          <p>严格筛选品质保证</p>
        </div>
        <div class="feature-card">
          <span class="feature-icon">💰</span>
          <h3>优惠多多</h3>
          <p>新人立减，满减活动</p>
        </div>
        <div class="feature-card">
          <span class="feature-icon">🛡️</span>
          <h3>安全保障</h3>
          <p>食品安全全程追踪</p>
        </div>
      </section>

      <!-- Popular Restaurants -->
      <section class="restaurants-section">
        <div class="section-header">
          <h2>热门商家</h2>
          <router-link to="/user/restaurants" class="view-all">查看全部 →</router-link>
        </div>
        <div class="restaurant-grid">
          <div
            v-for="shop in popularShops"
            :key="shop.id"
            class="restaurant-card"
            @click="goToRestaurant(shop.id)"
          >
            <div class="shop-image">
              <span class="shop-emoji">🏪</span>
            </div>
            <div class="shop-info">
              <h3 class="shop-name">{{ shop.name }}</h3>
              <div class="shop-meta">
                <span class="rating">⭐ {{ shop.rating || '4.5' }}</span>
                <span class="delivery-time">⏱️ {{ shop.deliveryTime || 30 }}分钟</span>
              </div>
              <p class="shop-description">{{ shop.address }}</p>
            </div>
          </div>
        </div>
      </section>

      <!-- Categories -->
      <section class="categories-section">
        <h2>美食分类</h2>
        <div class="category-grid">
          <div
            v-for="cat in categories"
            :key="cat.id"
            class="category-card"
            @click="filterByCategory(cat.id)"
          >
            <span class="category-emoji">{{ cat.emoji }}</span>
            <span class="category-name">{{ cat.name }}</span>
          </div>
        </div>
      </section>
    </div>
  </Layout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import Layout from '@/components/Layout.vue'
import { getStoreList } from '@/api/merchant'

const router = useRouter()
const searchKeyword = ref('')
const popularShops = ref([])

const categories = ref([
  { id: 1, name: '汉堡披萨', emoji: '🍔' },
  { id: 2, name: '中式快餐', emoji: '🍜' },
  { id: 3, name: '火锅烧烤', emoji: '🍲' },
  { id: 4, name: '奶茶饮品', emoji: '🧋' },
  { id: 5, name: '炸鸡小吃', emoji: '🍗' },
  { id: 6, name: '日料韩料', emoji: '🍣' },
  { id: 7, name: '早餐甜点', emoji: '🥐' },
  { id: 8, name: '素食轻食', emoji: '🥗' },
])

const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push(`/user/restaurants?keyword=${searchKeyword.value}`)
  }
}

const goToRestaurant = (id) => {
  router.push(`/user/restaurants/${id}`)
}

const filterByCategory = (categoryId) => {
  router.push(`/user/restaurants?category=${categoryId}`)
}

onMounted(async () => {
  try {
    const res = await getStoreList()
    popularShops.value = (res.data || []).slice(0, 6)
  } catch {
    // Fallback mock data if backend not available
    popularShops.value = [
      { id: 1, name: '麦当劳', rating: 4.8, deliveryTime: 25, address: '西式快餐，汉堡薯条' },
      { id: 2, name: '肯德基', rating: 4.7, deliveryTime: 30, address: '炸鸡专家，美味到家' },
      { id: 3, name: '必胜客', rating: 4.6, deliveryTime: 35, address: '比萨专家，意面小吃' },
      { id: 4, name: '海底捞', rating: 4.9, deliveryTime: 40, address: '火锅专家，配送到家' },
      { id: 5, name: '真功夫', rating: 4.5, deliveryTime: 28, address: '中式快餐，营养健康' },
      { id: 6, name: '喜茶', rating: 4.8, deliveryTime: 20, address: '新式茶饮，网红打卡' },
    ]
  }
})
</script>

<style scoped>
.home-page { animation: fadeIn 0.5s ease; }
@keyframes fadeIn { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }
.hero { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); border-radius: 20px; padding: 60px 40px; text-align: center; color: white; margin-bottom: 32px; }
.hero-title { font-size: 42px; margin: 0 0 16px 0; font-weight: bold; }
.hero-subtitle { font-size: 18px; opacity: 0.9; margin: 0 0 32px 0; }
.search-box { display: flex; max-width: 500px; margin: 0 auto; gap: 12px; }
.search-box input { flex: 1; padding: 16px 24px; border: none; border-radius: 12px; font-size: 16px; outline: none; }
.search-btn { padding: 16px 32px; background: #ff6b6b; color: white; border: none; border-radius: 12px; font-size: 16px; cursor: pointer; transition: all 0.3s; }
.search-btn:hover { background: #ff5252; transform: scale(1.05); }
.features { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 20px; margin-bottom: 40px; }
.feature-card { background: white; padding: 24px; border-radius: 16px; text-align: center; box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08); transition: transform 0.3s, box-shadow 0.3s; }
.feature-card:hover { transform: translateY(-4px); box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12); }
.feature-icon { font-size: 48px; display: block; margin-bottom: 12px; }
.feature-card h3 { margin: 8px 0; color: #2d3436; }
.feature-card p { color: #636e72; font-size: 14px; margin: 0; }
.section-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.section-header h2 { font-size: 28px; color: #2d3436; margin: 0; }
.view-all { color: #667eea; text-decoration: none; font-weight: 500; transition: color 0.3s; }
.view-all:hover { color: #764ba2; }
.restaurant-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 24px; margin-bottom: 40px; }
.restaurant-card { background: white; border-radius: 16px; overflow: hidden; box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08); cursor: pointer; transition: transform 0.3s, box-shadow 0.3s; }
.restaurant-card:hover { transform: translateY(-4px); box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12); }
.shop-image { height: 160px; background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%); display: flex; align-items: center; justify-content: center; }
.shop-emoji { font-size: 72px; }
.shop-info { padding: 16px; }
.shop-name { font-size: 18px; margin: 0 0 8px 0; color: #2d3436; }
.shop-meta { display: flex; gap: 12px; font-size: 14px; color: #636e72; margin-bottom: 8px; }
.rating { color: #ffa502; font-weight: 500; }
.shop-description { color: #b2bec3; font-size: 13px; margin: 0; }
.categories-section { margin-top: 40px; }
.categories-section h2 { font-size: 28px; color: #2d3436; margin: 0 0 24px 0; text-align: center; }
.category-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(120px, 1fr)); gap: 16px; }
.category-card { background: white; padding: 24px; border-radius: 16px; text-align: center; cursor: pointer; box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06); transition: all 0.3s; }
.category-card:hover { transform: scale(1.05); box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1); }
.category-emoji { font-size: 42px; display: block; margin-bottom: 8px; }
.category-name { font-size: 14px; color: #2d3436; font-weight: 500; }
</style>

<template>
  <div class="restaurants-page">
    <!-- Search & Filter Bar -->
    <div class="filter-bar glass-panel">
      <div class="search-section">
        <input
          type="text"
          v-model="searchKeyword"
          placeholder="搜索商家或菜品..."
          @keyup.enter="handleSearch"
        />
        <button class="search-btn" @click="handleSearch">搜索</button>
      </div>

      <div class="filter-section">
        <select v-model="selectedCategory">
          <option value="">全部分类</option>
          <option v-for="cat in categories" :key="cat.id" :value="cat.id">
            {{ cat.name }}
          </option>
        </select>

        <select v-model="sortBy">
          <option value="default">默认排序</option>
          <option value="rating">评分优先</option>
        </select>
      </div>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="loading">加载中...</div>

    <!-- Restaurant List -->
    <div v-else class="restaurant-list">
      <div
        v-for="shop in filteredShops"
        :key="shop.id"
        class="restaurant-card"
        @click="goToRestaurant(shop.id)"
      >
        <div class="shop-image">
          <span class="shop-emoji">🏪</span>
        </div>
        <div class="shop-info">
          <div class="shop-header">
            <h3 class="shop-name">{{ shop.name }}</h3>
            <span class="rating-badge">⭐ {{ shop.rating || '-' }}</span>
          </div>
          <div class="shop-meta">
            <span class="delivery-info">
              <span>{{ shop.address }}</span>
            </span>
          </div>
          <p class="shop-description">{{ shop.phone }}</p>
        </div>
      </div>
    </div>

    <!-- Empty State -->
    <div v-if="!loading && filteredShops.length === 0" class="empty-state">
      <span class="empty-emoji">🔍</span>
      <p>暂无符合条件的商家</p>
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
const selectedCategory = ref(route.query.category || '')
const sortBy = ref('default')
const shops = ref([])

const categories = ref([
  { id: 1, name: '汉堡披萨' },
  { id: 2, name: '中式快餐' },
  { id: 3, name: '火锅烧烤' },
  { id: 4, name: '奶茶饮品' },
  { id: 5, name: '炸鸡小吃' },
  { id: 6, name: '日料韩料' },
  { id: 7, name: '早餐甜点' },
  { id: 8, name: '素食轻食' },
])

const filteredShops = computed(() => {
  let result = [...shops.value]

  if (searchKeyword.value.trim()) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(shop =>
      shop.name.toLowerCase().includes(keyword) ||
      (shop.address && shop.address.toLowerCase().includes(keyword))
    )
  }

  switch (sortBy.value) {
    case 'rating':
      result.sort((a, b) => (b.rating || 0) - (a.rating || 0))
      break
  }

  return result
})

const handleSearch = () => {
  // Filtering is reactive via computed
}

const goToRestaurant = (id) => {
  router.push(`/user/restaurants/${id}`)
}

onMounted(async () => {
  try {
    const res = await getStoreList()
    shops.value = res.data || []
  } catch {
    // Fallback mock data
    shops.value = [
      { id: 1, name: '麦当劳', rating: 4.8, address: '西式快餐，汉堡薯条', phone: '400-123-4567' },
      { id: 2, name: '肯德基', rating: 4.7, address: '炸鸡专家，美味到家', phone: '400-234-5678' },
      { id: 3, name: '必胜客', rating: 4.6, address: '比萨专家，意面小吃', phone: '400-345-6789' },
      { id: 4, name: '海底捞', rating: 4.9, address: '火锅专家，配送到家', phone: '400-456-7890' },
      { id: 5, name: '真功夫', rating: 4.5, address: '中式快餐，营养健康', phone: '400-567-8901' },
      { id: 6, name: '喜茶', rating: 4.8, address: '新式茶饮，网红打卡', phone: '400-678-9012' },
    ]
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.restaurants-page { animation: fadeIn 0.5s ease; }
@keyframes fadeIn { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }
.loading { text-align: center; padding: 40px; color: #999; }
.filter-bar { padding: 20px; margin-bottom: 24px; display: flex; flex-wrap: wrap; gap: 20px; align-items: center; justify-content: space-between; }
.search-section { display: flex; gap: 12px; flex: 1; min-width: 300px; }
.search-section input { flex: 1; padding: 12px 16px; border: 1px solid rgba(0, 0, 0, 0.1); border-radius: 8px; font-size: 14px; outline: none; transition: border-color 0.3s; }
.search-section input:focus { border-color: #667eea; }
.search-btn { padding: 12px 24px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; border: none; border-radius: 8px; cursor: pointer; transition: transform 0.3s; }
.search-btn:hover { transform: scale(1.05); }
.filter-section { display: flex; gap: 12px; }
.filter-section select { padding: 12px 16px; border: 1px solid rgba(0, 0, 0, 0.1); border-radius: 8px; font-size: 14px; background: white; cursor: pointer; outline: none; }
.restaurant-list { display: flex; flex-direction: column; gap: 16px; }
.restaurant-card { display: flex; background: white; border-radius: 16px; overflow: hidden; box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08); cursor: pointer; transition: transform 0.3s, box-shadow 0.3s; }
.restaurant-card:hover { transform: translateY(-4px); box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12); }
.shop-image { width: 140px; min-height: 120px; background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%); display: flex; align-items: center; justify-content: center; }
.shop-emoji { font-size: 60px; }
.shop-info { flex: 1; padding: 16px; display: flex; flex-direction: column; gap: 6px; }
.shop-header { display: flex; justify-content: space-between; align-items: center; }
.shop-name { font-size: 18px; margin: 0; color: #2d3436; }
.rating-badge { background: #ffa502; color: white; padding: 4px 10px; border-radius: 20px; font-size: 13px; font-weight: 500; }
.shop-meta { display: flex; gap: 16px; font-size: 14px; color: #636e72; }
.delivery-info { display: flex; align-items: center; gap: 8px; }
.shop-description { color: #b2bec3; font-size: 13px; margin: 0; }
.empty-state { text-align: center; padding: 60px 20px; color: #b2bec3; }
.empty-emoji { font-size: 64px; display: block; margin-bottom: 16px; }
</style>

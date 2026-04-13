<template>
  <Layout>
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
          <select v-model="selectedCategory" @change="handleFilter">
            <option value="">全部分类</option>
            <option v-for="cat in categories" :key="cat.id" :value="cat.id">
              {{ cat.name }}
            </option>
          </select>

          <select v-model="sortBy" @change="handleSort">
            <option value="default">默认排序</option>
            <option value="sales">销量优先</option>
            <option value="rating">评分优先</option>
            <option value="deliveryTime">配送最快</option>
          </select>
        </div>
      </div>

      <!-- Restaurant List -->
      <div class="restaurant-list">
        <div
          v-for="shop in filteredShops"
          :key="shop.id"
          class="restaurant-card"
          @click="goToRestaurant(shop.id)"
        >
          <div class="shop-image">
            <span class="shop-emoji">{{ shop.emoji }}</span>
          </div>
          <div class="shop-info">
            <div class="shop-header">
              <h3 class="shop-name">{{ shop.name }}</h3>
              <span class="rating-badge">
                ⭐ {{ shop.rating }}
              </span>
            </div>
            <div class="shop-meta">
              <span class="sales">月售 {{ shop.sales }}</span>
              <span class="delivery-info">
                <span v-if="shop.deliveryFee > 0">配送费 ¥{{ shop.deliveryFee }}</span>
                <span v-else>免配送费</span>
                <span class="divider">|</span>
                <span>{{ shop.deliveryTime }}分钟</span>
              </span>
            </div>
            <p class="shop-description">{{ shop.description }}</p>
            <div class="shop-tags">
              <span v-for="tag in shop.tags" :key="tag" class="tag">{{ tag }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Empty State -->
      <div v-if="filteredShops.length === 0" class="empty-state">
        <span class="empty-emoji">🔍</span>
        <p>暂无符合条件的商家</p>
      </div>
    </div>
  </Layout>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import Layout from '@/components/Layout.vue'

const router = useRouter()
const route = useRoute()

const searchKeyword = ref('')
const selectedCategory = ref('')
const sortBy = ref('default')

// 分类数据
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

// 商家数据
const shops = ref([
  {
    id: 1,
    name: '麦当劳',
    emoji: '🍔',
    rating: 4.8,
    sales: 9999,
    deliveryTime: 25,
    deliveryFee: 9,
    description: '西式快餐，汉堡薯条',
    tags: ['品牌连锁', '配送快'],
    category: 1
  },
  {
    id: 2,
    name: '肯德基',
    emoji: '🍗',
    rating: 4.7,
    sales: 8888,
    deliveryTime: 30,
    deliveryFee: 8,
    description: '炸鸡专家，美味到家',
    tags: ['品牌连锁', '炸鸡'],
    category: 1
  },
  {
    id: 3,
    name: '必胜客',
    emoji: '🍕',
    rating: 4.6,
    sales: 7777,
    deliveryTime: 35,
    deliveryFee: 12,
    description: '比萨专家，意面小吃',
    tags: ['品牌连锁', '比萨'],
    category: 1
  },
  {
    id: 4,
    name: '海底捞',
    emoji: '🍲',
    rating: 4.9,
    sales: 6666,
    deliveryTime: 40,
    deliveryFee: 0,
    description: '火锅专家，配送到家',
    tags: ['品牌连锁', '火锅', '热门'],
    category: 3
  },
  {
    id: 5,
    name: '真功夫',
    emoji: '🍜',
    rating: 4.5,
    sales: 5555,
    deliveryTime: 28,
    deliveryFee: 6,
    description: '中式快餐，营养健康',
    tags: ['中式', '健康'],
    category: 2
  },
  {
    id: 6,
    name: '喜茶',
    emoji: '🧋',
    rating: 4.8,
    sales: 4444,
    deliveryTime: 20,
    deliveryFee: 5,
    description: '新式茶饮，网红打卡',
    tags: ['奶茶', '网红'],
    category: 4
  },
  {
    id: 7,
    name: '小龙坎火锅',
    emoji: '🥘',
    rating: 4.7,
    sales: 3333,
    deliveryTime: 45,
    deliveryFee: 15,
    description: '正宗川味火锅',
    tags: ['火锅', '川味', '辣'],
    category: 3
  },
  {
    id: 8,
    name: '寿司之家',
    emoji: '🍣',
    rating: 4.6,
    sales: 2222,
    deliveryTime: 35,
    deliveryFee: 10,
    description: '新鲜日式料理',
    tags: ['日料', '新鲜'],
    category: 6
  },
])

// 筛选和排序逻辑
const filteredShops = computed(() => {
  let result = [...shops.value]

  // 搜索筛选
  if (searchKeyword.value.trim()) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(shop =>
      shop.name.toLowerCase().includes(keyword) ||
      shop.description.toLowerCase().includes(keyword) ||
      shop.tags.some(tag => tag.toLowerCase().includes(keyword))
    )
  }

  // 分类筛选
  if (selectedCategory.value) {
    result = result.filter(shop => shop.category === parseInt(selectedCategory.value))
  }

  // 排序
  switch (sortBy.value) {
    case 'sales':
      result.sort((a, b) => b.sales - a.sales)
      break
    case 'rating':
      result.sort((a, b) => b.rating - a.rating)
      break
    case 'deliveryTime':
      result.sort((a, b) => a.deliveryTime - b.deliveryTime)
      break
  }

  return result
})

// 处理搜索
const handleSearch = () => {
  // 搜索逻辑已经在 computed 中处理
}

// 处理筛选
const handleFilter = () => {
  // 筛选逻辑已经在 computed 中处理
}

// 处理排序
const handleSort = () => {
  // 排序逻辑已经在 computed 中处理
}

// 跳转商家详情
const goToRestaurant = (id) => {
  router.push(`/user/restaurants/${id}`)
}

// 初始化时检查 URL 参数
if (route.query.keyword) {
  searchKeyword.value = route.query.keyword
}
if (route.query.category) {
  selectedCategory.value = route.query.category
}
</script>

<style scoped>
.restaurants-page {
  animation: fadeIn 0.5s ease;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.filter-bar {
  padding: 20px;
  margin-bottom: 24px;
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  align-items: center;
  justify-content: space-between;
}

.search-section {
  display: flex;
  gap: 12px;
  flex: 1;
  min-width: 300px;
}

.search-section input {
  flex: 1;
  padding: 12px 16px;
  border: 1px solid rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  font-size: 14px;
  outline: none;
  transition: border-color 0.3s;
}

.search-section input:focus {
  border-color: #667eea;
}

.search-btn {
  padding: 12px 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: transform 0.3s;
}

.search-btn:hover {
  transform: scale(1.05);
}

.filter-section {
  display: flex;
  gap: 12px;
}

.filter-section select {
  padding: 12px 16px;
  border: 1px solid rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  font-size: 14px;
  background: white;
  cursor: pointer;
  outline: none;
}

.restaurant-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.restaurant-card {
  display: flex;
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
}

.restaurant-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.shop-image {
  width: 200px;
  min-height: 160px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.shop-emoji {
  font-size: 80px;
}

.shop-info {
  flex: 1;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.shop-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.shop-name {
  font-size: 20px;
  margin: 0;
  color: #2d3436;
}

.rating-badge {
  background: #ffa502;
  color: white;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
}

.shop-meta {
  display: flex;
  gap: 16px;
  font-size: 14px;
  color: #636e72;
}

.sales {
  color: #667eea;
}

.delivery-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.divider {
  color: #dfe6e9;
}

.shop-description {
  color: #636e72;
  font-size: 14px;
  margin: 0;
}

.shop-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.tag {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
  color: #667eea;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #b2bec3;
}

.empty-emoji {
  font-size: 64px;
  display: block;
  margin-bottom: 16px;
}
</style>

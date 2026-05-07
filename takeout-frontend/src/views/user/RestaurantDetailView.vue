<template>
  <div class="restaurant-detail">
    <!-- Loading -->
    <div v-if="loading" class="loading">加载中...</div>

    <template v-else>
      <!-- 商家信息 -->
      <div class="restaurant-header">
        <h1>{{ restaurant.name }}</h1>
        <div class="restaurant-info">
          <span class="rating">⭐ {{ restaurant.rating || '-' }}</span>
          <span class="delivery-time">⏱️ {{ restaurant.deliveryTime || 30 }}分钟</span>
          <span class="min-order">起送¥{{ restaurant.minOrder || 20 }}</span>
          <span class="delivery-fee">配送费¥{{ restaurant.deliveryFee || 5 }}</span>
        </div>
      </div>

      <!-- 菜品分类 + 菜品列表 -->
      <div class="menu-container">
        <div class="category-list">
          <div
            v-for="category in categories"
            :key="category.id"
            class="category-item"
            :class="{ active: activeCategoryId === category.id }"
            @click="activeCategoryId = category.id"
          >
            {{ category.name }}
          </div>
        </div>
        <div class="food-list">
          <div v-for="food in filteredFoods" :key="food.id" class="food-item">
            <div class="food-image-placeholder">🍽️</div>
            <div class="food-info">
              <h3>{{ food.name }}</h3>
              <p class="food-description">{{ food.description }}</p>
              <div class="food-price">
                <span class="price">¥{{ food.price }}</span>
                <div class="food-actions">
                  <button class="decrease-btn" @click="decreaseFood(food)">-</button>
                  <span class="food-count">{{ cartStore.getQuantity(food.id) }}</span>
                  <button class="increase-btn" @click="increaseFood(food)">+</button>
                </div>
              </div>
            </div>
          </div>
          <div v-if="filteredFoods.length === 0" class="empty-foods">该分类暂无菜品</div>
        </div>
      </div>

      <!-- 购物车底部栏 -->
      <div class="cart-bar" v-if="!cartStore.isEmpty">
        <div class="cart-info">
          <span class="cart-count">{{ cartStore.totalCount }}</span>
          <span class="cart-total">¥{{ cartStore.totalPrice.toFixed(2) }}</span>
        </div>
        <button class="checkout-btn" @click="goToCart">去结算</button>
      </div>
    </template>
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
  console.log('[RestaurantDetail] 加载商家详情, id:', id)
  try {
    const [merchantRes, dishRes] = await Promise.all([
      getMerchantById(id),
      getDishList(id)
    ])
    console.log('[RestaurantDetail] API成功 merchant:', merchantRes, 'dishes:', dishRes)
    restaurant.value = merchantRes.data || {}
    const dishList = dishRes.data || []
    foods.value = dishList.map(d => ({
      id: d.id,
      name: d.name,
      price: d.price,
      description: d.description,
      stock: d.stock,
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
  } catch (e) {
    console.error('[RestaurantDetail] API失败:', e.message)
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
  padding-bottom: 100px;
}

.loading {
  text-align: center;
  padding: 60px;
  color: #999;
}

.restaurant-header {
  padding: 20px;
  background-color: #fff;
  border-bottom: 1px solid #f0f0f0;
  border-radius: 12px;
  margin-bottom: 16px;
}

.restaurant-header h1 {
  margin: 0 0 10px 0;
  font-size: 24px;
  font-weight: bold;
}

.restaurant-info {
  display: flex;
  gap: 15px;
  font-size: 14px;
  color: #666;
}

.menu-container {
  display: flex;
  height: calc(100vh - 250px);
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
}

.category-list {
  width: 100px;
  background-color: #f5f5f5;
  overflow-y: auto;
}

.category-item {
  padding: 15px 10px;
  text-align: center;
  border-bottom: 1px solid #e0e0e0;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s;
}

.category-item.active {
  background-color: #fff;
  color: #ff6b00;
  font-weight: bold;
  border-left: 3px solid #ff6b00;
}

.food-list {
  flex: 1;
  padding: 15px;
  background-color: #fafafa;
  overflow-y: auto;
}

.food-item {
  display: flex;
  padding: 15px;
  margin-bottom: 10px;
  background-color: #fff;
  border-radius: 8px;
}

.food-image-placeholder {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  background: #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 36px;
  margin-right: 15px;
  flex-shrink: 0;
}

.food-info {
  flex: 1;
}

.food-info h3 {
  margin: 0 0 5px 0;
  font-size: 16px;
}

.food-description {
  margin: 0 0 10px 0;
  font-size: 13px;
  color: #999;
  line-height: 1.4;
}

.food-price {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price {
  font-size: 18px;
  font-weight: bold;
  color: #ff6b00;
}

.food-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.decrease-btn, .increase-btn {
  width: 28px;
  height: 28px;
  border: 1px solid #e0e0e0;
  border-radius: 50%;
  background-color: #fff;
  font-size: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
}

.decrease-btn:hover, .increase-btn:hover {
  border-color: #ff6b00;
  color: #ff6b00;
}

.food-count {
  min-width: 30px;
  text-align: center;
  font-weight: 500;
}

.empty-foods {
  text-align: center;
  padding: 40px;
  color: #999;
}

.cart-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 70px;
  background-color: #fff;
  border-top: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.1);
  z-index: 50;
}

.cart-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.cart-count {
  background-color: #ff6b00;
  color: #fff;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: bold;
}

.cart-total {
  font-size: 20px;
  font-weight: bold;
  color: #ff6b00;
}

.checkout-btn {
  background-color: #ff6b00;
  color: #fff;
  border: none;
  border-radius: 25px;
  padding: 12px 30px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  transition: background-color 0.2s;
}

.checkout-btn:hover {
  background-color: #ff8533;
}
</style>

<template>
  <div class="restaurant-detail">
    <!-- 商家信息 -->
    <div class="restaurant-header">
      <h1>{{ restaurant.name }}</h1>
      <div class="restaurant-info">
        <span class="rating">{{ restaurant.rating }}</span>
        <span class="distance">{{ restaurant.distance }}m</span>
        <span class="delivery-time">{{ restaurant.deliveryTime }}分钟</span>
        <span class="min-order">起送¥{{ restaurant.minOrder }}</span>
        <span class="delivery-fee">配送费¥{{ restaurant.deliveryFee }}</span>
      </div>
    </div>

    <!-- 菜品分类 -->
    <div class="menu-container">
      <div class="category-list">
        <div 
          v-for="category in categories" 
          :key="category.id"
          class="category-item"
          :class="{ active: activeCategoryId === category.id }"
          @click="selectCategory(category.id)"
        >
          {{ category.name }}
        </div>
      </div>
      <div class="food-list">
        <div v-for="food in foods" :key="food.id" class="food-item">
          <img :src="food.image" alt="food.name" class="food-image">
          <div class="food-info">
            <h3>{{ food.name }}</h3>
            <p class="food-description">{{ food.description }}</p>
            <div class="food-price">
              <span class="price">¥{{ food.price }}</span>
              <div class="food-actions">
                <button class="decrease-btn" @click="decreaseFood(food.id)">-</button>
                <span class="food-count">{{ cart[food.id] || 0 }}</span>
                <button class="increase-btn" @click="increaseFood(food.id)">+</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 购物车底部栏 -->
    <div class="cart-bar">
      <div class="cart-info">
        <span class="cart-count">{{ totalCount }}</span>
        <span class="cart-total">¥{{ totalPrice.toFixed(2) }}</span>
      </div>
      <button class="checkout-btn" @click="goToCart">去结算</button>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getMenu } from '../../api/merchant'

export default {
  name: 'RestaurantDetailView',
  props: {
    storeId: {
      type: String,
      default: () => {
        // 从路由参数中获取storeId
        const router = useRouter()
        return router.currentRoute.value.params.id
      }
    }
  },
  setup(props) {
    const router = useRouter()
    const restaurant = ref({
      name: '示例餐厅',
      rating: 4.8,
      distance: 500,
      deliveryTime: 30,
      minOrder: 20,
      deliveryFee: 5
    })
    const categories = ref([
      { id: 1, name: '推荐' },
      { id: 2, name: '主食' },
      { id: 3, name: '小吃' },
      { id: 4, name: '饮料' }
    ])
    const foods = ref([
      { id: 1, name: '宫保鸡丁', description: '经典川菜，香辣可口', price: 28, image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=delicious%20kung%20pao%20chicken%20dish&image_size=square' },
      { id: 2, name: '麻婆豆腐', description: '麻辣鲜香，下饭神器', price: 22, image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=mapo%20tofu%20chinese%20dish&image_size=square' },
      { id: 3, name: '红烧肉', description: '肥而不腻，入口即化', price: 38, image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=braised%20pork%20belly%20chinese%20dish&image_size=square' },
      { id: 4, name: '可乐', description: '冰镇可乐', price: 8, image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=coca%20cola%20drink&image_size=square' }
    ])
    const activeCategoryId = ref(1)
    const cart = ref({})

    const totalCount = computed(() => {
      return Object.values(cart.value).reduce((sum, count) => sum + count, 0)
    })

    const totalPrice = computed(() => {
      return Object.entries(cart.value).reduce((sum, [foodId, count]) => {
        const food = foods.value.find(f => f.id == foodId)
        return sum + (food ? food.price * count : 0)
      }, 0)
    })

    const selectCategory = (categoryId) => {
      activeCategoryId.value = categoryId
    }

    const increaseFood = (foodId) => {
      cart.value[foodId] = (cart.value[foodId] || 0) + 1
    }

    const decreaseFood = (foodId) => {
      if (cart.value[foodId] > 0) {
        cart.value[foodId]--
      }
    }

    const goToCart = () => {
      router.push('/cart')
    }

    onMounted(async () => {
      try {
        // 实际项目中调用API获取数据
        // const response = await getMenu(props.storeId)
        // restaurant.value = response.data.restaurant
        // categories.value = response.data.categories
        // foods.value = response.data.foods
      } catch (error) {
        console.error('Failed to get menu:', error)
      }
    })

    return {
      restaurant,
      categories,
      foods,
      activeCategoryId,
      cart,
      totalCount,
      totalPrice,
      selectCategory,
      increaseFood,
      decreaseFood,
      goToCart
    }
  }
}
</script>

<style scoped>
.restaurant-detail {
  padding-bottom: 100px;
}

.restaurant-header {
  padding: 20px;
  background-color: #fff;
  border-bottom: 1px solid #f0f0f0;
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

.food-image {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  object-fit: cover;
  margin-right: 15px;
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
  font-size: 14px;
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
}

.food-count {
  min-width: 30px;
  text-align: center;
}

.cart-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 80px;
  background-color: #fff;
  border-top: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.1);
}

.cart-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.cart-count {
  background-color: #ff6b00;
  color: #fff;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
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
}

.checkout-btn:hover {
  background-color: #ff8533;
}
</style>
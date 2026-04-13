<template>
  <div class="cart-view">
    <h1>购物车</h1>
    
    <!-- 商家信息 -->
    <div class="merchant-info">
      <h2>{{ merchant.name }}</h2>
      <div class="merchant-details">
        <span class="distance">{{ merchant.distance }}m</span>
        <span class="delivery-time">{{ merchant.deliveryTime }}分钟</span>
      </div>
    </div>

    <!-- 购物车商品列表 -->
    <div class="cart-items">
      <div v-for="item in cartItems" :key="item.id" class="cart-item">
        <img :src="item.image" alt="item.name" class="item-image">
        <div class="item-info">
          <h3>{{ item.name }}</h3>
          <p class="item-description">{{ item.description }}</p>
          <div class="item-price-actions">
            <span class="item-price">¥{{ item.price }}</span>
            <div class="item-actions">
              <button class="decrease-btn" @click="decreaseItem(item.id)">-</button>
              <span class="item-count">{{ item.count }}</span>
              <button class="increase-btn" @click="increaseItem(item.id)">+</button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部结算栏 -->
    <div class="checkout-bar">
      <div class="checkout-info">
        <div class="subtotal">
          <span>小计：</span>
          <span class="subtotal-price">¥{{ subtotal.toFixed(2) }}</span>
        </div>
        <div class="delivery-fee">
          <span>配送费：</span>
          <span>¥{{ merchant.deliveryFee }}</span>
        </div>
        <div class="total">
          <span>总计：</span>
          <span class="total-price">¥{{ total.toFixed(2) }}</span>
        </div>
      </div>
      <button class="checkout-btn" @click="checkout">去结算</button>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'

export default {
  name: 'CartView',
  setup() {
    const router = useRouter()
    const merchant = ref({
      name: '示例餐厅',
      distance: 500,
      deliveryTime: 30,
      deliveryFee: 5
    })
    const cartItems = ref([
      {
        id: 1,
        name: '宫保鸡丁',
        description: '经典川菜，香辣可口',
        price: 28,
        count: 2,
        image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=delicious%20kung%20pao%20chicken%20dish&image_size=square'
      },
      {
        id: 2,
        name: '麻婆豆腐',
        description: '麻辣鲜香，下饭神器',
        price: 22,
        count: 1,
        image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=mapo%20tofu%20chinese%20dish&image_size=square'
      },
      {
        id: 4,
        name: '可乐',
        description: '冰镇可乐',
        price: 8,
        count: 2,
        image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=coca%20cola%20drink&image_size=square'
      }
    ])

    const subtotal = computed(() => {
      return cartItems.value.reduce((sum, item) => sum + item.price * item.count, 0)
    })

    const total = computed(() => {
      return subtotal.value + merchant.value.deliveryFee
    })

    const increaseItem = (itemId) => {
      const item = cartItems.value.find(item => item.id === itemId)
      if (item) {
        item.count++
      }
    }

    const decreaseItem = (itemId) => {
      const item = cartItems.value.find(item => item.id === itemId)
      if (item && item.count > 1) {
        item.count--
      }
    }

    const checkout = () => {
      // 实际项目中，这里会调用API创建订单
      // 然后跳转到订单详情页
      router.push('/orders')
    }

    onMounted(() => {
      // 实际项目中，这里会从本地存储或API获取购物车数据
    })

    return {
      merchant,
      cartItems,
      subtotal,
      total,
      increaseItem,
      decreaseItem,
      checkout
    }
  }
}
</script>

<style scoped>
.cart-view {
  padding-bottom: 150px;
}

h1 {
  padding: 20px;
  margin: 0;
  font-size: 24px;
  font-weight: bold;
  background-color: #fff;
  border-bottom: 1px solid #f0f0f0;
}

.merchant-info {
  padding: 15px 20px;
  background-color: #fff;
  margin-top: 10px;
  border-bottom: 1px solid #f0f0f0;
}

.merchant-info h2 {
  margin: 0 0 5px 0;
  font-size: 18px;
}

.merchant-details {
  display: flex;
  gap: 15px;
  font-size: 14px;
  color: #666;
}

.cart-items {
  margin-top: 10px;
  background-color: #fff;
}

.cart-item {
  display: flex;
  padding: 15px 20px;
  border-bottom: 1px solid #f0f0f0;
}

.item-image {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  object-fit: cover;
  margin-right: 15px;
}

.item-info {
  flex: 1;
}

.item-info h3 {
  margin: 0 0 5px 0;
  font-size: 16px;
}

.item-description {
  margin: 0 0 10px 0;
  font-size: 14px;
  color: #999;
  line-height: 1.4;
}

.item-price-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.item-price {
  font-size: 18px;
  font-weight: bold;
  color: #ff6b00;
}

.item-actions {
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

.item-count {
  min-width: 30px;
  text-align: center;
}

.checkout-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: #fff;
  border-top: 1px solid #f0f0f0;
  padding: 15px 20px;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.1);
}

.checkout-info {
  margin-bottom: 15px;
}

.subtotal, .delivery-fee, .total {
  display: flex;
  justify-content: space-between;
  margin-bottom: 5px;
  font-size: 14px;
}

.subtotal-price {
  font-weight: bold;
}

.total {
  font-size: 16px;
  font-weight: bold;
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px solid #f0f0f0;
}

.total-price {
  color: #ff6b00;
  font-size: 18px;
}

.checkout-btn {
  width: 100%;
  background-color: #ff6b00;
  color: #fff;
  border: none;
  border-radius: 25px;
  padding: 15px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
}

.checkout-btn:hover {
  background-color: #ff8533;
}
</style>
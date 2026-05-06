<template>
  <div class="cart-view">
    <h1>购物车</h1>

    <!-- 空购物车 -->
    <div v-if="cartStore.isEmpty" class="empty-cart">
      <span class="empty-emoji">🛒</span>
      <p>购物车是空的</p>
      <button class="go-shop-btn" @click="$router.push('/user/restaurants')">去选购</button>
    </div>

    <template v-else>
      <!-- 商家信息 -->
      <div class="merchant-info">
        <h2>{{ cartStore.merchantName || '商家' }}</h2>
      </div>

      <!-- 购物车商品列表 -->
      <div class="cart-items">
        <div v-for="item in cartStore.items" :key="item.dishId" class="cart-item">
          <div class="item-image-placeholder">🍽️</div>
          <div class="item-info">
            <h3>{{ item.dishName }}</h3>
            <p class="item-description">{{ item.description }}</p>
            <div class="item-price-actions">
              <span class="item-price">¥{{ item.price }}</span>
              <div class="item-actions">
                <button class="decrease-btn" @click="decreaseItem(item)">-</button>
                <span class="item-count">{{ item.quantity }}</span>
                <button class="increase-btn" @click="increaseItem(item)">+</button>
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
            <span class="subtotal-price">¥{{ cartStore.totalPrice.toFixed(2) }}</span>
          </div>
          <div class="total">
            <span>总计：</span>
            <span class="total-price">¥{{ cartStore.totalPrice.toFixed(2) }}</span>
          </div>
        </div>
        <button class="checkout-btn" @click="checkout">去结算 ({{ cartStore.totalCount }}件)</button>
      </div>
    </template>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useCartStore } from '@/stores/cart'

const router = useRouter()
const cartStore = useCartStore()

function increaseItem(item) {
  cartStore.updateQuantity(item.dishId, item.quantity + 1)
}

function decreaseItem(item) {
  if (item.quantity <= 1) {
    cartStore.removeItem(item.dishId)
  } else {
    cartStore.updateQuantity(item.dishId, item.quantity - 1)
  }
}

function checkout() {
  // TODO: call createOrder API when backend order-service is ready
  router.push('/user/orders')
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
  border-radius: 12px;
}

.empty-cart {
  text-align: center;
  padding: 80px 20px;
  color: #999;
}

.empty-emoji {
  font-size: 64px;
  display: block;
  margin-bottom: 16px;
}

.go-shop-btn {
  margin-top: 16px;
  padding: 12px 32px;
  background: #ff6b00;
  color: #fff;
  border: none;
  border-radius: 25px;
  font-size: 16px;
  cursor: pointer;
}

.merchant-info {
  padding: 15px 20px;
  background-color: #fff;
  margin-top: 10px;
  border-radius: 12px;
}

.merchant-info h2 {
  margin: 0;
  font-size: 18px;
}

.cart-items {
  margin-top: 10px;
  background-color: #fff;
  border-radius: 12px;
  overflow: hidden;
}

.cart-item {
  display: flex;
  padding: 15px 20px;
  border-bottom: 1px solid #f0f0f0;
}

.item-image-placeholder {
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

.item-info {
  flex: 1;
}

.item-info h3 {
  margin: 0 0 5px 0;
  font-size: 16px;
}

.item-description {
  margin: 0 0 10px 0;
  font-size: 13px;
  color: #999;
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
  transition: all 0.2s;
}

.decrease-btn:hover, .increase-btn:hover {
  border-color: #ff6b00;
  color: #ff6b00;
}

.item-count {
  min-width: 30px;
  text-align: center;
  font-weight: 500;
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
  z-index: 50;
}

.checkout-info {
  margin-bottom: 10px;
}

.subtotal, .total {
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
  padding-top: 8px;
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
  padding: 14px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  transition: background-color 0.2s;
}

.checkout-btn:hover {
  background-color: #ff8533;
}
</style>

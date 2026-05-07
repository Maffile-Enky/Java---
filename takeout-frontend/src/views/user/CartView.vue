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
        <button class="checkout-btn" @click="showCheckout = true">去结算 ({{ cartStore.totalCount }}件)</button>
      </div>

      <!-- 结算弹窗 -->
      <div v-if="showCheckout" class="modal-overlay" @click.self="showCheckout = false">
        <div class="modal">
          <h2 class="modal-title">确认订单</h2>
          <form @submit.prevent="submitOrder">
            <div class="form-group">
              <label>收货人</label>
              <input v-model="orderForm.name" type="text" placeholder="请输入收货人姓名" required />
            </div>
            <div class="form-group">
              <label>联系电话</label>
              <input v-model="orderForm.phone" type="tel" placeholder="请输入联系电话" required />
            </div>
            <div class="form-group">
              <label>收货地址</label>
              <input v-model="orderForm.address" type="text" placeholder="请输入收货地址" required />
            </div>
            <div class="form-group">
              <label>备注</label>
              <textarea v-model="orderForm.note" placeholder="选填，如口味偏好等" rows="2"></textarea>
            </div>
            <div class="order-summary">
              <span>共 {{ cartStore.totalCount }} 件，合计</span>
              <span class="summary-price">¥{{ cartStore.totalPrice.toFixed(2) }}</span>
            </div>
            <div class="modal-actions">
              <button type="button" class="btn-cancel" @click="showCheckout = false">取消</button>
              <button type="submit" class="btn-confirm" :disabled="submitting">
                {{ submitting ? '提交中...' : '提交订单' }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '@/stores/cart'
import { createOrder } from '@/api/order'

const router = useRouter()
const cartStore = useCartStore()

const showCheckout = ref(false)
const submitting = ref(false)
const orderForm = ref({ name: '', phone: '', address: '', note: '' })

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

async function submitOrder() {
  submitting.value = true
  try {
    await createOrder({
      merchantId: cartStore.merchantId,
      merchantName: cartStore.merchantName,
      deliveryName: orderForm.value.name,
      deliveryPhone: orderForm.value.phone,
      deliveryAddress: orderForm.value.address,
      note: orderForm.value.note,
      items: cartStore.items.map(item => ({
        dishId: item.dishId,
        dishName: item.dishName,
        price: item.price,
        quantity: item.quantity
      }))
    })
    cartStore.clearCart()
    showCheckout.value = false
    alert('下单成功!')
    router.push('/user/orders')
  } catch (e) {
    alert('下单失败: ' + (e.message || '请重试'))
  } finally {
    submitting.value = false
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

.modal-overlay {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.45);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}
.modal {
  background: #fff;
  border-radius: 12px;
  padding: 28px;
  width: 420px;
  max-width: 90vw;
  box-shadow: 0 8px 40px rgba(0,0,0,0.15);
}
.modal-title {
  font-size: 20px;
  font-weight: 700;
  margin: 0 0 20px 0;
}
.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: 14px;
}
.form-group label {
  font-size: 14px;
  font-weight: 600;
  color: #555;
}
.form-group input,
.form-group textarea {
  padding: 10px 14px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 14px;
  outline: none;
}
.form-group input:focus,
.form-group textarea:focus {
  border-color: #ff6b00;
}
.order-summary {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 0;
  border-top: 1px solid #f0f0f0;
  font-size: 15px;
  font-weight: 600;
}
.summary-price {
  color: #ff6b00;
  font-size: 20px;
}
.modal-actions {
  display: flex;
  gap: 12px;
  margin-top: 12px;
}
.btn-cancel {
  flex: 1;
  padding: 10px;
  background: #f5f5f5;
  color: #666;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
}
.btn-confirm {
  flex: 1;
  padding: 10px;
  background: #ff6b00;
  color: #fff;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
}
.btn-confirm:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>

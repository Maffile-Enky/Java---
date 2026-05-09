<template>
  <div class="cart-view">
    <!-- Empty -->
    <div v-if="cartStore.isEmpty" class="empty-state">
      <img src="/images/empty-states/empty-cart.svg" alt="空购物车" class="empty-img" />
      <p>购物车是空的</p>
      <button class="btn-primary" @click="$router.push('/user/restaurants')">去选购</button>
    </div>

    <template v-else>
      <!-- Merchant header -->
      <div class="merchant-bar card">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="18" height="18"><path d="M3 3h18v18H3z"/><path d="M3 9h18"/><path d="M9 21V9"/></svg>
        <span class="merchant-name">{{ cartStore.merchantName || '商家' }}</span>
      </div>

      <!-- Items -->
      <div class="items-list card">
        <div v-for="item in cartStore.items" :key="item.dishId" class="cart-item">
          <div class="item-img">
            <img :src="item.imageUrl || '/images/placeholders/dish-default.png'" :alt="item.dishName" />
          </div>
          <div class="item-info">
            <h3 class="item-name">{{ item.dishName }}</h3>
            <p class="item-desc">{{ item.description }}</p>
            <div class="item-bottom">
              <span class="item-price">¥{{ item.price }}</span>
              <div class="item-stepper">
                <button class="stepper-btn" @click="decreaseItem(item)">-</button>
                <span class="stepper-qty">{{ item.quantity }}</span>
                <button class="stepper-btn stepper-plus" @click="increaseItem(item)">+</button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Bottom bar -->
      <div class="checkout-bar">
        <div class="checkout-summary">
          <span class="summary-count">共{{ cartStore.totalCount }}件</span>
          <span class="summary-price">¥{{ cartStore.totalPrice.toFixed(2) }}</span>
        </div>
        <button class="checkout-btn" @click="showCheckout = true">去结算</button>
      </div>

      <!-- Checkout modal -->
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
              <span class="summary-total">¥{{ cartStore.totalPrice.toFixed(2) }}</span>
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
    const res = await createOrder({
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
    const orderData = res.data || res
    router.push({
      path: '/user/payment',
      query: {
        orderNo: orderData.orderNo,
        amount: orderData.totalPrice,
        subject: cartStore.merchantName + ' - 外卖订单'
      }
    })
  } catch (e) {
    alert('下单失败: ' + (e.message || '请重试'))
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.cart-view {
  padding-bottom: 90px;
}

/* Empty */
.empty-state {
  text-align: center;
  padding: 80px 20px;
  color: var(--color-text-hint);
}

.empty-img {
  width: 200px;
  margin: 0 auto 20px;
  opacity: 0.7;
}

.empty-state p {
  margin-bottom: 20px;
  font-size: var(--font-size-md);
}

/* Merchant bar */
.merchant-bar {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-lg);
  margin-bottom: var(--spacing-md);
  color: var(--color-text-primary);
}

.merchant-name {
  font-size: var(--font-size-md);
  font-weight: 600;
}

/* Items */
.items-list {
  overflow: hidden;
}

.cart-item {
  display: flex;
  gap: var(--spacing-md);
  padding: var(--spacing-lg);
  border-bottom: 1px solid var(--color-divider);
}

.cart-item:last-child {
  border-bottom: none;
}

.item-img {
  width: 80px;
  height: 80px;
  border-radius: var(--radius-md);
  overflow: hidden;
  flex-shrink: 0;
  background: var(--color-bg-page);
}

.item-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.item-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.item-name {
  font-size: var(--font-size-base);
  font-weight: 600;
  margin: 0 0 4px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.item-desc {
  font-size: var(--font-size-xs);
  color: var(--color-text-hint);
  margin: 0 0 auto 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.item-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 8px;
}

.item-price {
  font-size: var(--font-size-md);
  font-weight: 700;
  color: var(--color-accent);
}

.item-stepper {
  display: flex;
  align-items: center;
  gap: 10px;
}

.stepper-btn {
  width: 28px;
  height: 28px;
  border-radius: var(--radius-full);
  border: 1px solid var(--color-border);
  background: var(--color-bg-card);
  font-size: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
}

.stepper-plus {
  background: var(--color-primary);
  border-color: var(--color-primary);
}

.stepper-btn:hover {
  border-color: var(--color-accent);
  color: var(--color-accent);
}

.stepper-qty {
  min-width: 24px;
  text-align: center;
  font-weight: 600;
}

/* Checkout bar */
.checkout-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 60px;
  background: var(--color-bg-card);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 var(--spacing-xl);
  box-shadow: 0 -2px 10px rgba(0,0,0,0.06);
  z-index: 50;
}

.checkout-summary {
  display: flex;
  align-items: baseline;
  gap: var(--spacing-sm);
}

.summary-count {
  font-size: var(--font-size-sm);
  color: var(--color-text-hint);
}

.summary-price {
  font-size: var(--font-size-xl);
  font-weight: 700;
  color: var(--color-accent);
}

.checkout-btn {
  background: var(--color-accent);
  color: #fff;
  border: none;
  border-radius: var(--radius-xl);
  padding: 10px 32px;
  font-size: var(--font-size-base);
  font-weight: 700;
  cursor: pointer;
  transition: background 0.2s;
}

.checkout-btn:hover {
  background: var(--color-accent-dark);
}

/* Modal */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.45);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal {
  background: var(--color-bg-card);
  border-radius: var(--radius-xl);
  padding: 28px;
  width: 420px;
  max-width: 90vw;
  box-shadow: var(--shadow-lg);
}

.modal-title {
  font-size: var(--font-size-lg);
  font-weight: 700;
  margin: 0 0 20px 0;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: var(--spacing-lg);
}

.form-group label {
  font-size: var(--font-size-sm);
  font-weight: 600;
  color: var(--color-text-secondary);
}

.form-group input,
.form-group textarea {
  padding: 10px 14px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  font-size: var(--font-size-base);
  transition: border-color 0.2s;
}

.form-group input:focus,
.form-group textarea:focus {
  border-color: var(--color-primary);
}

.order-summary {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-lg) 0;
  border-top: 1px solid var(--color-divider);
  font-size: var(--font-size-base);
  font-weight: 600;
}

.summary-total {
  color: var(--color-accent);
  font-size: var(--font-size-lg);
}

.modal-actions {
  display: flex;
  gap: var(--spacing-md);
  margin-top: var(--spacing-md);
}

.btn-cancel {
  flex: 1;
  padding: 10px;
  background: var(--color-bg-page);
  color: var(--color-text-secondary);
  border: none;
  border-radius: var(--radius-md);
  font-size: var(--font-size-base);
  cursor: pointer;
}

.btn-confirm {
  flex: 1;
  padding: 10px;
  background: var(--color-primary);
  color: var(--color-text-primary);
  border: none;
  border-radius: var(--radius-md);
  font-size: var(--font-size-base);
  font-weight: 700;
  cursor: pointer;
}

.btn-confirm:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>

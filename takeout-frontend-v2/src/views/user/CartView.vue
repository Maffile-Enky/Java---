<template>
  <div class="cart-page">
    <div class="container">
      <h1 class="page-title">购物车</h1>

      <EmptyState v-if="!cart.items.length" icon="🛒" text="购物车是空的">
        <GlassButton variant="primary" size="sm" style="margin-top:16px;" @click="$router.push('/user/restaurants')">
          去点餐
        </GlassButton>
      </EmptyState>

      <template v-else>
        <div class="cart-merchant">
          <span class="merchant-icon">🏪</span>
          <span class="merchant-name">{{ cart.merchantName }}</span>
        </div>

        <div class="cart-items">
          <div v-for="item in cart.items" :key="item.dishId" class="cart-item glass-panel">
            <div class="item-info">
              <h3 class="item-name">{{ item.dishName }}</h3>
              <span class="item-price">¥{{ Number(item.price).toFixed(2) }}</span>
            </div>
            <div class="item-qty">
              <button class="qty-btn" @click="cart.updateQuantity(item.dishId, item.quantity - 1)">−</button>
              <span class="qty-num">{{ item.quantity }}</span>
              <button class="qty-btn" @click="cart.updateQuantity(item.dishId, item.quantity + 1)">+</button>
            </div>
          </div>
        </div>

        <div class="cart-summary glass-panel">
          <div class="summary-row">
            <span>小计</span>
            <span class="summary-price">¥{{ cart.totalPrice.toFixed(2) }}</span>
          </div>
          <div class="summary-row">
            <span>配送费</span>
            <span>¥5.00</span>
          </div>
          <div class="summary-divider"></div>
          <div class="summary-row summary-total">
            <span>合计</span>
            <span class="total-price">¥{{ (cart.totalPrice + 5).toFixed(2) }}</span>
          </div>
          <GlassButton variant="primary" block @click="goCheckout">
            去结算 ({{ cart.totalCount }}件)
          </GlassButton>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useCartStore } from '@/stores/cart'
import { createOrder } from '@/api/order'
import GlassButton from '@/components/ui/GlassButton.vue'
import EmptyState from '@/components/common/EmptyState.vue'

const router = useRouter()
const cart = useCartStore()

async function goCheckout() {
  try {
    const orderData = {
      merchantId: cart.merchantId,
      items: cart.items.map(item => ({
        dishId: item.dishId,
        dishName: item.dishName,
        price: item.price,
        quantity: item.quantity
      }))
    }
    const res = await createOrder(orderData)
    const orderId = res.data?.id
    if (orderId) {
      cart.clearCart()
      router.push(`/user/orders/${orderId}`)
    }
  } catch (error) {
    console.error('创建订单失败:', error)
  }
}
</script>

<style scoped>
.cart-page {
  padding: var(--space-6) 0;
}

.page-title {
  font-family: var(--font-serif);
  font-size: 1.8rem;
  font-weight: 900;
  margin-bottom: var(--space-6);
}

.cart-merchant {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-3) var(--space-4);
  background: var(--glass);
  border: 1px solid var(--glass-border);
  border-radius: var(--radius-md);
  margin-bottom: var(--space-4);
}

.merchant-icon { font-size: 1.2rem; }
.merchant-name { font-weight: 600; font-size: var(--text-sm); }

.cart-items {
  display: flex;
  flex-direction: column;
  gap: var(--space-3);
  margin-bottom: var(--space-6);
}

.cart-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--space-4);
}

.item-info {
  flex: 1;
}

.item-name {
  font-size: var(--text-base);
  font-weight: 600;
  margin-bottom: var(--space-1);
}

.item-price {
  font-size: var(--text-sm);
  color: var(--accent);
  font-weight: 700;
}

.item-qty {
  display: flex;
  align-items: center;
  gap: var(--space-3);
}

.qty-btn {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: var(--glass);
  border: 1px solid var(--glass-border);
  color: var(--text-primary);
  font-size: 1rem;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all var(--duration-fast);
}

.qty-btn:hover {
  border-color: var(--accent);
  background: rgba(110, 231, 160, 0.1);
}

.qty-num {
  font-weight: 700;
  min-width: 24px;
  text-align: center;
}

.cart-summary {
  padding: var(--space-6);
}

.summary-row {
  display: flex;
  justify-content: space-between;
  font-size: var(--text-sm);
  color: var(--text-secondary);
  margin-bottom: var(--space-2);
}

.summary-price {
  color: var(--text-primary);
  font-weight: 600;
}

.summary-divider {
  height: 1px;
  background: var(--glass-border);
  margin: var(--space-3) 0;
}

.summary-total {
  font-size: 1rem;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: var(--space-6);
}

.total-price {
  color: var(--accent);
  font-size: 1.3rem;
  font-weight: 800;
}
</style>

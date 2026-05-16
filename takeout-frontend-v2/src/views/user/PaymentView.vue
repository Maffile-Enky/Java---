<template>
  <div class="payment-page">
    <div class="container">
      <div class="detail-header">
        <button class="back-btn" @click="$router.back()">← 返回</button>
        <h1 class="page-title">支付订单</h1>
      </div>

      <LoadingSpinner v-if="loading" text="加载中..." />

      <template v-else-if="order">
        <!-- Order Summary -->
        <div class="payment-summary glass-panel">
          <h2 class="summary-title">{{ order.merchantName || '商家' }}</h2>
          <div class="summary-items">
            <span v-for="item in (order.items || []).slice(0, 3)" :key="item.id" class="summary-item">
              {{ item.dishName || item.name }} x{{ item.quantity }}
            </span>
          </div>
          <div class="summary-total">
            <span>应付金额</span>
            <span class="total-price">¥{{ Number(order.totalAmount || 0).toFixed(2) }}</span>
          </div>
        </div>

        <!-- Payment Methods -->
        <div class="payment-methods glass-panel">
          <h3 class="methods-title">选择支付方式</h3>
          <label class="method-item" v-for="method in methods" :key="method.id">
            <input type="radio" v-model="selectedMethod" :value="method.id" name="payment" />
            <span class="method-icon">{{ method.icon }}</span>
            <span class="method-name">{{ method.name }}</span>
          </label>
        </div>

        <GlassButton variant="primary" block :loading="paying" @click="handlePay">
          确认支付 ¥{{ Number(order.totalAmount || 0).toFixed(2) }}
        </GlassButton>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getOrderDetail } from '@/api/order'
import { createPayment } from '@/api/payment'
import LoadingSpinner from '@/components/common/LoadingSpinner.vue'
import GlassButton from '@/components/ui/GlassButton.vue'

const route = useRoute()
const router = useRouter()
const order = ref(null)
const loading = ref(true)
const paying = ref(false)
const selectedMethod = ref('wechat')

const methods = [
  { id: 'wechat', name: '微信支付', icon: '💚' },
  { id: 'alipay', name: '支付宝', icon: '💙' }
]

async function fetchOrder() {
  try {
    const res = await getOrderDetail(route.params.id)
    order.value = res.data
  } catch { order.value = null }
  finally { loading.value = false }
}

async function handlePay() {
  paying.value = true
  try {
    await createPayment({
      orderId: route.params.id,
      paymentMethod: selectedMethod.value
    })
    router.push(`/user/orders/${route.params.id}`)
  } catch {}
  finally { paying.value = false }
}

onMounted(fetchOrder)
</script>

<style scoped>
.payment-page {
  padding: var(--space-6) 0;
}

.detail-header {
  display: flex;
  align-items: center;
  gap: var(--space-4);
  margin-bottom: var(--space-6);
}

.back-btn {
  background: none;
  border: none;
  color: var(--text-secondary);
  font-size: var(--text-sm);
  cursor: pointer;
  font-family: var(--font-sans);
}

.back-btn:hover { color: var(--accent); }

.page-title {
  font-family: var(--font-serif);
  font-size: 1.8rem;
  font-weight: 900;
}

.payment-summary {
  padding: var(--space-6);
  margin-bottom: var(--space-4);
}

.summary-title {
  font-size: var(--text-base);
  font-weight: 700;
  margin-bottom: var(--space-3);
}

.summary-items {
  display: flex;
  flex-wrap: wrap;
  gap: var(--space-2);
  margin-bottom: var(--space-4);
}

.summary-item {
  padding: var(--space-1) var(--space-3);
  background: var(--glass);
  border-radius: var(--radius-sm);
  font-size: var(--text-xs);
  color: var(--text-secondary);
}

.summary-total {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: var(--space-4);
  border-top: 1px solid var(--glass-border);
}

.total-price {
  font-size: 1.5rem;
  font-weight: 800;
  color: var(--accent);
}

.payment-methods {
  padding: var(--space-6);
  margin-bottom: var(--space-6);
}

.methods-title {
  font-size: var(--text-base);
  font-weight: 700;
  margin-bottom: var(--space-4);
}

.method-item {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-3) var(--space-4);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: background var(--duration-fast);
}

.method-item:hover {
  background: var(--glass);
}

.method-item input[type="radio"] {
  accent-color: var(--accent);
}

.method-icon { font-size: 1.3rem; }
.method-name { font-size: var(--text-sm); font-weight: 500; }
</style>

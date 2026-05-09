<template>
  <div class="payment-view">
    <!-- Loading -->
    <div v-if="loading" class="loading">
      <div class="loading-spinner"></div>
      <p>正在创建支付订单...</p>
    </div>

    <!-- Payment success -->
    <div v-else-if="payStatus === 'SUCCESS'" class="result-card success">
      <div class="result-icon">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" width="48" height="48">
          <path d="M20 6L9 17l-5-5"/>
        </svg>
      </div>
      <h2>支付成功</h2>
      <p class="result-amount">¥{{ amount }}</p>
      <p class="result-hint">订单已确认，商家正在准备中</p>
      <div class="result-actions">
        <button class="btn-primary" @click="goOrders">查看订单</button>
      </div>
    </div>

    <!-- Payment failed / closed -->
    <div v-else-if="payStatus === 'FAILED' || payStatus === 'CLOSED'" class="result-card failed">
      <div class="result-icon">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" width="48" height="48">
          <circle cx="12" cy="12" r="10"/><path d="M15 9l-6 6M9 9l6 6"/>
        </svg>
      </div>
      <h2>支付{{ payStatus === 'CLOSED' ? '已关闭' : '失败' }}</h2>
      <p class="result-hint">{{ payStatus === 'CLOSED' ? '支付超时已自动关闭' : '支付过程中出现问题' }}</p>
      <div class="result-actions">
        <button class="btn-outline" @click="goOrders">返回订单</button>
        <button class="btn-primary" @click="retryPayment">重新支付</button>
      </div>
    </div>

    <!-- Payment pending - show payment method selection / QR code -->
    <template v-else>
      <!-- Order summary -->
      <div class="order-summary card">
        <h3 class="summary-title">{{ subject || '外卖订单' }}</h3>
        <p class="summary-amount">¥{{ amount }}</p>
        <p v-if="expireSeconds > 0" class="summary-timer">
          支付剩余时间：{{ formatTime(expireSeconds) }}
        </p>
      </div>

      <!-- Payment method selection (before creating payment) -->
      <div v-if="!paymentNo" class="pay-methods card">
        <h3 class="section-title">选择支付方式</h3>
        <div class="method-list">
          <div
            class="method-item"
            :class="{ active: selectedChannel === 'ALIPAY' }"
            @click="selectedChannel = 'ALIPAY'"
          >
            <div class="method-icon alipay">
              <svg viewBox="0 0 24 24" width="24" height="24"><circle cx="12" cy="12" r="10" fill="#1677FF"/><text x="12" y="16" text-anchor="middle" fill="#fff" font-size="10" font-weight="bold">支</text></svg>
            </div>
            <span class="method-name">支付宝</span>
            <span class="method-check" :class="{ checked: selectedChannel === 'ALIPAY' }">
              <svg v-if="selectedChannel === 'ALIPAY'" viewBox="0 0 24 24" fill="currentColor" width="20" height="20"><path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/></svg>
            </span>
          </div>
          <div
            class="method-item"
            :class="{ active: selectedChannel === 'WECHAT' }"
            @click="selectedChannel = 'WECHAT'"
          >
            <div class="method-icon wechat">
              <svg viewBox="0 0 24 24" width="24" height="24"><circle cx="12" cy="12" r="10" fill="#07C160"/><text x="12" y="16" text-anchor="middle" fill="#fff" font-size="9" font-weight="bold">微</text></svg>
            </div>
            <span class="method-name">微信支付</span>
            <span class="method-check" :class="{ checked: selectedChannel === 'WECHAT' }">
              <svg v-if="selectedChannel === 'WECHAT'" viewBox="0 0 24 24" fill="currentColor" width="20" height="20"><path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/></svg>
            </span>
          </div>
        </div>
        <button class="btn-pay" :disabled="submitting" @click="handleCreatePayment">
          {{ submitting ? '创建中...' : '确认支付 ¥' + amount }}
        </button>
      </div>

      <!-- QR code / payment info (after creating payment) -->
      <div v-else class="pay-qrcode card">
        <h3 class="section-title">
          {{ selectedChannel === 'ALIPAY' ? '支付宝' : '微信' }}扫码支付
        </h3>
        <div class="qrcode-box">
          <div class="qrcode-placeholder">
            <svg viewBox="0 0 120 120" width="180" height="180">
              <rect width="120" height="120" fill="#f5f5f5" rx="8"/>
              <rect x="10" y="10" width="25" height="25" fill="#333" rx="2"/>
              <rect x="85" y="10" width="25" height="25" fill="#333" rx="2"/>
              <rect x="10" y="85" width="25" height="25" fill="#333" rx="2"/>
              <rect x="40" y="40" width="40" height="40" fill="#333" rx="4"/>
              <rect x="45" y="45" width="30" height="30" fill="#fff" rx="2"/>
              <rect x="52" y="52" width="16" height="16" fill="#333" rx="1"/>
              <text x="60" y="110" text-anchor="middle" fill="#666" font-size="8">沙箱环境</text>
            </svg>
          </div>
          <p class="qrcode-hint">
            请使用{{ selectedChannel === 'ALIPAY' ? '支付宝' : '微信' }}扫一扫完成支付
          </p>
          <p class="qrcode-info">支付流水号：{{ paymentNo }}</p>
        </div>

        <!-- Sandbox: simulate payment button -->
        <div class="sandbox-actions">
          <p class="sandbox-hint">沙箱环境 - 点击模拟支付成功</p>
          <button class="btn-simulate" :disabled="simulating" @click="simulatePaymentSuccess">
            {{ simulating ? '处理中...' : '模拟支付成功' }}
          </button>
        </div>

        <button class="btn-cancel-pay" @click="handleClosePayment">取消支付</button>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { createPayment, queryPaymentStatus, closePayment } from '@/api/payment'

const router = useRouter()
const route = useRoute()

const loading = ref(false)
const submitting = ref(false)
const simulating = ref(false)

// From route query
const orderNo = ref(route.query.orderNo || '')
const amount = ref(route.query.amount || '0')
const subject = ref(route.query.subject || '')

// Payment state
const selectedChannel = ref('ALIPAY')
const paymentNo = ref('')
const payStatus = ref('PENDING')
const expireSeconds = ref(0)

let pollTimer = null
let countdownTimer = null

async function handleCreatePayment() {
  if (!orderNo.value) {
    alert('订单信息异常')
    return
  }
  submitting.value = true
  try {
    const res = await createPayment({
      orderNo: orderNo.value,
      amount: parseFloat(amount.value),
      payChannel: selectedChannel.value,
      payType: 'NATIVE',
      subject: subject.value || '外卖订单'
    })
    const data = res.data || res
    paymentNo.value = data.paymentNo
    payStatus.value = data.status || 'PENDING'
    expireSeconds.value = data.expireSeconds || 1800

    // Start polling and countdown
    startPolling()
    startCountdown()
  } catch (e) {
    alert('创建支付失败: ' + (e.message || '请重试'))
  } finally {
    submitting.value = false
  }
}

function startPolling() {
  pollTimer = setInterval(async () => {
    if (!paymentNo.value) return
    try {
      const res = await queryPaymentStatus(paymentNo.value)
      const data = res.data || res
      payStatus.value = data.status
      if (data.status === 'SUCCESS' || data.status === 'FAILED' || data.status === 'CLOSED' || data.status === 'REFUNDED') {
        stopPolling()
      }
    } catch {
      // ignore poll errors
    }
  }, 2000)
}

function startCountdown() {
  countdownTimer = setInterval(() => {
    if (expireSeconds.value > 0) {
      expireSeconds.value--
    } else {
      stopPolling()
      payStatus.value = 'CLOSED'
    }
  }, 1000)
}

function stopPolling() {
  if (pollTimer) { clearInterval(pollTimer); pollTimer = null }
  if (countdownTimer) { clearInterval(countdownTimer); countdownTimer = null }
}

function formatTime(seconds) {
  const m = Math.floor(seconds / 60)
  const s = seconds % 60
  return `${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`
}

async function handleClosePayment() {
  if (!paymentNo.value) return
  if (!confirm('确定取消支付？')) return
  try {
    await closePayment(paymentNo.value)
    payStatus.value = 'CLOSED'
    stopPolling()
  } catch (e) {
    alert('取消失败: ' + (e.message || '请重试'))
  }
}

async function simulatePaymentSuccess() {
  if (!paymentNo.value) return
  simulating.value = true
  try {
    // In sandbox, we directly query status - the backend callback simulation
    // would have already updated it. For demo, we just poll once more.
    const res = await queryPaymentStatus(paymentNo.value)
    const data = res.data || res
    if (data.status === 'SUCCESS') {
      payStatus.value = 'SUCCESS'
      stopPolling()
    } else {
      // Simulate by refreshing - in real sandbox the callback would fire
      alert('沙箱环境：请等待后端模拟回调处理，或刷新页面查看状态')
    }
  } catch (e) {
    alert('操作失败')
  } finally {
    simulating.value = false
  }
}

function retryPayment() {
  paymentNo.value = ''
  payStatus.value = 'PENDING'
  expireSeconds.value = 0
}

function goOrders() {
  router.push('/user/orders')
}

onMounted(() => {
  // If orderNo is missing, redirect back
  if (!orderNo.value) {
    router.replace('/user/orders')
    return
  }
})

onUnmounted(() => {
  stopPolling()
})
</script>

<style scoped>
.payment-view {
  max-width: 480px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

/* Loading */
.loading {
  text-align: center;
  padding: 80px 20px;
  color: var(--color-text-hint);
}

.loading-spinner {
  width: 36px;
  height: 36px;
  border: 3px solid var(--color-border);
  border-top-color: var(--color-primary);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin: 0 auto 16px;
}

@keyframes spin { to { transform: rotate(360deg); } }

/* Result card */
.result-card {
  text-align: center;
  padding: 48px 24px;
  border-radius: var(--radius-xl);
  background: var(--color-bg-card);
}

.result-icon {
  margin-bottom: 16px;
}

.result-card.success .result-icon { color: #52c41a; }
.result-card.failed .result-icon { color: #ff4d4f; }

.result-card h2 {
  font-size: 20px;
  margin: 0 0 8px;
}

.result-amount {
  font-size: 28px;
  font-weight: 700;
  color: var(--color-accent);
  margin: 0 0 8px;
}

.result-hint {
  color: var(--color-text-hint);
  font-size: var(--font-size-sm);
  margin: 0 0 24px;
}

.result-actions {
  display: flex;
  gap: var(--spacing-md);
  justify-content: center;
}

/* Order summary */
.order-summary {
  text-align: center;
  padding: var(--spacing-xl);
}

.summary-title {
  font-size: var(--font-size-md);
  margin: 0 0 8px;
  color: var(--color-text-secondary);
}

.summary-amount {
  font-size: 32px;
  font-weight: 700;
  color: var(--color-accent);
  margin: 0 0 8px;
}

.summary-timer {
  font-size: var(--font-size-sm);
  color: var(--color-text-hint);
  margin: 0;
}

/* Payment methods */
.pay-methods {
  padding: var(--spacing-lg);
}

.section-title {
  font-size: var(--font-size-md);
  font-weight: 600;
  margin: 0 0 var(--spacing-md);
}

.method-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: var(--spacing-xl);
}

.method-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  padding: 14px 16px;
  border: 2px solid var(--color-border);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all 0.2s;
}

.method-item.active {
  border-color: var(--color-primary);
  background: rgba(var(--color-primary-rgb, 255, 140, 0), 0.05);
}

.method-icon {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-full);
}

.method-name {
  flex: 1;
  font-size: var(--font-size-base);
  font-weight: 500;
}

.method-check {
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-primary);
  opacity: 0.3;
}

.method-check.checked {
  opacity: 1;
}

.btn-pay {
  width: 100%;
  padding: 14px;
  background: var(--color-accent);
  color: #fff;
  border: none;
  border-radius: var(--radius-xl);
  font-size: var(--font-size-md);
  font-weight: 700;
  cursor: pointer;
  transition: opacity 0.2s;
}

.btn-pay:hover { opacity: 0.9; }
.btn-pay:disabled { opacity: 0.5; cursor: not-allowed; }

/* QR code */
.pay-qrcode {
  padding: var(--spacing-lg);
  text-align: center;
}

.qrcode-box {
  margin: var(--spacing-lg) 0;
}

.qrcode-placeholder {
  display: inline-block;
  padding: 12px;
  background: #fff;
  border-radius: var(--radius-md);
  border: 1px solid var(--color-border);
}

.qrcode-hint {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  margin: 12px 0 4px;
}

.qrcode-info {
  font-size: var(--font-size-xs);
  color: var(--color-text-hint);
  margin: 0;
}

/* Sandbox actions */
.sandbox-actions {
  margin: var(--spacing-lg) 0;
  padding: var(--spacing-md);
  background: #fffbe6;
  border-radius: var(--radius-md);
  border: 1px solid #ffe58f;
}

.sandbox-hint {
  font-size: var(--font-size-xs);
  color: #d48806;
  margin: 0 0 10px;
}

.btn-simulate {
  padding: 10px 24px;
  background: #faad14;
  color: #fff;
  border: none;
  border-radius: var(--radius-md);
  font-size: var(--font-size-sm);
  font-weight: 600;
  cursor: pointer;
}

.btn-simulate:disabled { opacity: 0.6; cursor: not-allowed; }

.btn-cancel-pay {
  padding: 10px 24px;
  background: none;
  color: var(--color-text-hint);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  font-size: var(--font-size-sm);
  cursor: pointer;
  margin-top: var(--spacing-sm);
}

/* Buttons */
.btn-primary {
  padding: 10px 28px;
  background: var(--color-accent);
  color: #fff;
  border: none;
  border-radius: var(--radius-xl);
  font-size: var(--font-size-base);
  font-weight: 600;
  cursor: pointer;
}

.btn-outline {
  padding: 10px 28px;
  background: var(--color-bg-card);
  color: var(--color-text-secondary);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-xl);
  font-size: var(--font-size-base);
  cursor: pointer;
}
</style>

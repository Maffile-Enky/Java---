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
            <span class="total-price">¥{{ Number(order.totalPrice || 0).toFixed(2) }}</span>
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
          确认支付 ¥{{ Number(order.totalPrice || 0).toFixed(2) }}
        </GlassButton>
      </template>
    </div>

    <!-- 支付二维码弹窗 -->
    <GlassModal v-model:visible="showPayModal" title="扫码支付" width="400px" :maskClosable="false">
      <div class="pay-modal-content">
        <template v-if="payStatus === 'WAITING'">
          <p class="pay-tip">请使用{{ selectedMethod === 'WECHAT' ? '微信' : '支付宝' }}扫描二维码完成支付</p>
          <div class="qrcode-container">
            <img v-if="payInfo" :src="getQrcodeUrl(payInfo)" alt="支付二维码" class="qrcode-img" />
            <div v-else class="qrcode-placeholder">正在生成二维码...</div>
          </div>
          <p class="pay-amount">¥{{ Number(order?.totalPrice || 0).toFixed(2) }}</p>
          <div class="countdown-bar">
            <div class="countdown-progress" :style="{ width: (countdown / 10 * 100) + '%' }"></div>
          </div>
          <p class="pay-hint">扫码后 {{ countdown }} 秒自动确认支付</p>
        </template>
        <template v-else-if="payStatus === 'SUCCESS'">
          <div class="pay-success">
            <span class="success-icon">✓</span>
            <p class="success-text">支付成功！</p>
            <p class="success-hint">正在跳转到订单详情...</p>
          </div>
        </template>
        <template v-else-if="payStatus === 'CLOSED'">
          <div class="pay-closed">
            <p class="closed-text">支付已关闭或过期</p>
            <p class="closed-hint">请重新发起支付</p>
          </div>
        </template>
      </div>
      <template #footer>
        <GlassButton v-if="payStatus === 'WAITING'" variant="secondary" @click="closePayModal">
          取消支付
        </GlassButton>
        <GlassButton v-else-if="payStatus === 'CLOSED'" variant="primary" @click="closePayModal">
          确定
        </GlassButton>
      </template>
    </GlassModal>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getOrderDetail } from '@/api/order'
import { createPayment, mockPaymentSuccess } from '@/api/payment'
import LoadingSpinner from '@/components/common/LoadingSpinner.vue'
import GlassButton from '@/components/ui/GlassButton.vue'
import GlassModal from '@/components/ui/GlassModal.vue'

const route = useRoute()
const router = useRouter()
const order = ref(null)
const loading = ref(true)
const paying = ref(false)
const selectedMethod = ref('WECHAT')

// 支付弹窗相关状态
const showPayModal = ref(false)
const payInfo = ref('')
const paymentNo = ref('')
const payStatus = ref('') // WAITING, SUCCESS, CLOSED
const countdown = ref(10) // 倒计时10秒
let countdownTimer = null

const methods = [
  { id: 'WECHAT', name: '微信支付', icon: '💚' },
  { id: 'ALIPAY', name: '支付宝', icon: '💙' }
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
    const res = await createPayment({
      orderNo: order.value.orderNo,
      amount: order.value.totalPrice,
      payChannel: selectedMethod.value
    })
    const data = res.data
    paymentNo.value = data.paymentNo
    payInfo.value = data.payInfo
    payStatus.value = 'WAITING'
    countdown.value = 10
    showPayModal.value = true

    // 开始倒计时
    startCountdown()
  } catch (e) {
    console.error('创建支付失败:', e)
  } finally {
    paying.value = false
  }
}

function startCountdown() {
  stopCountdown()
  countdownTimer = setInterval(async () => {
    countdown.value--
    if (countdown.value <= 0) {
      stopCountdown()
      // 倒计时结束，调用后端模拟支付成功
      try {
        await mockPaymentSuccess(paymentNo.value)
        payStatus.value = 'SUCCESS'
        // 2秒后跳转到订单详情
        setTimeout(() => {
          showPayModal.value = false
          router.push(`/user/orders/${route.params.id}`)
        }, 2000)
      } catch (e) {
        console.error('模拟支付失败:', e)
        payStatus.value = 'CLOSED'
      }
    }
  }, 1000)
}

function stopCountdown() {
  if (countdownTimer) {
    clearInterval(countdownTimer)
    countdownTimer = null
  }
}

function closePayModal() {
  stopCountdown()
  showPayModal.value = false
}

// 生成二维码图片URL（使用第三方API）
function getQrcodeUrl(text) {
  return `https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=${encodeURIComponent(text)}`
}

onMounted(fetchOrder)
onUnmounted(stopCountdown)
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

/* 支付弹窗样式 */
.pay-modal-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-4);
  text-align: center;
}

.pay-tip {
  font-size: var(--text-sm);
  color: var(--text-secondary);
}

.qrcode-container {
  width: 200px;
  height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: white;
  border-radius: var(--radius-md);
  padding: var(--space-4);
}

.qrcode-img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.qrcode-placeholder {
  color: var(--text-secondary);
  font-size: var(--text-sm);
}

.pay-amount {
  font-size: 1.5rem;
  font-weight: 800;
  color: var(--accent);
}

.pay-hint {
  font-size: var(--text-xs);
  color: var(--text-secondary);
}

.countdown-bar {
  width: 100%;
  height: 4px;
  background: var(--glass);
  border-radius: 2px;
  overflow: hidden;
}

.countdown-progress {
  height: 100%;
  background: var(--accent);
  border-radius: 2px;
  transition: width 1s linear;
}

.pay-success {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-6) 0;
}

.success-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: var(--accent);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 2rem;
  font-weight: bold;
}

.success-text {
  font-size: var(--text-lg);
  font-weight: 700;
  color: var(--accent);
}

.success-hint {
  font-size: var(--text-sm);
  color: var(--text-secondary);
}

.pay-closed {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-6) 0;
}

.closed-text {
  font-size: var(--text-lg);
  font-weight: 700;
}

.closed-hint {
  font-size: var(--text-sm);
  color: var(--text-secondary);
}
</style>

<template>
  <div class="order-detail-view">
    <div v-if="loading" class="loading">加载中...</div>

    <template v-else>
      <h1>订单详情</h1>

      <!-- 订单状态 -->
      <div class="order-status-section">
        <div class="status-icon" :class="statusClass(order.status)">{{ statusIcon(order.status) }}</div>
        <div class="status-info">
          <h2>{{ statusText(order.status) }}</h2>
          <p>订单号：{{ order.id }}</p>
        </div>
      </div>

      <!-- 配送信息 -->
      <div class="delivery-section" v-if="order.deliveryAddress">
        <h3>配送信息</h3>
        <div class="delivery-info">
          <div class="delivery-item">
            <span class="label">收货地址：</span>
            <span class="value">{{ order.deliveryAddress }}</span>
          </div>
        </div>
      </div>

      <!-- 商家信息 -->
      <div class="merchant-section">
        <h3>{{ order.merchantName }}</h3>
      </div>

      <!-- 订单商品 -->
      <div class="items-section">
        <h3>订单商品</h3>
        <div class="items-list">
          <div v-for="item in order.items" :key="item.id" class="item">
            <div class="item-info">
              <span class="item-name">{{ item.name || item.dishName }}</span>
              <span class="item-quantity">x{{ item.quantity }}</span>
            </div>
            <span class="item-price">¥{{ (item.price * item.quantity).toFixed(2) }}</span>
          </div>
        </div>
      </div>

      <!-- 订单金额 -->
      <div class="price-section">
        <h3>订单金额</h3>
        <div class="price-info">
          <div class="price-item">
            <span class="label">商品金额：</span>
            <span class="value">¥{{ order.subtotal || order.totalPrice }}</span>
          </div>
          <div class="price-item" v-if="order.deliveryFee">
            <span class="label">配送费：</span>
            <span class="value">¥{{ order.deliveryFee }}</span>
          </div>
          <div class="price-item total">
            <span class="label">实付金额：</span>
            <span class="value">¥{{ order.totalPrice }}</span>
          </div>
        </div>
      </div>

      <!-- 订单信息 -->
      <div class="order-info-section">
        <h3>订单信息</h3>
        <div class="order-info">
          <div class="info-item">
            <span class="label">订单编号：</span>
            <span class="value">{{ order.id }}</span>
          </div>
          <div class="info-item">
            <span class="label">下单时间：</span>
            <span class="value">{{ order.createTime }}</span>
          </div>
        </div>
      </div>

      <!-- 底部操作按钮 -->
      <div class="action-bar">
        <button class="action-btn secondary" @click="router.push('/user/orders')">返回列表</button>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getOrderDetail } from '@/api/order'

const router = useRouter()
const route = useRoute()
const loading = ref(true)
const order = ref({})

function statusText(status) {
  const map = { PENDING: '待支付', DELIVERING: '配送中', COMPLETED: '已完成', CANCELLED: '已取消' }
  return map[status] || status || '未知'
}

function statusClass(status) {
  const map = { PENDING: 'status-pending', DELIVERING: 'status-processing', COMPLETED: 'status-completed', CANCELLED: 'status-cancelled' }
  return map[status] || ''
}

function statusIcon(status) {
  const map = { PENDING: '⏳', DELIVERING: '🚚', COMPLETED: '✓', CANCELLED: '✕' }
  return map[status] || '?'
}

onMounted(async () => {
  const id = route.params.id
  try {
    const res = await getOrderDetail(id)
    order.value = res.data || {}
  } catch {
    // Fallback mock
    order.value = {
      id: id || '100001',
      status: 'COMPLETED',
      merchantName: '示例餐厅',
      deliveryAddress: '北京市朝阳区建国路88号',
      items: [
        { id: 1, name: '宫保鸡丁', quantity: 2, price: 28 },
        { id: 2, name: '麻婆豆腐', quantity: 1, price: 22 },
        { id: 4, name: '可乐', quantity: 2, price: 8 }
      ],
      subtotal: 66,
      deliveryFee: 5,
      totalPrice: 71,
      createTime: '2026-05-06 12:00'
    }
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.order-detail-view { padding-bottom: 100px; }
.loading { text-align: center; padding: 60px; color: #999; }
h1 { padding: 20px; margin: 0; font-size: 24px; font-weight: bold; background-color: #fff; border-bottom: 1px solid #f0f0f0; border-radius: 12px; }
.order-status-section { display: flex; align-items: center; padding: 20px; margin-top: 10px; background-color: #fff; border-radius: 12px; }
.status-icon { width: 50px; height: 50px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 24px; margin-right: 20px; }
.status-completed { background-color: #d4edda; color: #155724; }
.status-pending { background-color: #fff3cd; color: #856404; }
.status-processing { background-color: #cce7ff; color: #004085; }
.status-cancelled { background-color: #f8d7da; color: #721c24; }
.status-info h2 { margin: 0 0 5px 0; font-size: 18px; }
.status-info p { margin: 0; font-size: 14px; color: #666; }
.delivery-section, .merchant-section, .items-section, .price-section, .order-info-section { margin-top: 10px; padding: 15px 20px; background-color: #fff; border-radius: 12px; }
h3 { margin: 0 0 15px 0; font-size: 16px; font-weight: bold; padding-bottom: 10px; border-bottom: 1px solid #f0f0f0; }
.delivery-item, .price-item, .info-item { display: flex; justify-content: space-between; margin-bottom: 10px; font-size: 14px; }
.label { color: #666; }
.value { color: #333; }
.items-list { margin-top: 10px; }
.item { display: flex; justify-content: space-between; margin-bottom: 10px; font-size: 14px; }
.item-info { display: flex; gap: 10px; }
.item-name { flex: 1; }
.item-quantity { color: #999; }
.item-price { color: #ff6b00; font-weight: 500; }
.price-item.total { font-weight: bold; margin-top: 10px; padding-top: 10px; border-top: 1px solid #f0f0f0; }
.price-item.total .value { color: #ff6b00; font-size: 16px; }
.action-bar { position: fixed; bottom: 0; left: 0; right: 0; display: flex; gap: 10px; padding: 15px 20px; background-color: #fff; border-top: 1px solid #f0f0f0; box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.1); z-index: 50; }
.action-btn { flex: 1; padding: 10px 0; border: 1px solid #e0e0e0; border-radius: 20px; background-color: #fff; font-size: 14px; cursor: pointer; transition: all 0.2s; }
.action-btn.primary { background-color: #ff6b00; color: #fff; border-color: #ff6b00; }
.action-btn.secondary { color: #666; }
.action-btn:hover { opacity: 0.8; }
</style>

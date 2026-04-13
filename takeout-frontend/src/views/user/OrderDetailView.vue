<template>
  <div class="order-detail-view">
    <h1>订单详情</h1>
    
    <!-- 订单状态 -->
    <div class="order-status-section">
      <div class="status-icon" :class="order.statusClass">
        {{ order.statusIcon }}
      </div>
      <div class="status-info">
        <h2>{{ order.statusText }}</h2>
        <p>{{ order.statusDescription }}</p>
      </div>
    </div>

    <!-- 配送信息 -->
    <div class="delivery-section">
      <h3>配送信息</h3>
      <div class="delivery-info">
        <div class="delivery-item">
          <span class="label">收货人：</span>
          <span class="value">{{ order.delivery.name }} {{ order.delivery.phone }}</span>
        </div>
        <div class="delivery-item">
          <span class="label">收货地址：</span>
          <span class="value">{{ order.delivery.address }}</span>
        </div>
        <div class="delivery-item">
          <span class="label">配送时间：</span>
          <span class="value">{{ order.delivery.time }}</span>
        </div>
      </div>
    </div>

    <!-- 商家信息 -->
    <div class="merchant-section">
      <h3>{{ order.merchantName }}</h3>
      <div class="merchant-info">
        <span class="distance">{{ order.merchantDistance }}m</span>
        <span class="delivery-time">{{ order.merchantDeliveryTime }}分钟</span>
      </div>
    </div>

    <!-- 订单商品 -->
    <div class="items-section">
      <h3>订单商品</h3>
      <div class="items-list">
        <div v-for="item in order.items" :key="item.id" class="item">
          <div class="item-info">
            <span class="item-name">{{ item.name }}</span>
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
          <span class="value">¥{{ order.subtotal }}</span>
        </div>
        <div class="price-item">
          <span class="label">配送费：</span>
          <span class="value">¥{{ order.deliveryFee }}</span>
        </div>
        <div class="price-item">
          <span class="label">优惠：</span>
          <span class="value">-¥{{ order.discount }}</span>
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
        <div class="info-item">
          <span class="label">支付方式：</span>
          <span class="value">{{ order.paymentMethod }}</span>
        </div>
      </div>
    </div>

    <!-- 底部操作按钮 -->
    <div class="action-bar">
      <button 
        v-for="action in order.actions" 
        :key="action.value"
        class="action-btn"
        :class="action.type"
        @click="handleAction(action.value)"
      >
        {{ action.label }}
      </button>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'

export default {
  name: 'OrderDetailView',
  setup() {
    const router = useRouter()
    const route = useRoute()
    const orderId = route.params.id
    
    const order = ref({
      id: orderId || '100001',
      status: 'completed',
      statusText: '已完成',
      statusDescription: '订单已送达，感谢您的购买',
      statusClass: 'status-completed',
      statusIcon: '✓',
      merchantName: '示例餐厅',
      merchantDistance: 500,
      merchantDeliveryTime: 30,
      delivery: {
        name: '张三',
        phone: '138****8888',
        address: '北京市朝阳区建国路88号',
        time: '2023-06-15 12:30'
      },
      items: [
        { id: 1, name: '宫保鸡丁', quantity: 2, price: 28 },
        { id: 2, name: '麻婆豆腐', quantity: 1, price: 22 },
        { id: 4, name: '可乐', quantity: 2, price: 8 }
      ],
      subtotal: 66,
      deliveryFee: 5,
      discount: 5,
      totalPrice: 66,
      createTime: '2023-06-15 11:30',
      paymentMethod: '微信支付',
      actions: [
        { label: '再次购买', value: 'rebuy', type: 'primary' },
        { label: '联系商家', value: 'contact', type: 'secondary' },
        { label: '返回列表', value: 'back', type: 'secondary' }
      ]
    })

    const handleAction = (action) => {
      switch (action) {
        case 'rebuy':
          // 实际项目中，这里会跳转到商家页面并将商品加入购物车
          console.log('Rebuy for order:', orderId)
          break
        case 'contact':
          // 实际项目中，这里会打开联系商家的界面
          console.log('Contact merchant for order:', orderId)
          break
        case 'back':
          router.push('/orders')
          break
      }
    }

    onMounted(() => {
      // 实际项目中，这里会调用API获取订单详情
    })

    return {
      order,
      handleAction
    }
  }
}
</script>

<style scoped>
.order-detail-view {
  padding-bottom: 100px;
}

h1 {
  padding: 20px;
  margin: 0;
  font-size: 24px;
  font-weight: bold;
  background-color: #fff;
  border-bottom: 1px solid #f0f0f0;
}

.order-status-section {
  display: flex;
  align-items: center;
  padding: 20px;
  margin-top: 10px;
  background-color: #fff;
}

.status-icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  margin-right: 20px;
}

.status-completed {
  background-color: #d4edda;
  color: #155724;
}

.status-pending {
  background-color: #fff3cd;
  color: #856404;
}

.status-processing {
  background-color: #cce7ff;
  color: #004085;
}

.status-cancelled {
  background-color: #f8d7da;
  color: #721c24;
}

.status-info h2 {
  margin: 0 0 5px 0;
  font-size: 18px;
}

.status-info p {
  margin: 0;
  font-size: 14px;
  color: #666;
}

.delivery-section,
.merchant-section,
.items-section,
.price-section,
.order-info-section {
  margin-top: 10px;
  padding: 15px 20px;
  background-color: #fff;
}

h3 {
  margin: 0 0 15px 0;
  font-size: 16px;
  font-weight: bold;
  padding-bottom: 10px;
  border-bottom: 1px solid #f0f0f0;
}

.delivery-item,
.price-item,
.info-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  font-size: 14px;
}

.label {
  color: #666;
}

.value {
  color: #333;
}

.merchant-info {
  display: flex;
  gap: 15px;
  font-size: 14px;
  color: #666;
}

.items-list {
  margin-top: 10px;
}

.item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  font-size: 14px;
}

.item-info {
  display: flex;
  gap: 10px;
}

.item-name {
  flex: 1;
}

.price-item.total {
  font-weight: bold;
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px solid #f0f0f0;
}

.price-item.total .value {
  color: #ff6b00;
  font-size: 16px;
}

.action-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  gap: 10px;
  padding: 15px 20px;
  background-color: #fff;
  border-top: 1px solid #f0f0f0;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.1);
}

.action-btn {
  flex: 1;
  padding: 10px 0;
  border: 1px solid #e0e0e0;
  border-radius: 20px;
  background-color: #fff;
  font-size: 14px;
  cursor: pointer;
}

.action-btn.primary {
  background-color: #ff6b00;
  color: #fff;
  border-color: #ff6b00;
}

.action-btn.secondary {
  color: #666;
}
</style>
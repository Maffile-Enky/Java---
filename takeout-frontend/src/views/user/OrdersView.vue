<template>
  <div class="orders-view">
    <h1>我的订单</h1>
    
    <!-- 订单状态筛选 -->
    <div class="order-filters">
      <button 
        v-for="filter in filters" 
        :key="filter.value"
        class="filter-btn"
        :class="{ active: activeFilter === filter.value }"
        @click="activeFilter = filter.value"
      >
        {{ filter.label }}
      </button>
    </div>

    <!-- 订单列表 -->
    <div class="order-list">
      <div v-for="order in orders" :key="order.id" class="order-item">
        <div class="order-header">
          <span class="order-merchant">{{ order.merchantName }}</span>
          <span class="order-status" :class="order.statusClass">{{ order.statusText }}</span>
        </div>
        <div class="order-content">
          <div v-for="item in order.items" :key="item.id" class="order-item-detail">
            <span class="item-name">{{ item.name }}</span>
            <span class="item-quantity">x{{ item.quantity }}</span>
            <span class="item-price">¥{{ item.price }}</span>
          </div>
        </div>
        <div class="order-footer">
          <span class="order-total">共{{ order.itemCount }}件商品，合计：¥{{ order.totalPrice }}</span>
          <div class="order-actions">
            <button 
              v-for="action in order.actions" 
              :key="action.value"
              class="action-btn"
              :class="action.type"
              @click="handleAction(order.id, action.value)"
            >
              {{ action.label }}
            </button>
          </div>
        </div>
        <div class="order-divider"></div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'

export default {
  name: 'OrdersView',
  setup() {
    const router = useRouter()
    const activeFilter = ref('all')
    const filters = [
      { label: '全部', value: 'all' },
      { label: '待支付', value: 'pending' },
      { label: '待配送', value: 'processing' },
      { label: '已完成', value: 'completed' },
      { label: '已取消', value: 'cancelled' }
    ]
    const orders = ref([
      {
        id: '100001',
        merchantName: '示例餐厅',
        status: 'completed',
        statusText: '已完成',
        statusClass: 'status-completed',
        totalPrice: 66,
        itemCount: 3,
        items: [
          { id: 1, name: '宫保鸡丁', quantity: 2, price: 28 },
          { id: 2, name: '麻婆豆腐', quantity: 1, price: 22 },
          { id: 4, name: '可乐', quantity: 2, price: 8 }
        ],
        actions: [
          { label: '再次购买', value: 'rebuy', type: 'primary' },
          { label: '查看详情', value: 'detail', type: 'secondary' }
        ]
      },
      {
        id: '100002',
        merchantName: '示例餐厅',
        status: 'pending',
        statusText: '待支付',
        statusClass: 'status-pending',
        totalPrice: 36,
        itemCount: 2,
        items: [
          { id: 1, name: '宫保鸡丁', quantity: 1, price: 28 },
          { id: 4, name: '可乐', quantity: 1, price: 8 }
        ],
        actions: [
          { label: '立即支付', value: 'pay', type: 'primary' },
          { label: '取消订单', value: 'cancel', type: 'secondary' },
          { label: '查看详情', value: 'detail', type: 'secondary' }
        ]
      },
      {
        id: '100003',
        merchantName: '示例餐厅',
        status: 'processing',
        statusText: '待配送',
        statusClass: 'status-processing',
        totalPrice: 50,
        itemCount: 2,
        items: [
          { id: 3, name: '红烧肉', quantity: 1, price: 38 },
          { id: 4, name: '可乐', quantity: 1, price: 8 }
        ],
        actions: [
          { label: '查看详情', value: 'detail', type: 'secondary' }
        ]
      }
    ])

    const filteredOrders = computed(() => {
      if (activeFilter.value === 'all') {
        return orders.value
      }
      return orders.value.filter(order => order.status === activeFilter.value)
    })

    const handleAction = (orderId, action) => {
      switch (action) {
        case 'pay':
          // 实际项目中，这里会跳转到支付页面
          console.log('Pay for order:', orderId)
          break
        case 'cancel':
          // 实际项目中，这里会调用API取消订单
          console.log('Cancel order:', orderId)
          break
        case 'rebuy':
          // 实际项目中，这里会跳转到商家页面并将商品加入购物车
          console.log('Rebuy for order:', orderId)
          break
        case 'detail':
          // 跳转到订单详情页
          router.push(`/order/${orderId}`)
          break
      }
    }

    onMounted(() => {
      // 实际项目中，这里会调用API获取订单列表
    })

    return {
      activeFilter,
      filters,
      orders: filteredOrders,
      handleAction
    }
  }
}
</script>

<style scoped>
.orders-view {
  padding-bottom: 20px;
}

h1 {
  padding: 20px;
  margin: 0;
  font-size: 24px;
  font-weight: bold;
  background-color: #fff;
  border-bottom: 1px solid #f0f0f0;
}

.order-filters {
  display: flex;
  background-color: #fff;
  margin-top: 10px;
  border-bottom: 1px solid #f0f0f0;
  overflow-x: auto;
}

.filter-btn {
  flex: 1;
  padding: 15px 0;
  border: none;
  background: none;
  font-size: 14px;
  color: #666;
  cursor: pointer;
  border-bottom: 2px solid transparent;
}

.filter-btn.active {
  color: #ff6b00;
  border-bottom-color: #ff6b00;
  font-weight: bold;
}

.order-list {
  margin-top: 10px;
  background-color: #fff;
}

.order-item {
  padding: 15px 20px;
}

.order-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
}

.order-merchant {
  font-size: 16px;
  font-weight: bold;
}

.order-status {
  font-size: 14px;
  padding: 2px 8px;
  border-radius: 10px;
}

.status-pending {
  background-color: #fff3cd;
  color: #856404;
}

.status-processing {
  background-color: #cce7ff;
  color: #004085;
}

.status-completed {
  background-color: #d4edda;
  color: #155724;
}

.status-cancelled {
  background-color: #f8d7da;
  color: #721c24;
}

.order-content {
  margin-bottom: 10px;
}

.order-item-detail {
  display: flex;
  justify-content: space-between;
  margin-bottom: 5px;
  font-size: 14px;
}

.item-name {
  flex: 1;
}

.item-quantity {
  margin: 0 10px;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
}

.order-total {
  font-size: 14px;
  color: #666;
}

.order-actions {
  display: flex;
  gap: 10px;
}

.action-btn {
  padding: 5px 12px;
  border: 1px solid #e0e0e0;
  border-radius: 15px;
  background-color: #fff;
  font-size: 12px;
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

.order-divider {
  height: 10px;
  background-color: #f5f5f5;
  margin: 15px -20px 0 -20px;
}
</style>
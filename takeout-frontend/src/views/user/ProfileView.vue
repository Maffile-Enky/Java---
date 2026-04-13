<template>
  <div class="profile-view">
    <!-- 用户信息 -->
    <div class="user-info-section">
      <div class="user-avatar">
        <img :src="user.avatar" alt="User Avatar">
      </div>
      <div class="user-details">
        <h2>{{ user.name }}</h2>
        <p class="user-phone">{{ user.phone }}</p>
      </div>
      <button class="edit-btn">编辑资料</button>
    </div>

    <!-- 订单管理 -->
    <div class="order-management">
      <div class="section-header">
        <h3>订单管理</h3>
        <router-link to="/orders" class="view-all">查看全部</router-link>
      </div>
      <div class="order-types">
        <div class="order-type-item" @click="navigateToOrders('pending')">
          <div class="order-icon">待支付</div>
          <span>待支付</span>
        </div>
        <div class="order-type-item" @click="navigateToOrders('processing')">
          <div class="order-icon">待配送</div>
          <span>待配送</span>
        </div>
        <div class="order-type-item" @click="navigateToOrders('completed')">
          <div class="order-icon">已完成</div>
          <span>已完成</span>
        </div>
        <div class="order-type-item" @click="navigateToOrders('cancelled')">
          <div class="order-icon">已取消</div>
          <span>已取消</span>
        </div>
      </div>
    </div>

    <!-- 功能列表 -->
    <div class="feature-list">
      <div class="feature-item">
        <div class="feature-icon">📍</div>
        <span>收货地址</span>
        <div class="feature-arrow">›</div>
      </div>
      <div class="feature-item">
        <div class="feature-icon">🎁</div>
        <span>我的优惠</span>
        <div class="feature-arrow">›</div>
      </div>
      <div class="feature-item">
        <div class="feature-icon">⭐</div>
        <span>我的收藏</span>
        <div class="feature-arrow">›</div>
      </div>
      <div class="feature-item">
        <div class="feature-icon">💬</div>
        <span>客服中心</span>
        <div class="feature-arrow">›</div>
      </div>
      <div class="feature-item">
        <div class="feature-icon">⚙️</div>
        <span>设置</span>
        <div class="feature-arrow">›</div>
      </div>
    </div>

    <!-- 退出登录按钮 -->
    <button class="logout-btn">退出登录</button>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

export default {
  name: 'ProfileView',
  setup() {
    const router = useRouter()
    const user = ref({
      name: '张三',
      phone: '138****8888',
      avatar: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%20portrait&image_size=square'
    })

    const navigateToOrders = (status) => {
      router.push(`/orders?status=${status}`)
    }

    onMounted(() => {
      // 实际项目中，这里会调用API获取用户信息
    })

    return {
      user,
      navigateToOrders
    }
  }
}
</script>

<style scoped>
.profile-view {
  padding-bottom: 20px;
}

.user-info-section {
  display: flex;
  align-items: center;
  padding: 20px;
  background-color: #ff6b00;
  color: #fff;
}

.user-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  overflow: hidden;
  margin-right: 20px;
  border: 3px solid rgba(255, 255, 255, 0.3);
}

.user-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.user-details h2 {
  margin: 0 0 5px 0;
  font-size: 20px;
  font-weight: bold;
}

.user-phone {
  margin: 0;
  font-size: 14px;
  opacity: 0.8;
}

.edit-btn {
  margin-left: auto;
  padding: 8px 16px;
  border: 1px solid #fff;
  border-radius: 20px;
  background-color: transparent;
  color: #fff;
  font-size: 14px;
  cursor: pointer;
}

.order-management {
  margin-top: 10px;
  padding: 15px 20px;
  background-color: #fff;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.section-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: bold;
}

.view-all {
  font-size: 14px;
  color: #666;
  text-decoration: none;
}

.order-types {
  display: flex;
  justify-content: space-between;
}

.order-type-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
}

.order-icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background-color: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  margin-bottom: 5px;
}

.order-type-item span {
  font-size: 12px;
  color: #666;
}

.feature-list {
  margin-top: 10px;
  background-color: #fff;
}

.feature-item {
  display: flex;
  align-items: center;
  padding: 15px 20px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
}

.feature-icon {
  font-size: 20px;
  margin-right: 15px;
}

.feature-item span {
  flex: 1;
  font-size: 16px;
}

.feature-arrow {
  font-size: 20px;
  color: #999;
}

.logout-btn {
  width: 90%;
  margin: 20px auto;
  display: block;
  padding: 12px;
  border: 1px solid #ff6b00;
  border-radius: 25px;
  background-color: #fff;
  color: #ff6b00;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
}

.logout-btn:hover {
  background-color: #fff3e6;
}
</style>
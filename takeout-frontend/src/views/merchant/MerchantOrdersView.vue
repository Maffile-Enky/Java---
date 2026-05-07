<template>
  <div class="orders-placeholder">
    <div class="placeholder-card">
      <div class="placeholder-icon">&#9830;</div>
      <h1 class="placeholder-title">订单管理功能开发中...</h1>
      <p class="placeholder-desc">
        我们正在努力开发订单管理功能，敬请期待。<br />
        该功能上线后，您将可以在这里查看和处理客户订单。
      </p>
      <router-link to="/merchant" class="btn-primary">返回店铺概览</router-link>
    </div>
  </div>
</template>

<script setup>
</script>

<style scoped>
.orders-placeholder {
  max-width: 600px;
  margin: 0 auto;
  padding-top: 60px;
}

.placeholder-card {
  background: #fff;
  border-radius: 12px;
  padding: 60px 40px;
  text-align: center;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.placeholder-icon {
  font-size: 56px;
  color: #ff6b00;
  margin-bottom: 20px;
}

.placeholder-title {
  font-size: 22px;
  font-weight: 700;
  color: #333;
  margin: 0 0 12px 0;
}

.placeholder-desc {
  font-size: 14px;
  color: #888;
  line-height: 1.8;
  margin: 0 0 32px 0;
}

.btn-primary {
  display: inline-block;
  padding: 12px 28px;
  background: #ff6b00;
  color: #fff;
  border: none;
  border-radius: 8px;
  font-size: 15px;
  font-weight: 600;
  text-decoration: none;
  cursor: pointer;
  transition: background 0.2s;
}

.btn-primary:hover {
  background: #e66000;
}
</style>

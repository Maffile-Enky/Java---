<template>
  <div class="merchant-layout">
    <aside class="sidebar">
      <div class="sidebar-header">
        <div class="logo-icon">M</div>
        <h2 class="logo-text">商家中心</h2>
      </div>
      <nav class="sidebar-nav">
        <router-link to="/merchant" class="nav-link" exact-active-class="active">
          <span class="nav-icon">&#9632;</span>
          <span>店铺概览</span>
        </router-link>
        <router-link to="/merchant/dishes" class="nav-link" active-class="active">
          <span class="nav-icon">&#9638;</span>
          <span>菜品管理</span>
        </router-link>
        <router-link to="/merchant/orders" class="nav-link" active-class="active">
          <span class="nav-icon">&#9830;</span>
          <span>订单管理</span>
        </router-link>
        <router-link to="/merchant/settings" class="nav-link" active-class="active">
          <span class="nav-icon">&#9881;</span>
          <span>店铺设置</span>
        </router-link>
      </nav>
      <div class="sidebar-footer">
        <router-link to="/" class="back-link">
          &larr; 返回首页
        </router-link>
      </div>
    </aside>
    <main class="main-content">
      <router-view />
    </main>
  </div>
</template>

<script setup>
</script>

<style scoped>
.merchant-layout {
  display: flex;
  min-height: 100vh;
}

.sidebar {
  width: 220px;
  background: linear-gradient(180deg, #ff6b00, #ff8c38);
  display: flex;
  flex-direction: column;
  position: fixed;
  top: 0;
  left: 0;
  bottom: 0;
  z-index: 100;
}

.sidebar-header {
  padding: 24px 20px;
  display: flex;
  align-items: center;
  gap: 12px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
}

.logo-icon {
  width: 36px;
  height: 36px;
  background: rgba(255, 255, 255, 0.25);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 18px;
  color: #fff;
}

.logo-text {
  font-size: 18px;
  font-weight: 700;
  color: #fff;
  margin: 0;
}

.sidebar-nav {
  flex: 1;
  padding: 16px 12px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.nav-link {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  border-radius: 10px;
  color: rgba(255, 255, 255, 0.85);
  text-decoration: none;
  font-size: 15px;
  font-weight: 500;
  transition: background 0.2s, color 0.2s;
}

.nav-link:hover {
  background: rgba(255, 255, 255, 0.15);
  color: #fff;
}

.nav-link.active {
  background: rgba(255, 255, 255, 0.25);
  color: #fff;
  font-weight: 600;
}

.nav-icon {
  font-size: 16px;
  width: 20px;
  text-align: center;
}

.sidebar-footer {
  padding: 16px 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.2);
}

.back-link {
  color: rgba(255, 255, 255, 0.8);
  text-decoration: none;
  font-size: 14px;
  display: block;
  padding: 8px 0;
  transition: color 0.2s;
}

.back-link:hover {
  color: #fff;
}

.main-content {
  flex: 1;
  margin-left: 220px;
  padding: 32px;
  background: #f5f5f5;
  min-height: 100vh;
}
</style>

<template>
  <div class="admin-layout">
    <aside class="sidebar">
      <div class="sidebar-header">
        <span class="logo-icon">⚙</span>
        <h2 class="logo-text">管理后台</h2>
      </div>
      <nav class="sidebar-nav">
        <router-link to="/admin" class="nav-item" exact-active-class="active">
          <span class="nav-icon">📊</span>
          <span class="nav-label">数据概览</span>
        </router-link>
        <router-link to="/admin/users" class="nav-item" active-class="active">
          <span class="nav-icon">👥</span>
          <span class="nav-label">用户管理</span>
        </router-link>
        <router-link to="/admin/applications" class="nav-item" active-class="active">
          <span class="nav-icon">📋</span>
          <span class="nav-label">入驻审核</span>
        </router-link>
        <router-link to="/admin/merchants" class="nav-item" active-class="active">
          <span class="nav-icon">🏪</span>
          <span class="nav-label">商家管理</span>
        </router-link>
      </nav>
      <div class="sidebar-footer">
        <router-link to="/" class="back-link">
          <span class="nav-icon">🏠</span>
          <span class="nav-label">返回首页</span>
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
.admin-layout {
  display: flex;
  min-height: 100vh;
  background: #f0f2f5;
}

.sidebar {
  width: 220px;
  background: linear-gradient(180deg, #667eea 0%, #764ba2 100%);
  display: flex;
  flex-direction: column;
  position: fixed;
  top: 0;
  left: 0;
  bottom: 0;
  z-index: 100;
}

.sidebar-header {
  padding: 28px 20px 20px;
  display: flex;
  align-items: center;
  gap: 12px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.15);
}

.logo-icon {
  font-size: 28px;
}

.logo-text {
  margin: 0;
  font-size: 20px;
  color: #fff;
  font-weight: 700;
}

.sidebar-nav {
  flex: 1;
  padding: 16px 12px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  border-radius: 10px;
  color: rgba(255, 255, 255, 0.8);
  text-decoration: none;
  font-size: 15px;
  font-weight: 500;
  transition: all 0.25s ease;
}

.nav-item:hover {
  background: rgba(255, 255, 255, 0.15);
  color: #fff;
}

.nav-item.active {
  background: rgba(255, 255, 255, 0.25);
  color: #fff;
  font-weight: 600;
}

.nav-icon {
  font-size: 18px;
  width: 24px;
  text-align: center;
}

.sidebar-footer {
  padding: 16px 12px;
  border-top: 1px solid rgba(255, 255, 255, 0.15);
}

.back-link {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  border-radius: 10px;
  color: rgba(255, 255, 255, 0.7);
  text-decoration: none;
  font-size: 15px;
  font-weight: 500;
  transition: all 0.25s ease;
}

.back-link:hover {
  background: rgba(255, 255, 255, 0.15);
  color: #fff;
}

.main-content {
  flex: 1;
  margin-left: 220px;
  padding: 28px 32px;
  min-height: 100vh;
}
</style>

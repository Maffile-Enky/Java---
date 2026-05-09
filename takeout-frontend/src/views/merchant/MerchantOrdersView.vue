<template>
  <div class="merchant-orders">
    <h1 class="page-title">订单管理</h1>

    <div class="card placeholder-card">
      <div class="placeholder-icon">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" width="56" height="56"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14,2 14,8 20,8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/></svg>
      </div>
      <h2>订单管理功能开发中...</h2>
      <p>我们正在努力开发订单管理功能，敬请期待。</p>
      <router-link to="/merchant" class="btn-primary">返回店铺概览</router-link>
    </div>
  </div>
</template>

<script setup>
</script>

<style scoped>
.merchant-orders {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xl);
  max-width: 600px;
}

.page-title {
  font-size: var(--font-size-xl);
  font-weight: 700;
  margin: 0;
}

.placeholder-card {
  text-align: center;
  padding: 60px 40px;
}

.placeholder-icon {
  color: var(--color-accent);
  margin-bottom: 20px;
}

.placeholder-card h2 {
  font-size: var(--font-size-lg);
  font-weight: 700;
  margin: 0 0 12px 0;
}

.placeholder-card p {
  font-size: var(--font-size-base);
  color: var(--color-text-hint);
  line-height: 1.6;
  margin: 0 0 28px 0;
}
</style>

<template>
  <div class="layout">
    <AppHeader />
    <main class="main-content">
      <div class="content-container">
        <slot></slot>
      </div>
    </main>
    <AppNav />
  </div>
</template>

<script setup>
import AppHeader from './AppHeader.vue'
import AppNav from './AppNav.vue'
</script>

<style scoped>
.layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: var(--color-bg-page);
  position: relative;
}

.layout::before {
  content: '';
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 300px;
  background: linear-gradient(180deg, rgba(253, 246, 236, 0.6) 0%, transparent 100%);
  pointer-events: none;
  z-index: -1;
}

.main-content {
  flex: 1;
  padding: var(--spacing-2xl);
  padding-bottom: calc(var(--nav-height) + var(--spacing-2xl) + env(safe-area-inset-bottom, 0px));
}

.content-container {
  max-width: var(--content-max-width);
  margin: 0 auto;
}

@media (min-width: 768px) {
  .main-content {
    padding-bottom: var(--spacing-2xl);
  }
}
</style>

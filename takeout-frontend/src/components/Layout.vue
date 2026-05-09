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
}

.main-content {
  flex: 1;
  padding: var(--spacing-lg);
  padding-bottom: calc(var(--nav-height) + var(--spacing-lg) + env(safe-area-inset-bottom, 0px));
}

.content-container {
  max-width: var(--content-max-width);
  margin: 0 auto;
}

@media (min-width: 768px) {
  .main-content {
    padding-bottom: var(--spacing-lg);
  }
}
</style>

<template>
  <div class="admin-layout">
    <AppSidebar role="rider" />
    <main class="admin-main">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import AppSidebar from '@/components/layout/AppSidebar.vue'
</script>

<style scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
}

.admin-main {
  flex: 1;
  margin-left: var(--sidebar-width);
  padding: var(--space-8);
  position: relative;
  z-index: 2;
}

@media (max-width: 768px) {
  .admin-main { margin-left: 60px; padding: var(--space-4); }
}
</style>

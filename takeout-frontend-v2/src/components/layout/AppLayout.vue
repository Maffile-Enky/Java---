<template>
  <div class="app-layout">
    <AppHeader v-if="showHeader" />
    <main class="app-main" :class="{ 'has-header': showHeader, 'has-nav': showNav }">
      <slot />
    </main>
    <GlassNav v-if="showNav" />
  </div>
</template>

<script setup>
import AppHeader from './AppHeader.vue'
import GlassNav from '@/components/ui/GlassNav.vue'

defineProps({
  showHeader: { type: Boolean, default: true },
  showNav: { type: Boolean, default: true }
})
</script>

<style scoped>
.app-layout {
  min-height: 100vh;
  position: relative;
  z-index: 2;
}

.app-main {
  padding-bottom: calc(var(--nav-height) + var(--space-4));
}

.app-main.has-header {
  padding-top: var(--header-height);
}

.app-main.has-nav {
  padding-bottom: calc(var(--nav-height) + var(--space-8));
}
</style>

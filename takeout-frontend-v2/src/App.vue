<template>
  <!-- Ambient background -->
  <div class="ambient">
    <div class="ambient-orb"></div>
    <div class="ambient-orb"></div>
    <div class="ambient-orb"></div>
  </div>
  <div class="grain"></div>
  <div class="deco-line"></div>

  <!-- Router view with page transition -->
  <router-view v-slot="{ Component }">
    <transition name="page" mode="out-in">
      <component :is="Component" />
    </transition>
  </router-view>
</template>

<script setup>
</script>

<style scoped>
.page-enter-active {
  animation: fadeSlideUp 0.4s var(--ease-out);
}

.page-leave-active {
  animation: fadeSlideUp 0.2s var(--ease-out) reverse;
}
</style>

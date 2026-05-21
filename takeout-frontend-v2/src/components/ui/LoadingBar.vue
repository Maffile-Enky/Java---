<template>
  <Teleport to="body">
    <Transition name="bar">
      <div v-if="loading" class="loading-bar">
        <div class="loading-bar-progress"></div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup>
defineProps({
  loading: { type: Boolean, default: false }
})
</script>

<style scoped>
.loading-bar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  z-index: 10001;
  overflow: hidden;
}

.loading-bar-progress {
  height: 100%;
  background: var(--gradient-green);
  animation: loadingProgress 2s var(--ease-in-out) infinite;
  box-shadow: 0 0 10px var(--accent-glow);
}

.bar-enter-active {
  animation: barIn 0.2s var(--ease-out);
}

.bar-leave-active {
  animation: barOut 0.3s var(--ease-out);
}

@keyframes loadingProgress {
  0% { width: 0%; margin-left: 0%; }
  50% { width: 60%; margin-left: 20%; }
  100% { width: 0%; margin-left: 100%; }
}

@keyframes barIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes barOut {
  from { opacity: 1; }
  to { opacity: 0; }
}
</style>

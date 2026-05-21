<template>
  <div class="loading-spinner" :class="`spinner--${size}`">
    <div class="spinner-ring"></div>
    <p v-if="text" class="spinner-text">{{ text }}</p>
  </div>
</template>

<script setup>
defineProps({
  size: { type: String, default: 'md' }, // sm | md | lg
  text: { type: String, default: '' }
})
</script>

<style scoped>
.loading-spinner {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: var(--space-3);
  padding: var(--space-8);
}

.spinner-ring {
  border-radius: 50%;
  border: 3px solid var(--glass-border);
  border-top-color: var(--accent);
  animation: spin 0.8s linear infinite;
}

.spinner--sm .spinner-ring { width: 20px; height: 20px; border-width: 2px; }
.spinner--md .spinner-ring { width: 32px; height: 32px; }
.spinner--lg .spinner-ring { width: 48px; height: 48px; border-width: 4px; }

.spinner-text {
  font-size: var(--text-sm);
  color: var(--text-muted);
  margin: 0;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}
</style>

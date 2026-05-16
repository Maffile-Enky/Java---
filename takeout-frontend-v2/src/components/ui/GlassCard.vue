<template>
  <div
    class="glass-card"
    :class="[
      `glass-card--${variant}`,
      { 'glass-card--hover': hover, 'glass-card--padded': padded }
    ]"
  >
    <slot />
  </div>
</template>

<script setup>
defineProps({
  variant: { type: String, default: 'default' }, // default | solid | flat
  hover: { type: Boolean, default: false },
  padded: { type: Boolean, default: true }
})
</script>

<style scoped>
.glass-card {
  background: var(--glass);
  border: 1px solid var(--glass-border);
  border-radius: var(--radius-lg);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  transition: all var(--duration-normal) var(--ease-out);
}

.glass-card--padded {
  padding: var(--space-6);
}

.glass-card--solid {
  background: var(--glass-solid);
  backdrop-filter: blur(20px);
}

.glass-card--flat {
  background: transparent;
  border: 1px solid var(--glass-border);
  backdrop-filter: none;
}

.glass-card--hover:hover {
  transform: translateY(-4px);
  border-color: rgba(110, 231, 160, 0.2);
  box-shadow: var(--shadow-glow-green);
}
</style>

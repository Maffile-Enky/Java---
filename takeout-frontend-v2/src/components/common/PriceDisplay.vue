<template>
  <span class="price-display" :class="[`price--${size}`, { 'price--strike': strike }]">
    <span class="price-symbol">¥</span>{{ formatted }}
  </span>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  value: { type: [Number, String], required: true },
  size: { type: String, default: 'md' }, // sm | md | lg
  strike: { type: Boolean, default: false }
})

const formatted = computed(() => Number(props.value).toFixed(2))
</script>

<style scoped>
.price-display {
  font-weight: 800;
  color: var(--accent);
  white-space: nowrap;
}

.price-symbol {
  font-size: 0.75em;
  margin-right: 1px;
}

.price--sm { font-size: var(--text-sm); }
.price--md { font-size: 1rem; }
.price--lg { font-size: 1.5rem; }

.price--strike {
  color: var(--text-muted);
  text-decoration: line-through;
  font-weight: 400;
}
</style>

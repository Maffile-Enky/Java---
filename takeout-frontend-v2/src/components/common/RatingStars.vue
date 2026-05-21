<template>
  <span class="rating-stars" :class="`rating--${size}`">
    <span v-for="i in 5" :key="i" class="star" :class="{ filled: i <= Math.round(value), half: i - 0.5 <= value && i > Math.round(value) }">
      ★
    </span>
  </span>
</template>

<script setup>
defineProps({
  value: { type: Number, required: true },
  size: { type: String, default: 'md' } // sm | md
})
</script>

<style scoped>
.rating-stars {
  display: inline-flex;
  gap: 1px;
}

.rating--sm { font-size: 10px; }
.rating--md { font-size: var(--text-sm); }

.star {
  color: var(--glass-border);
  line-height: 1;
}

.star.filled {
  color: var(--accent-secondary);
}
</style>

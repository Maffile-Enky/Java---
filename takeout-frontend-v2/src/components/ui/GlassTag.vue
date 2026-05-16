<template>
  <span class="glass-tag" :class="[`glass-tag--${variant}`, `glass-tag--${size}`]">
    <slot />
  </span>
</template>

<script setup>
defineProps({
  variant: { type: String, default: 'green' }, // green | gold | purple | muted
  size: { type: String, default: 'md' } // sm | md
})
</script>

<style scoped>
.glass-tag {
  display: inline-flex;
  align-items: center;
  border-radius: var(--radius-full);
  font-weight: 600;
  letter-spacing: 0.02em;
  white-space: nowrap;
}

.glass-tag--sm { padding: 2px var(--space-3); font-size: var(--text-xs); }
.glass-tag--md { padding: var(--space-1) var(--space-3); font-size: var(--text-xs); }

.glass-tag--green { background: var(--accent); color: var(--text-inverse); }
.glass-tag--gold { background: var(--accent-secondary); color: var(--text-inverse); }
.glass-tag--purple { background: var(--accent-whisper); color: var(--text-inverse); }
.glass-tag--muted { background: var(--glass); color: var(--text-secondary); border: 1px solid var(--glass-border); }
</style>

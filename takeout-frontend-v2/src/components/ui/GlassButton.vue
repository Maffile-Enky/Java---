<template>
  <button
    class="glass-btn"
    :class="[
      `glass-btn--${variant}`,
      `glass-btn--${size}`,
      { 'glass-btn--loading': loading, 'glass-btn--block': block }
    ]"
    :disabled="disabled || loading"
    @click="$emit('click', $event)"
  >
    <span v-if="loading" class="spinner" style="width:16px;height:16px;border-width:2px;"></span>
    <slot v-else />
  </button>
</template>

<script setup>
defineProps({
  variant: { type: String, default: 'primary' }, // primary | ghost | danger | gold
  size: { type: String, default: 'md' }, // sm | md | lg
  loading: { type: Boolean, default: false },
  disabled: { type: Boolean, default: false },
  block: { type: Boolean, default: false }
})

defineEmits(['click'])
</script>

<style scoped>
.glass-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-2);
  border-radius: var(--radius-full);
  font-weight: 600;
  transition: all var(--duration-normal) var(--ease-out);
  position: relative;
  overflow: hidden;
  white-space: nowrap;
  cursor: pointer;
  border: none;
  font-family: var(--font-sans);
}

.glass-btn--block { width: 100%; }

/* Sizes */
.glass-btn--sm { padding: var(--space-2) var(--space-4); font-size: var(--text-sm); }
.glass-btn--md { padding: var(--space-4) var(--space-8); font-size: var(--text-base); }
.glass-btn--lg { padding: var(--space-5) var(--space-10); font-size: var(--text-lg); }

/* Primary — green gradient with dark text */
.glass-btn--primary {
  background: var(--gradient-green);
  color: var(--text-inverse);
  box-shadow: 0 0 0 0 var(--accent-glow);
}

.glass-btn--primary::before {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, transparent, rgba(255,255,255,0.2), transparent);
  transform: translateX(-100%);
  transition: transform 0.6s;
}

.glass-btn--primary:hover::before { transform: translateX(100%); }
.glass-btn--primary:hover { transform: translateY(-2px); box-shadow: var(--shadow-glow-green); }
.glass-btn--primary:active { transform: translateY(0); }

/* Ghost */
.glass-btn--ghost {
  background: transparent;
  color: var(--text-secondary);
  border: 1px solid var(--glass-border);
}

.glass-btn--ghost:hover {
  border-color: var(--accent);
  color: var(--text-primary);
  background: var(--glass);
}

/* Danger */
.glass-btn--danger {
  background: var(--color-danger);
  color: #fff;
}

.glass-btn--danger:hover { opacity: 0.9; transform: translateY(-1px); }

/* Gold */
.glass-btn--gold {
  background: var(--gradient-gold);
  color: var(--text-inverse);
}

.glass-btn--gold:hover { transform: translateY(-2px); box-shadow: var(--shadow-glow-gold); }

/* Disabled / Loading */
.glass-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
  transform: none !important;
}

.glass-btn--loading { pointer-events: none; }
</style>

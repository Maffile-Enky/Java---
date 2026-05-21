<template>
  <div class="glass-input-wrap" :class="{ 'has-error': error }">
    <label v-if="label" class="glass-input-label">{{ label }}</label>
    <div class="glass-input-inner">
      <input
        :type="type"
        :value="modelValue"
        :placeholder="placeholder"
        :disabled="disabled"
        class="glass-input-field"
        @input="$emit('update:modelValue', $event.target.value)"
        @keyup.enter="$emit('enter')"
      />
      <span v-if="$slots.suffix" class="glass-input-suffix">
        <slot name="suffix" />
      </span>
    </div>
    <p v-if="error" class="glass-input-error">{{ error }}</p>
  </div>
</template>

<script setup>
defineProps({
  modelValue: { type: [String, Number], default: '' },
  type: { type: String, default: 'text' },
  placeholder: { type: String, default: '' },
  label: { type: String, default: '' },
  error: { type: String, default: '' },
  disabled: { type: Boolean, default: false }
})

defineEmits(['update:modelValue', 'enter'])
</script>

<style scoped>
.glass-input-wrap { width: 100%; }

.glass-input-label {
  display: block;
  font-size: var(--text-sm);
  color: var(--text-secondary);
  margin-bottom: var(--space-2);
  font-weight: 500;
}

.glass-input-inner {
  position: relative;
  display: flex;
  align-items: center;
}

.glass-input-field {
  width: 100%;
  padding: var(--space-4) var(--space-6);
  background: rgba(255,255,255,0.05);
  border: 1px solid var(--glass-border);
  border-radius: var(--radius-full);
  color: var(--text-primary);
  font-size: var(--text-base);
  font-family: var(--font-sans);
  transition: border-color var(--duration-fast), box-shadow var(--duration-fast);
}

.glass-input-field::placeholder { color: var(--text-muted); }

.glass-input-field:focus {
  border-color: var(--accent);
  box-shadow: 0 0 0 3px var(--accent-glow);
}

.glass-input-field:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.has-error .glass-input-field {
  border-color: var(--color-danger);
}

.has-error .glass-input-field:focus {
  box-shadow: 0 0 0 3px rgba(248, 113, 113, 0.2);
}

.glass-input-suffix {
  position: absolute;
  right: var(--space-4);
  display: flex;
  align-items: center;
}

.glass-input-error {
  font-size: var(--text-xs);
  color: var(--color-danger);
  margin-top: var(--space-1);
  padding-left: var(--space-4);
}
</style>

<template>
  <Teleport to="body">
    <Transition name="toast">
      <div v-if="visible" class="toast-container" :class="type">
        <span class="toast-icon">{{ icon }}</span>
        <span class="toast-message">{{ message }}</span>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  message: { type: String, default: '' },
  type: { type: String, default: 'info' },
  duration: { type: Number, default: 3000 }
})

const visible = ref(false)
const icon = ref('')

watch(() => props.message, (val) => {
  if (val) {
    icon.value = {
      success: '✓',
      error: '✕',
      warning: '⚠',
      info: 'ℹ'
    }[props.type] || 'ℹ'
    visible.value = true
    setTimeout(() => {
      visible.value = false
    }, props.duration)
  }
})
</script>

<style scoped>
.toast-container {
  position: fixed;
  top: 80px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 10000;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 24px;
  border-radius: var(--radius-full);
  backdrop-filter: blur(20px);
  font-size: var(--text-sm);
  font-weight: 500;
  box-shadow: var(--shadow-lg);
}

.toast-container.success {
  background: rgba(110, 231, 160, 0.15);
  border: 1px solid rgba(110, 231, 160, 0.3);
  color: var(--accent);
}

.toast-container.error {
  background: rgba(248, 113, 113, 0.15);
  border: 1px solid rgba(248, 113, 113, 0.3);
  color: #f87171;
}

.toast-container.warning {
  background: rgba(240, 197, 90, 0.15);
  border: 1px solid rgba(240, 197, 90, 0.3);
  color: var(--accent-secondary);
}

.toast-container.info {
  background: rgba(96, 165, 250, 0.15);
  border: 1px solid rgba(96, 165, 250, 0.3);
  color: #60a5fa;
}

.toast-icon {
  font-size: 1rem;
}

.toast-enter-active {
  animation: toastIn 0.3s var(--ease-out);
}

.toast-leave-active {
  animation: toastOut 0.2s var(--ease-out);
}

@keyframes toastIn {
  from { opacity: 0; transform: translate(-50%, -20px); }
  to { opacity: 1; transform: translate(-50%, 0); }
}

@keyframes toastOut {
  from { opacity: 1; transform: translate(-50%, 0); }
  to { opacity: 0; transform: translate(-50%, -20px); }
}
</style>

<template>
  <teleport to="body">
    <transition name="modal">
      <div v-if="visible" class="modal-overlay" @click.self="close">
        <div class="modal-panel" :style="{ maxWidth: width }">
          <div v-if="title" class="modal-header">
            <h3 class="modal-title">{{ title }}</h3>
            <button class="modal-close" @click="close">&times;</button>
          </div>
          <div class="modal-body">
            <slot />
          </div>
          <div v-if="$slots.footer" class="modal-footer">
            <slot name="footer" />
          </div>
        </div>
      </div>
    </transition>
  </teleport>
</template>

<script setup>
const props = defineProps({
  visible: { type: Boolean, default: false },
  title: { type: String, default: '' },
  width: { type: String, default: '480px' },
  maskClosable: { type: Boolean, default: true }
})

const emit = defineEmits(['update:visible', 'close'])

function close() {
  if (props.maskClosable) {
    emit('update:visible', false)
    emit('close')
  }
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  inset: 0;
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(4px);
  padding: var(--space-6);
}

.modal-panel {
  width: 100%;
  background: var(--bg-lift);
  border: 1px solid var(--glass-border);
  border-radius: var(--radius-xl);
  backdrop-filter: blur(20px);
  box-shadow: var(--shadow-lg);
  overflow: hidden;
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--space-6) var(--space-6) 0;
}

.modal-title {
  font-family: var(--font-serif);
  font-size: var(--text-xl);
  font-weight: 700;
}

.modal-close {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.3rem;
  color: var(--text-secondary);
  transition: all var(--duration-fast);
  background: none;
  border: none;
  cursor: pointer;
}

.modal-close:hover {
  background: var(--glass);
  color: var(--text-primary);
}

.modal-body {
  padding: var(--space-6);
}

.modal-footer {
  padding: 0 var(--space-6) var(--space-6);
  display: flex;
  gap: var(--space-3);
  justify-content: flex-end;
}

/* Transition */
.modal-enter-active { animation: fadeIn 0.25s var(--ease-out); }
.modal-leave-active { animation: fadeIn 0.2s var(--ease-out) reverse; }
.modal-enter-active .modal-panel { animation: scaleIn 0.3s var(--ease-out); }
.modal-leave-active .modal-panel { animation: scaleIn 0.2s var(--ease-out) reverse; }

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes scaleIn {
  from { opacity: 0; transform: scale(0.95) translateY(10px); }
  to { opacity: 1; transform: scale(1) translateY(0); }
}
</style>

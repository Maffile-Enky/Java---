<template>
  <div class="skeleton" :class="type">
    <div v-if="type === 'card'" class="skeleton-card">
      <div class="skeleton-image"></div>
      <div class="skeleton-content">
        <div class="skeleton-title"></div>
        <div class="skeleton-text"></div>
        <div class="skeleton-text short"></div>
      </div>
    </div>
    <div v-else-if="type === 'list'" class="skeleton-list">
      <div v-for="i in count" :key="i" class="skeleton-item">
        <div class="skeleton-avatar"></div>
        <div class="skeleton-content">
          <div class="skeleton-title"></div>
          <div class="skeleton-text"></div>
        </div>
      </div>
    </div>
    <div v-else class="skeleton-block">
      <div v-for="i in count" :key="i" class="skeleton-line" :style="{ width: getRandomWidth() }"></div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  type: { type: String, default: 'block' },
  count: { type: Number, default: 3 }
})

function getRandomWidth() {
  return `${60 + Math.random() * 40}%`
}
</script>

<style scoped>
.skeleton {
  animation: pulse 1.5s ease-in-out infinite;
}

.skeleton-card {
  background: var(--glass);
  border: 1px solid var(--glass-border);
  border-radius: var(--radius-lg);
  overflow: hidden;
}

.skeleton-image {
  width: 100%;
  aspect-ratio: 4/3;
  background: linear-gradient(90deg, var(--glass) 0%, var(--glass-hover) 50%, var(--glass) 100%);
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
}

.skeleton-content {
  padding: 20px;
}

.skeleton-title {
  height: 20px;
  width: 60%;
  background: linear-gradient(90deg, var(--glass) 0%, var(--glass-hover) 50%, var(--glass) 100%);
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
  border-radius: 4px;
  margin-bottom: 12px;
}

.skeleton-text {
  height: 14px;
  width: 80%;
  background: linear-gradient(90deg, var(--glass) 0%, var(--glass-hover) 50%, var(--glass) 100%);
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
  border-radius: 4px;
  margin-bottom: 8px;
}

.skeleton-text.short {
  width: 40%;
}

.skeleton-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.skeleton-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: var(--glass);
  border: 1px solid var(--glass-border);
  border-radius: var(--radius-md);
}

.skeleton-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: linear-gradient(90deg, var(--glass) 0%, var(--glass-hover) 50%, var(--glass) 100%);
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
  flex-shrink: 0;
}

.skeleton-block {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.skeleton-line {
  height: 16px;
  background: linear-gradient(90deg, var(--glass) 0%, var(--glass-hover) 50%, var(--glass) 100%);
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
  border-radius: 4px;
}

@keyframes shimmer {
  0% { background-position: -200% 0; }
  100% { background-position: 200% 0; }
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}
</style>

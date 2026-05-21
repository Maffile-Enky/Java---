<template>
  <!-- Ambient background -->
  <div class="ambient">
    <div class="ambient-orb"></div>
    <div class="ambient-orb"></div>
    <div class="ambient-orb"></div>
  </div>
  <div class="grain"></div>
  <div class="deco-line"></div>

  <!-- Loading bar -->
  <LoadingBar :loading="routeLoading" />

  <!-- Toast notification -->
  <Toast :message="toastState.message" :type="toastState.type" :duration="toastState.duration" />

  <!-- Router view with page transition -->
  <router-view v-slot="{ Component, route }">
    <Transition :name="route.meta.transition || 'page'" mode="out-in">
      <Suspense>
        <template #default>
          <component :is="Component" :key="route.path" />
        </template>
        <template #fallback>
          <div class="page-loading">
            <div class="loading-spinner"></div>
          </div>
        </template>
      </Suspense>
    </Transition>
  </router-view>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import Toast from '@/components/ui/Toast.vue'
import LoadingBar from '@/components/ui/LoadingBar.vue'
import { useToast } from '@/composables/useToast'

const route = useRoute()
const { toastState } = useToast()
const routeLoading = ref(false)

watch(() => route.path, () => {
  routeLoading.value = true
  setTimeout(() => {
    routeLoading.value = false
  }, 500)
})
</script>

<style scoped>
.page-enter-active {
  animation: fadeSlideUp 0.4s var(--ease-out);
}

.page-leave-active {
  animation: fadeSlideUp 0.2s var(--ease-out) reverse;
}

.slide-enter-active {
  animation: slideIn 0.3s var(--ease-out);
}

.slide-leave-active {
  animation: slideOut 0.2s var(--ease-out);
}

.page-loading {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid var(--glass-border);
  border-top-color: var(--accent);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes fadeSlideUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes slideIn {
  from { opacity: 0; transform: translateX(30px); }
  to { opacity: 1; transform: translateX(0); }
}

@keyframes slideOut {
  from { opacity: 1; transform: translateX(0); }
  to { opacity: 0; transform: translateX(-30px); }
}

@keyframes spin {
  to { transform: rotate(360deg); }
}
</style>

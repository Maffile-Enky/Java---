<template>
  <div class="restaurants-page">
    <div class="container">
      <!-- Search & Filter -->
      <div class="page-header reveal">
        <h1 class="page-title">
          <span v-if="keyword">搜索: "{{ keyword }}"</span>
          <span v-else>发现美食</span>
        </h1>
        <div class="filter-bar">
          <button
            v-for="cat in categories"
            :key="cat"
            class="filter-chip"
            :class="{ active: selectedCat === cat }"
            @click="selectedCat = cat"
          >{{ cat }}</button>
        </div>
      </div>

      <!-- Loading Skeleton -->
      <div v-if="loading" class="merchant-grid">
        <div v-for="i in 6" :key="i" class="skeleton-card">
          <SkeletonLoader type="card" />
        </div>
      </div>

      <!-- Empty -->
      <EmptyState v-else-if="!merchants.length" icon="🏪" text="暂无商家" />

      <!-- Grid -->
      <div v-else class="merchant-grid">
        <MerchantCard
          v-for="m in merchants"
          :key="m.id"
          :merchant="m"
          class="reveal"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { searchMerchants } from '@/api/search'
import MerchantCard from '@/components/common/MerchantCard.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import SkeletonLoader from '@/components/common/SkeletonLoader.vue'
import { useScrollReveal } from '@/composables/useScrollReveal'

const route = useRoute()
const merchants = ref([])
const loading = ref(true)
const keyword = ref(route.query.keyword || '')
const selectedCat = ref('全部')

const categories = ['全部', '中餐', '西餐', '日料', '韩餐', '快餐', '饮品', '甜品']

const { reobserve } = useScrollReveal()

async function fetchMerchants() {
  loading.value = true
  try {
    const res = await searchMerchants({
      keyword: keyword.value,
      category: selectedCat.value === '全部' ? '' : selectedCat.value
    })
    merchants.value = res.data?.merchants || res.data || []
  } catch {
    merchants.value = []
  } finally {
    loading.value = false
    reobserve()
  }
}

onMounted(fetchMerchants)
watch(() => route.query.keyword, (val) => {
  keyword.value = val || ''
  fetchMerchants()
})
watch(selectedCat, fetchMerchants)
</script>

<style scoped>
.restaurants-page {
  padding: var(--space-6) 0;
}

.page-header {
  margin-bottom: var(--space-8);
}

.page-title {
  font-family: var(--font-serif);
  font-size: 1.8rem;
  font-weight: 900;
  margin-bottom: var(--space-4);
}

.filter-bar {
  display: flex;
  gap: var(--space-2);
  flex-wrap: wrap;
  overflow-x: auto;
  -webkit-overflow-scrolling: touch;
  scrollbar-width: none;
  padding-bottom: var(--space-2);
}

.filter-bar::-webkit-scrollbar {
  display: none;
}

.filter-chip {
  padding: var(--space-2) var(--space-4);
  background: var(--glass);
  border: 1px solid var(--glass-border);
  border-radius: var(--radius-full);
  color: var(--text-secondary);
  font-size: var(--text-sm);
  cursor: pointer;
  transition: all var(--duration-fast);
  font-family: var(--font-sans);
  white-space: nowrap;
  flex-shrink: 0;
}

.filter-chip:hover {
  border-color: var(--accent);
  color: var(--text-primary);
}

.filter-chip.active {
  background: var(--gradient-green);
  color: var(--text-inverse);
  border-color: transparent;
  font-weight: 600;
}

.merchant-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: var(--space-6);
}

.skeleton-card {
  animation: fadeIn 0.3s ease-out;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

@media (max-width: 640px) {
  .merchant-grid {
    grid-template-columns: 1fr;
  }

  .page-title {
    font-size: 1.4rem;
  }
}
</style>

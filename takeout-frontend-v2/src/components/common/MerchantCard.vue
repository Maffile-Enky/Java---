<template>
  <router-link :to="`/user/restaurants/${merchant.id}`" class="merchant-card">
    <div class="merchant-cover">
      <img v-if="merchant.coverImage" :src="merchant.coverImage" :alt="merchant.name" loading="lazy" />
      <div v-else class="cover-placeholder">
        <span>🏪</span>
      </div>
      <span v-if="merchant.status === 'APPROVED'" class="merchant-badge tag tag-green">营业中</span>
      <span v-else class="merchant-badge tag tag-muted">休息中</span>
    </div>
    <div class="merchant-info">
      <h3 class="merchant-name">{{ merchant.name }}</h3>
      <p class="merchant-address">{{ merchant.address }}</p>
      <div class="merchant-meta">
        <span class="meta-rating">
          <RatingStars v-if="merchant.rating != null" :value="merchant.rating" size="sm" />
          <span v-if="merchant.rating != null" class="rating-num">{{ merchant.rating.toFixed(1) }}</span>
        </span>
        <span v-if="merchant.monthlySales != null" class="meta-sales">月售{{ merchant.monthlySales }}</span>
      </div>
      <div class="merchant-tags" v-if="merchant.categories && merchant.categories.length">
        <span v-for="cat in merchant.categories.slice(0, 3)" :key="cat" class="cat-tag">{{ cat }}</span>
      </div>
    </div>
  </router-link>
</template>

<script setup>
import RatingStars from './RatingStars.vue'

defineProps({
  merchant: { type: Object, required: true }
})
</script>

<style scoped>
.merchant-card {
  display: block;
  background: var(--glass);
  border: 1px solid var(--glass-border);
  border-radius: var(--radius-lg);
  overflow: hidden;
  text-decoration: none;
  transition: all var(--duration-normal) var(--ease-out);
}

.merchant-card:hover {
  border-color: var(--accent);
  box-shadow: 0 0 24px var(--accent-glow);
  transform: translateY(-3px);
}

.merchant-cover {
  position: relative;
  width: 100%;
  aspect-ratio: 2/1;
  overflow: hidden;
  background: var(--bg-elevated);
}

.merchant-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform var(--duration-slow);
}

.merchant-card:hover .merchant-cover img {
  transform: scale(1.05);
}

.cover-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 3rem;
  opacity: 0.3;
  background: linear-gradient(135deg, var(--bg-elevated), rgba(110, 231, 160, 0.05));
}

.merchant-badge {
  position: absolute;
  top: var(--space-3);
  left: var(--space-3);
}

.merchant-info {
  padding: var(--space-4);
}

.merchant-name {
  font-size: var(--text-base);
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 var(--space-1);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.merchant-address {
  font-size: var(--text-xs);
  color: var(--text-muted);
  margin: 0 0 var(--space-2);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.merchant-meta {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  margin-bottom: var(--space-2);
}

.meta-rating {
  display: flex;
  align-items: center;
  gap: var(--space-1);
}

.rating-num {
  font-size: var(--text-xs);
  color: var(--accent-secondary);
  font-weight: 600;
}

.meta-sales {
  font-size: var(--text-xs);
  color: var(--text-muted);
}

.merchant-tags {
  display: flex;
  gap: var(--space-1);
  flex-wrap: wrap;
}

.cat-tag {
  padding: 2px var(--space-2);
  background: var(--glass);
  border: 1px solid var(--glass-border);
  border-radius: var(--radius-sm);
  font-size: 10px;
  color: var(--text-muted);
}
</style>

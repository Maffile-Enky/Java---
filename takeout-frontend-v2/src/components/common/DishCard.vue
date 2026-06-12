<template>
  <div class="dish-card" @mouseenter="hovered = true" @mouseleave="hovered = false">
    <div class="dish-image">
      <img v-if="dish.image" :src="dish.image" :alt="dish.name" loading="lazy" @error="handleImageError" />
      <div v-else class="image-placeholder">
        <img src="@/assets/pic/enk.png" alt="默认图片" class="placeholder-img" />
      </div>
      <span v-if="dish.tag" class="dish-tag tag tag-green">{{ dish.tag }}</span>
    </div>
    <div class="dish-info">
      <h3 class="dish-name">{{ dish.name }}</h3>
      <p v-if="dish.description" class="dish-desc">{{ dish.description }}</p>
      <div class="dish-bottom">
        <span class="dish-price">
          <span class="price-symbol">¥</span>{{ formatPrice(dish.price) }}
        </span>
        <span v-if="dish.sales != null" class="dish-sales">月售{{ dish.sales }}</span>
      </div>
      <div class="dish-rating" v-if="dish.rating != null">
        <RatingStars :value="dish.rating" size="sm" />
        <span class="rating-text">{{ dish.rating.toFixed(1) }}</span>
      </div>
    </div>
    <div class="dish-action" :class="{ visible: hovered || alwaysShowAction }">
      <button class="add-btn" @click.stop="$emit('add', dish)">
        <span>+</span>
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import RatingStars from './RatingStars.vue'
import enkImg from '@/assets/pic/enk.png'

defineProps({
  dish: { type: Object, required: true },
  alwaysShowAction: { type: Boolean, default: false }
})

defineEmits(['add'])

const hovered = ref(false)

function formatPrice(val) {
  return Number(val).toFixed(2)
}

function handleImageError(e) {
  e.target.src = enkImg
  e.target.alt = '加载失败'
}
</script>

<style scoped>
.dish-card {
  position: relative;
  background: var(--glass);
  border: 1px solid var(--glass-border);
  border-radius: var(--radius-lg);
  overflow: hidden;
  transition: all var(--duration-normal) var(--ease-out);
}

.dish-card:hover {
  border-color: var(--accent);
  box-shadow: 0 0 20px var(--accent-glow);
  transform: translateY(-2px);
}

.dish-image {
  position: relative;
  width: 100%;
  aspect-ratio: 16/10;
  overflow: hidden;
  background: var(--bg-elevated);
}

.dish-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform var(--duration-slow);
}

.dish-card:hover .dish-image img {
  transform: scale(1.05);
}

.image-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, var(--bg-elevated), rgba(110, 231, 160, 0.05));
}

.placeholder-img {
  width: 60%;
  height: 60%;
  object-fit: contain;
  opacity: 0.6;
}

.dish-tag {
  position: absolute;
  top: var(--space-2);
  left: var(--space-2);
}

.dish-info {
  padding: var(--space-3) var(--space-4);
}

.dish-name {
  font-size: var(--text-sm);
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 var(--space-1);
  line-height: 1.3;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.dish-desc {
  font-size: var(--text-xs);
  color: var(--text-muted);
  margin: 0 0 var(--space-2);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.dish-bottom {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: var(--space-2);
}

.dish-price {
  font-size: 1rem;
  font-weight: 800;
  color: var(--accent);
}

.price-symbol {
  font-size: var(--text-xs);
  margin-right: 1px;
}

.dish-sales {
  font-size: var(--text-xs);
  color: var(--text-muted);
}

.dish-rating {
  display: flex;
  align-items: center;
  gap: var(--space-1);
  margin-top: var(--space-1);
}

.rating-text {
  font-size: var(--text-xs);
  color: var(--accent-secondary);
  font-weight: 600;
}

.dish-action {
  position: absolute;
  bottom: var(--space-3);
  right: var(--space-3);
  opacity: 0;
  transform: scale(0.8);
  transition: all var(--duration-fast) var(--ease-out);
}

.dish-action.visible {
  opacity: 1;
  transform: scale(1);
}

.add-btn {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: var(--gradient-green);
  color: var(--text-inverse);
  border: none;
  font-size: 1.2rem;
  font-weight: 700;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all var(--duration-fast);
  box-shadow: 0 2px 8px rgba(110, 231, 160, 0.3);
}

.add-btn:hover {
  transform: scale(1.1);
  box-shadow: 0 4px 16px rgba(110, 231, 160, 0.5);
}
</style>

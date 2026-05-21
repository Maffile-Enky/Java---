<template>
  <div class="home-page">
    <!-- Hero -->
    <section class="hero">
      <div class="container">
        <div class="hero-content">
          <div class="hero-text">
            <div class="hero-badge">✦ 神造之味 · 首单立减 20 元</div>
            <h1>
              每一口，<br />
              都是一场<br />
              <span class="gradient-text">味觉旅行</span>
            </h1>
            <p class="hero-sub">
              甄选城市百大名厨，30 分钟极速送达。<br />
              从清晨的第一杯咖啡到深夜的慰藉小食，<br />
              我们让美食找到你。
            </p>
            <div class="hero-tags">
              <span class="hero-tag hero-tag--green">鲜食直送</span>
              <span class="hero-tag hero-tag--gold">金选臻品</span>
              <span class="hero-tag hero-tag--purple">限定特供</span>
            </div>
            <div class="hero-actions">
              <GlassButton variant="primary" size="lg" @click="scrollToDishes">
                探索美食 →
              </GlassButton>
              <GlassButton variant="ghost" size="lg">
                ▷ 观看视频
              </GlassButton>
            </div>
          </div>
          <div class="hero-visual">
            <div class="float-stat float-stat--1">
              <div class="float-stat-num">98.6%</div>
              <div class="float-stat-label">好评率</div>
            </div>
            <div class="float-stat float-stat--2">
              <div class="float-stat-num">28 min</div>
              <div class="float-stat-label">平均送达</div>
            </div>
            <div class="hero-emoji-float">🍜</div>
          </div>
        </div>
      </div>
    </section>

    <!-- Divider -->
    <div class="container"><div class="section-divider"></div></div>

    <!-- Stats -->
    <section id="about">
      <div class="container">
        <div class="stats-bar reveal">
          <div class="stat-item" v-for="stat in stats" :key="stat.label">
            <div class="stat-num" :data-target="stat.target">0</div>
            <div class="stat-label">{{ stat.label }}</div>
          </div>
        </div>
      </div>
    </section>

    <!-- Dishes -->
    <section id="dishes">
      <div class="container">
        <div class="reveal">
          <div class="section-label">✦ 精选推荐</div>
          <div class="section-title">今日人气菜品</div>
          <div class="section-desc">由资深美食编辑精心挑选，每一道都经过千人口碑验证。新鲜食材，匠心烹制。</div>
        </div>
        <div class="dishes-grid">
          <div
            v-for="(dish, i) in featuredDishes"
            :key="dish.name"
            class="dish-card reveal"
            :style="{ transitionDelay: `${i * 80}ms` }"
          >
            <div class="dish-img" :style="{ background: dish.bg }">
              <span class="dish-tag" :style="dish.tagStyle">{{ dish.tag }}</span>
              {{ dish.emoji }}
            </div>
            <div class="dish-body">
              <div class="dish-name">{{ dish.name }}</div>
              <div class="dish-desc">{{ dish.desc }}</div>
              <div class="dish-footer">
                <div class="dish-price">¥{{ dish.price }} <small>/份</small></div>
                <div class="dish-rating">★ {{ dish.rating }} <span>({{ dish.reviews }})</span></div>
              </div>
            </div>
            <button class="dish-order-btn" @click="$router.push('/user/restaurants')">+</button>
          </div>
        </div>
      </div>
    </section>

    <!-- Reviews -->
    <section id="reviews" class="reviews-section">
      <div class="container">
        <div class="reveal">
          <div class="section-label">✦ 真实评价</div>
          <div class="section-title">听听他们怎么说</div>
          <div class="section-desc">来自真实用户的用餐反馈，每一条评价都经过平台认证。</div>
        </div>
      </div>
      <div class="reviews-track-wrapper">
        <div class="reviews-track">
          <div v-for="(review, i) in [...reviews, ...reviews]" :key="i" class="review-card">
            <div class="review-stars">{{ review.stars }}</div>
            <div class="review-text">"{{ review.text }}"</div>
            <div class="review-author">
              <div class="review-avatar">{{ review.avatar }}</div>
              <div>
                <div class="review-name">{{ review.name }}</div>
                <div class="review-date">{{ review.date }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- CTA -->
    <section class="cta-section" id="contact">
      <div class="container">
        <div class="cta-box reveal">
          <div class="section-label">✦ 开启美食之旅</div>
          <div class="cta-title">今天想吃点什么？</div>
          <div class="cta-desc">输入你的地址，发现身边的美味。新用户注册即享首单 20 元优惠。</div>
          <div class="cta-input-group">
            <input
              v-model="address"
              type="text"
              class="cta-input"
              placeholder="输入你的配送地址..."
              @keyup.enter="goSearch"
            />
            <GlassButton variant="primary" @click="goSearch">搜索美食</GlassButton>
          </div>
        </div>
      </div>
    </section>

    <!-- Footer -->
    <footer class="site-footer">
      <div class="container">
        <div class="footer-grid">
          <div class="footer-brand">
            <div class="footer-logo">味觉星球 <span>FLAVOR PLANET</span></div>
            <p>致力于连接城市最好的厨师与最挑剔的味蕾。每一单，都是一次对美食的致敬。</p>
          </div>
          <div class="footer-col">
            <h4>产品</h4>
            <router-link to="/user/restaurants">点餐</router-link>
            <a href="#">预约配送</a>
            <a href="#">企业团餐</a>
            <a href="#">礼品卡</a>
          </div>
          <div class="footer-col">
            <h4>公司</h4>
            <a href="#">关于我们</a>
            <a href="#">加入团队</a>
            <a href="#">新闻动态</a>
            <a href="#">合作伙伴</a>
          </div>
          <div class="footer-col">
            <h4>支持</h4>
            <a href="#">帮助中心</a>
            <a href="#">食品安全</a>
            <a href="#">用户协议</a>
            <a href="#">隐私政策</a>
          </div>
        </div>
        <div class="footer-bottom">
          <span>&copy; 2026 味觉星球 Flavor Planet. All rights reserved.</span>
          <div class="footer-socials">
            <a href="#" title="微信">微</a>
            <a href="#" title="微博">博</a>
            <a href="#" title="抖音">抖</a>
          </div>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useScrollReveal } from '@/composables/useScrollReveal'
import { useCounterAnimation } from '@/composables/useCounterAnimation'
import GlassButton from '@/components/ui/GlassButton.vue'

const router = useRouter()
const address = ref('')

useScrollReveal()
useCounterAnimation()

const stats = [
  { target: 50000, label: '日均订单' },
  { target: 1200, label: '合作商户' },
  { target: 98, label: '准时率 %' },
  { target: 30, label: '覆盖城市' }
]

const featuredDishes = [
  {
    name: '黯然销魂饭', emoji: '🍛', price: 38, rating: 4.9, reviews: '2,847',
    tag: '人气 TOP1', tagStyle: '',
    bg: 'linear-gradient(145deg, #12261a, #1a3d24, #245a34)',
    desc: '秘制叉烧搭配溏心蛋，淋上特调酱汁，每一口都是回忆'
  },
  {
    name: '酸菜鱼煲', emoji: '🥘', price: 52, rating: 4.8, reviews: '1,923',
    tag: '厨师推荐', tagStyle: 'background:var(--accent-secondary);color:#0e1612;',
    bg: 'linear-gradient(145deg, #1a2018, #2a3828, #3a5038)',
    desc: '鲜嫩鲈鱼片配正宗老坛酸菜，汤鲜味美，开胃下饭'
  },
  {
    name: '兰州手拉牛肉面', emoji: '🍜', price: 28, rating: 4.9, reviews: '3,156',
    tag: '新品上市', tagStyle: 'background:var(--accent-whisper);color:#0e1612;',
    bg: 'linear-gradient(145deg, #141e18, #1e3024, #284230)',
    desc: '一清二白三红四绿五黄，传承百年工艺，手工拉制'
  },
  {
    name: '黑松露芝士披萨', emoji: '🍕', price: 68, rating: 4.7, reviews: '987',
    tag: '限时特惠', tagStyle: 'background:linear-gradient(135deg, var(--accent-whisper), var(--accent));',
    bg: 'linear-gradient(145deg, #1e1a10, #302a18, #423a20)',
    desc: '意大利进口芝士搭配黑松露，薄底酥脆，浓郁醇香'
  },
  {
    name: '日式寿喜锅', emoji: '🍲', price: 78, rating: 4.8, reviews: '1,456',
    tag: '深夜食堂', tagStyle: 'background:var(--accent-secondary);color:#0e1612;',
    bg: 'linear-gradient(145deg, #141820, #1e2830, #283840)',
    desc: '澳洲肥牛搭配新鲜蔬菜，甜咸汤底，暖胃暖心'
  },
  {
    name: '牛油果三文鱼沙拉', emoji: '🥗', price: 45, rating: 4.6, reviews: '2,103',
    tag: '健康轻食', tagStyle: '',
    bg: 'linear-gradient(145deg, #101e14, #18301e, #204228)',
    desc: '挪威三文鱼配新鲜牛油果，低卡高蛋白，健身首选'
  }
]

const reviews = [
  { stars: '★★★★★', text: '深夜加班点了一份酸菜鱼，30分钟就送到了，汤还是热腾腾的！鱼片嫩滑，酸菜够味，吃完感觉又能再战三小时。', avatar: '👩', name: '林小雨', date: '3 天前' },
  { stars: '★★★★★', text: '作为健身党，牛油果三文鱼沙拉简直是我的救星。食材新鲜看得见，三文鱼切得很厚实，性价比超高。', avatar: '💪', name: '张教练', date: '5 天前' },
  { stars: '★★★★★', text: '黯然销魂饭名不虚传！叉烧肥瘦相间，溏心蛋戳开的那一刻太治愈了。已经连续点了一周，完全吃不腻。', avatar: '🧑‍💻', name: '王同学', date: '1 天前' },
  { stars: '★★★★☆', text: '黑松露披萨芝士拉丝超长，薄底很脆。唯一遗憾是送到的时候稍微凉了一点，不过味道还是很棒的。', avatar: '👨‍🍳', name: '美食家老陈', date: '2 天前' },
  { stars: '★★★★★', text: '寿喜锅的牛肉纹理太漂亮了，配上生鸡蛋液简直入口即化。冬天来一锅幸福感爆棚，强烈推荐！', avatar: '👩‍🎨', name: '苏苏', date: '4 天前' },
  { stars: '★★★★★', text: '兰州牛肉面的面条劲道十足，汤底清亮鲜美。在这个价位能吃到这个品质，真的太良心了。', avatar: '🍜', name: '面食爱好者', date: '6 天前' }
]

function scrollToDishes() {
  document.getElementById('dishes')?.scrollIntoView({ behavior: 'smooth' })
}

function goSearch() {
  if (address.value.trim()) {
    router.push({ path: '/user/restaurants', query: { keyword: address.value.trim() } })
  } else {
    router.push('/user/restaurants')
  }
}
</script>

<style scoped>
.home-page {
  position: relative;
}

/* Hero */
.hero {
  min-height: 100vh;
  display: flex;
  align-items: center;
  padding-top: 80px;
  position: relative;
}

.hero-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 80px;
  align-items: center;
}

.hero-text {
  animation: fadeSlideUp 1s cubic-bezier(0.16, 1, 0.3, 1) both;
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 6px 16px;
  background: var(--glass);
  border: 1px solid var(--glass-border);
  border-radius: var(--radius-full);
  font-size: 0.78rem;
  color: var(--accent);
  margin-bottom: 28px;
  backdrop-filter: blur(10px);
}

.hero-badge::before {
  content: '';
  width: 6px;
  height: 6px;
  background: var(--accent);
  border-radius: 50%;
  animation: pulse 2s ease-in-out infinite;
}

.hero h1 {
  font-family: var(--font-serif);
  font-size: clamp(2.8rem, 5.5vw, 4.5rem);
  font-weight: 900;
  line-height: 1.1;
  margin-bottom: 24px;
  letter-spacing: -0.03em;
  color: var(--text-primary);
}

.gradient-text {
  background: var(--gradient-hero);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.hero-sub {
  font-size: 1.15rem;
  color: var(--text-secondary);
  line-height: 1.75;
  margin-bottom: 40px;
  max-width: 460px;
  font-weight: 300;
}

.hero-tags {
  display: flex;
  gap: 8px;
  margin-bottom: 28px;
}

.hero-tag {
  padding: 4px 14px;
  border-radius: var(--radius-full);
  font-size: 0.7rem;
  font-weight: 600;
  letter-spacing: 0.02em;
}

.hero-tag--green { background: var(--accent); color: #0e1612; }
.hero-tag--gold { background: var(--accent-secondary); color: #0e1612; }
.hero-tag--purple { background: var(--accent-whisper); color: #0e1612; }

.hero-actions {
  display: flex;
  gap: 16px;
  align-items: center;
}

.hero-visual {
  position: relative;
  animation: fadeSlideUp 1s cubic-bezier(0.16, 1, 0.3, 1) 0.2s both;
}

.hero-emoji-float {
  font-size: 8rem;
  filter: drop-shadow(0 20px 40px rgba(0, 0, 0, 0.25));
  animation: float 4s ease-in-out infinite;
  text-align: center;
}

.float-stat {
  position: absolute;
  padding: 14px 20px;
  background: var(--glass-solid);
  border: 1px solid var(--glass-border);
  border-radius: var(--radius-md);
  backdrop-filter: blur(20px);
  box-shadow: 0 16px 40px rgba(0, 0, 0, 0.25);
  animation: floatSlow 6s ease-in-out infinite;
}

.float-stat--1 { top: 10%; right: -20px; animation-delay: -2s; }
.float-stat--2 { bottom: 15%; left: -30px; animation-delay: -4s; }

.float-stat-num {
  font-size: 1.5rem;
  font-weight: 700;
  color: var(--accent);
  font-family: var(--font-serif);
}

.float-stat-label {
  font-size: 0.75rem;
  color: var(--text-secondary);
  margin-top: 2px;
}

/* Divider */
.section-divider {
  height: 1px;
  max-width: 600px;
  margin: 0 auto;
  background: linear-gradient(90deg, transparent, var(--accent), var(--accent-secondary), var(--accent), transparent);
  opacity: 0.3;
}

/* Stats */
section {
  padding: 120px 0;
}

.section-label {
  font-size: 0.75rem;
  letter-spacing: 0.2em;
  text-transform: uppercase;
  color: var(--accent);
  margin-bottom: 12px;
  font-weight: 500;
}

.section-title {
  font-family: var(--font-serif);
  font-size: clamp(2rem, 3.5vw, 2.8rem);
  font-weight: 900;
  margin-bottom: 16px;
  letter-spacing: -0.02em;
}

.section-desc {
  color: var(--text-secondary);
  font-size: 1.05rem;
  max-width: 520px;
  line-height: 1.75;
  font-weight: 300;
  margin-bottom: 60px;
}

.stats-bar {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
}

.stat-item {
  text-align: center;
  padding: 32px 16px;
  background: var(--glass);
  border: 1px solid var(--glass-border);
  border-radius: var(--radius-lg);
  backdrop-filter: blur(10px);
  transition: all 0.3s;
}

.stat-item:hover {
  border-color: rgba(110, 231, 160, 0.2);
  transform: translateY(-4px);
  box-shadow: 0 0 30px var(--accent-glow);
}

.stat-num {
  font-family: var(--font-serif);
  font-size: 2.5rem;
  font-weight: 900;
  background: var(--gradient-green);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.stat-label {
  font-size: 0.85rem;
  color: var(--text-secondary);
  margin-top: 6px;
}

/* Dishes */
.dishes-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}

.dish-card {
  background: var(--glass);
  border: 1px solid var(--glass-border);
  border-radius: var(--radius-lg);
  overflow: hidden;
  transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1);
  cursor: pointer;
  position: relative;
  backdrop-filter: blur(10px);
}

.dish-card:hover {
  transform: translateY(-8px);
  border-color: rgba(110, 231, 160, 0.25);
  box-shadow: 0 24px 60px rgba(0, 0, 0, 0.2), 0 0 40px var(--accent-glow);
}

.dish-img {
  width: 100%;
  aspect-ratio: 4/3;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 4rem;
  position: relative;
  overflow: hidden;
}

.dish-img::after {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(to top, var(--bg-deep), transparent 40%);
}

.dish-tag {
  position: absolute;
  top: 16px;
  left: 16px;
  padding: 4px 12px;
  background: var(--accent);
  color: #0e1612;
  font-size: 0.7rem;
  font-weight: 600;
  border-radius: var(--radius-full);
  z-index: 1;
  letter-spacing: 0.05em;
}

.dish-body {
  padding: 24px;
}

.dish-name {
  font-family: var(--font-serif);
  font-size: 1.2rem;
  font-weight: 700;
  margin-bottom: 6px;
}

.dish-desc {
  font-size: 0.82rem;
  color: var(--text-secondary);
  line-height: 1.6;
  margin-bottom: 16px;
}

.dish-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.dish-price {
  font-size: 1.3rem;
  font-weight: 700;
  color: var(--accent);
  font-family: var(--font-serif);
}

.dish-price small {
  font-size: 0.75rem;
  font-weight: 400;
  color: var(--text-muted);
}

.dish-rating {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 0.85rem;
  color: var(--accent-secondary);
}

.dish-rating span {
  color: var(--text-secondary);
  font-size: 0.8rem;
}

.dish-order-btn {
  position: absolute;
  bottom: 24px;
  right: 24px;
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: var(--gradient-green);
  border: none;
  color: #0e1612;
  font-size: 1.3rem;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
  opacity: 0;
  transform: scale(0.8);
}

.dish-card:hover .dish-order-btn {
  opacity: 1;
  transform: scale(1);
}

.dish-order-btn:hover {
  background: var(--accent-secondary);
  transform: scale(1.1) !important;
  box-shadow: 0 8px 20px rgba(240, 197, 90, 0.3);
}

/* Reviews */
.reviews-section {
  overflow: hidden;
}

.reviews-track-wrapper {
  overflow: hidden;
}

.reviews-track {
  display: flex;
  gap: 24px;
  animation: scroll 30s linear infinite;
  width: max-content;
}

.reviews-track:hover {
  animation-play-state: paused;
}

.review-card {
  flex-shrink: 0;
  width: 380px;
  padding: 32px;
  background: var(--glass);
  border: 1px solid var(--glass-border);
  border-radius: var(--radius-lg);
  backdrop-filter: blur(10px);
  transition: all 0.3s;
}

.review-card:hover {
  border-color: rgba(110, 231, 160, 0.18);
  background: rgba(180, 220, 195, 0.1);
}

.review-stars {
  color: var(--accent-secondary);
  font-size: 0.9rem;
  margin-bottom: 16px;
  letter-spacing: 2px;
}

.review-text {
  font-size: 0.95rem;
  line-height: 1.75;
  color: var(--text-primary);
  margin-bottom: 20px;
  font-weight: 300;
}

.review-author {
  display: flex;
  align-items: center;
  gap: 12px;
}

.review-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: var(--gradient-green);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.1rem;
}

.review-name {
  font-weight: 500;
  font-size: 0.9rem;
}

.review-date {
  font-size: 0.75rem;
  color: var(--text-muted);
}

/* CTA */
.cta-section {
  text-align: center;
}

.cta-box {
  padding: 80px 60px;
  background: linear-gradient(145deg, rgba(110, 231, 160, 0.05), rgba(240, 197, 90, 0.03));
  border: 1px solid var(--glass-border);
  border-radius: 28px;
  backdrop-filter: blur(20px);
  position: relative;
  overflow: hidden;
}

.cta-box::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: conic-gradient(from 0deg, transparent, var(--accent-glow), rgba(240, 197, 90, 0.12), transparent 35%);
  animation: rotateSlow 12s linear infinite;
  opacity: 0.35;
}

.cta-box > * {
  position: relative;
  z-index: 1;
}

.cta-title {
  font-family: var(--font-serif);
  font-size: clamp(2rem, 4vw, 3rem);
  font-weight: 900;
  margin-bottom: 16px;
}

.cta-desc {
  color: var(--text-secondary);
  font-size: 1.1rem;
  margin-bottom: 40px;
  max-width: 500px;
  margin-inline: auto;
  line-height: 1.75;
  font-weight: 300;
}

.cta-input-group {
  display: flex;
  gap: 12px;
  max-width: 460px;
  margin: 0 auto;
}

.cta-input {
  flex: 1;
  padding: 16px 24px;
  background: rgba(255, 255, 255, 0.06);
  border: 1px solid var(--glass-border);
  border-radius: var(--radius-full);
  color: var(--text-primary);
  font-size: 0.95rem;
  font-family: var(--font-sans);
  outline: none;
  transition: border-color 0.3s;
}

.cta-input::placeholder {
  color: var(--text-muted);
}

.cta-input:focus {
  border-color: var(--accent);
}

/* Footer */
.site-footer {
  padding: 60px 0 40px;
  border-top: 1px solid var(--glass-border);
}

.footer-grid {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr;
  gap: 60px;
  margin-bottom: 60px;
}

.footer-logo {
  font-family: var(--font-serif);
  font-size: 1.4rem;
  font-weight: 900;
  background: var(--gradient-green);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 16px;
  display: inline-block;
}

.footer-logo span {
  font-weight: 400;
  font-size: 0.72rem;
  -webkit-text-fill-color: var(--text-muted);
  margin-left: 8px;
  letter-spacing: 0.12em;
}

.footer-brand p {
  color: var(--text-secondary);
  font-size: 0.85rem;
  line-height: 1.7;
  max-width: 280px;
}

.footer-col h4 {
  font-size: 0.8rem;
  letter-spacing: 0.15em;
  text-transform: uppercase;
  color: var(--text-muted);
  margin-bottom: 20px;
  font-weight: 500;
}

.footer-col a {
  display: block;
  color: var(--text-secondary);
  text-decoration: none;
  font-size: 0.88rem;
  padding: 6px 0;
  transition: color 0.3s;
}

.footer-col a:hover {
  color: var(--text-primary);
}

.footer-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 30px;
  border-top: 1px solid var(--glass-border);
  font-size: 0.8rem;
  color: var(--text-muted);
}

.footer-socials {
  display: flex;
  gap: 16px;
}

.footer-socials a {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: var(--glass);
  border: 1px solid var(--glass-border);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-secondary);
  text-decoration: none;
  font-size: 0.9rem;
  transition: all 0.3s;
}

.footer-socials a:hover {
  background: var(--accent);
  border-color: var(--accent);
  color: #0e1612;
  box-shadow: 0 4px 16px var(--accent-glow);
}

/* Animations */
@keyframes float {
  0%, 100% { transform: translate(-50%, -60%); }
  50% { transform: translate(-50%, -68%); }
}

@keyframes floatSlow {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

@keyframes scroll {
  0% { transform: translateX(0); }
  100% { transform: translateX(-50%); }
}

@keyframes rotateSlow {
  to { transform: rotate(360deg); }
}

@keyframes fadeSlideUp {
  from { opacity: 0; transform: translateY(40px); }
  to { opacity: 1; transform: translateY(0); }
}

/* Responsive */
@media (max-width: 1024px) {
  .hero-content { grid-template-columns: 1fr; gap: 48px; }
  .hero-visual { max-width: 500px; margin: 0 auto; }
  .dishes-grid { grid-template-columns: repeat(2, 1fr); }
  .stats-bar { grid-template-columns: repeat(2, 1fr); }
  .footer-grid { grid-template-columns: 1fr 1fr; gap: 40px; }
  section { padding: 80px 0; }
}

@media (max-width: 640px) {
  .hero { min-height: auto; padding-top: 100px; padding-bottom: 60px; }
  .hero h1 { font-size: 2rem; line-height: 1.2; }
  .hero-sub { font-size: 0.95rem; }
  .hero-tags { flex-wrap: wrap; }
  .hero-actions { flex-direction: column; width: 100%; }
  .hero-actions :deep(.glass-btn) { width: 100%; }
  .hero-emoji-float { font-size: 5rem; }
  .float-stat { display: none; }
  .dishes-grid { grid-template-columns: 1fr; gap: 16px; }
  .stats-bar { grid-template-columns: 1fr 1fr; gap: 12px; }
  .stat-item { padding: 20px 12px; }
  .stat-num { font-size: 1.8rem; }
  .cta-input-group { flex-direction: column; }
  .cta-box { padding: 40px 24px; }
  .footer-grid { grid-template-columns: 1fr; gap: 32px; }
  .footer-bottom { flex-direction: column; gap: 16px; text-align: center; }
  .review-card { width: 280px; padding: 24px; }
  section { padding: 60px 0; }
  .section-title { font-size: 1.6rem; }
  .section-desc { font-size: 0.9rem; margin-bottom: 40px; }
}
</style>

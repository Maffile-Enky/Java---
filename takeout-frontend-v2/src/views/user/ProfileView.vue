<template>
  <div class="profile-page">
    <div class="container">
      <h1 class="page-title fade-in">个人中心</h1>

      <!-- User Card -->
      <div class="user-card glass-panel fade-in-up">
        <div class="avatar-wrapper" @click="triggerUpload" :class="{ uploading: uploading }">
          <img v-if="auth.userInfo?.avatar" :src="auth.userInfo.avatar" class="avatar-img" alt="头像" />
          <div v-else class="avatar-placeholder">
            {{ (auth.nickname || '用户').charAt(0) }}
          </div>
          <div class="avatar-overlay">
            <span v-if="uploading" class="upload-spinner"></span>
            <span v-else>📷</span>
          </div>
          <input
            ref="fileInput"
            type="file"
            accept="image/jpeg,image/png,image/gif,image/webp"
            style="display: none"
            @change="handleFileChange"
          />
        </div>
        <div class="user-info">
          <h2 class="user-name">{{ auth.nickname || '未登录' }}</h2>
          <span class="user-role tag tag-green">{{ roleLabel }}</span>
          <p class="user-phone" v-if="auth.userInfo?.phone">{{ auth.userInfo.phone }}</p>
        </div>
      </div>

      <!-- Edit Profile Section -->
      <div class="section-card glass-panel fade-in-up" style="animation-delay: 0.1s">
        <h3 class="section-title">编辑资料</h3>
        <div class="form-group">
          <label class="form-label">昵称</label>
          <div class="input-row">
            <GlassInput v-model="editForm.nickname" placeholder="请输入昵称" />
            <GlassButton variant="ghost" size="sm" @click="saveNickname" :disabled="saving">
              {{ saving ? '保存中...' : '保存' }}
            </GlassButton>
          </div>
        </div>
        <div class="form-group">
          <label class="form-label">手机号</label>
          <div class="input-row">
            <GlassInput v-model="editForm.phone" placeholder="请输入手机号" />
            <GlassButton variant="ghost" size="sm" @click="savePhone" :disabled="saving">
              {{ saving ? '保存中...' : '保存' }}
            </GlassButton>
          </div>
        </div>
      </div>

      <!-- Role Upgrade -->
      <div v-if="auth.userInfo?.role === 'USER'" class="section-card glass-panel fade-in-up" style="animation-delay: 0.18s">
        <h3 class="section-title">身份升级</h3>
        <p class="upgrade-hint">升级身份即可开通对应功能</p>
        <div class="upgrade-actions">
          <GlassButton variant="primary" size="sm" :loading="upgrading === 'MERCHANT'" @click="handleUpgrade('MERCHANT')">
            申请成为商家
          </GlassButton>
          <GlassButton variant="ghost" size="sm" :loading="upgrading === 'RIDER'" @click="handleUpgrade('RIDER')">
            申请成为骑手
          </GlassButton>
        </div>
      </div>

      <!-- Menu -->
      <div class="profile-menu">
        <router-link to="/user/orders" class="menu-link glass-panel fade-in-up" style="animation-delay: 0.15s">
          <span class="menu-icon">📋</span>
          <span class="menu-text">我的订单</span>
          <span class="menu-arrow">→</span>
        </router-link>
        <router-link to="/user/address" class="menu-link glass-panel fade-in-up" style="animation-delay: 0.2s">
          <span class="menu-icon">📍</span>
          <span class="menu-text">收货地址</span>
          <span class="menu-arrow">→</span>
        </router-link>
        <router-link to="/user/notifications" class="menu-link glass-panel fade-in-up" style="animation-delay: 0.25s">
          <span class="menu-icon">🔔</span>
          <span class="menu-text">消息通知</span>
          <span class="menu-arrow">→</span>
        </router-link>
        <a v-if="auth.userInfo?.role === 'MERCHANT' || auth.userInfo?.role === 'ADMIN'" href="#" class="menu-link glass-panel fade-in-up" style="animation-delay: 0.3s" @click.prevent="$router.push('/merchant')">
          <span class="menu-icon">🏪</span>
          <span class="menu-text">商家后台</span>
          <span class="menu-arrow">→</span>
        </a>
        <a v-if="auth.userInfo?.role === 'ADMIN'" href="#" class="menu-link glass-panel fade-in-up" style="animation-delay: 0.35s" @click.prevent="$router.push('/admin')">
          <span class="menu-icon">⚙️</span>
          <span class="menu-text">管理后台</span>
          <span class="menu-arrow">→</span>
        </a>
        <a v-if="auth.userInfo?.role === 'RIDER' || auth.userInfo?.role === 'ADMIN'" href="#" class="menu-link glass-panel fade-in-up" style="animation-delay: 0.4s" @click.prevent="$router.push('/rider')">
          <span class="menu-icon">📦</span>
          <span class="menu-text">骑手中心</span>
          <span class="menu-arrow">→</span>
        </a>
      </div>

      <GlassButton variant="ghost" block @click="handleLogout" class="logout-btn fade-in-up" style="animation-delay: 0.45s">
        退出登录
      </GlassButton>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { getProfile, updateProfile, uploadAvatar, upgradeRole } from '@/api/user'
import GlassButton from '@/components/ui/GlassButton.vue'
import GlassInput from '@/components/ui/GlassInput.vue'

const router = useRouter()
const auth = useAuthStore()

const fileInput = ref(null)
const uploading = ref(false)
const saving = ref(false)
const upgrading = ref(null)
const editForm = ref({
  nickname: '',
  phone: ''
})

const roleLabel = computed(() => {
  const map = { USER: '用户', MERCHANT: '商家', ADMIN: '管理员', RIDER: '骑手' }
  return map[auth.userInfo?.role] || '用户'
})

onMounted(async () => {
  try {
    const res = await getProfile()
    if (res.data) {
      auth.updateUserInfo(res.data)
      editForm.value.nickname = res.data.nickname || ''
      editForm.value.phone = res.data.phone || ''
    }
  } catch {}
})

function triggerUpload() {
  if (uploading.value) return
  fileInput.value?.click()
}

async function handleFileChange(e) {
  const file = e.target.files?.[0]
  if (!file) return

  if (file.size > 5 * 1024 * 1024) {
    alert('文件大小不能超过5MB')
    return
  }

  uploading.value = true
  try {
    const res = await uploadAvatar(file)
    if (res.data?.url) {
      auth.updateUserInfo({ avatar: res.data.url })
    }
  } catch (err) {
    alert('头像上传失败: ' + (err.message || '未知错误'))
  } finally {
    uploading.value = false
    if (fileInput.value) fileInput.value.value = ''
  }
}

async function saveNickname() {
  if (!editForm.value.nickname.trim()) return
  saving.value = true
  try {
    await updateProfile({ nickname: editForm.value.nickname })
    auth.updateUserInfo({ nickname: editForm.value.nickname })
  } catch {}
  saving.value = false
}

async function savePhone() {
  if (!editForm.value.phone.trim()) return
  saving.value = true
  try {
    await updateProfile({ phone: editForm.value.phone })
    auth.updateUserInfo({ phone: editForm.value.phone })
  } catch {}
  saving.value = false
}

async function handleUpgrade(role) {
  const label = role === 'MERCHANT' ? '商家' : '骑手'
  if (!confirm(`确认申请成为${label}？`)) return
  upgrading.value = role
  try {
    const res = await upgradeRole(role)
    if (res.data) {
      auth.setAuth(res.data.token, res.data.userInfo)
      alert(`已升级为${label}`)
    }
  } catch (err) {
    alert('升级失败: ' + (err.message || '未知错误'))
  } finally {
    upgrading.value = null
  }
}

function handleLogout() {
  auth.logout()
  router.push('/')
}
</script>

<style scoped>
.profile-page {
  padding: var(--space-6) 0;
}

.page-title {
  font-family: var(--font-serif);
  font-size: 1.8rem;
  font-weight: 900;
  margin-bottom: var(--space-6);
}

/* Animations */
.fade-in {
  animation: fadeIn 0.6s var(--ease-out) both;
}

.fade-in-up {
  animation: fadeInUp 0.6s var(--ease-out) both;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

/* User Card */
.user-card {
  display: flex;
  align-items: center;
  gap: var(--space-5);
  padding: var(--space-6);
  margin-bottom: var(--space-4);
  transition: all var(--duration-normal);
}

.user-card:hover {
  border-color: rgba(110, 231, 160, 0.2);
  box-shadow: 0 0 20px var(--accent-glow);
}

.avatar-wrapper {
  position: relative;
  width: 72px;
  height: 72px;
  border-radius: 50%;
  cursor: pointer;
  flex-shrink: 0;
  overflow: hidden;
  transition: transform var(--duration-fast);
}

.avatar-wrapper:hover {
  transform: scale(1.05);
}

.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  background: var(--gradient-green);
  color: var(--text-inverse);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.8rem;
  font-weight: 800;
}

.avatar-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.2rem;
  opacity: 0;
  transition: opacity var(--duration-fast);
  border-radius: 50%;
}

.avatar-wrapper:hover .avatar-overlay {
  opacity: 1;
}

.avatar-wrapper.uploading .avatar-overlay {
  opacity: 1;
  background: rgba(0, 0, 0, 0.6);
}

.upload-spinner {
  width: 20px;
  height: 20px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.user-info {
  flex: 1;
}

.user-name {
  font-size: 1.3rem;
  font-weight: 700;
  margin-bottom: var(--space-1);
}

.user-phone {
  font-size: var(--text-sm);
  color: var(--text-muted);
  margin-top: var(--space-1);
}

/* Edit Section */
.section-card {
  padding: var(--space-5);
  margin-bottom: var(--space-4);
}

.section-title {
  font-size: var(--text-base);
  font-weight: 700;
  margin-bottom: var(--space-4);
  color: var(--text-primary);
}

.form-group {
  margin-bottom: var(--space-4);
}

.form-group:last-child {
  margin-bottom: 0;
}

.form-label {
  display: block;
  font-size: var(--text-sm);
  color: var(--text-secondary);
  margin-bottom: var(--space-2);
}

.input-row {
  display: flex;
  gap: var(--space-2);
  align-items: center;
}

.input-row :deep(.glass-input) {
  flex: 1;
}

/* Role Upgrade */
.upgrade-hint {
  font-size: var(--text-sm);
  color: var(--text-muted);
  margin-bottom: var(--space-4);
}

.upgrade-actions {
  display: flex;
  gap: var(--space-3);
}

/* Menu */
.profile-menu {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
  margin-bottom: var(--space-4);
}

.menu-link {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-4);
  text-decoration: none;
  color: var(--text-primary);
  transition: all var(--duration-fast);
}

.menu-link:hover {
  border-color: rgba(110, 231, 160, 0.2);
  transform: translateX(4px);
}

.menu-icon { font-size: 1.2rem; }
.menu-text { flex: 1; font-size: var(--text-sm); font-weight: 500; }
.menu-arrow {
  color: var(--text-muted);
  transition: transform var(--duration-fast);
}

.menu-link:hover .menu-arrow {
  transform: translateX(4px);
}

/* Logout */
.logout-btn {
  margin-top: var(--space-2);
}

@media (max-width: 640px) {
  .user-card {
    flex-direction: column;
    text-align: center;
  }

  .input-row {
    flex-direction: column;
  }

  .input-row .glass-btn {
    width: 100%;
  }
}
</style>

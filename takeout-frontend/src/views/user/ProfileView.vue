<template>
  <div class="profile-view">
    <!-- User card -->
    <div class="user-card">
      <div class="user-card-inner">
        <img :src="profile.avatar || '/images/placeholders/avatar-default.png'" class="user-avatar" @error="$event.target.src='/images/placeholders/avatar-default.png'" />
        <div class="user-info">
          <h2 class="user-name">{{ profile.nickname || profile.username || '未登录' }}</h2>
          <span class="user-role-badge" :class="'role-' + profile.role">{{ getRoleName(profile.role) }}</span>
          <p class="user-phone" v-if="profile.phone">{{ profile.phone }}</p>
        </div>
        <button class="edit-btn" @click="showEdit = true">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="16" height="16"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
        </button>
      </div>
    </div>

    <!-- Order shortcuts -->
    <div class="order-shortcuts card">
      <div class="shortcut-header">
        <h3>我的订单</h3>
        <router-link to="/user/orders" class="view-all">
          查看全部
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="14" height="14"><path d="m9 18 6-6-6-6"/></svg>
        </router-link>
      </div>
      <div class="shortcut-grid">
        <div class="shortcut-item" @click="navigateToOrders('PENDING')">
          <div class="shortcut-icon">⏳</div>
          <span>待支付</span>
        </div>
        <div class="shortcut-item" @click="navigateToOrders('DELIVERING')">
          <div class="shortcut-icon">🚚</div>
          <span>待配送</span>
        </div>
        <div class="shortcut-item" @click="navigateToOrders('COMPLETED')">
          <div class="shortcut-icon">✅</div>
          <span>已完成</span>
        </div>
        <div class="shortcut-item" @click="navigateToOrders('CANCELLED')">
          <div class="shortcut-icon">❌</div>
          <span>已取消</span>
        </div>
      </div>
    </div>

    <!-- Feature list -->
    <div class="feature-list card">
      <div v-if="authStore.userRole === 'USER' && (!merchantApp || merchantApp.status === 2)" class="feature-item" @click="showMerchantApply = true">
        <div class="fi-icon">🍳</div>
        <span class="fi-label">申请成为商家</span>
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="16" height="16"><path d="m9 18 6-6-6-6"/></svg>
      </div>
      <div v-if="merchantApp" class="feature-item">
        <div class="fi-icon">📋</div>
        <span class="fi-label">商家申请：<b :class="'status-' + merchantApp.status">{{ getAppStatusText(merchantApp.status) }}</b></span>
        <span v-if="merchantApp.status === 2 && merchantApp.adminNote" class="fi-action" @click="showRejectNote">查看</span>
      </div>
      <div class="feature-item" @click="$router.push('/user/address')">
        <div class="fi-icon">📍</div>
        <span class="fi-label">收货地址</span>
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="16" height="16"><path d="m9 18 6-6-6-6"/></svg>
      </div>
      <div class="feature-item" @click="showPassword = true">
        <div class="fi-icon">🔒</div>
        <span class="fi-label">修改密码</span>
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="16" height="16"><path d="m9 18 6-6-6-6"/></svg>
      </div>
      <div class="feature-item">
        <div class="fi-icon">🎁</div>
        <span class="fi-label">我的优惠</span>
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="16" height="16"><path d="m9 18 6-6-6-6"/></svg>
      </div>
      <div class="feature-item">
        <div class="fi-icon">💬</div>
        <span class="fi-label">客服中心</span>
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="16" height="16"><path d="m9 18 6-6-6-6"/></svg>
      </div>
    </div>

    <!-- Logout -->
    <button class="logout-btn" @click="handleLogout">退出登录</button>

    <!-- Edit modal -->
    <div v-if="showEdit" class="modal-overlay" @click.self="showEdit = false">
      <div class="modal">
        <h3>编辑资料</h3>
        <form @submit.prevent="handleUpdateProfile">
          <label>昵称</label>
          <input v-model="editForm.nickname" placeholder="昵称" />
          <label>手机号</label>
          <input v-model="editForm.phone" placeholder="手机号" />
          <label>头像URL</label>
          <input v-model="editForm.avatar" placeholder="头像链接" />
          <div class="form-actions">
            <button type="button" class="btn-cancel" @click="showEdit = false">取消</button>
            <button type="submit" class="btn-confirm">保存</button>
          </div>
        </form>
      </div>
    </div>

    <!-- Merchant apply modal -->
    <div v-if="showMerchantApply" class="modal-overlay" @click.self="showMerchantApply = false">
      <div class="modal">
        <h3>申请成为商家</h3>
        <form @submit.prevent="handleMerchantApply">
          <label>店铺名称</label>
          <input v-model="applyForm.shopName" placeholder="请输入店铺名称" required />
          <label>店铺地址</label>
          <input v-model="applyForm.shopAddress" placeholder="请输入店铺地址" required />
          <label>联系电话</label>
          <input v-model="applyForm.contactPhone" placeholder="请输入联系电话" required />
          <label>店铺描述</label>
          <input v-model="applyForm.description" placeholder="请描述您的店铺" />
          <div class="form-actions">
            <button type="button" class="btn-cancel" @click="showMerchantApply = false">取消</button>
            <button type="submit" class="btn-confirm">提交申请</button>
          </div>
        </form>
      </div>
    </div>

    <!-- Password modal -->
    <div v-if="showPassword" class="modal-overlay" @click.self="showPassword = false">
      <div class="modal">
        <h3>修改密码</h3>
        <form @submit.prevent="handleChangePassword">
          <label>原密码</label>
          <input type="password" v-model="pwdForm.oldPassword" placeholder="原密码" required />
          <label>新密码</label>
          <input type="password" v-model="pwdForm.newPassword" placeholder="新密码" required />
          <div class="form-actions">
            <button type="button" class="btn-cancel" @click="showPassword = false">取消</button>
            <button type="submit" class="btn-confirm">确认</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useCartStore } from '@/stores/cart'
import { getProfile, updateProfile, changePassword, submitMerchantApplication, getMerchantApplicationStatus } from '@/api/user'

const router = useRouter()
const authStore = useAuthStore()
const cartStore = useCartStore()

const profile = ref({})
const showEdit = ref(false)
const showPassword = ref(false)
const showMerchantApply = ref(false)
const merchantApp = ref(null)
const applyForm = ref({ shopName: '', shopAddress: '', contactPhone: '', description: '' })
const editForm = ref({ nickname: '', phone: '', avatar: '' })
const pwdForm = ref({ oldPassword: '', newPassword: '' })

const getRoleName = (role) => {
  const map = { USER: '普通用户', MERCHANT: '商家', RIDER: '骑手', ADMIN: '管理员' }
  return map[role] || role
}

const getAppStatusText = (status) => {
  const map = { 0: '待审核', 1: '已通过', 2: '已拒绝' }
  return map[status] || '未知'
}

const handleMerchantApply = async () => {
  try {
    await submitMerchantApplication(applyForm.value)
    showMerchantApply.value = false
    alert('申请已提交，请等待管理员审核')
    loadMerchantApp()
  } catch (e) {
    alert(e?.response?.data?.message || e?.message || '提交失败')
  }
}

const showRejectNote = () => {
  alert('拒绝原因：' + (merchantApp.value?.adminNote || '无'))
}

const loadMerchantApp = async () => {
  try {
    const res = await getMerchantApplicationStatus()
    merchantApp.value = res.data || null
  } catch {
    merchantApp.value = null
  }
}

const navigateToOrders = (status) => {
  router.push(`/user/orders?status=${status}`)
}

const loadProfile = async () => {
  try {
    const res = await getProfile()
    profile.value = res.data || {}
    authStore.updateUserInfo(profile.value)
  } catch {
    profile.value = authStore.userInfo || {}
  }
}

const handleUpdateProfile = async () => {
  try {
    await updateProfile(editForm.value)
    showEdit.value = false
    loadProfile()
  } catch (e) {
    alert(e?.response?.data?.message || '更新失败')
  }
}

const handleChangePassword = async () => {
  try {
    await changePassword(pwdForm.value)
    showPassword.value = false
    pwdForm.value = { oldPassword: '', newPassword: '' }
    alert('密码修改成功')
  } catch (e) {
    alert(e?.response?.data?.message || '修改失败')
  }
}

const handleLogout = () => {
  authStore.logout()
  cartStore.clearCart()
  router.push('/auth/login')
}

onMounted(() => {
  loadProfile()
  loadMerchantApp()
  editForm.value = {
    nickname: authStore.userInfo?.nickname || '',
    phone: authStore.userInfo?.phone || '',
    avatar: authStore.userInfo?.avatar || ''
  }
})
</script>

<style scoped>
.profile-view {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

/* User card */
.user-card {
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-accent) 100%);
  border-radius: var(--radius-card);
  padding: var(--spacing-xl);
}

.user-card-inner {
  display: flex;
  align-items: center;
  gap: var(--spacing-lg);
}

.user-avatar {
  width: 64px;
  height: 64px;
  border-radius: var(--radius-full);
  object-fit: cover;
  border: 3px solid rgba(255,255,255,0.5);
  flex-shrink: 0;
}

.user-info {
  flex: 1;
}

.user-name {
  font-family: var(--font-heading);
  font-size: var(--font-size-lg);
  font-weight: 700;
  margin: 0 0 4px 0;
  color: #fff;
}

.user-role-badge {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 10px;
  font-size: var(--font-size-xs);
  font-weight: 500;
  background: rgba(0,0,0,0.1);
  color: var(--color-text-primary);
}

.role-ADMIN { background: rgba(255,77,79,0.2); color: #a8071a; }
.role-MERCHANT { background: rgba(255,107,0,0.2); color: #ad4e00; }

.user-phone {
  font-size: var(--font-size-sm);
  margin: 4px 0 0 0;
  color: rgba(255,255,255,0.7);
}

.edit-btn {
  width: 36px;
  height: 36px;
  border-radius: var(--radius-full);
  background: rgba(0,0,0,0.08);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: background 0.2s;
  flex-shrink: 0;
}

.edit-btn:hover {
  background: rgba(0,0,0,0.15);
}

.edit-btn svg {
  color: var(--color-text-primary);
}

/* Order shortcuts */
.order-shortcuts {
  padding: var(--spacing-lg);
}

.shortcut-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-lg);
}

.shortcut-header h3 {
  margin: 0;
  font-size: var(--font-size-md);
  font-weight: 600;
}

.view-all {
  display: flex;
  align-items: center;
  gap: 2px;
  font-size: var(--font-size-sm);
  color: var(--color-text-hint);
  text-decoration: none;
}

.shortcut-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--spacing-md);
}

.shortcut-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  transition: transform 0.2s;
}

.shortcut-item:hover {
  transform: translateY(-2px);
}

.shortcut-icon {
  width: 44px;
  height: 44px;
  border-radius: var(--radius-full);
  background: var(--color-surface-warm);
  border: 1px solid var(--color-divider);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
}

.shortcut-item span {
  font-size: var(--font-size-xs);
  color: var(--color-text-secondary);
}

/* Feature list */
.feature-list {
  overflow: hidden;
}

.feature-item {
  display: flex;
  align-items: center;
  padding: var(--spacing-lg);
  border-bottom: 1px solid var(--color-divider);
  cursor: pointer;
  transition: background 0.15s;
}

.feature-item:last-child {
  border-bottom: none;
}

.feature-item:hover {
  background: var(--color-bg-hover);
}

.fi-icon {
  font-size: 20px;
  margin-right: var(--spacing-md);
}

.fi-label {
  flex: 1;
  font-size: var(--font-size-base);
}

.fi-label b.status-0 { color: var(--color-warning); }
.fi-label b.status-1 { color: var(--color-success); }
.fi-label b.status-2 { color: var(--color-error); }

.fi-action {
  font-size: var(--font-size-sm);
  color: var(--color-accent);
  cursor: pointer;
}

.feature-item svg {
  color: var(--color-text-hint);
}

/* Logout */
.logout-btn {
  width: 100%;
  padding: 14px;
  border: 1px solid var(--color-error);
  border-radius: var(--radius-lg);
  background: var(--color-bg-card);
  color: var(--color-error);
  font-size: var(--font-size-base);
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.logout-btn:hover {
  background: #F5EDEB;
}

/* Modal */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(45, 35, 25, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal {
  background: var(--color-bg-card);
  border-radius: var(--radius-xl);
  padding: 28px;
  width: 420px;
  max-width: 90vw;
  box-shadow: var(--shadow-lg);
}

.modal h3 {
  margin: 0 0 20px 0;
  font-family: var(--font-heading);
  font-size: var(--font-size-lg);
  font-weight: 700;
}

.modal label {
  display: block;
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  margin-bottom: 4px;
  margin-top: var(--spacing-md);
}

.modal label:first-of-type {
  margin-top: 0;
}

.modal input {
  width: 100%;
  padding: 10px 14px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  font-size: var(--font-size-base);
  box-sizing: border-box;
  transition: border-color 0.2s;
}

.modal input:focus {
  border-color: var(--color-primary);
}

.form-actions {
  display: flex;
  gap: var(--spacing-md);
  margin-top: var(--spacing-xl);
}

.btn-cancel {
  flex: 1;
  padding: 10px;
  background: var(--color-bg-page);
  color: var(--color-text-secondary);
  border: none;
  border-radius: var(--radius-md);
  font-size: var(--font-size-base);
  cursor: pointer;
}

.btn-confirm {
  flex: 1;
  padding: 10px;
  background: var(--color-primary);
  color: #fff;
  border: none;
  border-radius: var(--radius-md);
  font-size: var(--font-size-base);
  font-weight: 700;
  cursor: pointer;
  transition: background var(--transition-smooth);
}

.btn-confirm:hover {
  background: var(--color-primary-dark);
}
</style>

<template>
  <div class="profile-view">
    <!-- 用户信息 -->
    <div class="user-info-section">
      <div class="user-avatar">
        <img v-if="profile.avatar" :src="profile.avatar" alt="avatar" />
        <span v-else class="avatar-emoji">👤</span>
      </div>
      <div class="user-details">
        <h2>{{ profile.nickname || profile.username || '未登录' }}</h2>
        <p class="user-role">{{ getRoleName(profile.role) }}</p>
        <p class="user-phone" v-if="profile.phone">{{ profile.phone }}</p>
      </div>
      <button class="edit-profile-btn" @click="showEdit = true">编辑</button>
    </div>

    <!-- 订单管理 -->
    <div class="order-management">
      <div class="section-header">
        <h3>订单管理</h3>
        <router-link to="/user/orders" class="view-all">查看全部</router-link>
      </div>
      <div class="order-types">
        <div class="order-type-item" @click="navigateToOrders('PENDING')">
          <div class="order-icon">⏳</div>
          <span>待支付</span>
        </div>
        <div class="order-type-item" @click="navigateToOrders('DELIVERING')">
          <div class="order-icon">🚚</div>
          <span>待配送</span>
        </div>
        <div class="order-type-item" @click="navigateToOrders('COMPLETED')">
          <div class="order-icon">✅</div>
          <span>已完成</span>
        </div>
        <div class="order-type-item" @click="navigateToOrders('CANCELLED')">
          <div class="order-icon">❌</div>
          <span>已取消</span>
        </div>
      </div>
    </div>

    <!-- 功能列表 -->
    <div class="feature-list">
      <div v-if="authStore.userRole === 'USER' && (!merchantApp || merchantApp.status === 2)" class="feature-item" @click="showMerchantApply = true">
        <div class="feature-icon">🍳</div>
        <span>申请成为商家</span>
        <div class="feature-arrow">›</div>
      </div>
      <div v-if="merchantApp" class="feature-item">
        <div class="feature-icon">📋</div>
        <span>商家申请状态：<b :class="'status-' + merchantApp.status">{{ getAppStatusText(merchantApp.status) }}</b></span>
        <div class="feature-arrow" v-if="merchantApp.status === 2 && merchantApp.adminNote" @click="showRejectNote">查看</div>
      </div>
      <div class="feature-item" @click="$router.push('/user/address')">
        <div class="feature-icon">📍</div>
        <span>收货地址</span>
        <div class="feature-arrow">›</div>
      </div>
      <div class="feature-item" @click="showPassword = true">
        <div class="feature-icon">🔒</div>
        <span>修改密码</span>
        <div class="feature-arrow">›</div>
      </div>
      <div class="feature-item">
        <div class="feature-icon">🎁</div>
        <span>我的优惠</span>
        <div class="feature-arrow">›</div>
      </div>
      <div class="feature-item">
        <div class="feature-icon">💬</div>
        <span>客服中心</span>
        <div class="feature-arrow">›</div>
      </div>
    </div>

    <!-- 退出登录按钮 -->
    <button class="logout-btn" @click="handleLogout">退出登录</button>

    <!-- 编辑资料弹窗 -->
    <div v-if="showEdit" class="modal-overlay" @click.self="showEdit = false">
      <div class="modal glass-panel">
        <h3>编辑资料</h3>
        <form @submit.prevent="handleUpdateProfile">
          <label>昵称</label>
          <input v-model="editForm.nickname" placeholder="昵称" />
          <label>手机号</label>
          <input v-model="editForm.phone" placeholder="手机号" />
          <label>头像URL</label>
          <input v-model="editForm.avatar" placeholder="头像链接" />
          <div class="form-actions">
            <button type="button" @click="showEdit = false">取消</button>
            <button type="submit" class="primary">保存</button>
          </div>
        </form>
      </div>
    </div>

    <!-- 商家入驻弹窗 -->
    <div v-if="showMerchantApply" class="modal-overlay" @click.self="showMerchantApply = false">
      <div class="modal glass-panel">
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
            <button type="button" @click="showMerchantApply = false">取消</button>
            <button type="submit" class="primary">提交申请</button>
          </div>
        </form>
      </div>
    </div>

    <!-- 修改密码弹窗 -->
    <div v-if="showPassword" class="modal-overlay" @click.self="showPassword = false">
      <div class="modal glass-panel">
        <h3>修改密码</h3>
        <form @submit.prevent="handleChangePassword">
          <label>原密码</label>
          <input type="password" v-model="pwdForm.oldPassword" placeholder="原密码" required />
          <label>新密码</label>
          <input type="password" v-model="pwdForm.newPassword" placeholder="新密码" required />
          <div class="form-actions">
            <button type="button" @click="showPassword = false">取消</button>
            <button type="submit" class="primary">确认</button>
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
  const map = { USER: '用户', MERCHANT: '商家', RIDER: '骑手', ADMIN: '管理员' }
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
.profile-view { padding-bottom: 20px; }
.user-info-section {
  display: flex; align-items: center; padding: 30px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff; border-radius: 12px; position: relative;
}
.user-avatar {
  width: 70px; height: 70px; border-radius: 50%; background: rgba(255,255,255,0.2);
  display: flex; align-items: center; justify-content: center; margin-right: 20px;
  overflow: hidden;
}
.user-avatar img { width: 100%; height: 100%; object-fit: cover; }
.avatar-emoji { font-size: 36px; }
.user-details h2 { margin: 0 0 5px 0; font-size: 20px; font-weight: bold; }
.user-role { margin: 0; font-size: 14px; opacity: 0.8; }
.user-phone { margin: 4px 0 0; font-size: 13px; opacity: 0.7; }
.edit-profile-btn {
  position: absolute; right: 16px; top: 50%; transform: translateY(-50%);
  background: rgba(255,255,255,0.2); color: #fff; border: 1px solid rgba(255,255,255,0.4);
  padding: 6px 14px; border-radius: 20px; cursor: pointer; font-size: 13px;
}
.edit-profile-btn:hover { background: rgba(255,255,255,0.3); }
.order-management {
  margin-top: 10px; padding: 15px 20px; background-color: #fff; border-radius: 12px;
}
.section-header {
  display: flex; justify-content: space-between; align-items: center; margin-bottom: 15px;
}
.section-header h3 { margin: 0; font-size: 16px; font-weight: bold; }
.view-all { font-size: 14px; color: #667eea; text-decoration: none; }
.order-types { display: flex; justify-content: space-around; }
.order-type-item {
  display: flex; flex-direction: column; align-items: center;
  cursor: pointer; transition: transform 0.2s;
}
.order-type-item:hover { transform: scale(1.05); }
.order-icon {
  width: 50px; height: 50px; border-radius: 50%; background-color: #f5f5f5;
  display: flex; align-items: center; justify-content: center; font-size: 22px; margin-bottom: 5px;
}
.order-type-item span { font-size: 12px; color: #666; }
.feature-list {
  margin-top: 10px; background-color: #fff; border-radius: 12px; overflow: hidden;
}
.feature-item {
  display: flex; align-items: center; padding: 15px 20px;
  border-bottom: 1px solid #f0f0f0; cursor: pointer; transition: background 0.2s;
}
.feature-item:hover { background: #f9f9f9; }
.feature-item:last-child { border-bottom: none; }
.feature-icon { font-size: 20px; margin-right: 15px; }
.feature-item span { flex: 1; font-size: 16px; }
.feature-arrow { font-size: 20px; color: #999; }
.logout-btn {
  width: 90%; margin: 20px auto; display: block; padding: 12px;
  border: 1px solid #ff6b00; border-radius: 25px; background-color: #fff;
  color: #ff6b00; font-size: 16px; font-weight: bold; cursor: pointer; transition: all 0.2s;
}
.logout-btn:hover { background-color: #fff3e6; }
.status-0 { color: #ffa502; }
.status-1 { color: #2ed573; }
.status-2 { color: #ff4757; }

/* Modal */
.modal-overlay {
  position: fixed; top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.5); display: flex; align-items: center;
  justify-content: center; z-index: 1000;
}
.modal { width: 90%; max-width: 420px; padding: 24px; }
.modal h3 { margin: 0 0 20px 0; }
.modal label { display: block; font-size: 13px; color: #666; margin-bottom: 4px; margin-top: 10px; }
.modal input {
  width: 100%; padding: 10px 12px; border: 1px solid #ddd;
  border-radius: 8px; font-size: 14px; box-sizing: border-box; outline: none;
}
.modal input:focus { border-color: var(--primary-color); }
.form-actions { display: flex; gap: 10px; justify-content: flex-end; margin-top: 20px; }
.form-actions button {
  padding: 10px 20px; border: 1px solid #ddd; border-radius: 8px;
  background: #fff; cursor: pointer;
}
.form-actions button.primary {
  background: var(--primary-color); color: #fff; border-color: var(--primary-color);
}
</style>

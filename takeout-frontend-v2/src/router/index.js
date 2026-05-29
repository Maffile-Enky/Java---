import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('../views/HomeView.vue')
    },
    {
      path: '/auth/login',
      name: 'login',
      component: () => import('../views/auth/LoginView.vue')
    },
    {
      path: '/auth/register',
      name: 'register',
      component: () => import('../views/auth/RegisterView.vue')
    },
    {
      path: '/user',
      component: () => import('../views/user/UserLayout.vue'),
      meta: { requiresAuth: true },
      children: [
        { path: 'restaurants', name: 'restaurants', component: () => import('../views/user/RestaurantsView.vue') },
        { path: 'restaurants/:id', name: 'restaurantDetail', component: () => import('../views/user/RestaurantDetailView.vue') },
        { path: 'cart', name: 'cart', component: () => import('../views/user/CartView.vue') },
        { path: 'orders', name: 'orders', component: () => import('../views/user/OrdersView.vue') },
        { path: 'orders/:id', name: 'orderDetail', component: () => import('../views/user/OrderDetailView.vue') },
        { path: 'payment/:id', name: 'payment', component: () => import('../views/user/PaymentView.vue') },
        { path: 'profile', name: 'profile', component: () => import('../views/user/ProfileView.vue') },
        { path: 'address', name: 'address', component: () => import('../views/user/AddressView.vue') }
      ]
    },
    {
      path: '/merchant',
      component: () => import('../views/merchant/MerchantLayout.vue'),
      meta: { requiresAuth: true, roles: ['MERCHANT', 'ADMIN'] },
      children: [
        { path: '', name: 'merchantDashboard', component: () => import('../views/merchant/DashboardView.vue') },
        { path: 'dishes', name: 'merchantDishes', component: () => import('../views/merchant/DishManageView.vue') },
        { path: 'orders', name: 'merchantOrders', component: () => import('../views/merchant/MerchantOrdersView.vue') },
        { path: 'settings', name: 'merchantSettings', component: () => import('../views/merchant/SettingsView.vue') }
      ]
    },
    {
      path: '/admin',
      component: () => import('../views/admin/AdminLayout.vue'),
      meta: { requiresAuth: true, roles: ['ADMIN'] },
      children: [
        { path: '', name: 'adminDashboard', component: () => import('../views/admin/DashboardView.vue') },
        { path: 'users', name: 'adminUsers', component: () => import('../views/admin/UserManageView.vue') },
        { path: 'applications', name: 'adminApplications', component: () => import('../views/admin/ApplicationManageView.vue') },
        { path: 'merchants', name: 'adminMerchants', component: () => import('../views/admin/MerchantManageView.vue') }
      ]
    },
    {
      path: '/user/notifications',
      name: 'notifications',
      component: () => import('../views/user/NotificationView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/user/orders/:id/track',
      name: 'deliveryTracking',
      component: () => import('../views/user/DeliveryTrackingView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/rider',
      component: () => import('../views/rider/RiderLayout.vue'),
      meta: { requiresAuth: true, roles: ['RIDER', 'MERCHANT', 'ADMIN'] },
      children: [
        { path: '', name: 'riderDashboard', component: () => import('../views/rider/RiderDashboardView.vue') },
        { path: 'tasks', name: 'riderTasks', component: () => import('../views/rider/RiderTasksView.vue') },
        { path: 'tasks/:taskNo', name: 'riderTaskDetail', component: () => import('../views/rider/RiderTaskDetailView.vue') }
      ]
    },
    {
      path: '/rider/register',
      name: 'riderRegister',
      component: () => import('../views/rider/RiderRegisterView.vue'),
      meta: { requiresAuth: true }
    },
    { path: '/:pathMatch(.*)*', name: 'notFound', redirect: '/' }
  ]
})

// Navigation guard
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || 'null')
  const role = userInfo?.role || 'USER'

  if (to.meta.requiresAuth && !token) {
    next('/auth/login')
    return
  }

  if (to.meta.roles && !to.meta.roles.includes(role)) {
    next('/')
    return
  }

  if ((to.path === '/auth/login' || to.path === '/auth/register') && token) {
    next('/')
    return
  }

  next()
})

export default router

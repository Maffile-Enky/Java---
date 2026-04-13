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
      path: '/restaurants',
      name: 'restaurants',
      component: () => import('../views/user/RestaurantsView.vue')
    },
    {
      path: '/restaurant/:id',
      name: 'restaurantDetail',
      component: () => import('../views/user/RestaurantDetailView.vue')
    },
    {
      path: '/cart',
      name: 'cart',
      component: () => import('../views/user/CartView.vue')
    },
    {
      path: '/orders',
      name: 'orders',
      component: () => import('../views/user/OrdersView.vue')
    },
    {
      path: '/order/:id',
      name: 'orderDetail',
      component: () => import('../views/user/OrderDetailView.vue')
    },
    {
      path: '/profile',
      name: 'profile',
      component: () => import('../views/user/ProfileView.vue')
    }
  ]
})

export default router

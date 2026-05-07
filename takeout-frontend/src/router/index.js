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
      children: [
        {
          path: 'restaurants',
          name: 'restaurants',
          component: () => import('../views/user/RestaurantsView.vue')
        },
        {
          path: 'restaurants/:id',
          name: 'restaurantDetail',
          component: () => import('../views/user/RestaurantDetailView.vue')
        },
        {
          path: 'cart',
          name: 'cart',
          component: () => import('../views/user/CartView.vue')
        },
        {
          path: 'orders',
          name: 'orders',
          component: () => import('../views/user/OrdersView.vue')
        },
        {
          path: 'orders/:id',
          name: 'orderDetail',
          component: () => import('../views/user/OrderDetailView.vue')
        },
        {
          path: 'profile',
          name: 'profile',
          component: () => import('../views/user/ProfileView.vue')
        },
        {
          path: 'address',
          name: 'address',
          component: () => import('../views/user/AddressView.vue')
        }
      ]
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'notFound',
      redirect: '/'
    }
  ]
})

// Navigation guard
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.path.startsWith('/user') && !token) {
    next('/auth/login')
  } else if ((to.path === '/auth/login' || to.path === '/auth/register') && token) {
    next('/')
  } else {
    next()
  }
})

export default router

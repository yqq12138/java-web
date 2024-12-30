import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/auth/Login.vue'
import Register from '../views/auth/Register.vue'
import AdList from '../views/advertisement/AdList.vue'
import AdminAdList from '../views/admin/AdminAdList.vue'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/register',
    name: 'Register',
    component: Register
  },
  {
    path: '/admin',
    name: 'AdminDashboard',
    component: () => import('../views/admin/Dashboard.vue'),
    meta: { requiresAuth: true, roles: ['ADMIN'] },
    children: [
      {
        path: 'applications',
        name: 'AdminApplications',
        component: () => import('../views/admin/AdminApplicationList.vue')
      },
      {
        path: 'ads',
        name: 'AdminAds',
        component: () => import('../views/admin/AdminAdList.vue')
      },
      {
        path: 'stats',
        name: 'AdStats',
        component: () => import('../views/advertisement/AdStats.vue')
      }
    ]
  },
  {
    path: '/ad-application',
    name: 'AdApplication',
    component: () => import('../views/advertisement/AdApplication.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/advertiser',
    name: 'AdvertiserDashboard',
    component: () => import('../views/advertisement/AdDashboard.vue'),
    meta: { requiresAuth: true, roles: ['ADVERTISER'] },
    children: [
      {
        path: 'ads',
        name: 'AdList',
        component: () => import('../views/advertisement/AdList.vue')
      },
      {
        path: 'stats',
        name: 'AdvertiserStats',
        component: () => import('../views/advertisement/AdStats.vue')
      },
      {
        path: 'ad-application',
        name: 'AdApplication',
        component: () => import('../views/advertisement/AdApplication.vue'),
        meta: { requiresAuth: false }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 安全地获取用户数据
  let user = null;
  try {
    const userStr = localStorage.getItem('user')
    user = userStr ? JSON.parse(userStr) : null
  } catch (error) {
    localStorage.removeItem('user')  // 清除无效的数据
  }
  
  if (to.meta.requiresAuth && !user) {
    next('/login')
  } else if (to.meta.roles && user && !to.meta.roles.includes(user.role)) {
    // 根据角色重定向到正确的页面
    if (user.role === 'ADMIN') {
      next('/admin/ads')
    } else if (user.role === 'ADVERTISER') {
      next('/advertiser/ads')
    } else {
      next('/login')
    }
  } else {
    next()
  }
})

export default router 
import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', redirect: '/index.html' },
    { path: '/login.html', name: 'Login', component: () => import('@/views/LoginView.vue'), meta: { public: true } },
    { path: '/index.html', name: 'Home', component: () => import('@/views/HomeView.vue') },
    { path: '/attorney.html', name: 'Attorney', component: () => import('@/views/AttorneyView.vue') },
    { path: '/service.html', name: 'Service', component: () => import('@/views/ServiceView.vue') },
    { path: '/case.html', name: 'Case', component: () => import('@/views/CaseView.vue') },
    { path: '/share.html', name: 'Share', component: () => import('@/views/ShareView.vue') },
    { path: '/consultation.html', name: 'Consultation', component: () => import('@/views/ConsultationView.vue') },
    { path: '/banner.html', name: 'Banner', component: () => import('@/views/BannerView.vue') },
    { path: '/site.html', name: 'Site', component: () => import('@/views/SiteView.vue') },
    { path: '/admin-user.html', name: 'AdminUser', component: () => import('@/views/AdminUserView.vue') },
  ],
})

router.beforeEach(async (to, _from, next) => {
  const authStore = useAuthStore()

  if (to.meta.public) {
    next()
    return
  }

  await authStore.fetchMe()

  if (!authStore.isLoggedIn) {
    const redirectPath = to.fullPath
    next({ name: 'Login', query: { redirect: redirectPath } })
  } else {
    next()
  }
})

router.onError((error) => {
  console.error('路由錯誤:', error)
})

export default router
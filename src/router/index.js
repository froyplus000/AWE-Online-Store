import { createRouter, createWebHistory } from 'vue-router'
import LoginForm from '@/components/LoginForm.vue'
import SignUpForm from '@/components/SignUpForm.vue'

// For other pages
import Catalogue from '@/views/Catalogue.vue'
import AdminDashboard from '@/views/AdminDashboard.vue'

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', component: LoginForm },
  { path: '/signup', component: SignUpForm },
  { path: '/catalogue', component: Catalogue },
  { path: '/admin/dashboard', component: AdminDashboard },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

//  Navigation Guard
router.beforeEach((to, from, next) => {
  const publicPages = ['/login', '/signup']
  const user = JSON.parse(localStorage.getItem('activeUser'))

  if (!publicPages.includes(to.path)) {
    if (!user) return next('/login')

    // block admin from accessing customer route
    if (to.path === '/catalogue' && user.role !== 'CUSTOMER') return next('/admin/dashboard')

    // block customer from accessing admin route
    if (to.path.startsWith('/admin') && user.role !== 'ADMIN') return next('/catalogue')
  }

  next()
})

export default router

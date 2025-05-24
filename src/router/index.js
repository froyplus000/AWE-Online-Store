import { createRouter, createWebHistory } from 'vue-router'

// Components
import LoginForm from '@/components/LoginForm.vue'
import SignUpForm from '@/components/SignUpForm.vue'

// Views
import Catalogue from '@/views/Catalogue.vue'
import AdminDashboard from '@/views/AdminDashboard.vue'
import ProductDetails from '@/views/ProductDetails.vue'
import Checkout from '@/views/Checkout.vue'
import Payment from '@/views/Payment.vue'


const routes = [
  { path: '/', redirect: '/catalogue' },
  { path: '/login', component: LoginForm },
  { path: '/signup', component: SignUpForm },
  { path: '/catalogue', component: Catalogue },
  { path: '/admin/dashboard', component: AdminDashboard },
  { path: '/product-details/:id', component: ProductDetails, props: true },
  { path: '/checkout', component: Checkout },
  { path: '/payment', component: Payment },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// Navigation Guard
router.beforeEach((to, from, next) => {
  const user = JSON.parse(localStorage.getItem('activeUser'))

  const publicPaths = ['/login', '/signup', '/catalogue']
  const isProductDetailsPage = /^\/product-details\/\d+$/.test(to.path)

  const isPublic = publicPaths.includes(to.path) || isProductDetailsPage

  if (!isPublic) {
    if (!user) return next('/login')

    if (to.path === '/catalogue' && user.role !== 'CUSTOMER') return next('/admin/dashboard')
    if (to.path.startsWith('/admin') && user.role !== 'ADMIN') return next('/catalogue')
  }

  // Redirect logged-in admin away from catalogue
  if (to.path === '/catalogue' && user && user.role === 'ADMIN') {
    return next('/admin/dashboard')
  }

  next()
})

export default router

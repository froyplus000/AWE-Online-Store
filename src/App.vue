<template>
  <div class="min-h-screen bg-gray-100 flex flex-col">
    <!-- Header -->
    <header class="bg-white border-b shadow-sm">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4 flex justify-between items-center">
        <h1 class="text-xl font-bold text-indigo-600">AWE Electronics</h1>
        <nav class="space-x-4 text-sm">
          <router-link to="/" class="hover:text-indigo-600">Home</router-link>
          <router-link to="/cart" class="hover:text-indigo-600">MyCart</router-link>
          <router-link v-if="!isLoggedIn" to="/login" class="hover:text-indigo-600">Login</router-link>
          <router-link v-if="!isLoggedIn" to="/signup" class="hover:text-indigo-600">Sign Up</router-link>
          <button v-if="isLoggedIn" @click="logout" class="hover:text-indigo-600">Logout</button>
        </nav>
      </div>
    </header>

    <main class="flex-grow p-4">
      <router-view />
    </main>

    <footer class="p-4 bg-gray-200 text-center text-sm">
      <div v-if="!isLoggedIn">
        Already have an account?
        <router-link to="/login" class="text-blue-600 hover:underline">Log in</router-link>
      </div>
      <div class="mb-2 sm:mb-0">&copy; 2025 AWE Electronics</div>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'

const isLoggedIn = ref(false)
const router = useRouter()

function checkAuth() {
  isLoggedIn.value = !!localStorage.getItem('activeUser')
}

onMounted(() => {
  checkAuth()

  // Listen for cross-tab login/logout
  window.addEventListener('storage', checkAuth)

  // Also recheck periodically for same-tab changes
  const interval = setInterval(checkAuth, 500)

  onUnmounted(() => {
    window.removeEventListener('storage', checkAuth)
    clearInterval(interval)
  })
})

function logout() {
  localStorage.removeItem('activeUser')
  isLoggedIn.value = false
  router.push('/login')
}
</script>

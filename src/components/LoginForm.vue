<template>
  <div class="max-w-md mx-auto mt-10 p-6 bg-white rounded shadow">
    <h2 class="text-2xl font-semibold mb-4">Log In</h2>
    <form @submit.prevent="handleLogin">
      <input v-model="email" placeholder="Email" class="input" type="email" required />
      <input v-model="password" placeholder="Password" class="input" type="password" required />
      <button type="submit" class="btn bg-blue-600">Log In</button>
    </form>
    <p v-if="error" class="text-red-500 mt-2">{{ error }}</p>
    <p class="mt-4 text-sm text-center">
      Don’t have an account? <router-link to="/signup" class="text-blue-500 underline">Create one</router-link>
    </p>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { loginUser } from '@/services/authService'

const email = ref('')
const password = ref('')
const error = ref('')
const router = useRouter()

async function handleLogin() {
  try {
    const user = await loginUser({ email: email.value, password: password.value })
    localStorage.setItem('activeUser', JSON.stringify(user))

    // Role-based redirect
    if (user.role === 'ADMIN') {
      router.push('/admin/dashboard')
    } else if (user.role === 'CUSTOMER') {
      router.push('/catalogue')
    } else {
      error.value = 'Invalid user role'
    }
  } catch {
    error.value = 'Invalid credentials'
  }
}
</script>

<style scoped>
.input {
  @apply block w-full mb-3 p-2 border rounded;
}
.btn {
  @apply text-white px-4 py-2 rounded w-full;
}
</style>

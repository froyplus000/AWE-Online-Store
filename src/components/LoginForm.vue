<template>
  <div class="max-w-md mx-auto mt-10 p-6 bg-white rounded shadow">
    <h2 class="text-2xl font-semibold mb-4">Log In</h2>

    <form @submit.prevent="handleLogin">
      <input v-model="email" placeholder="Email" class="input" type="email" required />
      <input v-model="password" placeholder="Password" class="input" type="password" required />

      <button type="submit" class="btn bg-blue-600" :disabled="isLoading">
        {{ isLoading ? 'Logging in...' : 'Log In' }}
      </button>
    </form>

    <p v-if="error" class="text-red-500 mt-2">{{ error }}</p>

    <p class="mt-4 text-sm text-center">
      Don’t have an account?
      <router-link to="/signup" class="text-blue-500 underline">Create one</router-link>
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
const isLoading = ref(false)
const router = useRouter()

async function handleLogin() {
  isLoading.value = true
  error.value = ''

  try {
    const { token, role, email: userEmail } = await loginUser({
      email: email.value,
      password: password.value
    })

    // Store token and basic user info
    localStorage.setItem('authToken', token)
    localStorage.setItem('userRole', role)
    localStorage.setItem('userEmail', userEmail)

    // Redirect based on role
    if (role === 'ADMIN') {
      router.push('/admin/dashboard')
    } else if (role === 'CUSTOMER') {
      router.push('/catalogue')
    } else {
      error.value = 'Unknown role.'
    }
  } catch (err) {
    error.value = err.message || 'Login failed'
  } finally {
    isLoading.value = false
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

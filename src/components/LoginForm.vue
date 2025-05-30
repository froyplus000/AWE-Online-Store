<template>
  <div class="max-w-md mx-auto mt-10 p-6 bg-white rounded shadow">
    <h2 class="text-2xl font-semibold mb-4">Log In</h2>
    <form @submit.prevent="handleLogin">
      <input v-model="email" placeholder="Email" class="input" type="email" required />
      <input v-model="password" placeholder="Password" class="input" type="password" required />
      <button type="submit" :disabled="isLoading" class="btn bg-blue-600 disabled:bg-gray-400">
        {{ isLoading ? 'Logging in...' : 'Log In' }}
      </button>
    </form>
    <p v-if="error" class="text-red-500 mt-2">{{ error }}</p>
    <p class="mt-4 text-sm text-center">
      Don't have an account? <router-link to="/signup" class="text-blue-500 underline">Create one</router-link>
    </p>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const email = ref('')
const password = ref('')
const error = ref('')
const isLoading = ref(false)
const router = useRouter()

async function handleLogin() {
  error.value = ''
  isLoading.value = true
  
  try {
    const res = await fetch('http://localhost:8080/api/users/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      credentials: 'include',
      body: JSON.stringify({
        email: email.value,
        password: password.value
      })
    })

    if (!res.ok) {
      throw new Error('Invalid credentials')
    }

    const response = await res.json()

    if (!response.user || !response.token) {
      throw new Error('Invalid server response')
    }

    const { user, token } = response

    if (!user.id) {
      throw new Error('Invalid user data')
    }

    // Store authentication data
    localStorage.setItem('activeUser', JSON.stringify(user))
    localStorage.setItem('token', token)

    // Redirect based on role
    if (user.role === 'ADMIN') {
      router.push('/admin/dashboard')
    } else {
      router.push('/catalogue')
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

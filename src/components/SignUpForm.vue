<template>
  <div class="max-w-md mx-auto mt-10 p-6 bg-white rounded shadow">
    <h2 class="text-2xl font-semibold mb-4">Customer Sign Up</h2>
    <form @submit.prevent="handleRegister">
      <input v-model="fullName" placeholder="Full Name" class="input" required />
      <input v-model="email" placeholder="Email" class="input" type="email" required />
      <input v-model="password" placeholder="Password" class="input" type="password" required />
      <input v-model="confirmPassword" placeholder="Confirm Password" class="input" type="password" required />
      <button type="submit" class="btn bg-blue-600">Register</button>
    </form>
    <p v-if="message" :class="messageClass" class="mt-2">{{ message }}</p>
    <p class="mt-4 text-sm text-center">
      Already have an account? <router-link to="/login" class="text-blue-500 underline">Log In</router-link>
    </p>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { registerCustomer } from '@/services/authService'

const router = useRouter()
const fullName = ref('')
const email = ref('')
const password = ref('')
const confirmPassword = ref('')
const message = ref('')
const messageClass = ref('text-green-500')

async function handleRegister() {
  if (password.value !== confirmPassword.value) {
    message.value = 'Passwords do not match'
    messageClass.value = 'text-red-500'
    return
  }

  try {
    await registerCustomer({
      fullName: fullName.value,
      email: email.value,
      password: password.value
    })
    message.value = 'Registration successful! You can now log in.'
    messageClass.value = 'text-green-500'
    setTimeout(() => router.push('/login'), 2000)
  } catch (err) {
    message.value = err.message || 'An error occurred.'
    messageClass.value = 'text-red-500'
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

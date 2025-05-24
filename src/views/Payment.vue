<template>
    <div class="max-w-3xl mx-auto text-center p-10">
      <h2 class="text-2xl font-bold mb-4 text-gray-800">Thank You!</h2>
      <p class="text-gray-600 mb-6">Your order has been placed successfully.</p>
      <router-link to="/catalogue">
        <button class="bg-indigo-500 hover:bg-indigo-600 text-white px-5 py-2 rounded">
          Back to Store
        </button>
      </router-link>
    </div>
  </template>
  
  <script>
  export default {
    name: 'Payment',
    mounted() {
      // Clear the cart after "payment"
      localStorage.removeItem('cart');
    }
  }
  </script>
  
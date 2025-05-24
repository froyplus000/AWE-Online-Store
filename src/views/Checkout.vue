<template>
    <div class="max-w-4xl mx-auto p-6">
      <h2 class="text-2xl font-bold mb-6 text-center text-gray-800">Checkout Summary</h2>
  
      <div v-if="cart.length" class="space-y-4">
        <div v-for="(item, index) in cart" :key="index" class="flex items-center justify-between border-b pb-4">
          <div>
            <h3 class="font-semibold text-lg">{{ item.name }}</h3>
            <p class="text-sm text-gray-600">Quantity: {{ item.quantity }}</p>
          </div>
          <span class="text-indigo-600 font-bold">${{ item.price.toFixed(2) }}</span>
        </div>
  
        <div class="text-right mt-6">
          <p class="text-lg font-semibold">Total: ${{ totalPrice }}</p>
          <router-link to="/payment">
            <button class="mt-4 bg-green-500 hover:bg-green-600 text-white px-6 py-2 rounded">
              Proceed to Payment
            </button>
          </router-link>
        </div>
      </div>
  
      <p v-else class="text-center text-gray-500">Your cart is empty.</p>
    </div>
  </template>
  
  <script>
  export default {
    name: 'Checkout',
    data() {
      return {
        cart: [],
      };
    },
    computed: {
      totalPrice() {
        return this.cart.reduce((total, item) => total + item.price * item.quantity, 0).toFixed(2);
      },
    },
    created() {
      const storedCart = localStorage.getItem('cart');
      this.cart = storedCart ? JSON.parse(storedCart) : [];
    },
  };
  </script>
  
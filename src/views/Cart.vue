<template>
    <div class="max-w-4xl mx-auto p-6">
      <h2 class="text-2xl font-bold mb-6 text-center text-gray-800">Your Cart</h2>
  
      <div v-if="cart.length === 0" class="text-center text-gray-500">
        Your cart is empty.
      </div>
  
      <div v-else class="space-y-6">
        <div
          v-for="(item, index) in cart"
          :key="index"
          class="flex items-center justify-between bg-white shadow rounded p-4 border"
        >
          <div class="flex items-center space-x-4">
            <img :src="item.image" :alt="item.name" class="w-20 h-20 object-cover rounded" />
            <div>
              <h3 class="font-semibold text-gray-900">{{ item.name }}</h3>
              <p class="text-gray-500 text-sm">${{ item.price }} x {{ item.quantity }}</p>
            </div>
          </div>
          <div class="flex items-center space-x-2">
            <button
              @click="decreaseQuantity(index)"
              class="px-2 py-1 bg-gray-200 rounded hover:bg-gray-300"
            >-</button>
            <span class="px-2">{{ item.quantity }}</span>
            <button
              @click="increaseQuantity(index)"
              class="px-2 py-1 bg-gray-200 rounded hover:bg-gray-300"
            >+</button>
            <button
              @click="removeItem(index)"
              class="ml-4 px-3 py-1 text-red-500 hover:underline"
            >Remove</button>
          </div>
        </div>
  
        <div class="text-right font-semibold text-lg text-gray-700">
          Total: ${{ cartTotal.toFixed(2) }}
        </div>
  
        <div class="text-right">
          <router-link to="/checkout">
            <button class="mt-4 bg-indigo-500 hover:bg-indigo-600 text-white px-5 py-2 rounded">
              Proceed to Checkout
            </button>
          </router-link>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  export default {
    name: 'CartPage',
    data() {
      return {
        cart: []
      }
    },
    computed: {
      cartTotal() {
        return this.cart.reduce((total, item) => total + item.price * item.quantity, 0);
      }
    },
    methods: {
      loadCart() {
        this.cart = JSON.parse(localStorage.getItem('cart')) || [];
      },
      removeItem(index) {
        this.cart.splice(index, 1);
        this.updateCart();
      },
      increaseQuantity(index) {
        this.cart[index].quantity++;
        this.updateCart();
      },
      decreaseQuantity(index) {
        if (this.cart[index].quantity > 1) {
          this.cart[index].quantity--;
          this.updateCart();
        }
      },
      updateCart() {
        localStorage.setItem('cart', JSON.stringify(this.cart));
      }
    },
    mounted() {
      this.loadCart();
    }
  }
  </script>
  
  <style scoped>
  button:focus {
    outline: none;
  }
  </style>
  
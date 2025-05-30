<template>
  <div class="max-w-4xl mx-auto p-6">
    <h2 class="text-2xl font-bold mb-6 text-center text-gray-800">Product Details</h2>
    <div class="border rounded-lg p-6 shadow bg-white">
      <div class="flex justify-center mb-6">
        <img :src="product.image" :alt="product.name" class="max-h-[400px] w-full object-contain rounded" />
      </div>
      <h3 class="text-xl font-semibold text-gray-900 mb-2">{{ product.name }}</h3>
      <p class="text-gray-600 mb-3">{{ product.description }}</p>
      <p class="text-indigo-600 font-bold text-lg mb-6">${{ product.price }}</p>
      <button @click="addToCartAndGo" class="bg-green-500 hover:bg-green-600 text-white px-5 py-2 rounded shadow mr-3">
        Add to Cart
      </button>
    </div>
  </div>
</template>

<script>
import { addToCart } from '@/utils/cart.js'

export default {
  props: ['id'],
  data() {
    return {
      product: {},
      defaultProducts: [
        {
          id: 1,
          name: 'Wireless Mouse',
          description: 'Ergonomic and responsive wireless mouse.',
          price: 19.99,
          image: '/mouse.jpg',
        },
        {
          id: 2,
          name: 'Mechanical Keyboard',
          description: 'Tactile keys with RGB lighting.',
          price: 59.99,
          image: '/keyboard.jpg',
        },
        {
          id: 3,
          name: 'HD Webcam',
          description: '1080p webcam with built-in mic.',
          price: 29.99,
          image: '/webcam.jpg',
        },
        {
          id: 4,
          name: 'Noise Cancelling Headphones',
          description: 'Wireless headphones with ANC.',
          price: 89.99,
          image: '/headphones.jpg',
        },
      ]
    }
  },
  created() {
    fetch(`http://localhost:8080/api/products/${this.id}`)
      .then(res => {
        if (!res.ok) throw new Error('Product not found');
        return res.json();
      })
      .then(data => {
        this.product = data;
      })
      .catch(() => {
        console.warn('Backend failed, using fallback');
        this.product = this.defaultProducts.find(p => p.id === parseInt(this.id)) || {
          name: 'Product not found',
          description: '',
          price: 0,
          image: ''
        };
      });
  },
  methods: {
    addToCartAndGo() {
      addToCart(this.product);
      this.$router.push('/cart');
    }
  }
}
</script>

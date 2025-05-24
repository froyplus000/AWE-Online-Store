<template>
  <div class="flex flex-col min-h-screen bg-white text-gray-800">
    <!-- Hero Section -->
    <section class="bg-gradient-to-r from-indigo-500 to-blue-600 text-white py-12 px-6 text-center">
      <h2 class="text-4xl font-bold mb-2">Welcome to AWE Electronics</h2>
      <p class="text-lg">Explore top-quality gadgets at the best prices</p>
    </section>

    <!-- Product Grid -->
    <main class="flex-grow max-w-7xl mx-auto py-12 px-4 sm:px-6 lg:px-8">
      <h3 class="text-2xl font-semibold mb-8 text-center">Our Electronics Collection</h3>
      <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-8">
        <div
          v-for="product in products"
          :key="product.id"
          class="flex flex-col bg-white shadow-md rounded-xl overflow-hidden border hover:shadow-lg transition duration-300"
        >
          <router-link
            :to="`/product-details/${product.id}`"
            class="block"
          >
            <img
              :src="product.image"
              :alt="product.name"
              class="w-full h-48 object-cover"
            />
          </router-link>
          <div class="flex flex-col justify-between flex-1 p-4">
            <div>
              <h4 class="text-lg font-semibold text-gray-900">{{ product.name }}</h4>
              <p class="text-sm text-gray-500 mt-1 mb-3">{{ product.description }}</p>
            </div>
            <div class="mt-auto flex justify-between items-center">
              <span class="text-indigo-600 font-bold text-lg">${{ product.price }}</span>
              <button
                @click="addToCart(product)"
                class="bg-indigo-500 hover:bg-indigo-600 text-white text-sm px-4 py-2 rounded-lg"
              >
                Add to Cart
              </button>
            </div>
          </div>
        </div>
      </div>
    </main>

    <!-- Footer -->
    <footer class="bg-gray-100 border-t py-6 mt-12">
      <div class="max-w-7xl mx-auto px-4 flex justify-between flex-wrap items-center text-sm text-gray-600">
        <div class="mb-2 sm:mb-0">&copy; 2025 AWE Electronics</div>
        <div class="flex space-x-4">
          <router-link to="/login" class="hover:text-indigo-600 underline">Login</router-link>
          <router-link to="/signup" class="hover:text-indigo-600 underline">Sign Up</router-link>
          <router-link to="/cart" class="hover:text-indigo-600 underline">Cart</router-link>
          <router-link to="/checkout" class="hover:text-indigo-600 underline">Checkout</router-link>
        </div>
      </div>
    </footer>
  </div>
</template>

<script>
export default {
  name: 'Catalogue',
  data() {
    return {
      products: [
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
          description: '1080p webcam with built-in microphone.',
          price: 29.99,
          image: '/webcam.jpg',
        },
        {
          id: 4,
          name: 'Noise Cancelling Headphones',
          description: 'Wireless headphones with active noise cancellation.',
          price: 89.99,
          image: '/headphones.jpg',
        },
      ],
    };
  },
  methods: {
    addToCart(product) {
      const cart = JSON.parse(localStorage.getItem('cart')) || [];
      const existing = cart.find(item => item.id === product.id);
      if (existing) {
        existing.quantity += 1;
      } else {
        cart.push({ ...product, quantity: 1 });
      }
      localStorage.setItem('cart', JSON.stringify(cart));
      alert(`${product.name} added to cart`);
    },
  },
};
</script>

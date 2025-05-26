<template>
  <div class="min-h-screen bg-gray-100 p-6">
    <!-- Home Link -->
    <router-link
      to="/catalogue"
      class="inline-block text-indigo-600 underline mb-4 hover:text-indigo-800"
    >
      ← Home (Go to Catalogue)
    </router-link>

    <h1 class="text-3xl font-bold mb-6">Admin Dashboard</h1>

    <!-- Product Management -->
    <section class="mb-8 bg-white p-6 rounded shadow">
      <details open>
        <summary class="text-xl font-semibold cursor-pointer mb-4">Manage Products</summary>

        <!-- Add Product Form -->
        <form @submit.prevent="addProduct" class="space-y-2 mb-6">
          <input v-model="newProduct.name" placeholder="Product Name" class="input" />
          <input v-model="newProduct.price" type="number" step="0.01" placeholder="Price" class="input" />
          <input type="file" @change="handleImageUpload" class="input" accept="image/*" />
          <textarea v-model="newProduct.description" placeholder="Description" class="input" />
          <button class="btn bg-indigo-600 text-white">Add Product</button>
        </form>

        <!-- Product List -->
        <div
          v-for="(product, index) in products"
          :key="index"
          class="border p-4 rounded mb-3 bg-gray-50 flex justify-between items-center"
        >
          <div class="flex items-center space-x-4">
            <img :src="product.image" alt="Product Image" class="w-20 h-20 object-cover rounded border" />
            <div>
              <h3 class="font-bold">{{ product.name }}</h3>
              <p>${{ product.price }} – {{ product.description }}</p>
            </div>
          </div>
          <button @click="deleteProduct(index)" class="text-red-500 hover:underline">Delete</button>
        </div>
      </details>
    </section>

    <!-- Order Management -->
    <section class="bg-white p-6 rounded shadow">
      <details open>
        <summary class="text-xl font-semibold cursor-pointer mb-4">Manage Orders</summary>

        <div v-if="orders.length === 0" class="text-gray-500">
          No orders yet.
        </div>
        <div v-else>
          <div
            v-for="(order, i) in orders"
            :key="i"
            class="border p-4 rounded mb-3 bg-gray-50"
          >
            <h3 class="font-semibold mb-2">Order #{{ i + 1 }}</h3>
            <ul class="mb-2 list-disc pl-5">
              <li v-for="item in order.items" :key="item.id">
                {{ item.quantity }} × {{ item.name }} - ${{ item.price }}
              </li>
            </ul>
            <p class="font-semibold">Total: ${{ order.total.toFixed(2) }}</p>
            <p>Status: <span class="text-green-600 font-medium">{{ order.status }}</span></p>
            <button
              @click="markAsProcessed(i)"
              class="mt-2 text-sm bg-green-600 text-white px-3 py-1 rounded hover:bg-green-700"
            >
              Mark as Processed
            </button>
          </div>
        </div>
      </details>
    </section>
  </div>
</template>

<script setup>
import { ref } from 'vue'

// PRODUCTS
const products = ref(JSON.parse(localStorage.getItem('adminProducts')) || [])
const newProduct = ref({ name: '', price: '', description: '', image: '' })

function saveProducts() {
  localStorage.setItem('adminProducts', JSON.stringify(products.value))
}

function addProduct() {
  products.value.push({ ...newProduct.value })
  saveProducts()
  Object.keys(newProduct.value).forEach(key => newProduct.value[key] = '')
}

function deleteProduct(index) {
  products.value.splice(index, 1)
  saveProducts()
}

function handleImageUpload(event) {
  const file = event.target.files[0]
  if (!file) return

  const reader = new FileReader()
  reader.onload = () => {
    newProduct.value.image = reader.result // base64 string
  }
  reader.readAsDataURL(file)
}

// ORDERS
const orders = ref(JSON.parse(localStorage.getItem('orders')) || [])

function markAsProcessed(index) {
  orders.value[index].status = 'Processed'
  localStorage.setItem('orders', JSON.stringify(orders.value))
}
</script>

<style scoped>
.input {
  @apply block w-full p-2 border rounded;
}
.btn {
  @apply px-4 py-2 rounded;
}
</style>

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
          <input v-model="newProduct.category" placeholder="Category" class="input" />
          <input v-model="newProduct.price" type="number" step="0.01" placeholder="Price" class="input" />
          <input type="file" @change="handleImageUpload" class="input" accept="image/*" />
          <textarea v-model="newProduct.description" placeholder="Description" class="input" />
          <button class="btn bg-indigo-600 text-white">Add Product</button>
        </form>

        <!-- Product List -->
        <div
          v-for="(product, index) in products"
          :key="product.id"
          class="border p-4 rounded mb-3 bg-gray-50 flex justify-between items-center"
        >
          <div class="flex items-center space-x-4">
            <img :src="product.image" alt="Product Image" class="w-20 h-20 object-cover rounded border" />
            <div>
              <h3 class="font-bold">{{ product.name }}</h3>
              <p>Category: {{ product.category || 'N/A' }}</p>
              <p>${{ product.price }} – {{ product.description }}</p>
            </div>
          </div>
          <button @click="deleteProduct(product.id, index)" class="text-red-500 hover:underline">Delete</button>
        </div>
      </details>
    </section>

    <!-- Order Management -->
    <section class="bg-white p-6 rounded shadow">
      <details open>
        <summary class="text-xl font-semibold cursor-pointer mb-4">Manage Orders</summary>

        <div class="mb-4 flex space-x-2">
          <button 
            @click="fetchOrders"
            class="bg-blue-500 hover:bg-blue-600 text-white px-4 py-2 rounded"
          >
            Refresh Orders
          </button>
          <span v-if="isLoadingOrders" class="text-gray-500 py-2">Loading orders...</span>
        </div>

        <div v-if="orders.length === 0" class="text-gray-500">
          No orders yet.
        </div>
        <div v-else>
          <div
            v-for="(order, i) in orders"
            :key="order.id || i"
            class="border p-4 rounded mb-3 bg-gray-50"
          >
            <div class="flex justify-between items-start mb-2">
              <h3 class="font-semibold">Order #{{ order.id || (i + 1) }}</h3>
              <span class="text-sm text-gray-500">
                {{ formatDate(order.createdAt) }}
              </span>
            </div>
            
            <div v-if="order.userEmail" class="text-sm text-gray-600 mb-2">
              Customer: {{ order.userEmail }}
            </div>
            
            <ul class="mb-2 list-disc pl-5">
              <li v-for="item in order.items" :key="item.id || item.productId">
                {{ item.quantity }} × {{ item.productName || item.name }} - ${{ item.price }}
              </li>
            </ul>
            
            <p class="font-semibold">Total: ${{ order.total ? order.total.toFixed(2) : 'N/A' }}</p>
            <p>Status: 
              <span 
                :class="{
                  'text-yellow-600': order.status === 'PENDING',
                  'text-green-600': order.status === 'PAID',
                  'text-blue-600': order.status === 'SHIPPED',
                  'text-purple-600': order.status === 'COMPLETED',
                  'text-red-600': order.status === 'CANCELLED'
                }" 
                class="font-medium"
              >
                {{ order.status }}
              </span>
            </p>
            
            <div class="mt-2 space-x-2">
              <button
                v-if="order.status === 'PENDING'"
                @click="updateOrderStatus(order.id || order.orderId, i, 'PAID')"
                class="text-sm bg-green-600 text-white px-3 py-1 rounded hover:bg-green-700"
              >
                Mark as Paid
              </button>
              <button
                v-if="order.status === 'PAID'"
                @click="updateOrderStatus(order.id || order.orderId, i, 'SHIPPED')"
                class="text-sm bg-blue-600 text-white px-3 py-1 rounded hover:bg-blue-700"
              >
                Mark as Shipped
              </button>
              <button
                v-if="order.status === 'SHIPPED'"
                @click="updateOrderStatus(order.id || order.orderId, i, 'COMPLETED')"
                class="text-sm bg-purple-600 text-white px-3 py-1 rounded hover:bg-purple-700"
              >
                Mark as Completed
              </button>
            </div>
          </div>
        </div>
      </details>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

// PRODUCTS
const defaultProducts = [
  { id: 1, name: 'Wireless Mouse', price: 19.99, description: 'Ergonomic and responsive wireless mouse.', image: '/mouse.jpg', category: 'Accessories' },
  { id: 2, name: 'Mechanical Keyboard', price: 59.99, description: 'Tactile keys with RGB lighting.', image: '/keyboard.jpg', category: 'Keyboards' },
  { id: 3, name: 'HD Webcam', price: 29.99, description: '1080p webcam with built-in mic.', image: '/webcam.jpg', category: 'Cameras' },
  { id: 4, name: 'Noise Cancelling Headphones', price: 89.99, description: 'Wireless headphones with ANC.', image: '/headphones.jpg', category: 'Audio' }
]

const products = ref([])
const newProduct = ref({ name: '', category: '', price: '', description: '', image: '' })

// ORDERS
const orders = ref([])
const isLoadingOrders = ref(false)

onMounted(async () => {
  await fetchProducts()
  await fetchOrders()
})

// PRODUCT FUNCTIONS
async function fetchProducts() {
  try {
    const response = await fetch('http://localhost:8080/api/products')
    if (!response.ok) throw new Error('Failed to fetch products')
    const data = await response.json()
    products.value = data.length ? data : loadLocalProducts()
  } catch (err) {
    console.warn('Backend unavailable or error fetching products')
    products.value = loadLocalProducts()
  }
}

function loadLocalProducts() {
  const stored = localStorage.getItem('adminProducts')
  return stored ? JSON.parse(stored) : defaultProducts
}

async function addProduct() {
  try {
    const token = localStorage.getItem('token')
    const response = await fetch('http://localhost:8080/api/products/add', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      credentials: 'include',
      body: JSON.stringify(newProduct.value)
    })
    if (!response.ok) throw new Error('Failed to add product')
    const createdProduct = await response.json()
    products.value.push(createdProduct)
    alert('Product added successfully!')
  } catch (err) {
    console.warn('Backend unavailable, saving locally')
    addProductLocally()
  }
  Object.keys(newProduct.value).forEach(key => newProduct.value[key] = '')
}

function addProductLocally() {
  const newId = products.value.length ? Math.max(...products.value.map(p => p.id)) + 1 : 1
  const product = { id: newId, ...newProduct.value }
  products.value.push(product)
  saveLocalProducts()
  alert('Product added locally!')
}

function saveLocalProducts() {
  localStorage.setItem('adminProducts', JSON.stringify(products.value))
}

async function deleteProduct(productId, index) {
  try {
    const token = localStorage.getItem('token')
    const response = await fetch(`http://localhost:8080/api/products/${productId}`, {
      method: 'DELETE',
      headers: {
        'Authorization': `Bearer ${token}`
      },
      credentials: 'include'
    })
    if (!response.ok) throw new Error('Failed to delete product')
    products.value.splice(index, 1)
    alert('Product deleted successfully!')
  } catch (err) {
    console.warn('Backend unavailable, deleting locally')
    deleteProductLocally(index)
  }
}

function deleteProductLocally(index) {
  products.value.splice(index, 1)
  saveLocalProducts()
  alert('Product deleted locally!')
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

// ORDER FUNCTIONS
async function fetchOrders() {
  isLoadingOrders.value = true
  try {
    const token = localStorage.getItem('token')
    const headers = {
      'Content-Type': 'application/json'
    }
    
    if (token) {
      headers['Authorization'] = `Bearer ${token}`
    }
    
    // FIXED: Use /api/orders instead of /api/orders/all
    const response = await fetch('http://localhost:8080/api/orders', {
      method: 'GET',
      headers: headers,
      credentials: 'include'
    })
    
    if (!response.ok) throw new Error('Failed to fetch orders')
    
    const data = await response.json()
    orders.value = data
    console.log('Orders fetched from backend:', data)
  } catch (err) {
    console.warn('Backend unavailable for orders, using local storage:', err)
    orders.value = JSON.parse(localStorage.getItem('orders')) || []
  }
  isLoadingOrders.value = false
}

async function updateOrderStatus(orderId, index, newStatus) {
  try {
    const token = localStorage.getItem('token')
    const headers = {
      'Content-Type': 'application/json'
    }
    
    if (token) {
      headers['Authorization'] = `Bearer ${token}`
    }
    
    const response = await fetch(`http://localhost:8080/api/orders/${orderId}/status`, {
      method: 'PUT',
      headers: headers,
      credentials: 'include',
      body: JSON.stringify({ status: newStatus })
    })
    
    if (!response.ok) throw new Error('Failed to update order status')
    
    orders.value[index].status = newStatus
    alert(`Order marked as ${newStatus.toLowerCase()}!`)
  } catch (err) {
    console.warn('Backend unavailable, updating locally:', err)
    orders.value[index].status = newStatus
    localStorage.setItem('orders', JSON.stringify(orders.value))
    alert(`Order marked as ${newStatus.toLowerCase()} locally!`)
  }
}

// UTILITY FUNCTIONS
function formatDate(dateString) {
  if (!dateString) return 'N/A'
  try {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    })
  } catch (err) {
    return dateString
  }
}

// Legacy function for backward compatibility
function markAsProcessed(index) {
  updateOrderStatus(orders.value[index].id, index, 'PROCESSED')
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

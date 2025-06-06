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
        <form @submit.prevent="addProduct" class="space-y-2 mb-6" v-if="!editingProduct">
          <input v-model="newProduct.name" placeholder="Product Name" class="input" />
          <input v-model="newProduct.category" placeholder="Category" class="input" />
          <input v-model="newProduct.price" type="number" step="0.01" placeholder="Price" class="input" />
          <input v-model="newProduct.imageUrl" placeholder="Image URL" class="input" />
          <textarea v-model="newProduct.description" placeholder="Description" class="input" />
          <button class="btn bg-indigo-600 text-white">Add Product</button>
        </form>

        <!-- Edit Product Form -->
        <form @submit.prevent="updateProduct" class="space-y-2 mb-6" v-if="editingProduct">
          <h3 class="text-lg font-semibold mb-2">Edit Product</h3>
          <input v-model="editingProduct.name" placeholder="Product Name" class="input" />
          <input v-model="editingProduct.category" placeholder="Category" class="input" />
          <input v-model="editingProduct.price" type="number" step="0.01" placeholder="Price" class="input" />
          <input v-model="editingProduct.imageUrl" placeholder="Image URL" class="input" />
          <textarea v-model="editingProduct.description" placeholder="Description" class="input" />
          <div class="flex space-x-2">
            <button type="submit" class="btn bg-green-600 text-white">Update Product</button>
            <button type="button" @click="cancelEdit" class="btn bg-gray-600 text-white">Cancel</button>
          </div>
        </form>

        <!-- Product List -->
        <div
          v-for="(product, index) in products"
          :key="product.id"
          class="border p-4 rounded mb-3 bg-gray-50 flex justify-between items-center"
        >
          <div class="flex items-center space-x-4">
            <img :src="product.imageUrl || product.image" alt="Product Image" class="w-20 h-20 object-cover rounded border" />
            <div>
              <h3 class="font-bold">{{ product.name }}</h3>
              <p>Category: {{ product.category || 'N/A' }}</p>
              <p>${{ product.price }} – {{ product.description }}</p>
            </div>
          </div>
          <div class="flex space-x-2">
            <button @click="editProduct(product)" class="text-blue-500 hover:underline">Edit</button>
            <button @click="deleteProduct(product.id, index)" class="text-red-500 hover:underline">Delete</button>
          </div>
        </div>
      </details>
    </section>

    <!-- Order Management -->
    <section class="bg-white p-6 rounded shadow mb-8">
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
              <div class="flex space-x-2">
                <button 
                  @click="viewOrderDetails(order.id)"
                  class="text-sm bg-gray-500 text-white px-2 py-1 rounded hover:bg-gray-600"
                >
                  View Details
                </button>
                <span class="text-sm text-gray-500">
                  {{ formatDate(order.createdAt) }}
                </span>
              </div>
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

    <!-- User Management -->
    <section class="bg-white p-6 rounded shadow mb-8">
      <details>
        <summary class="text-xl font-semibold cursor-pointer mb-4">User Information</summary>
        
        <div class="mb-4">
          <button 
            @click="getCurrentUser"
            class="bg-indigo-500 hover:bg-indigo-600 text-white px-4 py-2 rounded"
          >
            Get Current User Info
          </button>
        </div>

        <div v-if="currentUserInfo" class="bg-gray-50 p-4 rounded">
          <h4 class="font-semibold mb-2">Current User Information:</h4>
          <p><strong>Email:</strong> {{ currentUserInfo.email }}</p>
          <p><strong>Role:</strong> {{ currentUserInfo.role }}</p>
          <p><strong>Full Name:</strong> {{ currentUserInfo.fullName || 'N/A' }}</p>
          <p><strong>Phone:</strong> {{ currentUserInfo.phone || 'N/A' }}</p>
          <p><strong>Address:</strong> {{ currentUserInfo.address || 'N/A' }}</p>
        </div>
      </details>
    </section>

    <!-- Order Details Modal -->
    <div v-if="selectedOrderDetails" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white p-6 rounded-lg max-w-2xl w-full mx-4 max-h-[80vh] overflow-y-auto">
        <div class="flex justify-between items-center mb-4">
          <h3 class="text-xl font-semibold">Order Details #{{ selectedOrderDetails.id }}</h3>
          <button @click="closeOrderDetails" class="text-gray-500 hover:text-gray-700">✕</button>
        </div>
        
        <div class="space-y-4">
          <div>
            <strong>Total Price:</strong> ${{ selectedOrderDetails.total }}
          </div>
          <div>
            <strong>Status:</strong> {{ selectedOrderDetails.status }}
          </div>
          <div>
            <strong>Items:</strong>
            <ul class="list-disc pl-5 mt-2">
              <li v-for="item in selectedOrderDetails.items" :key="item.productName">
                {{ item.quantity }} × {{ item.productName }} - ${{ item.price }}
              </li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

// PRODUCTS
const defaultProducts = [
  { id: 1, name: 'Wireless Mouse', price: 19.99, description: 'Ergonomic and responsive wireless mouse.', imageUrl: '/mouse.jpg', category: 'Accessories' },
  { id: 2, name: 'Mechanical Keyboard', price: 59.99, description: 'Tactile keys with RGB lighting.', imageUrl: '/keyboard.jpg', category: 'Keyboards' },
  { id: 3, name: 'HD Webcam', price: 29.99, description: '1080p webcam with built-in mic.', imageUrl: '/webcam.jpg', category: 'Cameras' },
  { id: 4, name: 'Noise Cancelling Headphones', price: 89.99, description: 'Wireless headphones with ANC.', imageUrl: '/headphones.jpg', category: 'Audio' }
]

const products = ref([])
const newProduct = ref({ name: '', category: '', price: '', description: '', imageUrl: '' })
const editingProduct = ref(null)

// ORDERS
const orders = ref([])
const isLoadingOrders = ref(false)
const selectedOrderDetails = ref(null)

// USER INFO
const currentUserInfo = ref(null)

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

async function updateProduct() {
  try {
    const token = localStorage.getItem('token')
    const response = await fetch(`http://localhost:8080/api/products/${editingProduct.value.id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      credentials: 'include',
      body: JSON.stringify({
        name: editingProduct.value.name,
        category: editingProduct.value.category,
        price: editingProduct.value.price,
        description: editingProduct.value.description,
        imageUrl: editingProduct.value.imageUrl
      })
    })
    if (!response.ok) throw new Error('Failed to update product')
    const updatedProduct = await response.json()
    
    // Update local products array
    const index = products.value.findIndex(p => p.id === editingProduct.value.id)
    if (index !== -1) {
      products.value[index] = updatedProduct
    }
    
    editingProduct.value = null
    alert('Product updated successfully!')
  } catch (err) {
    console.warn('Backend unavailable, updating locally')
    updateProductLocally()
  }
}

function editProduct(product) {
  editingProduct.value = { ...product }
}

function cancelEdit() {
  editingProduct.value = null
}

function addProductLocally() {
  const newId = products.value.length ? Math.max(...products.value.map(p => p.id)) + 1 : 1
  const product = { id: newId, ...newProduct.value }
  products.value.push(product)
  saveLocalProducts()
  alert('Product added locally!')
}

function updateProductLocally() {
  const index = products.value.findIndex(p => p.id === editingProduct.value.id)
  if (index !== -1) {
    products.value[index] = { ...editingProduct.value }
    saveLocalProducts()
    alert('Product updated locally!')
  }
  editingProduct.value = null
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

async function viewOrderDetails(orderId) {
  try {
    const token = localStorage.getItem('token')
    const response = await fetch(`http://localhost:8080/api/orders/${orderId}`, {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      },
      credentials: 'include'
    })
    
    if (response.ok) {
      selectedOrderDetails.value = await response.json()
    } else {
      throw new Error('Failed to fetch order details')
    }
  } catch (error) {
    console.warn('Failed to fetch order details:', error)
    alert('Failed to load order details')
  }
}

function closeOrderDetails() {
  selectedOrderDetails.value = null
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

// USER FUNCTIONS
async function getCurrentUser() {
  try {
    const token = localStorage.getItem('token')
    const response = await fetch('http://localhost:8080/api/users/me', {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      },
      credentials: 'include'
    })
    
    if (response.ok) {
      currentUserInfo.value = await response.json()
    } else {
      throw new Error('Failed to fetch user info')
    }
  } catch (error) {
    console.warn('Failed to fetch user info:', error)
    alert('Failed to load user information')
  }
}


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
</script>

<style scoped>
.input {
  @apply block w-full p-2 border rounded;
}
.btn {
  @apply px-4 py-2 rounded;
}
</style>

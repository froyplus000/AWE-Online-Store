<template>
  <div class="max-w-4xl mx-auto p-6">
    <h2 class="text-2xl font-bold mb-6 text-center text-gray-800">Checkout Summary</h2>

    <div v-if="cart.length" class="space-y-4">
      <div v-for="(item, index) in cart" :key="index" class="flex items-center justify-between border-b pb-4">
        <div>
          <h3 class="font-semibold text-lg">{{ item.name }}</h3>
          <p class="text-sm text-gray-600">Quantity: {{ item.quantity }}</p>
        </div>
        <span class="text-indigo-600 font-bold">${{ (item.price * item.quantity).toFixed(2) }}</span>
      </div>

      <div class="text-right mt-6">
        <p class="text-lg font-semibold">Total: ${{ totalPrice }}</p>
        <button 
          @click="submitOrder" 
          :disabled="isSubmitting"
          class="mt-4 bg-green-500 hover:bg-green-600 disabled:bg-gray-400 text-white px-6 py-2 rounded"
        >
          {{ isSubmitting ? 'Processing...' : 'Proceed to Payment' }}
        </button>
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
      isSubmitting: false
    }
  },
  computed: {
    totalPrice() {
      return this.cart.reduce((total, item) => total + item.price * item.quantity, 0).toFixed(2)
    }
  },
  created() {
    const storedCart = localStorage.getItem('cart')
    this.cart = storedCart ? JSON.parse(storedCart) : []
  },
  methods: {
    async syncCartToBackend() {
      const user = JSON.parse(localStorage.getItem('activeUser'))
      const token = localStorage.getItem('token')
      
      if (!user || !token) return
      
      try {
        // Skip cart clear since the endpoint doesn't exist
        // Just add each cart item to backend
        for (const item of this.cart) {
          await fetch('http://localhost:8080/api/cart/add', {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json',
              'Authorization': `Bearer ${token}`
            },
            credentials: 'include',
            body: JSON.stringify({
              productId: item.id,
              quantity: item.quantity
            })
          })
        }
        
        console.log('Cart synced to backend')
      } catch (error) {
        console.warn('Failed to sync cart to backend:', error)
      }
    },
    
    async submitOrder() {
      const user = JSON.parse(localStorage.getItem('activeUser'))
      if (!user || !user.id) {
        alert('You must be logged in to place an order.')
        this.$router.push('/login')
        return
      }

      if (this.cart.length === 0) {
        alert('Your cart is empty.')
        return
      }

      this.isSubmitting = true

      try {
        // First sync cart to backend
        await this.syncCartToBackend()

        console.log('Submitting order with JWT auth')

        const headers = {
          'Content-Type': 'application/json'
        }

        const token = localStorage.getItem('token')
        if (token) {
          headers['Authorization'] = `Bearer ${token}`
          console.log('Using JWT token:', token.substring(0, 20) + '...')
        } else {
          throw new Error('No authentication token found. Please log in again.')
        }

        // FIXED: Use /api/orders/place instead of /api/orders
        const response = await fetch('http://localhost:8080/api/orders/place', {
          method: 'POST',
          headers: headers,
          credentials: 'include'
        })

        console.log('Order response status:', response.status)

        if (!response.ok) {
          const errorText = await response.text()
          console.error('Order failed:', errorText)
          
          if (response.status === 403) {
            throw new Error(`Access denied. Please log in again. Details: ${errorText}`)
          } else if (response.status === 401) {
            throw new Error('Session expired. Please log in again.')
          }
          
          throw new Error(errorText || `Server error: ${response.status}`)
        }

        const orderResult = await response.json()
        console.log('Order placed successfully:', orderResult)

        // Create payment record
        await this.createPayment(orderResult.id, parseFloat(this.totalPrice))

        // Clear cart and redirect
        localStorage.removeItem('cart')
        this.$router.push('/payment')

      } catch (error) {
        console.error('Order submission failed:', error)
        
        if (error.message.includes('log in again')) {
          localStorage.removeItem('activeUser')
          localStorage.removeItem('token')
          alert(error.message)
          this.$router.push('/login')
        } else {
          alert(`Failed to place order: ${error.message}`)
        }
      } finally {
        this.isSubmitting = false
      }
    },
    
    async createPayment(orderId, amount) {
      try {
        const token = localStorage.getItem('token')
        
        const paymentData = {
          orderId: orderId,
          amount: amount,
          paymentMethod: 'CREDIT_CARD',
          status: 'COMPLETED'
        }
        
        const response = await fetch('http://localhost:8080/api/payments', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
          },
          credentials: 'include',
          body: JSON.stringify(paymentData)
        })
        
        if (response.ok) {
          console.log('Payment record created successfully')
        } else {
          console.warn('Failed to create payment record')
        }
      } catch (error) {
        console.warn('Payment creation failed:', error)
      }
    }
  }
}
</script>

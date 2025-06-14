<template>
  <div class="max-w-4xl mx-auto p-6">
    <h2 class="text-2xl font-bold mb-6 text-center text-gray-800">Checkout Summary</h2>

    <div v-if="isLoading" class="text-center text-gray-500">
      Loading cart...
    </div>

    <div v-else-if="cart.length" class="space-y-4">
      <div v-for="(item, index) in cart" :key="index" class="flex items-center justify-between border-b pb-4">
        <div>
          <h3 class="font-semibold text-lg">{{ item.productName || item.name }}</h3>
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
      isSubmitting: false,
      isLoading: false
    }
  },
  computed: {
    totalPrice() {
      return this.cart.reduce((total, item) => {
        const price = item.price || 0;
        const quantity = item.quantity || 0;
        return total + (price * quantity);
      }, 0).toFixed(2);
    },
    isLoggedIn() {
      return !!localStorage.getItem('activeUser');
    }
  },
  async created() {
    await this.loadCart();
  },
  methods: {
    async loadCart() {
      if (!this.isLoggedIn) {
        // Falls back to localStorage if not logged in
        const storedCart = localStorage.getItem('cart');
        this.cart = storedCart ? JSON.parse(storedCart) : [];
        return;
      }

      this.isLoading = true;
      try {
        const token = localStorage.getItem('token');
        const response = await fetch('http://localhost:8080/api/cart', {
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          },
          credentials: 'include'
        });

        if (response.ok) {
          this.cart = await response.json();
          console.log('Cart loaded from backend for checkout:', this.cart);
        } else {
          throw new Error('Failed to load cart');
        }
      } catch (error) {
        console.warn('Failed to load cart from backend, using localStorage:', error);
        const storedCart = localStorage.getItem('cart');
        this.cart = this.transformToCartFormat(JSON.parse(storedCart) || []);
      } finally {
        this.isLoading = false;
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

        // Create order from backend cart 
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

        // Clear local cart and redirect
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
    },

    transformToCartFormat(localCart) {
      return localCart.map(item => ({
        productId: item.id,
        productName: item.name,
        price: item.price,
        quantity: item.quantity,
        image: item.image,
        total: item.price * item.quantity
      }));
    }
  }
}
</script>

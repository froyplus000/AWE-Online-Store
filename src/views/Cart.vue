<template>
  <div class="max-w-4xl mx-auto p-6">
    <h2 class="text-2xl font-bold mb-6 text-center text-gray-800">Your Cart</h2>

    <div v-if="isLoading" class="text-center text-gray-500">
      Loading cart...
    </div>

    <div v-else-if="cart.length === 0" class="text-center text-gray-500">
      Your cart is empty.
    </div>

    <div v-else class="space-y-6">
      <div
        v-for="(item, index) in cart"
        :key="index"
        class="flex items-center justify-between bg-white shadow rounded p-4 border"
      >
        <div class="flex items-center space-x-4">
          <img :src="item.image || item.imageUrl" :alt="item.productName" class="w-20 h-20 object-cover rounded" />
          <div>
            <h3 class="font-semibold text-gray-900">{{ item.productName }}</h3>
            <p class="text-gray-500 text-sm">${{ item.price }} x {{ item.quantity }}</p>
            <p class="text-gray-600 text-sm">Total: ${{ item.total?.toFixed(2) || (item.price * item.quantity).toFixed(2) }}</p>
          </div>
        </div>
        <div class="flex items-center space-x-2">
          <button
            @click="updateQuantity(item, item.quantity - 1)"
            :disabled="item.quantity <= 1 || isUpdating"
            class="px-2 py-1 bg-gray-200 rounded hover:bg-gray-300 disabled:opacity-50"
          >-</button>
          <span class="px-2">{{ item.quantity }}</span>
          <button
            @click="updateQuantity(item, item.quantity + 1)"
            :disabled="isUpdating"
            class="px-2 py-1 bg-gray-200 rounded hover:bg-gray-300 disabled:opacity-50"
          >+</button>
          <button
            @click="removeItem(item)"
            :disabled="isUpdating"
            class="ml-4 px-3 py-1 text-red-500 hover:underline disabled:opacity-50"
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
      cart: [],
      isLoading: false,
      isUpdating: false,
      lastError: null,
      showDebug: false,
      productNameToIdMap: {} // Store mapping of product names to IDs
    }
  },
  computed: {
    cartTotal() {
      return this.cart.reduce((total, item) => {
        const price = item.price || 0;
        const quantity = item.quantity || 0;
        return total + (price * quantity);
      }, 0);
    },
    isLoggedIn() {
      return !!localStorage.getItem('activeUser');
    }
  },
  async mounted() {
    await this.loadProductMapping();
    await this.loadCart();
  },
  methods: {
    async loadProductMapping() {
      // Load all products to create name → ID mapping
      try {
        const response = await fetch('http://localhost:8080/api/products');
        if (response.ok) {
          const products = await response.json();
          this.productNameToIdMap = {};
          products.forEach(product => {
            this.productNameToIdMap[product.name] = product.id;
          });
          console.log('Product mapping loaded:', this.productNameToIdMap);
        }
      } catch (error) {
        console.warn('Failed to load product mapping:', error);
        
        // Fallback: try to load from localStorage cart
        const localCart = JSON.parse(localStorage.getItem('cart')) || [];
        localCart.forEach(item => {
          if (item.name && item.id) {
            this.productNameToIdMap[item.name] = item.id;
          }
        });
      }
    },

    getProductIdFromItem(item) {
      // Try multiple ways to get the product ID
      if (item.productId) return item.productId;
      if (item.id) return item.id;
      
      // Use the name → ID mapping
      if (item.productName && this.productNameToIdMap[item.productName]) {
        return this.productNameToIdMap[item.productName];
      }
      
      return null;
    },

    async loadCart() {
      if (!this.isLoggedIn) {
        // Fall back to localStorage if not logged in
        this.cart = JSON.parse(localStorage.getItem('cart')) || [];
        this.syncLocalStorageFormat();
        return;
      }

      this.isLoading = true;
      this.lastError = null;
      
      try {
        const token = localStorage.getItem('token');
        
        console.log('Loading cart from backend...');
        
        const response = await fetch('http://localhost:8080/api/cart', {
          method: 'GET',
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          },
          credentials: 'include'
        });

        console.log('Cart API response status:', response.status);

        if (response.ok) {
          const backendCart = await response.json();
          

          console.log('Raw backend cart response:', backendCart);
          if (backendCart.length > 0) {
            console.log('First cart item structure:', backendCart[0]);
            console.log('Available fields:', Object.keys(backendCart[0]));
          }
          
          this.cart = backendCart;
          console.log('Cart loaded from backend:', backendCart);
          
          this.updateLocalStorageFromBackend();
        } else {
          const errorText = await response.text();
          throw new Error(`Failed to load cart: ${response.status} - ${errorText}`);
        }
      } catch (error) {
        console.warn('Failed to load cart from backend:', error);
        this.lastError = error.message;
        
        // Fall back to localStorage
        this.cart = JSON.parse(localStorage.getItem('cart')) || [];
        this.syncLocalStorageFormat();
      } finally {
        this.isLoading = false;
      }
    },

    async updateQuantity(item, newQuantity) {
      if (newQuantity < 1) return;
      
      this.isUpdating = true;
      this.lastError = null;

      try {
        if (this.isLoggedIn) {
          await this.updateBackendQuantity(item, newQuantity);
        } else {
          this.updateLocalQuantity(item, newQuantity);
        }
      } catch (error) {
        console.error('Failed to update quantity:', error);
        this.lastError = error.message;
        
        // Fall back to local update
        this.updateLocalQuantity(item, newQuantity);
      } finally {
        this.isUpdating = false;
      }
    },

    async updateBackendQuantity(item, newQuantity) {
      const token = localStorage.getItem('token');
      const productId = this.getProductIdFromItem(item);
      
      console.log('Full cart item:', item);
      console.log('Updating quantity in backend:', {
        productId: productId,
        quantity: newQuantity,
        productName: item.productName,
        foundInMapping: this.productNameToIdMap[item.productName]
      });

      if (!productId) {
        throw new Error(`No productId found for item: ${item.productName}`);
      }

      const response = await fetch('http://localhost:8080/api/cart/update', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        },
        credentials: 'include',
        body: JSON.stringify({
          productId: productId,
          quantity: newQuantity
        })
      });

      console.log('Update quantity response status:', response.status);

      if (response.ok) {
        // Update local state
        const cartItem = this.cart.find(c => 
          this.getProductIdFromItem(c) === productId
        );
        if (cartItem) {
          cartItem.quantity = newQuantity;
          cartItem.total = cartItem.price * newQuantity;
        }
        
        // Update localStorage
        this.updateLocalStorageFromBackend();
        console.log('Quantity updated successfully');
      } else {
        const errorText = await response.text();
        throw new Error(`Update failed: ${response.status} - ${errorText}`);
      }
    },

    updateLocalQuantity(item, newQuantity) {
      const cartItem = this.cart.find(c => 
        this.getProductIdFromItem(c) === this.getProductIdFromItem(item)
      );
      
      if (cartItem) {
        cartItem.quantity = newQuantity;
        if (cartItem.price) {
          cartItem.total = cartItem.price * newQuantity;
        }
      }
      
      this.updateLocalStorage();
    },

    async removeItem(item) {
      this.isUpdating = true;
      this.lastError = null;

      try {
        if (this.isLoggedIn) {
          await this.removeBackendItem(item);
        } else {
          this.removeLocalItem(item);
        }
      } catch (error) {
        console.error('Failed to remove item:', error);
        this.lastError = error.message;
        
        // Fall back to local removal
        this.removeLocalItem(item);
      } finally {
        this.isUpdating = false;
      }
    },

    async removeBackendItem(item) {
      const token = localStorage.getItem('token');
      const productId = this.getProductIdFromItem(item);
      
      console.log('Removing item from backend:', {
        productId: productId,
        productName: item.productName
      });

      if (!productId) {
        throw new Error(`No productId found for item: ${item.productName}`);
      }

      const response = await fetch('http://localhost:8080/api/cart/remove', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        },
        credentials: 'include',
        body: JSON.stringify({
          productId: productId
        })
      });

      console.log('Remove item response status:', response.status);

      if (response.ok) {
        this.cart = this.cart.filter(c => 
          this.getProductIdFromItem(c) !== productId
        );
        

        this.updateLocalStorageFromBackend();
        console.log('Item removed successfully');
      } else {
        const errorText = await response.text();
        throw new Error(`Remove failed: ${response.status} - ${errorText}`);
      }
    },

    removeLocalItem(item) {
      const productId = this.getProductIdFromItem(item);
      this.cart = this.cart.filter(c => 
        this.getProductIdFromItem(c) !== productId
      );
      this.updateLocalStorage();
    },

    async refreshCart() {
      await this.loadCart();
    },

    updateLocalStorage() {
      // Converts backend format to localStorage format
      const localFormat = this.cart.map(item => ({
        id: this.getProductIdFromItem(item),
        name: item.productName || item.name,
        price: item.price,
        quantity: item.quantity,
        image: item.image || item.imageUrl,
        description: item.description || ''
      }));
      
      localStorage.setItem('cart', JSON.stringify(localFormat));
    },

    updateLocalStorageFromBackend() {
      if (this.cart.length > 0) {
        this.updateLocalStorage();
      }
    },

    syncLocalStorageFormat() {
      // Convert localStorage format to backend-like format for consistency
      if (this.cart.length > 0 && this.cart[0].id && !this.cart[0].productName) {
        this.cart = this.cart.map(item => ({
          productName: item.name,
          price: item.price,
          quantity: item.quantity,
          image: item.image,
          total: item.price * item.quantity
        }));
        
        // Update the name → ID mapping
        this.cart.forEach(item => {
          if (item.productName && item.id) {
            this.productNameToIdMap[item.productName] = item.id;
          }
        });
      }
    }
  }
}
</script>

<style scoped>
button:focus {
  outline: none;
}
</style>

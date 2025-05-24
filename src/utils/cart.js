
export function addToCart(product) {
    const cart = JSON.parse(localStorage.getItem('cart')) || []
  
    const existing = cart.find(item => item.id === product.id)
    if (existing) {
      existing.quantity += 1
    } else {
      cart.push({ ...product, quantity: 1 })
    }
  
    localStorage.setItem('cart', JSON.stringify(cart))
  }
  
  export function getCart() {
    return JSON.parse(localStorage.getItem('cart')) || []
  }
  
  export function removeFromCart(productId) {
    const cart = getCart().filter(item => item.id !== productId)
    localStorage.setItem('cart', JSON.stringify(cart))
  }
  
  export function updateCart(cart) {
    localStorage.setItem('cart', JSON.stringify(cart))
  }
  
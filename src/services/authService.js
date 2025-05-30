const API_BASE_URL = 'http://localhost:8080/api'

// Register new user
export async function registerCustomer(formData) {
  const response = await fetch(`${API_BASE_URL}/users/register`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      email: formData.email.trim().toLowerCase(),
      password: formData.password,
      fullName: formData.fullName
    }),
  });

  if (!response.ok) {
    const message = await response.text()
    throw new Error(message || 'Registration failed')
  }

  return await response.text()
}

// Login user and return token + role
export async function loginUser(credentials) {
  console.log('AuthService: Attempting login for', credentials.email)
  
  const response = await fetch(`${API_BASE_URL}/users/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    credentials: 'include', // Important for session cookies
    body: JSON.stringify({
      email: credentials.email,
      password: credentials.password
    }),
  });

  console.log('AuthService: Login response status', response.status)

  if (!response.ok) {
    const message = await response.text()
    console.error('AuthService: Login failed', message)
    throw new Error(message || 'Login failed')
  }

  const responseData = await response.json()
  console.log('AuthService: Login response data', responseData)
  
  return responseData
}

// Get current user's auth token
export function getAuthToken() {
  return localStorage.getItem('token')
}

// Get current user data
export function getCurrentUser() {
  const userData = localStorage.getItem('activeUser')
  return userData ? JSON.parse(userData) : null
}

// Check if user is authenticated
export function isAuthenticated() {
  const user = getCurrentUser()
  const token = getAuthToken()
  return user && user.id && (token || true) // Allow session-based auth without token
}

// Logout user
export function logout() {
  localStorage.removeItem('activeUser')
  localStorage.removeItem('token')
  
  // Optional: call backend logout endpoint
  fetch(`${API_BASE_URL}/users/logout`, {
    method: 'POST',
    credentials: 'include'
  }).catch(err => console.warn('Logout endpoint failed:', err))
}

// Make authenticated API request
export async function makeAuthenticatedRequest(url, options = {}) {
  const token = getAuthToken()
  const headers = {
    'Content-Type': 'application/json',
    ...options.headers
  }

  if (token) {
    headers['Authorization'] = `Bearer ${token}`
  }

  return fetch(url, {
    ...options,
    headers,
    credentials: 'include' // Always include cookies
  })
}

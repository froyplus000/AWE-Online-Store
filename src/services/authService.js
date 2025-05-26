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
  const response = await fetch(`${API_BASE_URL}/users/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      email: credentials.email,
      password: credentials.password
    }),
  });

  if (!response.ok) {
    const message = await response.text()
    throw new Error(message || 'Login failed')
  }

  return await response.json()
}

const API_BASE_URL = 'http://localhost:8080/api'

// Used in SignUpForm.vue
export async function registerCustomer(formData) {
  const response = await fetch(`${API_BASE_URL}/users`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      username: formData.email,       // <-- remap email to username
      password: formData.password,
      fullName: formData.fullName     // optional, backend may ignore
    }),
  })

  if (!response.ok) throw new Error(await response.text())
  return await response.json()
}

// Used in LoginForm.vue
export async function loginUser(credentials) {
  const response = await fetch(`${API_BASE_URL}/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      username: credentials.email, // ✅ send as "username"
      password: credentials.password
    }),
  });
  
  if (!response.ok) throw new Error('Login failed');
  return await response.text();
}

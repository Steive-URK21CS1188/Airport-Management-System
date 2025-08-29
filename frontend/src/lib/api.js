// src/lib/api.js
import axios from 'axios'

// Hardcoded base URL for now — you can later switch to env-based:
// const baseURL = import.meta.env?.VITE_API_BASE || 'http://localhost:8081';
const baseURL = 'http://localhost:8081' // <-- change this if backend port changes

// Create a centralized axios instance
const api = axios.create({
  baseURL,
  withCredentials: true, // keeps cookies/session if needed
})

// Optional: attach token automatically if present
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

export default api
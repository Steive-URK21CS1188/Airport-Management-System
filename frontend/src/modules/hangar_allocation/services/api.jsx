// src/services/api.jsx
import axios from "axios";

// Create an axios instance
const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || "http://localhost:8081/api",
});

// Attach headers automatically
api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  const role = localStorage.getItem("role");

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  if (role) {
    config.headers.Role = role;
  }

  return config;
});

// ===== Hangar Allocation =====
export const checkAvailability = async (hangarId, from, to) => {
  const res = await api.get(`/hangar-allocation/availability`, {
    params: { hangarId, from, to },
  });
  return res.data;
};

export const allocateHangar = async (data) => {
  const res = await api.post(`/hangar-allocation/allocate`, data);
  return res.data;
};

// ===== Example: Fetch all hangars =====
export const getAllHangars = async () => {
  const res = await api.get(`/hangars/viewAll`);
  return res.data;
};

export default api;

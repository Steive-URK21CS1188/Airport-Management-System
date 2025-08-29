import api from '@/lib/api'

// All methods send token in headers (no query params)
const headerAuthConfig = {
  withCredentials: false,
  meta: { includeRole: true } // Your api interceptor should add token to headers
}

export const pilotsAPI = {
  list: async () =>
    (await api.get('/api/pilots/getAll', headerAuthConfig)).data,

  getById: async (id) =>
    (await api.get(`/api/pilots/${id}`, headerAuthConfig)).data,

  create: async (payload) =>
    (await api.post('/api/pilots/add', payload, headerAuthConfig)).data,

  update: async (id, payload) =>
    (await api.put(`/api/pilots/${id}`, payload, headerAuthConfig)).data,

  remove: async (id) =>
    (await api.delete(`/api/pilots/${id}`, headerAuthConfig)).data,
}

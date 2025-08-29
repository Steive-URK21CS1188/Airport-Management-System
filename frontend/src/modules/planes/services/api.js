import api from '@/lib/api'

// Plane APIs
export const getPlanes = () =>
  api.get('/api/planes/getAll', {
    meta: { includeTokenQuery: true, includeRole: true },
  })
export const getPlaneByNumber = (planeNumber) =>
  api.get(`/api/planes/getByNumber/${planeNumber}`, {
    meta: { includeTokenQuery: true, includeRole: true },
  })
export const addPlane = (plane) =>
  api.post('/api/planes/add', plane, { meta: { includeRole: true } })
export const updatePlane = (planeNumber, updatedPlane) =>
  api.put(`/api/planes/edit/${planeNumber}`, updatedPlane, {
    meta: { includeRole: true },
  })
export const deletePlane = (planeNumber) =>
  api.delete(`/api/planes/delete/${planeNumber}`, {
    meta: { includeRole: true },
  })
export const searchPlanesByOwnerEmail = (email) =>
  api.get(`/api/planes/getByOwnerEmail/${email}`, {
    meta: { includeTokenQuery: true, includeRole: true },
  })

// Owner APIs
export const getOwners = () =>
  api.get('/api/owner/getAll', {
    meta: { includeTokenQuery: true, includeRole: true },
  })
export const getOwnerByEmail = (email) =>
  api.get(`/api/owner/getByAddressEmail/${email}`, {
    meta: { includeTokenQuery: true, includeRole: true },
  })
export const updateOwnerByEmail = (email, updatedOwner) =>
  api.put(`/api/owner/updateByAddressEmail/${email}`, updatedOwner, {
    meta: { includeRole: true },
  })
export const searchOwnerPlanes = (planeNumber) =>
  api.get(`/api/owner/getByPlane/${planeNumber}`, {
    meta: { includeTokenQuery: true, includeRole: true },
  })

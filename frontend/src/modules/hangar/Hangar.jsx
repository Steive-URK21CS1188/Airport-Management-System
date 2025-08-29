import React, { useState, useEffect } from 'react'
import axios from 'axios'
import { Link, useNavigate } from 'react-router-dom' // NEW: useNavigate for redirect
import 'bootstrap/dist/css/bootstrap.min.css'
import './styles/hangar.css'

const BASE = `${
  import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081'
}/api/hangars`
const USER_BASE = `${
  import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081'
}/api/users`

const authHeader = () => {
  const token = localStorage.getItem('token')
  return token ? { Authorization: `Bearer ${token}` } : {}
}

const getHangars = async () => {
  const res = await axios.get(`${BASE}/viewAll`, { headers: authHeader() })
  return res.data
}

const getUserByIdApi = async (id) => {
  const res = await axios.get(`${USER_BASE}/id/${id}`, {
    headers: authHeader(),
  })
  return res.data
}

const addHangarApi = async (newHangar) => {
  const res = await axios.post(`${BASE}/add`, newHangar, {
    headers: authHeader(),
  })
  return res.data
}

const updateHangarApi = async (id, updatedHangar) => {
  const res = await axios.put(`${BASE}/update/${id}`, updatedHangar, {
    headers: authHeader(),
  })
  return res.data
}

const deleteHangarApi = async (id) => {
  const res = await axios.delete(`${BASE}/delete/${id}`, {
    headers: authHeader(),
  })
  return res.data
}

function Hangar() {
  const [hangars, setHangars] = useState([])
  const [myHangars, setMyHangars] = useState([])
  const [showAll, setShowAll] = useState(false)

  const [searchText, setSearchText] = useState('')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const [modalMode, setModalMode] = useState('')
  const [selectedHangar, setSelectedHangar] = useState(null)
  const [deleteModal, setDeleteModal] = useState(null)

  const [formName, setFormName] = useState('')
  const [formLocation, setFormLocation] = useState('')
  const [formCapacity, setFormCapacity] = useState('')
  const [formError, setFormError] = useState('')
  const [deleteError, setDeleteError] = useState('')

  const role = localStorage.getItem('role')
  const userId = localStorage.getItem('userId')
  const token = localStorage.getItem('token') // NEW
  const navigate = useNavigate() // NEW

  const fetchData = async () => {
    setLoading(true)
    setError('')
    try {
      const data = await getHangars()
      const hangarsWithCreator = await Promise.all(
        data.map(async (h) => {
          try {
            const creator = await getUserByIdApi(h.userId)
            return {
              ...h,
              creatorName: `${creator.username} (ID: ${creator.userId})`,
            }
          } catch {
            return { ...h, creatorName: 'Unknown' }
          }
        })
      )
      setHangars(hangarsWithCreator)
      if (role === 'ADMIN') {
        setMyHangars(
          hangarsWithCreator.filter((h) => String(h.userId) === String(userId))
        )
      } else if (role === 'MANAGER') {
        setMyHangars(hangarsWithCreator)
        setShowAll(true)
      }
    } catch (err) {
      console.error(err)
      setError(err.response?.data?.message || 'Failed to fetch hangars.')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    if (!token || !role) {
      navigate('/login')
      return
    }

    fetchData()
  }, [token, role, navigate])

  useEffect(() => {
    fetchData()
  }, [])

  const openModal = (mode, id = null) => {
    setModalMode(mode)
    setFormError('')
    if (id) {
      const data = hangars.find((h) => h.hangarId === id)
      setSelectedHangar(data)
      setFormName(data.hangarName)
      setFormLocation(data.hangarLocation)
      setFormCapacity(data.capacity)
    } else {
      setSelectedHangar(null)
      setFormName('')
      setFormLocation('')
      setFormCapacity('')
    }
  }

  const closeModal = () => {
    setModalMode('')
    setSelectedHangar(null)
    setFormName('')
    setFormLocation('')
    setFormCapacity('')
    setFormError('')
  }

  const handleSave = async (e) => {
    e.preventDefault()
    setFormError('')
    if (!formName || !formLocation || !formCapacity) {
      setFormError('Please fill in all fields')
      return
    }
    const capacityNum = parseInt(formCapacity, 10)
    if (capacityNum > 5) {
      setFormError('Max capacity allowed is 5')
      return
    }
    if (capacityNum < 2) {
      setFormError('Min capacity allowed is 2')
      return
    }
    try {
      if (modalMode === 'add') {
        await addHangarApi({
          hangarName: formName,
          hangarLocation: formLocation,
          capacity: capacityNum,
          userId: parseInt(userId, 10),
        })
      } else if (modalMode === 'edit') {
        await updateHangarApi(selectedHangar.hangarId, {
          hangarName: formName,
          hangarLocation: formLocation,
          capacity: capacityNum,
          userId: parseInt(userId, 10),
        })
      }
      closeModal()
      fetchData()
    } catch (err) {
      setFormError(err.response?.data?.message || 'Failed to save hangar.')
    }
  }

  const handleDeleteClick = (hangar) => {
    setDeleteError('')
    setDeleteModal(hangar)
  }

  const confirmDelete = async () => {
    setDeleteError('')
    try {
      await deleteHangarApi(deleteModal.hangarId)
      setDeleteModal(null)
      fetchData()
    } catch (err) {
      setDeleteError(
        err.response?.data?.message ||
          'Cannot delete hangar; it may be allocated to a plane.'
      )
    }
  }

  const filteredHangars = (showAll ? hangars : myHangars).filter((h) => {
    const lowerSearch = searchText.trim().toLowerCase()
    const minCap = parseInt(lowerSearch, 10)
    return (
      h.hangarName?.toLowerCase().includes(lowerSearch) ||
      h.hangarLocation?.toLowerCase().includes(lowerSearch) ||
      (!isNaN(minCap) && h.capacity >= minCap) ||
      h.creatorName?.toLowerCase().includes(lowerSearch)
    )
  })

  return (
    <div className="container py-4 hangar-page">
      <div className="card shadow-sm border-0 p-4">
        <h1 className="mb-4 hangar-title">Hangar Management</h1>
        <div className="d-flex flex-wrap justify-content-between align-items-center mb-4 gap-2">
          <div className="input-group w-50">
            <input
              type="text"
              className="form-control"
              placeholder="Search by name, location, capacity, or creator"
              value={searchText}
              onChange={(e) => setSearchText(e.target.value)}
            />
            <button
              className="btn btn-outline-secondary"
              onClick={() => setSearchText('')}
            >
              Clear
            </button>
          </div>
          {role === 'ADMIN' && (
            <div className="d-flex gap-2">
              <button
                className="btn btn-outline-primary"
                onClick={() => setShowAll((prev) => !prev)}
              >
                {showAll ? 'View My Hangars' : 'View All Hangars'}
              </button>
              <button
                className="btn btn-success"
                onClick={() => openModal('add')}
              >
                + Add Hangar
              </button>
            </div>
          )}
        </div>

        {loading ? (
          <div className="text-center py-5">
            <div className="spinner-border text-primary" role="status"></div>
            <p className="mt-3">Loading hangars...</p>
          </div>
        ) : error ? (
          <p className="text-danger">{error}</p>
        ) : filteredHangars.length === 0 ? (
          <p className="text-muted">No hangars found.</p>
        ) : (
          <div className="table-responsive">
            <table className="table table-hover align-middle">
              <thead className="table-primary">
                <tr>
                  <th>Name</th>
                  <th>Capacity</th>
                  <th>Location</th>
                  <th>Created By</th>
                  {role === 'ADMIN' && <th>Actions</th>}
                </tr>
              </thead>
              <tbody>
                {filteredHangars.map((hangar) => (
                  <tr key={hangar.hangarId}>
                    <td>{hangar.hangarName}</td>
                    <td>{hangar.capacity}</td>
                    <td>{hangar.hangarLocation}</td>
                    <td>{hangar.creatorName}</td>
                    {role === 'ADMIN' && (
                      <td>
                        <button
                          className="btn btn-info btn-sm me-2"
                          onClick={() => openModal('edit', hangar.hangarId)}
                        >
                          Edit
                        </button>
                        <button
                          className="btn btn-danger btn-sm"
                          onClick={() => handleDeleteClick(hangar)}
                        >
                          Delete
                        </button>
                      </td>
                    )}
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        <Link
          to="/"
          className="btn btn-outline-primary w-100 mt-4 rounded-pill"
        >
          Site Home
        </Link>
      </div>

      {/* Add/Edit Modal */}
      {modalMode && role === 'ADMIN' && (
        <div
          className="modal show d-block"
          tabIndex="-1"
          style={{ background: 'rgba(0,0,0,0.5)' }}
        >
          <div className="modal-dialog modal-dialog-centered">
            <div className="modal-content border-0 shadow">
              <form onSubmit={handleSave}>
                <div className="modal-header bg-primary text-white">
                  <h5 className="modal-title">
                    {modalMode === 'add' ? 'Add Hangar' : 'Edit Hangar'}
                  </h5>
                  <button
                    type="button"
                    className="btn-close"
                    onClick={closeModal}
                  ></button>
                </div>
                <div className="modal-body">
                  {formError && <p className="text-danger">{formError}</p>}
                  <div className="mb-3">
                    <label className="form-label">Hangar Name</label>
                    <input
                      type="text"
                      className="form-control"
                      value={formName}
                      onChange={(e) => setFormName(e.target.value)}
                    />
                  </div>
                  <div className="mb-3">
                    <label className="form-label">Location</label>
                    <input
                      type="text"
                      className="form-control"
                      value={formLocation}
                      onChange={(e) => setFormLocation(e.target.value)}
                    />
                  </div>
                  <div className="mb-3">
                    <label className="form-label">Capacity</label>
                    <input
                      type="number"
                      className="form-control"
                      value={formCapacity}
                      onChange={(e) => setFormCapacity(e.target.value)}
                    />
                  </div>
                </div>
                <div className="modal-footer">
                  <button
                    type="button"
                    className="btn btn-secondary"
                    onClick={closeModal}
                  >
                    Close
                  </button>
                  <button type="submit" className="btn btn-success">
                    Save
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      )}

      {/* Delete Modal */}
      {deleteModal && (
        <div
          className="modal show d-block"
          tabIndex="-1"
          style={{ background: 'rgba(0,0,0,0.56)' }}
        >
          <div className="modal-dialog modal-dialog-centered">
            <div className="modal-content border-0 shadow">
              <div className="modal-header bg-danger text-white">
                <h5 className="modal-title">Confirm Delete</h5>
                <button
                  type="button"
                  className="btn-close"
                  onClick={() => setDeleteModal(null)}
                ></button>
              </div>
              <div className="modal-body">
                {deleteError && <p className="text-danger">{deleteError}</p>}
                <p>
                  Are you sure you want to delete hangar{' '}
                  <strong>{deleteModal.hangarName}</strong>?
                </p>
              </div>
              <div className="modal-footer">
                <button
                  type="button"
                  className="btn btn-secondary"
                  onClick={() => setDeleteModal(null)}
                >
                  Cancel
                </button>
                <button
                  type="button"
                  className="btn btn-danger"
                  onClick={confirmDelete}
                >
                  Delete
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

export default Hangar

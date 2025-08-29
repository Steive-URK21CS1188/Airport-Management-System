import React, { useState, useEffect } from 'react'
import axios from 'axios'
import { Link, useNavigate } from 'react-router-dom'
import 'bootstrap/dist/css/bootstrap.min.css'
import './styles/pilots.css'

const BASE = `${
  import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081'
}/api/pilots`
const USER_BASE = `${
  import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081'
}/api/users`
const ADDRESS_BASE = `${
  import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081'
}/api/address`

const authHeader = () => {
  const token = localStorage.getItem('token')
  const role = localStorage.getItem('role')
  return token ? { Authorization: `Bearer ${token}`, Role: role } : {}
}

const getPilots = async () => {
  const res = await axios.get(`${BASE}/getAll`, {
    params: { token: localStorage.getItem('token') },
  })
  return res.data
}

const getPilotByIdApi = async (id) => {
  const res = await axios.get(`${BASE}/${id}`, {
    params: { token: localStorage.getItem('token') },
    headers: authHeader(),
  })
  return res.data
}

const getUserByIdApi = async (id) => {
  const res = await axios.get(`${USER_BASE}/id/${id}`, {
    headers: authHeader(),
  })
  return res.data
}

const getAddressByIdApi = async (id) => {
  const res = await axios.get(`${ADDRESS_BASE}/getById/${id}`, {
    headers: authHeader(),
  })
  return res.data
}

const addPilotApi = async (newPilot) => {
  const res = await axios.post(`${BASE}/add`, newPilot, {
    headers: authHeader(),
  })
  return res.data
}

const updatePilotApi = async (id, updatedPilot) => {
  const res = await axios.put(`${BASE}/${id}`, updatedPilot, {
    params: { token: localStorage.getItem('token') },
    headers: authHeader(),
  })
  return res.data
}

const deletePilotApi = async (id) => {
  const res = await axios.delete(`${BASE}/${id}`, {
    params: { token: localStorage.getItem('token') },
    headers: authHeader(),
  })
  return res.data
}

function Pilots() {
  const [pilots, setPilots] = useState([])
  const [searchText, setSearchText] = useState('')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [modalMode, setModalMode] = useState('')
  const [selectedPilot, setSelectedPilot] = useState(null)
  const [deleteModal, setDeleteModal] = useState(null)
  const [viewModal, setViewModal] = useState(null)
  const [formError, setFormError] = useState('')
  const [formErrors, setFormErrors] = useState({})
  const [popupMessage, setPopupMessage] = useState(null)
  const [popupType, setPopupType] = useState('success')

  const [name, setName] = useState('')
  const [licenseNo, setLicenseNo] = useState('')
  const [address, setAddress] = useState({
    addressId: null,
    houseNo: '',
    street: '',
    city: '',
    state: '',
    pincode: '',
    email: '',
    phoneno: '',
  })

  const role = localStorage.getItem('role')
  const userId = localStorage.getItem('userId')
  const navigate = useNavigate()
  const [showLoginModal, setShowLoginModal] = useState(false)

  
  useEffect(() => {
    if (!role || !localStorage.getItem('token')) {
      setShowLoginModal(true)
      setTimeout(() => {
        navigate('/login')
      }, 2000)
    } else {
      fetchData()
    }
  }, [role, navigate])

  const fetchData = async () => {
    setLoading(true)
    setError('')
    try {
      const data = await getPilots()
      const pilotsWithCreator = await Promise.all(
        data.map(async (p) => {
          try {
            const creator = await getUserByIdApi(p.userId)
            return {
              ...p,
              creatorName: `${creator.username} (ID: ${creator.userId})`,
            }
          } catch {
            return { ...p, creatorName: 'Unknown' }
          }
        })
      )
      setPilots(pilotsWithCreator)
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to fetch pilots.')
    } finally {
      setLoading(false)
    }
  }

  const validateForm = () => {
    const errors = {}
    if (!name.trim()) errors.name = 'Name is required'
    if (!licenseNo.trim()) {
      errors.licenseNo = 'License No is required'
    } else if (
      pilots.some(
        (p) =>
          p.licenseNo.toLowerCase() === licenseNo.toLowerCase() &&
          (!selectedPilot || p.pilotId !== selectedPilot.pilotId)
      )
    ) {
      errors.licenseNo = 'License No already exists'
    }
    if (!address.houseNo.trim()) errors.houseNo = 'House No is required'
    if (!address.street.trim()) errors.street = 'Street is required'
    if (!address.city.trim()) errors.city = 'City is required'
    if (!address.state.trim()) errors.state = 'State is required'
    if (!address.pincode.trim()) {
      errors.pincode = 'Pincode is required'
    } else if (!/^\d{6}$/.test(address.pincode)) {
      errors.pincode = 'Pincode must be 6 digits'
    }
    if (!address.email.trim()) {
      errors.email = 'Email is required'
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(address.email)) {
      errors.email = 'Invalid email format'
    }
    if (!address.phoneno.trim()) {
      errors.phoneno = 'Phone number is required'
    } else if (!/^\d{10}$/.test(address.phoneno)) {
      errors.phoneno = 'Phone number must be 10 digits'
    }
    return errors
  }

  const openModal = async (mode, id = null) => {
    setModalMode(mode)
    setFormError('')
    setFormErrors({})

    if (id) {
      try {
        const pilot = await getPilotByIdApi(id)
        let addressData = pilot.address

        if (typeof addressData === 'string') {
          const addrId = addressData.split('/').pop()
          addressData = await getAddressByIdApi(addrId)
        } else if (addressData?.addressId && !addressData.city) {
          addressData = await getAddressByIdApi(addressData.addressId)
        }

        setSelectedPilot(pilot)
        setName(pilot.name)
        setLicenseNo(pilot.licenseNo)
        setAddress({
          addressId: addressData?.addressId || null,
          houseNo: addressData?.houseNo || '',
          street: addressData?.street || '',
          city: addressData?.city || '',
          state: addressData?.state || '',
          pincode: addressData?.pincode || '',
          email: addressData?.email || '',
          phoneno: addressData?.phoneno || '',
        })
      } catch (err) {
        setFormError('Failed to load pilot data')
      }
    } else {
      setSelectedPilot(null)
      setName('')
      setLicenseNo('')
      setAddress({
        addressId: null,
        houseNo: '',
        street: '',
        city: '',
        state: '',
        pincode: '',
        email: '',
        phoneno: '',
      })
    }
  }

  const openViewModal = async (id) => {
    try {
      const pilot = await getPilotByIdApi(id)
      let addressData = pilot.address

      if (typeof addressData === 'string') {
        const addrId = addressData.split('/').pop()
        addressData = await getAddressByIdApi(addrId)
      } else if (addressData?.addressId && !addressData.city) {
        addressData = await getAddressByIdApi(addressData.addressId)
      }

      setViewModal({
        ...pilot,
        address: addressData,
      })
    } catch (err) {
      setPopupType('error')
      setPopupMessage('Failed to fetch pilot details')
    }
  }

  const closeModal = () => {
    setModalMode('')
    setSelectedPilot(null)
    setFormError('')
    setFormErrors({})
  }

  const handleSave = async (e) => {
    e.preventDefault()
    const errors = validateForm()
    if (Object.keys(errors).length > 0) {
      setFormErrors(errors)
      return
    }
    try {
      const payload = {
        name,
        licenseNo,
        userId: parseInt(userId, 10),
        address: { ...address },
      }
      if (modalMode === 'add') {
        await addPilotApi(payload)
        setPopupType('success')
        setPopupMessage('Pilot added successfully!')
      } else if (modalMode === 'edit') {
        await updatePilotApi(selectedPilot.pilotId, payload)
        setPopupType('success')
        setPopupMessage('Pilot updated successfully!')
      }
      closeModal()
      fetchData()
    } catch (err) {
      setFormError(err.response?.data?.message || 'Failed to save pilot.')
    }
  }

  const handleDeleteClick = (pilot) => {
    setPopupType('confirm')
    setPopupMessage(
      <>
        <p>
          Are you sure you want to delete pilot <strong>{pilot.name}</strong>?
        </p>
        <div className="d-flex justify-content-center gap-2 mt-3">
          <button
            className="btn btn-danger"
            onClick={() => setPopupMessage(null)}
          >
            Cancel
          </button>
          <button
            className="btn btn-danger"
            onClick={() => confirmDelete(pilot.pilotId)}
          >
            Delete
          </button>
        </div>
      </>
    )
  }
  const confirmDelete = async (id) => {
    try {
      await deletePilotApi(id)
      setPopupType('success')
      setPopupMessage('Pilot deleted successfully!')
      fetchData()
    } catch (err) {
      setPopupType('error')
      setPopupMessage(
        err.response?.data?.message ||
          'Failed to delete pilot because he/she has been allocated to a plane'
      )
    }
  }

  const filteredPilots = pilots.filter((p) => {
    const lowerSearch = searchText.trim().toLowerCase()
    return (
      p.name?.toLowerCase().includes(lowerSearch) ||
      p.licenseNo?.toLowerCase().includes(lowerSearch) ||
      p.creatorName?.toLowerCase().includes(lowerSearch)
    )
  })

 
  useEffect(() => {
    if (popupMessage) {
      const timer = setTimeout(() => setPopupMessage(null), 3000)
      return () => clearTimeout(timer)
    }
  }, [popupMessage])

  return (
    <div className="container py-4 pilot-page">
      {/* Login Modal */}
      {showLoginModal && (
        <div
          className="modal show d-block"
          style={{ background: 'rgba(0,0,0,0.5)' }}
        >
          <div className="modal-dialog modal-dialog-centered">
            <div className="modal-content border-0 shadow">
              <div className="modal-header bg-danger text-white">
                <h5 className="modal-title">Login Required</h5>
              </div>
              <div className="modal-body text-center">
                <p>Please login to access the Pilot Management page.</p>
                <p>Redirecting to login page...</p>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Popup Message Modal */}

      {popupMessage && (
        <div
          className="modal show d-block"
          style={{ background: 'rgba(0,0,0,0.5)' }}
        >
          <div className="modal-dialog modal-dialog-centered">
            <div className="modal-content shadow">
              <div
                className={`modal-header ${
                  popupType === 'success'
                    ? 'bg-success'
                    : popupType === 'error'
                    ? 'bg-danger'
                    : 'bg-warning'
                } text-white`}
              >
                <h5 className="modal-title">
                  {popupType === 'success'
                    ? 'Success'
                    : popupType === 'error'
                    ? 'Error'
                    : 'Confirm Delete'}
                </h5>
                <button
                  type="button"
                  className="btn-close"
                  onClick={() => setPopupMessage(null)}
                ></button>
              </div>
              <div className="modal-body text-center">
                {popupMessage}
                {popupType !== 'confirm' && (
                  <button
                    className="btn btn-primary mt-3"
                    onClick={() => setPopupMessage(null)}
                  >
                    OK
                  </button>
                )}
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Main Page */}
      {!showLoginModal && (
        <>
          {/* Pilots Card */}
          <div className="card shadow-sm border-0 p-4">
            <h1 className="mb-4 pilot-title">Pilot Management</h1>

            <div className="d-flex flex-wrap justify-content-between align-items-center mb-4 gap-2">
              <div className="input-group w-50">
                <input
                  type="text"
                  className="form-control"
                  placeholder="Search by name, license, or creator"
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
                <button
                  className="btn btn-success"
                  onClick={() => openModal('add')}
                >
                  + Add Pilot
                </button>
              )}
            </div>

            {loading ? (
              <div className="text-center py-5">
                <div
                  className="spinner-border text-primary"
                  role="status"
                ></div>
                <p className="mt-3">Loading pilots...</p>
              </div>
            ) : error ? (
              <p className="text-danger">{error}</p>
            ) : filteredPilots.length === 0 ? (
              <p className="text-muted">No pilots found.</p>
            ) : (
              <>
                <div className="table-responsive">
                  <table className="table table-hover align-middle">
                    <thead className="table-primary">
                      <tr>
                        <th>Name</th>
                        <th>License No</th>
                        <th>Created By</th>
                        {role === 'ADMIN' && <th>Actions</th>}
                      </tr>
                    </thead>
                    <tbody>
                      {filteredPilots.map((pilot) => (
                        <tr key={pilot.pilotId}>
                          <td>{pilot.name}</td>
                          <td>{pilot.licenseNo}</td>
                          <td>{pilot.creatorName}</td>
                          {role === 'ADMIN' && (
                            <td>
                              <button
                                className="btn btn-primary btn-sm me-2"
                                onClick={() => openViewModal(pilot.pilotId)}
                              >
                                View
                              </button>
                              <button
                                className="btn btn-info btn-sm me-2"
                                onClick={() => openModal('edit', pilot.pilotId)}
                              >
                                Edit
                              </button>
                              <button
                                className="btn btn-danger btn-sm"
                                onClick={() => handleDeleteClick(pilot)}
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

                <Link
                  to="/"
                  className="btn btn-outline-primary w-100 mt-4 rounded-pill"
                >
                  Site Home
                </Link>
              </>
            )}
          </div>

          {/* Add/Edit Modal */}
          {modalMode && (
            <div
              className="modal show d-block"
              style={{ background: 'rgba(0,0,0,0.5)' }}
            >
              <div className="modal-dialog modal-dialog-centered">
                <div className="modal-content shadow">
                  <div className="modal-header bg-info text-white">
                    <h5 className="modal-title">
                      {modalMode === 'add' ? 'Add Pilot' : 'Edit Pilot'}
                    </h5>
                    <button
                      type="button"
                      className="btn-close"
                      onClick={closeModal}
                    ></button>
                  </div>
                  <div className="modal-body">
                    {formError && <p className="text-danger">{formError}</p>}
                    <form onSubmit={handleSave}>
                      <div className="mb-2">
                        <label className="form-label">Name</label>
                        <input
                          type="text"
                          className={`form-control ${
                            formErrors.name ? 'is-invalid' : ''
                          }`}
                          value={name}
                          onChange={(e) => setName(e.target.value)}
                        />
                        {formErrors.name && (
                          <div className="invalid-feedback">
                            {formErrors.name}
                          </div>
                        )}
                      </div>
                      <div className="mb-2">
                        <label className="form-label">License No</label>
                        <input
                          type="text"
                          className={`form-control ${
                            formErrors.licenseNo ? 'is-invalid' : ''
                          }`}
                          value={licenseNo}
                          onChange={(e) => setLicenseNo(e.target.value)}
                        />
                        {formErrors.licenseNo && (
                          <div className="invalid-feedback">
                            {formErrors.licenseNo}
                          </div>
                        )}
                      </div>
                      <hr />
                      <h6>Address</h6>
                      {[
                        'houseNo',
                        'street',
                        'city',
                        'state',
                        'pincode',
                        'email',
                        'phoneno',
                      ].map((field) => (
                        <div className="mb-2" key={field}>
                          <label className="form-label">{field}</label>
                          <input
                            type="text"
                            className={`form-control ${
                              formErrors[field] ? 'is-invalid' : ''
                            }`}
                            value={address[field]}
                            onChange={(e) =>
                              setAddress({
                                ...address,
                                [field]: e.target.value,
                              })
                            }
                          />
                          {formErrors[field] && (
                            <div className="invalid-feedback">
                              {formErrors[field]}
                            </div>
                          )}
                        </div>
                      ))}
                      <div className="text-end">
                        <button
                          type="button"
                          className="btn btn-secondary me-2"
                          onClick={closeModal}
                        >
                          Cancel
                        </button>
                        <button type="submit" className="btn btn-primary">
                          Save
                        </button>
                      </div>
                    </form>
                  </div>
                </div>
              </div>
            </div>
          )}

          {/* Delete Confirmation Modal */}
          {deleteModal && (
            <div
              className="modal show d-block"
              style={{ background: 'rgba(0,0,0,0.5)' }}
            >
              <div className="modal-dialog modal-dialog-centered">
                <div className="modal-content shadow">
                  <div className="modal-header bg-danger text-white">
                    <h5 className="modal-title">Confirm Delete</h5>
                    <button
                      type="button"
                      className="btn-close"
                      onClick={() => setDeleteModal(null)}
                    ></button>
                  </div>
                  <div className="modal-body">
                    <p>
                      Are you sure you want to delete pilot{' '}
                      <strong>{deleteModal.name}</strong>?
                    </p>
                  </div>
                  <div className="modal-footer">
                    <button
                      className="btn btn-secondary"
                      onClick={() => setDeleteModal(null)}
                    >
                      Cancel
                    </button>
                    <button className="btn btn-danger" onClick={confirmDelete}>
                      Delete
                    </button>
                  </div>
                </div>
              </div>
            </div>
          )}

          {/* View Modal */}
          {viewModal && (
            <div
              className="modal show d-block"
              style={{ background: 'rgba(0,0,0,0.5)' }}
            >
              <div className="modal-dialog modal-dialog-centered">
                <div className="modal-content shadow">
                  <div className="modal-header bg-primary text-white">
                    <h5 className="modal-title">Pilot Details</h5>
                    <button
                      type="button"
                      className="btn-close"
                      onClick={() => setViewModal(null)}
                    ></button>
                  </div>
                  <div className="modal-body">
                    <p>
                      <strong>Name:</strong> {viewModal.name}
                    </p>
                    <p>
                      <strong>License No:</strong> {viewModal.licenseNo}
                    </p>
                    <hr />
                    <h6>Address</h6>
                    <p>
                      {viewModal.address?.houseNo}, {viewModal.address?.street},{' '}
                      {viewModal.address?.city}, {viewModal.address?.state} -{' '}
                      {viewModal.address?.pincode}
                    </p>
                    <p>
                      <strong>Email:</strong> {viewModal.address?.email}
                    </p>
                    <p>
                      <strong>Phone:</strong> {viewModal.address?.phoneno}
                    </p>
                  </div>
                  <div className="modal-footer">
                    <button
                      className="btn btn-secondary"
                      onClick={() => setViewModal(null)}
                    >
                      Close
                    </button>
                  </div>
                </div>
              </div>
            </div>
          )}
        </>
      )}
    </div>
  )
}

export default Pilots

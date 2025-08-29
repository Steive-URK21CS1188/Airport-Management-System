import React, { useEffect, useState } from 'react'
import axios from 'axios'
import { Link, useNavigate } from 'react-router-dom'
import 'bootstrap/dist/css/bootstrap.min.css'

const PlaneAllocation = () => {
  const [planes, setPlanes] = useState([])
  const [pilots, setPilots] = useState([])
  const [managers, setManagers] = useState([])
  const [allocations, setAllocations] = useState([])
  const [loading, setLoading] = useState(true)

  const [formData, setFormData] = useState({
    planeId: '',
    pilotId: '',
    fromDate: '',
    toDate: '',
    managerUserId: '',
  })

  const [modal, setModal] = useState({
    show: false,
    title: '',
    message: '',
    type: 'info',
    onConfirm: null,
  })

  const API_BASE = 'http://localhost:8081/api'
  const navigate = useNavigate()
  const token = localStorage.getItem('token')
  const role = localStorage.getItem('role')

  const getUserById = async (id) => {
    try {
      const res = await axios.get(`${API_BASE}/users/id/${id}`, {
        headers: { Authorization: `Bearer ${token}` },
      })
      return res.data
    } catch {
      return null
    }
  }

  const getPlaneById = async (id) => {
    try {
      const res = await axios.get(`${API_BASE}/planes/${id}`, {
        headers: { Authorization: `Bearer ${token}` },
      })
      return res.data
    } catch {
      return null
    }
  }

  const getPilotById = async (id) => {
    try {
      const res = await axios.get(`${API_BASE}/pilots/${id}`, {
        headers: { Authorization: `Bearer ${token}` },
      })
      return res.data
    } catch {
      return null
    }
  }

  const loadAllocations = (headers) => {
    axios
      .get(`${API_BASE}/plane-allocation/all`, headers)
      .then(async (res) => {
        const allocationsWithNames = await Promise.all(
          res.data.map(async (alloc) => {
            const plane = await getPlaneById(alloc.planeId)
            const pilot = await getPilotById(alloc.pilotId)
            const manager = await getUserById(alloc.managerUserId)

            return {
              ...alloc,
              planeDisplay: plane
                ? `${plane.planeNumber} - ${plane.model} (ID: ${plane.planeId})`
                : `ID: ${alloc.planeId}`,
              pilotDisplay: pilot
                ? `${pilot.name} (${pilot.licenseNo}) (ID: ${pilot.pilotId})`
                : `ID: ${alloc.pilotId}`,
              managerDisplay: manager
                ? `${manager.username} (ID: ${manager.userId})`
                : `ID: ${alloc.managerUserId}`,
              status:
                new Date(alloc.toDate) < new Date() ? 'Completed' : 'Active',
            }
          })
        )

        allocationsWithNames.sort((a, b) => {
          if (a.status === b.status)
            return new Date(a.fromDate) - new Date(b.fromDate)
          return a.status === 'Active' ? -1 : 1
        })

        setAllocations(allocationsWithNames)
      })
      .catch((err) => console.error('Error fetching allocations:', err))
  }

  useEffect(() => {
    if (!token || !role) {
      showModal('Login Required', 'Please login first!', 'error')
      navigate('/login')
      return
    }

    setLoading(false)
    const headers = {
      headers: { Role: role, Authorization: `Bearer ${token}` },
    }

    if (role === 'MANAGER') {
      axios
        .get(`${API_BASE}/planes/getAll`, {
          params: { token },
          headers: { Role: role },
        })
        .then((res) => setPlanes(res.data))
        .catch((err) => console.error('Error fetching planes:', err))

      axios
        .get(`${API_BASE}/pilots/getAll`, { params: { token } })
        .then((res) => setPilots(res.data))
        .catch((err) => console.error('Error fetching pilots:', err))

      axios
        .get(`${API_BASE}/users/role/MANAGER`, {
          headers: { Authorization: `Bearer ${token}` },
        })
        .then((res) => setManagers(res.data))
        .catch((err) => console.error('Error fetching managers:', err))
    }

    loadAllocations(headers)
  }, [token, role, navigate])

  const handleChange = (e) =>
    setFormData((prev) => ({ ...prev, [e.target.name]: e.target.value }))

  const showModal = (title, message, type = 'info', onConfirm = null) =>
    setModal({ show: true, title, message, type, onConfirm })

  const hideModal = () => setModal({ ...modal, show: false })

  const isPlaneAvailable = (planeId, fromDate, toDate) => {
    const from = new Date(fromDate)
    const to = new Date(toDate)
    return !allocations.some((alloc) => {
      if (alloc.planeId !== planeId || alloc.status === 'Completed')
        return false
      const allocFrom = new Date(alloc.fromDate)
      const allocTo = new Date(alloc.toDate)
      return from < allocTo && to > allocFrom
    })
  }

  const handleSubmit = async (e) => {
    e.preventDefault()

    if (role !== 'MANAGER') {
      showModal('Access Denied', 'Only managers can allocate planes.', 'error')
      return
    }

    const headers = {
      headers: { Role: role, Authorization: `Bearer ${token}` },
    }

    try {
      const res = await axios.get(`${API_BASE}/plane-allocation/all`, headers)
      const latestAllocations = res.data

      const from = new Date(formData.fromDate)
      const to = new Date(formData.toDate)

      const conflict = latestAllocations.some((alloc) => {
        if (alloc.planeId !== formData.planeId) return false

        const allocFrom = new Date(alloc.fromDate)
        const allocTo = new Date(alloc.toDate)
        const isCompleted = new Date(alloc.toDate) < new Date()

        return !isCompleted && from < allocTo && to > allocFrom
      })

      if (conflict) {
        showModal(
          'Allocation Conflict',
          'This plane is already allocated during the selected period.',
          'error'
        )
        return
      }

      const allocationRes = await axios.post(
        `${API_BASE}/plane-allocation/allocate`,
        formData,
        {
          headers: {
            'Content-Type': 'application/json',
            Role: role,
            Authorization: `Bearer ${token}`,
          },
        }
      )

      showModal('Success', allocationRes.data, 'success')
      loadAllocations(headers)
    } catch (err) {
      console.error('Error allocating plane:', err)

      let errorMessage = 'Failed to allocate plane'
      if (err.response && err.response.data) {
        errorMessage =
          err.response.data.message || JSON.stringify(err.response.data)
      }

      showModal('Error', errorMessage, 'error')
    }
  }

  const handleDelete = (alloc) => {
    if (role !== 'MANAGER') {
      showModal(
        'Access Denied',
        'Only managers can delete allocations.',
        'error'
      )
      return
    }

    showModal(
      'Confirm Delete',
      `Are you sure you want to delete this allocation?\nPlane ID: ${
        alloc.planeId
      }\nPilot ID: ${alloc.pilotId}\nFrom: ${new Date(
        alloc.fromDate
      ).toLocaleString()}`,
      'confirm',
      () => {
        const fromDateISO = encodeURIComponent(alloc.fromDate)
        const headers = {
          headers: { Role: role, Authorization: `Bearer ${token}` },
        }

        axios
          .delete(
            `${API_BASE}/plane-allocation/${alloc.planeId}/${alloc.pilotId}/${fromDateISO}`,
            headers
          )
          .then(() => {
            showModal('Success', 'Allocation deleted successfully', 'success')
            loadAllocations(headers)
          })
          .catch((err) => {
            console.error('Error deleting allocation:', err)
            showModal('Error', 'Failed to delete allocation', 'error')
          })

        hideModal()
      }
    )
  }

  if (loading) return <div className="container py-4">Checking login...</div>

  return (
    <div className="container py-4">
      <style>{`
        .allocation-table { border-radius: 8px; overflow: hidden; box-shadow: 0 4px 12px rgba(0,0,0,0.08); }
        .allocation-table thead { font-weight:600; font-size:0.95rem; text-transform:uppercase; }
        .allocation-table tbody tr { transition: transform 0.2s ease, background-color 0.2s ease; }
        .allocation-table tbody tr:hover { background-color: rgba(30,144,255,0.1); transform: scale(1.01); }
        .allocation-table td, .allocation-table th { vertical-align: middle; }
        .table-secondary td { color: #6c757d; }
        .modal-dialog { max-width: 500px; width: 90%; }
      `}</style>

      <h2
        className="fw-bold mb-4 text-center"
        style={{ color: 'var(--primary-color)' }}
      >
        Plane Allocation
      </h2>

      {/* Allocation Form */}
      {role === 'MANAGER' && (
        <div className="card shadow p-4 mb-4" style={{ overflow: 'visible' }}>
          <form onSubmit={handleSubmit}>
            <div className="row g-3">
              <div className="col-md-3">
                <label className="form-label">Plane</label>
                <select
                  name="planeId"
                  className="form-select"
                  value={formData.planeId}
                  onChange={handleChange}
                  required
                >
                  <option value="">Select Plane</option>
                  {planes.map((p) => (
                    <option key={p.planeId} value={p.planeId}>
                      (ID:{p.planeId}) - {p.planeNumber} - {p.model}
                    </option>
                  ))}
                </select>
              </div>

              <div className="col-md-3">
                <label className="form-label">Pilot</label>
                <select
                  name="pilotId"
                  className="form-select"
                  value={formData.pilotId}
                  onChange={handleChange}
                  required
                >
                  <option value="">Select Pilot</option>
                  {pilots.map((p) => (
                    <option key={p.pilotId} value={p.pilotId}>
                      {p.name} (ID:{p.pilotId}) : ({p.licenseNo})
                    </option>
                  ))}
                </select>
              </div>

              <div className="col-md-3">
                <label className="form-label">From Date</label>
                <input
                  type="datetime-local"
                  name="fromDate"
                  className="form-control"
                  value={formData.fromDate}
                  onChange={handleChange}
                  required
                  min={new Date().toISOString().slice(0, 16)} // restrict past dates
                />
              </div>

              <div className="col-md-3">
                <label className="form-label">To Date</label>
                <input
                  type="datetime-local"
                  name="toDate"
                  className="form-control"
                  value={formData.toDate}
                  onChange={handleChange}
                  required
                  min={
                    formData.fromDate || new Date().toISOString().slice(0, 16)
                  } // toDate cannot be before fromDate
                />
              </div>

              <div className="col-md-3">
                <label className="form-label">Manager</label>
                <select
                  name="managerUserId"
                  className="form-select"
                  value={formData.managerUserId}
                  onChange={handleChange}
                  required
                >
                  <option value="">Select Manager</option>
                  {managers.map((m) => (
                    <option key={m.userId} value={m.userId}>
                      Name: {m.username} | ID: {m.userId}
                    </option>
                  ))}
                </select>
              </div>

              <div className="col-12">
                <button type="submit" className="btn btn-primary">
                  Allocate
                </button>
              </div>
            </div>
          </form>
        </div>
      )}

      {/* Allocations Table */}
      <div className="card shadow p-4">
        <h4 className="mb-3" style={{ color: 'var(--primary-color)' }}>
          Current Allocations
        </h4>
        <div className="table-responsive">
          <table className="table table-hover table-bordered align-middle text-center allocation-table">
            <thead className="table-primary">
              <tr>
                <th>Plane</th>
                <th>Pilot</th>
                <th>From Date</th>
                <th>To Date</th>
                <th>Manager</th>
                {role === 'MANAGER' && <th>Actions</th>}
              </tr>
            </thead>
            <tbody>
              {allocations.map((alloc) => (
                <tr
                  key={`${alloc.planeId}-${alloc.pilotId}-${alloc.fromDate}`}
                  className={
                    alloc.status === 'Completed' ? 'table-secondary' : ''
                  }
                >
                  <td>{alloc.planeDisplay}</td>
                  <td>{alloc.pilotDisplay}</td>
                  <td>{new Date(alloc.fromDate).toLocaleString()}</td>
                  <td>{new Date(alloc.toDate).toLocaleString()}</td>
                  <td>{alloc.managerDisplay}</td>
                  {role === 'MANAGER' && (
                    <td>
                      {alloc.status === 'Active' ? (
                        <button
                          className="btn btn-danger btn-sm"
                          onClick={() => handleDelete(alloc)}
                        >
                          Delete
                        </button>
                      ) : (
                        <span className="badge bg-secondary">Completed</span>
                      )}
                    </td>
                  )}
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        <Link
          to="/"
          className="btn btn-outline-primary w-100 mt-3 rounded-pill"
        >
          Site Home
        </Link>
      </div>

      {/* Modal */}
      {modal.show && (
        <div
          className="modal fade show d-block"
          tabIndex="-1"
          style={{ backgroundColor: 'rgba(0,0,0,0.8)' }}
        >
          <div className="modal-dialog modal-dialog-centered">
            <div
              className={`modal-content border-${
                modal.type === 'error'
                  ? 'danger'
                  : modal.type === 'success'
                  ? 'success'
                  : 'primary'
              }`}
            >
              <div className="modal-header">
                <h5 className="modal-title">{modal.title}</h5>
                <button
                  type="button"
                  className="btn-close"
                  onClick={hideModal}
                ></button>
              </div>
              <div className="modal-body">
                <p style={{ whiteSpace: 'pre-line' }}>{modal.message}</p>
              </div>
              <div className="modal-footer">
                {modal.type === 'confirm' ? (
                  <>
                    <button
                      type="button"
                      className="btn btn-secondary"
                      onClick={hideModal}
                    >
                      Cancel
                    </button>
                    <button
                      type="button"
                      className="btn btn-danger"
                      onClick={() => {
                        modal.onConfirm()
                        hideModal()
                      }}
                    >
                      Delete
                    </button>
                  </>
                ) : (
                  <button
                    type="button"
                    className="btn btn-primary"
                    onClick={hideModal}
                  >
                    OK
                  </button>
                )}
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

export default PlaneAllocation

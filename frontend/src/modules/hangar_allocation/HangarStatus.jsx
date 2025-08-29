import React, { useEffect, useState } from 'react'
import axios from 'axios'
import { Link, useNavigate } from 'react-router-dom'
import 'bootstrap/dist/css/bootstrap.min.css'
import {
  Table,
  Button,
  Form,
  Row,
  Col,
  Card,
  Alert,
  Modal,
} from 'react-bootstrap'

const BASE = 'http://localhost:8081/api'
const api = axios.create({ baseURL: BASE })

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  const role = localStorage.getItem('role')
  config.headers = config.headers || {}
  if (token) config.headers['Authorization'] = `Bearer ${token}`
  if (role) config.headers['Role'] = role
  return config
})

const HangarStatus = () => {
  const [hangars, setHangars] = useState([])
  const [planes, setPlanes] = useState([])
  const [allocations, setAllocations] = useState([])
  const [loading, setLoading] = useState(true)

  const [selectedHangar, setSelectedHangar] = useState('')
  const [fromDateTime, setFromDateTime] = useState('')
  const [toDateTime, setToDateTime] = useState('')
  const [availabilityMessage, setAvailabilityMessage] = useState('')
  const [selectedPlane, setSelectedPlane] = useState('')
  const [showAllocateModal, setShowAllocateModal] = useState(false)
  const [deleteModal, setDeleteModal] = useState({
    show: false,
    allocation: null,
  })
  const [popup, setPopup] = useState({
    show: false,
    title: '',
    message: '',
    variant: 'info',
  })

  const navigate = useNavigate()
  const role = localStorage.getItem('role')
  const userId = localStorage.getItem('userId')

  const formatDisplayDateTime = (raw) => {
    if (!raw) return ''
    const date = new Date(raw)
    return date.toLocaleString('en-GB', {
      day: '2-digit',
      month: 'short',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    })
  }

  const showPopup = (title, message, variant = 'info') => {
    setPopup({ show: true, title, message, variant })
  }

  const fetchHangarsWithCreators = async () => {
    try {
      const hangarRes = await api.get('/hangars/viewAll', {
        params: { token: localStorage.getItem('token') },
      })
      const hangarData = hangarRes.data

      const updatedHangars = await Promise.all(
        hangarData.map(async (h) => {
          try {
            const userRes = await api.get(`/users/id/${h.userId}`, {
              params: { token: localStorage.getItem('token') },
            })
            return {
              ...h,
              creatorName: userRes.data.username,
              creatorId: userRes.data.userId,
            }
          } catch {
            return { ...h, creatorName: 'Unknown', creatorId: 'N/A' }
          }
        })
      )
      setHangars(updatedHangars)
    } catch (err) {
      console.error(err)
      showPopup('Error', 'Failed to fetch hangars', 'danger')
    }
  }

  const fetchAllData = async () => {
    try {
      setLoading(true)

      const allocationRes = await api.get('/hangar-allocation/all', {
        params: { token: localStorage.getItem('token') },
      })
      setAllocations(allocationRes.data)

      if (role === 'MANAGER') {
        await fetchHangarsWithCreators()
      } else {
        const hangarRes = await api.get('/hangars/viewAll', {
          params: { token: localStorage.getItem('token') },
        })
        setHangars(hangarRes.data)
      }

      const planeRes = await api.get('/planes/getAll', {
        params: { token: localStorage.getItem('token') },
      })
      setPlanes(planeRes.data)
    } catch (err) {
      console.error(err)
      showPopup('Error', 'Failed to fetch allocations', 'danger')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    if (!role) {
      navigate('/login')
      return
    }
    fetchAllData()
  }, [navigate, role])

  const formatDateTime = (value) => (value ? value + ':00' : '')

  const handleCheckAvailability = async () => {
    if (!selectedHangar || !fromDateTime || !toDateTime) {
      showPopup(
        'Validation Error',
        'Please select hangar, from and to date/time.',
        'warning'
      )
      return
    }

    const now = new Date()
    const from = new Date(fromDateTime)
    const to = new Date(toDateTime)

    if (from < now) {
      showPopup(
        'Validation Error',
        'From date/time cannot be in the past.',
        'warning'
      )
      return
    }
    if (to <= from) {
      showPopup(
        'Validation Error',
        'To date/time must be after From date/time.',
        'warning'
      )
      return
    }

    try {
      const res = await api.get('/hangar-allocation/availability', {
        params: {
          hangarId: selectedHangar,
          from: formatDateTime(fromDateTime),
          to: formatDateTime(toDateTime),
          token: localStorage.getItem('token'),
        },
      })
      setAvailabilityMessage(res.data)
      setShowAllocateModal(true)

      const planeRes = await api.get('/planes/getAll', {
        params: { token: localStorage.getItem('token') },
      })
      setPlanes(planeRes.data)
    } catch (err) {
      console.error(err)
      showPopup('Error', 'Failed to check availability.', 'danger')
      setPlanes([])
    }
  }

  const handleAllocate = async () => {
    if (!selectedPlane) {
      showPopup('Validation Error', 'Please select a plane.', 'warning')
      return
    }
    try {
      await api.post('/hangar-allocation/allocate', {
        hangarId: selectedHangar,
        planeId: selectedPlane,
        fromDate: formatDateTime(fromDateTime),
        toDate: formatDateTime(toDateTime),
        userId,
      })
      showPopup('Success', 'Plane allocated successfully!', 'success')
      setShowAllocateModal(false)
      setSelectedPlane('')
      fetchAllData()
    } catch (err) {
      console.error(err)
      const errorMsg = err.response?.data || 'Failed to allocate plane.'
      showPopup('Error', errorMsg, 'danger')
    }
  }

  const confirmDeleteAllocation = (alloc) =>
    setDeleteModal({ show: true, allocation: alloc })

  const handleDeleteConfirmed = async () => {
    if (!deleteModal.allocation) return
    const alloc = deleteModal.allocation
    try {
      const dateObj = new Date(alloc.fromDate)
      const formattedDate = `${dateObj.getFullYear()}-${String(
        dateObj.getMonth() + 1
      ).padStart(2, '0')}-${String(dateObj.getDate()).padStart(
        2,
        '0'
      )} ${String(dateObj.getHours()).padStart(2, '0')}:${String(
        dateObj.getMinutes()
      ).padStart(2, '0')}:${String(dateObj.getSeconds()).padStart(2, '0')}`
      await api.delete(
        `/hangar-allocation/delete/${alloc.planeId}/${alloc.hangarId}/${formattedDate}`,
        { params: { token: localStorage.getItem('token') } }
      )
      showPopup('Success', 'Allocation deleted successfully!', 'success')
      fetchAllData()
    } catch (err) {
      console.error(err)
      showPopup('Error', 'Failed to delete allocation.', 'danger')
    } finally {
      setDeleteModal({ show: false, allocation: null })
    }
  }

  const nowDate = new Date()
  const activeAllocations = allocations.filter(
    (a) => new Date(a.toDate) >= nowDate
  )
  const expiredAllocations = allocations.filter(
    (a) => new Date(a.toDate) < nowDate
  )

  return (
    <div className="container py-4">
      <style>{`
        .allocation-table thead { font-weight:600; text-transform:uppercase; }
        .allocation-table tbody tr:hover { background-color: rgba(30,144,255,0.1); transform: scale(1.01); }
        .card { border-radius: 8px; box-shadow: 0 4px 12px rgba(0,0,0,0.08); margin-bottom: 20px; }
        .btn-primary { background-color: #1e90ff; border: none; }
        .btn-primary:hover { background-color: #1c86ee; }
        .modal-header { background-color: #1e90ff; color: white; }
      `}</style>

      <h2
        className="fw-bold mb-4 text-center"
        style={{ color: 'var(--primary-color)' }}
      >
        Hangar Status
      </h2>

      {/* Allocation Form */}
      {role === 'MANAGER' && (
        <Card className="p-4 mb-4">
          <Form>
            <Row className="g-3">
              <Col md={4}>
                <Form.Group>
                  <Form.Label>Hangar</Form.Label>
                  <Form.Select
                    value={selectedHangar}
                    onChange={(e) => setSelectedHangar(e.target.value)}
                  >
                    <option value="">Select hangar</option>
                    {hangars.map((h) => (
                      <option key={h.hangarId} value={h.hangarId}>
                        {h.hangarName} - Capacity: {h.capacity}
                      </option>
                    ))}
                  </Form.Select>
                </Form.Group>
              </Col>
              <Col md={4}>
                <Form.Group>
                  <Form.Label>From Date & Time</Form.Label>
                  <Form.Control
                    type="datetime-local"
                    value={fromDateTime}
                    min={new Date().toISOString().slice(0, 16)}
                    onChange={(e) => setFromDateTime(e.target.value)}
                  />
                </Form.Group>
              </Col>
              <Col md={4}>
                <Form.Group>
                  <Form.Label>To Date & Time</Form.Label>
                  <Form.Control
                    type="datetime-local"
                    value={toDateTime}
                    min={fromDateTime}
                    onChange={(e) => setToDateTime(e.target.value)}
                  />
                </Form.Group>
              </Col>
              <Col md={12} className="d-flex justify-content-end mt-3">
                <Button variant="primary" onClick={handleCheckAvailability}>
                  Check
                </Button>
              </Col>
            </Row>
          </Form>
        </Card>
      )}

      {loading ? (
        <p>Loading data...</p>
      ) : (
        <>
          {/* Hangar Table */}
          {role === 'MANAGER' && (
            <Card className="p-3 mb-4">
              <table className="table table-hover table-bordered align-middle text-center allocation-table">
                <thead className="table-primary">
                  <tr>
                    <th>Hangar Name</th>
                    <th>Capacity</th>
                    <th>Location</th>
                    <th>Created By (ID)</th>
                  </tr>
                </thead>
                <tbody>
                  {hangars.map((h) => (
                    <tr key={h.hangarId}>
                      <td>{h.hangarName}</td>
                      <td>{h.capacity}</td>
                      <td>{h.hangarLocation}</td>
                      <td>
                        {h.creatorName} ({h.creatorId})
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </Card>
          )}

          {/* Allocations Table */}
          <Card className="p-3">
            <h4 className="mb-3" style={{ color: 'var(--primary-color)' }}>
              Hangar Allocations
            </h4>

            <table className="table table-hover table-bordered align-middle text-center allocation-table">
              <thead className="table-primary">
                <tr>
                  <th>Hangar</th>
                  <th>Plane</th>
                  <th>User ID</th>
                  <th>From</th>
                  <th>To</th>
                  {role === 'MANAGER' && <th>Actions</th>}
                </tr>
              </thead>
              <tbody>
                {activeAllocations.map((alloc, idx) => {
                  const hangarInfo = hangars.find(
                    (h) => h.hangarId === alloc.hangarId
                  )
                  const planeInfo = planes.find(
                    (p) => p.planeId === alloc.planeId
                  )
                  return (
                    <tr key={`active-${idx}`}>
                      <td>
                        {hangarInfo ? hangarInfo.hangarName : alloc.hangarId}
                      </td>
                      <td>
                        {planeInfo
                          ? `${planeInfo.planeNumber} (${planeInfo.model})`
                          : alloc.planeId}
                      </td>
                      <td>{alloc.userId}</td>
                      <td>{formatDisplayDateTime(alloc.fromDate)}</td>
                      <td>{formatDisplayDateTime(alloc.toDate)}</td>
                      {role === 'MANAGER' && (
                        <td>
                          <Button
                            variant="danger"
                            size="sm"
                            onClick={() => confirmDeleteAllocation(alloc)}
                          >
                            Delete
                          </Button>
                        </td>
                      )}
                    </tr>
                  )
                })}

                {expiredAllocations.length > 0 && (
                  <tr>
                    <td
                      colSpan={role === 'MANAGER' ? 6 : 5}
                      className="text-center fw-bold text-muted"
                    >
                      Completed / Expired Allocations
                    </td>
                  </tr>
                )}

                {expiredAllocations.map((alloc, idx) => {
                  const hangarInfo = hangars.find(
                    (h) => h.hangarId === alloc.hangarId
                  )
                  const planeInfo = planes.find(
                    (p) => p.planeId === alloc.planeId
                  )
                  return (
                    <tr key={`expired-${idx}`} className="bg-light text-muted">
                      <td>
                        {hangarInfo ? hangarInfo.hangarName : alloc.hangarId}
                      </td>
                      <td>
                        {planeInfo
                          ? `${planeInfo.planeNumber} (${planeInfo.model})`
                          : alloc.planeId}
                      </td>
                      <td>{alloc.userId}</td>
                      <td>{formatDisplayDateTime(alloc.fromDate)}</td>
                      <td>{formatDisplayDateTime(alloc.toDate)}</td>
                      {role === 'MANAGER' && (
                        <td>
                          <Button variant="danger" size="sm" disabled>
                            Delete
                          </Button>
                        </td>
                      )}
                    </tr>
                  )
                })}

                {allocations.length === 0 && (
                  <tr>
                    <td
                      colSpan={role === 'MANAGER' ? 6 : 5}
                      className="text-center"
                    >
                      No allocations found
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </Card>
        </>
      )}

      {/* Allocate Modal */}
      <Modal
        show={showAllocateModal}
        onHide={() => setShowAllocateModal(false)}
      >
        <Modal.Header closeButton>
          <Modal.Title>Allocate Plane</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {availabilityMessage.includes('available') ? (
            <>
              <Alert variant="success">{availabilityMessage}</Alert>
              <Form.Group>
                <Form.Label>Select Plane</Form.Label>
                <Form.Select
                  value={selectedPlane}
                  onChange={(e) => setSelectedPlane(e.target.value)}
                >
                  <option value="">Select plane</option>
                  {planes.map((p) => (
                    <option key={p.planeId} value={p.planeId}>
                      {p.planeNumber} ({p.model})
                    </option>
                  ))}
                </Form.Select>
              </Form.Group>
            </>
          ) : (
            <Alert variant="danger">{availabilityMessage}</Alert>
          )}
        </Modal.Body>
        <Modal.Footer>
          <Button
            variant="secondary"
            onClick={() => setShowAllocateModal(false)}
          >
            Cancel
          </Button>
          {availabilityMessage.includes('available') && (
            <Button variant="primary" onClick={handleAllocate}>
              Allocate
            </Button>
          )}
        </Modal.Footer>
      </Modal>

      {/* Delete Confirmation Modal */}
      <Modal
        show={deleteModal.show}
        onHide={() => setDeleteModal({ show: false, allocation: null })}
      >
        <Modal.Header closeButton>
          <Modal.Title>Confirm Delete</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          Are you sure you want to delete this allocation?
          <br />
          Hangar ID: {deleteModal.allocation?.hangarId}
          <br />
          Plane ID: {deleteModal.allocation?.planeId}
          <br />
          From: {formatDisplayDateTime(deleteModal.allocation?.fromDate)}
        </Modal.Body>
        <Modal.Footer>
          <Button
            variant="secondary"
            onClick={() => setDeleteModal({ show: false, allocation: null })}
          >
            Cancel
          </Button>
          <Button variant="danger" onClick={handleDeleteConfirmed}>
            Delete
          </Button>
        </Modal.Footer>
      </Modal>

      {/* Popup Modal */}
      <Modal
        show={popup.show}
        onHide={() => setPopup({ ...popup, show: false })}
      >
        <Modal.Header closeButton>
          <Modal.Title>{popup.title}</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Alert variant={popup.variant}>{popup.message}</Alert>
        </Modal.Body>
        <Modal.Footer>
          <Button
            variant="primary"
            onClick={() => setPopup({ ...popup, show: false })}
          >
            OK
          </Button>
        </Modal.Footer>
      </Modal>

      <Link to="/" className="btn btn-outline-primary w-100 mt-3 rounded-pill">
        Site Home
      </Link>
    </div>
  )
}

export default HangarStatus

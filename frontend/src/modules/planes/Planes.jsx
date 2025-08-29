import React, { useState, useEffect } from 'react'
import axios from 'axios'
import { Link, useNavigate } from 'react-router-dom'
import {
  Container,
  Row,
  Col,
  Table,
  Button,
  Form,
  Spinner,
  Modal,
  InputGroup,
  Collapse,
  Toast,
  ToastContainer,
} from 'react-bootstrap'

const BASE = `${
  import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081'
}/api/planes`
const OWNER_BASE = `${
  import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081'
}/api/owner`
const ALLOC_BASE = `${
  import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081'
}`

// ------------------- API CONFIG -------------------
const apiConfig = () => {
  const token = localStorage.getItem('token')
  const role = localStorage.getItem('role')
  return {
    params: { token },
    headers: { Authorization: `Bearer ${token}`, Role: role },
  }
}

// ------------------- API CALLS -------------------
const getPlanes = async () => {
  const res = await axios.get(`${BASE}/getAll`, apiConfig())
  return res.data
}

const getOwnerByPlane = async (planeNumber) => {
  const res = await axios.get(
    `${OWNER_BASE}/getByPlaneNumber/${planeNumber}`,
    apiConfig()
  )
  return res.data
}

const addPlaneApi = async (plane) => {
  const res = await axios.post(`${BASE}/add`, plane, apiConfig())
  return res.data
}

const updatePlaneApi = async (planeId, plane) => {
  const res = await axios.put(
    `${BASE}/updateById/${planeId}`,
    plane,
    apiConfig()
  )
  return res.data
}

const deletePlaneApi = async (planeId) => {
  const res = await axios.delete(`${BASE}/deleteById/${planeId}`, apiConfig())
  return res.data
}

const getAllocations = async () => {
  const [pilotRes, hangarRes] = await Promise.all([
    axios.get(`${ALLOC_BASE}/api/plane-allocation/all`, apiConfig()),
    axios.get(`${ALLOC_BASE}/api/hangar-allocation/all`, apiConfig()),
  ])
  return { pilot: pilotRes.data, hangar: hangarRes.data }
}

// ------------------- COMPONENT -------------------
function Planes() {
  const [planes, setPlanes] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [searchText, setSearchText] = useState('')
  const [expanded, setExpanded] = useState({})
  const [planeModal, setPlaneModal] = useState(false)
  const [planeForm, setPlaneForm] = useState({
    planeNumber: '',
    model: '',
    capacity: 10,
    userId: '',
    owner: {
      name: '',
      address: {
        email: '',
        phoneno: '',
        houseNo: '',
        street: '',
        city: '',
        state: '',
        pincode: '',
      },
    },
  })
  const [planeEditId, setPlaneEditId] = useState(null)
  const [planeError, setPlaneError] = useState('')

  const [deleteModal, setDeleteModal] = useState({ show: false, planeId: null })

  const [toast, setToast] = useState({
    show: false,
    message: '',
    variant: 'success',
  })

  const role = localStorage.getItem('role')
  const token = localStorage.getItem('token')
  const navigate = useNavigate()

  useEffect(() => {
    if (!token || !role) {
      navigate('/login')
      return
    }
    fetchData()
  }, [token, role])

  const fetchData = async () => {
    setLoading(true)
    setError('')
    try {
      const data = await getPlanes()
      setPlanes(data)
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to fetch planes.')
    } finally {
      setLoading(false)
    }
  }

  const toggleExpand = async (plane) => {
    if (expanded[plane.planeId]) {
      setExpanded({ ...expanded, [plane.planeId]: false })
    } else {
      try {
        const owner = await getOwnerByPlane(plane.planeNumber)
        setExpanded({ ...expanded, [plane.planeId]: owner })
      } catch {
        setExpanded({
          ...expanded,
          [plane.planeId]: { error: 'No owner found' },
        })
      }
    }
  }

  const openPlaneModal = (plane = null) => {
    if (plane) {
      // Edit mode
      setPlaneForm({
        planeNumber: plane.planeNumber,
        model: plane.model,
        capacity: plane.capacity,
        userId: plane.userId || '',
        owner: plane.owner || { name: '', address: {} },
      })
      setPlaneEditId(plane.planeId)
    } else {
      // Add mode
      setPlaneForm({
        planeNumber: '',
        model: '',
        capacity: 10,
        userId: '',
        owner: {
          name: '',
          address: {
            email: '',
            phoneno: '',
            houseNo: '',
            street: '',
            city: '',
            state: '',
            pincode: '',
          },
        },
      })
      setPlaneEditId(null)
    }
    setPlaneError('')
    setPlaneModal(true)
  }

  const closePlaneModal = () => {
    setPlaneModal(false)
    setPlaneEditId(null)
    setPlaneError('')
  }

  const handlePlaneSave = async (e) => {
    e.preventDefault()
    setPlaneError('')
    try {
      if (planeEditId) {
        await updatePlaneApi(planeEditId, planeForm)
        setToast({
          show: true,
          message: 'Plane updated successfully!',
          variant: 'success',
        })
      } else {
        await addPlaneApi(planeForm)
        setToast({
          show: true,
          message: 'Plane added successfully!',
          variant: 'success',
        })
      }
      closePlaneModal()
      fetchData()
    } catch (err) {
      setPlaneError(err.response?.data?.message || 'Failed to save plane')
      setToast({ show: true, message: 'Error saving plane', variant: 'danger' })
    }
  }

  const confirmDeletePlane = async (planeId) => {
    try {
      const { pilot, hangar } = await getAllocations()
      const allocated =
        pilot.some((a) => a.planeId === planeId) ||
        hangar.some((a) => a.planeId === planeId)

      if (allocated) {
        setToast({
          show: true,
          message: '🚫 Plane cannot be deleted, it is currently allocated!',
          variant: 'danger',
        })
        return
      }

      setDeleteModal({ show: true, planeId })
    } catch (err) {
      setToast({
        show: true,
        message: 'Error checking allocations',
        variant: 'danger',
      })
    }
  }

  const handleDeleteConfirmed = async () => {
    try {
      await deletePlaneApi(deleteModal.planeId)
      setToast({
        show: true,
        message: 'Plane deleted successfully!',
        variant: 'success',
      })
      fetchData()
    } catch (err) {
      setToast({
        show: true,
        message: err.response?.data?.message || 'Failed to delete plane',
        variant: 'danger',
      })
    } finally {
      setDeleteModal({ show: false, planeId: null })
    }
  }

  const filteredPlanes = planes.filter((p) => {
    const owner = expanded[p.planeId]
    const allValues = [
      p.planeNumber,
      p.model,
      p.capacity,
      owner?.name,
      owner?.address?.email,
      owner?.address?.phoneno,
      owner?.address?.city,
    ]
    return allValues.some((val) =>
      String(val || '')
        .toLowerCase()
        .includes(searchText.toLowerCase())
    )
  })

  return (
    <Container className="py-4">
      <div className="card shadow-sm border-0 p-4">
        <h1
          className="fw-bold mb-4 text-center"
          style={{ color: 'var(--primary-color)' }}
        >
          Plane Management
        </h1>

        <Row className="mb-3">
          <Col md={8}>
            <InputGroup>
              <Form.Control
                placeholder="Search planes by number, model..."
                value={searchText}
                onChange={(e) => setSearchText(e.target.value)}
              />
              <Button
                variant="outline-secondary"
                onClick={() => setSearchText('')}
              >
                Clear
              </Button>
            </InputGroup>
          </Col>
          {role === 'ADMIN' && (
            <Col md={3}>
              <Button variant="success" onClick={() => openPlaneModal()}>
                Add Plane
              </Button>
            </Col>
          )}
        </Row>

        {loading ? (
          <div className="text-center py-5">
            <Spinner animation="border" role="status" />
            <p className="mt-3">Loading planes...</p>
          </div>
        ) : error ? (
          <p className="text-danger">{error}</p>
        ) : filteredPlanes.length === 0 ? (
          <p className="text-muted">No planes found.</p>
        ) : (
          <Table bordered hover responsive>
            <thead className="table-primary">
              <tr>
                <th>Plane Number</th>
                <th>Model</th>
                <th>Capacity</th>
                <th>Owner</th>
                {role === 'ADMIN' && <th>Actions</th>}
              </tr>
            </thead>
            <tbody>
              {filteredPlanes.map((p) => (
                <React.Fragment key={p.planeId}>
                  <tr>
                    <td>{p.planeNumber}</td>
                    <td>{p.model}</td>
                    <td>{p.capacity}</td>
                    <td>
                      <Button
                        variant="link"
                        onClick={() => toggleExpand(p)}
                        className="p-0"
                      >
                        {expanded[p.planeId] ? 'Hide Owner' : 'Show Owner'}
                      </Button>
                    </td>
                    {role === 'ADMIN' && (
                      <td>
                        <Button
                          size="sm"
                          variant="info"
                          className="me-2"
                          onClick={() => openPlaneModal(p)}
                        >
                          Edit
                        </Button>
                        <Button
                          size="sm"
                          variant="danger"
                          onClick={() => confirmDeletePlane(p.planeId)}
                        >
                          Delete
                        </Button>
                      </td>
                    )}
                  </tr>
                  <tr>
                    <td colSpan={role === 'ADMIN' ? 5 : 4} className="p-0">
                      <Collapse in={!!expanded[p.planeId]}>
                        <div className="p-3 bg-light">
                          {expanded[p.planeId]?.error ? (
                            <p className="text-danger">
                              {expanded[p.planeId].error}
                            </p>
                          ) : (
                            expanded[p.planeId] && (
                              <div>
                                <h6>Owner Details</h6>
                                <p>
                                  <b>Name:</b> {expanded[p.planeId].name}
                                </p>
                                <p>
                                  <b>Email:</b>{' '}
                                  {expanded[p.planeId].address?.email}
                                </p>
                                <p>
                                  <b>Phone:</b>{' '}
                                  {expanded[p.planeId].address?.phoneno}
                                </p>
                                <p>
                                  <b>Address:</b>{' '}
                                  {expanded[p.planeId].address?.houseNo},{' '}
                                  {expanded[p.planeId].address?.street},{' '}
                                  {expanded[p.planeId].address?.city},{' '}
                                  {expanded[p.planeId].address?.state} -{' '}
                                  {expanded[p.planeId].address?.pincode}
                                </p>
                              </div>
                            )
                          )}
                        </div>
                      </Collapse>
                    </td>
                  </tr>
                </React.Fragment>
              ))}
            </tbody>
          </Table>
        )}

        <Link
          to="/"
          className="btn btn-outline-primary w-100 mt-4 rounded-pill"
        >
          Site Home
        </Link>
      </div>

      {/* Plane Add/Edit Modal */}
      <Modal show={planeModal} onHide={closePlaneModal} centered>
        <Form onSubmit={handlePlaneSave}>
          <Modal.Header
            closeButton
            className={`bg-${planeEditId ? 'info' : 'success'} text-white`}
          >
            <Modal.Title>
              {planeEditId ? 'Edit Plane' : 'Add Plane'}
            </Modal.Title>
          </Modal.Header>
          <Modal.Body>
            {planeError && <p className="text-danger">{planeError}</p>}

            <h6>Plane Details</h6>
            <Form.Group className="mb-2">
              <Form.Label>Plane Number</Form.Label>
              <Form.Control
                value={planeForm.planeNumber}
                onChange={(e) =>
                  setPlaneForm({ ...planeForm, planeNumber: e.target.value })
                }
                required
              />
            </Form.Group>
            <Form.Group className="mb-2">
              <Form.Label>Model</Form.Label>
              <Form.Control
                value={planeForm.model}
                onChange={(e) =>
                  setPlaneForm({ ...planeForm, model: e.target.value })
                }
                required
              />
            </Form.Group>
            <Form.Group className="mb-2">
              <Form.Label>Capacity</Form.Label>
              <Form.Control
                type="number"
                min={10}
                max={50}
                value={planeForm.capacity}
                onChange={(e) =>
                  setPlaneForm({
                    ...planeForm,
                    capacity: parseInt(e.target.value),
                  })
                }
                required
              />
            </Form.Group>

            <Form.Group className="mb-2">
              <Form.Label>User ID</Form.Label>
              <Form.Control
                type="number"
                value={planeForm.userId}
                onChange={(e) =>
                  setPlaneForm({ ...planeForm, userId: Number(e.target.value) })
                }
                required
              />
            </Form.Group>

            {/* Only show owner fields in Add mode */}
            {!planeEditId && (
              <>
                <h6 className="mt-3">Owner Details</h6>
                <Form.Group className="mb-2">
                  <Form.Label>Owner Name</Form.Label>
                  <Form.Control
                    value={planeForm.owner.name}
                    onChange={(e) =>
                      setPlaneForm({
                        ...planeForm,
                        owner: { ...planeForm.owner, name: e.target.value },
                      })
                    }
                    required
                  />
                </Form.Group>
                <Form.Group className="mb-2">
                  <Form.Label>Email</Form.Label>
                  <Form.Control
                    value={planeForm.owner.address.email}
                    onChange={(e) =>
                      setPlaneForm({
                        ...planeForm,
                        owner: {
                          ...planeForm.owner,
                          address: {
                            ...planeForm.owner.address,
                            email: e.target.value,
                          },
                        },
                      })
                    }
                    required
                  />
                </Form.Group>
                <Form.Group className="mb-2">
                  <Form.Label>Phone</Form.Label>
                  <Form.Control
                    value={planeForm.owner.address.phoneno}
                    onChange={(e) =>
                      setPlaneForm({
                        ...planeForm,
                        owner: {
                          ...planeForm.owner,
                          address: {
                            ...planeForm.owner.address,
                            phoneno: e.target.value,
                          },
                        },
                      })
                    }
                    required
                  />
                </Form.Group>
                <Form.Group className="mb-2">
                  <Form.Label>House No</Form.Label>
                  <Form.Control
                    value={planeForm.owner.address.houseNo}
                    onChange={(e) =>
                      setPlaneForm({
                        ...planeForm,
                        owner: {
                          ...planeForm.owner,
                          address: {
                            ...planeForm.owner.address,
                            houseNo: e.target.value,
                          },
                        },
                      })
                    }
                  />
                </Form.Group>
                <Form.Group className="mb-2">
                  <Form.Label>Street</Form.Label>
                  <Form.Control
                    value={planeForm.owner.address.street}
                    onChange={(e) =>
                      setPlaneForm({
                        ...planeForm,
                        owner: {
                          ...planeForm.owner,
                          address: {
                            ...planeForm.owner.address,
                            street: e.target.value,
                          },
                        },
                      })
                    }
                  />
                </Form.Group>
                <Form.Group className="mb-2">
                  <Form.Label>City</Form.Label>
                  <Form.Control
                    value={planeForm.owner.address.city}
                    onChange={(e) =>
                      setPlaneForm({
                        ...planeForm,
                        owner: {
                          ...planeForm.owner,
                          address: {
                            ...planeForm.owner.address,
                            city: e.target.value,
                          },
                        },
                      })
                    }
                  />
                </Form.Group>
                <Form.Group className="mb-2">
                  <Form.Label>State</Form.Label>
                  <Form.Control
                    value={planeForm.owner.address.state}
                    onChange={(e) =>
                      setPlaneForm({
                        ...planeForm,
                        owner: {
                          ...planeForm.owner,
                          address: {
                            ...planeForm.owner.address,
                            state: e.target.value,
                          },
                        },
                      })
                    }
                  />
                </Form.Group>
                <Form.Group className="mb-2">
                  <Form.Label>Pincode</Form.Label>
                  <Form.Control
                    value={planeForm.owner.address.pincode}
                    onChange={(e) =>
                      setPlaneForm({
                        ...planeForm,
                        owner: {
                          ...planeForm.owner,
                          address: {
                            ...planeForm.owner.address,
                            pincode: e.target.value,
                          },
                        },
                      })
                    }
                  />
                </Form.Group>
              </>
            )}
          </Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={closePlaneModal}>
              Cancel
            </Button>
            <Button variant="success" type="submit">
              {planeEditId ? 'Update' : 'Add Plane'}
            </Button>
          </Modal.Footer>
        </Form>
      </Modal>

      {/* Delete Confirmation Modal */}
      <Modal
        show={deleteModal.show}
        onHide={() => setDeleteModal({ show: false, planeId: null })}
        centered
      >
        <Modal.Header closeButton className="bg-danger text-white">
          <Modal.Title>Confirm Delete</Modal.Title>
        </Modal.Header>
        <Modal.Body>Are you sure you want to delete this plane?</Modal.Body>
        <Modal.Footer>
          <Button
            variant="secondary"
            onClick={() => setDeleteModal({ show: false, planeId: null })}
          >
            Cancel
          </Button>
          <Button variant="danger" onClick={handleDeleteConfirmed}>
            Delete
          </Button>
        </Modal.Footer>
      </Modal>

      {/* Toast Notifications */}
      <ToastContainer position="top-end" className="p-3">
        <Toast
          show={toast.show}
          onClose={() => setToast({ ...toast, show: false })}
          bg={toast.variant}
          delay={3000}
          autohide
        >
          <Toast.Body className="text-white">{toast.message}</Toast.Body>
        </Toast>
      </ToastContainer>
    </Container>
  )
}

export default Planes

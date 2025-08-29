import React, { useState } from 'react'
import 'bootstrap/dist/css/bootstrap.min.css'
import { Link, useNavigate } from 'react-router-dom'
import axios from 'axios'

const Register = () => {
  const navigate = useNavigate()

  const [formData, setFormData] = useState({
    username: '',
    role: '',
    password: '',
    confirmPassword: '',
    dateOfBirth: '',
    houseNo: '',
    street: '',
    city: '',
    state: '',
    pincode: '',
    email: '',
    phoneno: '',
  })

  const [errors, setErrors] = useState({})
  const [loading, setLoading] = useState(false)
  const [showSuccess, setShowSuccess] = useState(false)
  const [showPassword, setShowPassword] = useState(false)
  const [showConfirmPassword, setShowConfirmPassword] = useState(false)

  const handleChange = (e) => {
    let value = e.target.value
    if (e.target.name === 'role') value = value.toUpperCase()
    setFormData((prev) => ({ ...prev, [e.target.name]: value }))
    setErrors((prev) => ({ ...prev, [e.target.name]: '' }))
  }

  const validateForm = () => {
    const newErrors = {}
    const today = new Date()

    if (!formData.username.trim()) newErrors.username = 'Username is required'
    if (!formData.role) newErrors.role = 'Role is required'
    if (!formData.password) newErrors.password = 'Password is required'
    if (!formData.confirmPassword)
      newErrors.confirmPassword = 'Confirm password is required'
    if (
      formData.password &&
      formData.confirmPassword &&
      formData.password !== formData.confirmPassword
    )
      newErrors.confirmPassword = 'Passwords do not match'

    if (!formData.dateOfBirth)
      newErrors.dateOfBirth = 'Date of Birth is required'
    else {
      const dob = new Date(formData.dateOfBirth)
      let age = today.getFullYear() - dob.getFullYear()
      const m = today.getMonth() - dob.getMonth()
      if (m < 0 || (m === 0 && today.getDate() < dob.getDate())) age--
      if (age < 20) newErrors.dateOfBirth = 'Age must be 20 or above'
    }

    if (!formData.phoneno) newErrors.phoneno = 'Phone number is required'
    else if (!/^[6-9]\d{9}$/.test(formData.phoneno))
      newErrors.phoneno = 'Invalid phone number (10 digits, starts with 6-9)'

    if (!formData.houseNo) newErrors.houseNo = 'House number is required'
    if (!formData.street) newErrors.street = 'Street is required'
    if (!formData.city) newErrors.city = 'City is required'
    if (!formData.state) newErrors.state = 'State is required'
    if (!formData.pincode) newErrors.pincode = 'Pincode is required'
    if (!formData.email) newErrors.email = 'Email is required'
    else if (!/\S+@\S+\.\S+/.test(formData.email))
      newErrors.email = 'Invalid email address'

    setErrors(newErrors)
    return Object.keys(newErrors).length === 0
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    if (!validateForm()) return

    setLoading(true)
    try {
      await axios.post('http://localhost:8081/api/users/register', formData)
      setLoading(false)
      setShowSuccess(true)
    } catch (err) {
      setLoading(false)
      alert(err.response?.data?.message || 'Registration failed')
    }
  }

  const closeModal = () => {
    setShowSuccess(false)
    navigate('/login')
  }

  return (
    <>
      {/* Fixed Navbar */}
      <nav className="navbar navbar-expand-lg navbar-dark bg-dark fixed-top px-3">
        <Link className="navbar-brand" to="/">
          AirManager
        </Link>
        <div className="ms-auto">
          <Link className="btn btn-outline-light" to="/login">
            Login
          </Link>
        </div>
      </nav>

      {/* Full-page background */}
      <div
        style={{
          //position: "fixed",
          minHeight: '145vh',
          top: '50px', // navbar height
          left: 0,
          right: 0,
          bottom: 0,
          backgroundImage:
            'linear-gradient(rgba(0,0,0,0.4), rgba(0,0,0,0.4)), url("https://www.foreverwallpapers.com/wp-content/uploads/2019/10/Airplane-Images-HD.jpg")',
          backgroundSize: 'cover',
          backgroundPosition: 'center',
          backgroundRepeat: 'no-repeat',
          overflowY: 'scroll',
          paddingBottom: '2rem 1rem',
        }}
        className="d-flex justify-content-center align-items-center p-3"
      >
        <div
          className="card shadow-lg p-4 p-md-5 bg-white rounded-4"
          style={{ width: '100%', maxWidth: '800px' }}
        >
          <h2 className="fw-bold text-center mb-4">Create Your Account</h2>

          <form className="row g-3" onSubmit={handleSubmit}>
            {/* Username */}
            <div className="col-md-6">
              <label className="form-label">Username</label>
              <input
                type="text"
                name="username"
                className={`form-control rounded-pill ${
                  errors.username ? 'is-invalid' : ''
                }`}
                placeholder="Enter username"
                value={formData.username}
                onChange={handleChange}
              />
              <div className="invalid-feedback">{errors.username}</div>
            </div>

            {/* Role */}
            <div className="col-md-6">
              <label className="form-label">Role</label>
              <select
                name="role"
                className={`form-select rounded-pill ${
                  errors.role ? 'is-invalid' : ''
                }`}
                value={formData.role}
                onChange={handleChange}
              >
                <option value="">Select Role</option>
                <option value="ADMIN">Admin</option>
                <option value="MANAGER">Manager</option>
              </select>
              <div className="invalid-feedback">{errors.role}</div>
            </div>

            {/* Password */}
            <div className="col-md-6">
              <label className="form-label">Password</label>
              <div className="input-group">
                <input
                  type={showPassword ? 'text' : 'password'}
                  name="password"
                  className={`form-control rounded-pill ${
                    errors.password ? 'is-invalid' : ''
                  }`}
                  placeholder="Enter password"
                  value={formData.password}
                  onChange={handleChange}
                />
                <button
                  type="button"
                  className="btn btn-outline-secondary rounded-pill"
                  onClick={() => setShowPassword(!showPassword)}
                >
                  {showPassword ? 'Hide' : 'Show'}
                </button>
                <div className="invalid-feedback">{errors.password}</div>
              </div>
            </div>

            {/* Confirm Password */}
            <div className="col-md-6">
              <label className="form-label">Confirm Password</label>
              <div className="input-group">
                <input
                  type={showConfirmPassword ? 'text' : 'password'}
                  name="confirmPassword"
                  className={`form-control rounded-pill ${
                    errors.confirmPassword ? 'is-invalid' : ''
                  }`}
                  placeholder="Confirm password"
                  value={formData.confirmPassword}
                  onChange={handleChange}
                />
                <button
                  type="button"
                  className="btn btn-outline-secondary rounded-pill"
                  onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                >
                  {showConfirmPassword ? 'Hide' : 'Show'}
                </button>
                <div className="invalid-feedback">{errors.confirmPassword}</div>
              </div>
            </div>

            {/* DOB */}
            <div className="col-md-6">
              <label className="form-label">Date of Birth</label>
              <input
                type="date"
                name="dateOfBirth"
                className={`form-control rounded-pill ${
                  errors.dateOfBirth ? 'is-invalid' : ''
                }`}
                value={formData.dateOfBirth}
                onChange={handleChange}
              />
              <div className="invalid-feedback">{errors.dateOfBirth}</div>
            </div>

            {/* Phone */}
            <div className="col-md-6">
              <label className="form-label">Phone Number</label>
              <input
                type="tel"
                name="phoneno"
                className={`form-control rounded-pill ${
                  errors.phoneno ? 'is-invalid' : ''
                }`}
                placeholder="Enter phone number"
                value={formData.phoneno}
                onChange={handleChange}
              />
              <div className="invalid-feedback">{errors.phoneno}</div>
            </div>

            {/* Address Header */}
            <div className="col-12">
              <h5 className="fw-bold border-bottom pb-2">Address Details</h5>
            </div>

            {/* Address Inputs */}
            <div className="col-md-4">
              <label className="form-label">House No.</label>
              <input
                type="text"
                name="houseNo"
                className={`form-control ${errors.houseNo ? 'is-invalid' : ''}`}
                placeholder="Enter house number"
                value={formData.houseNo}
                onChange={handleChange}
              />
              <div className="invalid-feedback">{errors.houseNo}</div>
            </div>
            <div className="col-md-8">
              <label className="form-label">Street</label>
              <input
                type="text"
                name="street"
                className={`form-control ${errors.street ? 'is-invalid' : ''}`}
                placeholder="Enter street"
                value={formData.street}
                onChange={handleChange}
              />
              <div className="invalid-feedback">{errors.street}</div>
            </div>
            <div className="col-md-4">
              <label className="form-label">City</label>
              <input
                type="text"
                name="city"
                className={`form-control ${errors.city ? 'is-invalid' : ''}`}
                placeholder="Enter city"
                value={formData.city}
                onChange={handleChange}
              />
              <div className="invalid-feedback">{errors.city}</div>
            </div>
            <div className="col-md-4">
              <label className="form-label">State</label>
              <input
                type="text"
                name="state"
                className={`form-control ${errors.state ? 'is-invalid' : ''}`}
                placeholder="Enter state"
                value={formData.state}
                onChange={handleChange}
              />
              <div className="invalid-feedback">{errors.state}</div>
            </div>
            <div className="col-md-4">
              <label className="form-label">Pincode</label>
              <input
                type="text"
                name="pincode"
                className={`form-control ${errors.pincode ? 'is-invalid' : ''}`}
                placeholder="Enter pincode"
                value={formData.pincode}
                onChange={handleChange}
              />
              <div className="invalid-feedback">{errors.pincode}</div>
            </div>

            {/* Email */}
            <div className="col-12">
              <label className="form-label">Email</label>
              <input
                type="email"
                name="email"
                className={`form-control ${errors.email ? 'is-invalid' : ''}`}
                placeholder="Enter email"
                value={formData.email}
                onChange={handleChange}
              />
              <div className="invalid-feedback">{errors.email}</div>
            </div>

            {/* Submit Button */}
            <div className="col-12 mt-3">
              <button
                type="submit"
                className="btn btn-primary w-100 rounded-pill"
                disabled={loading}
              >
                {loading ? 'Registering...' : 'Register'}
              </button>
            </div>
          </form>

          <div className="text-center mt-3">
            <p>
              Already have an account?{' '}
              <Link to="/login" className="text-primary fw-semibold">
                Login
              </Link>
            </p>
          </div>

          <Link
            to="/"
            className="btn btn-outline-primary w-100 mt-2 rounded-pill"
          >
            Site Home
          </Link>
        </div>
      </div>

      {/* Success Modal */}
      {showSuccess && (
        <div
          className="modal fade show d-block"
          tabIndex="-1"
          style={{ backgroundColor: 'rgba(0,0,0,0.5)' }}
        >
          <div className="modal-dialog modal-dialog-centered">
            <div className="modal-content">
              <div className="modal-header bg-success text-white">
                <h5 className="modal-title">Registration Successful</h5>
                <button
                  type="button"
                  className="btn-close"
                  onClick={closeModal}
                ></button>
              </div>
              <div className="modal-body">
                <p>Your account has been created successfully! Please login.</p>
              </div>
              <div className="modal-footer">
                <button
                  type="button"
                  className="btn btn-success"
                  onClick={closeModal}
                >
                  OK
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </>
  )
}

export default Register

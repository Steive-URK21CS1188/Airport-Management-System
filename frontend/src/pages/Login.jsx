import React, { useState, useEffect } from 'react'
import 'bootstrap/dist/css/bootstrap.min.css'
import { Link, useNavigate, useLocation } from 'react-router-dom'
import axios from 'axios'

const Navbar = () => (
  <nav className="navbar navbar-expand-lg navbar-dark bg-dark px-3">
    <Link className="navbar-brand" to="/">
      AirManager
    </Link>
  </nav>
)

const Login = () => {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [popup, setPopup] = useState({
    show: false,
    title: '',
    message: '',
    variant: 'info',
  })

  const navigate = useNavigate()
  const location = useLocation()

  // Show popup if redirected due to session expiry
  useEffect(() => {
    if (location.state?.reason === 'expired') {
      showPopup(
        'Session Expired',
        'Your session has expired. Please log in again.',
        'warning'
      )
    }
  }, [location.state])

  const showPopup = (title, message, variant = 'info') => {
    setPopup({ show: true, title, message, variant })
  }

  const handleLogin = async (e) => {
    e.preventDefault()
    try {
      const res = await axios.post('http://localhost:8081/api/users/login', {
        username,
        password,
      })

      const { token, role, userId } = res.data

      // Store login/session data
      localStorage.setItem('userId', userId)
      localStorage.setItem('token', token)
      localStorage.setItem('role', role)
      localStorage.setItem('isLoggedIn', 'true')
      sessionStorage.setItem('loginTime', Date.now())

      showPopup('Success', 'Login successful!', 'success')

      // Redirect to last visited page or home after delay
      const lastVisited = sessionStorage.getItem('lastVisited') || '/'
      setTimeout(() => {
        navigate(lastVisited, { replace: true })
      }, 1000)
    } catch (err) {
      console.error('Login failed', err)
      showPopup('Login Failed', 'Invalid username or password', 'danger')
    }
  }

  return (
    <>
      <Navbar />
      <div
        className="d-flex flex-column"
        style={{
          position: 'fixed',
          top: '56px',
          left: 0,
          right: 0,
          bottom: 0,
          backgroundImage:
            'linear-gradient(rgba(0,0,0,0.4), rgba(0,0,0,0.4)), url("https://w0.peakpx.com/wallpaper/397/247/HD-wallpaper-flying-aeroplane-on-light-blue-sky-light-blue.jpg")',
          backgroundSize: 'cover',
          backgroundPosition: 'center',
          backgroundRepeat: 'no-repeat',
        }}
      >
        <main className="flex-grow-1 d-flex justify-content-center align-items-center w-100">
          <div
            className="card shadow-lg border-0 rounded-4 p-4 bg-white"
            style={{ width: '100%', maxWidth: '420px' }}
          >
            <div className="text-center mb-4">
              <h2 className="fw-bold text-dark">Welcome Back</h2>
              <p className="text-muted mb-0">
                Login to manage your airport operations.
              </p>
            </div>

            <form onSubmit={handleLogin}>
              <div className="mb-3">
                <label
                  htmlFor="username"
                  className="form-label fw-semibold text-secondary"
                >
                  Username
                </label>
                <input
                  type="text"
                  className="form-control rounded-pill py-2"
                  id="username"
                  placeholder="Enter your username"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  required
                />
              </div>

              <div className="mb-4">
                <label
                  htmlFor="password"
                  className="form-label fw-semibold text-secondary"
                >
                  Password
                </label>
                <input
                  type="password"
                  className="form-control rounded-pill py-2"
                  id="password"
                  placeholder="Enter your password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  required
                />
              </div>

              <button
                type="submit"
                className="btn btn-primary w-100 rounded-pill py-2 fw-bold"
              >
                Login
              </button>
            </form>

            <div className="text-center mt-4">
              <p className="text-muted mb-0">
                Don't have an account?{' '}
                <Link to="/register" className="text-primary fw-semibold">
                  Register
                </Link>
              </p>
            </div>

            <Link
              to="/"
              className="btn btn-outline-primary w-100 mt-3 rounded-pill"
            >
              Site Home
            </Link>
          </div>
        </main>
      </div>

      {/* Popup Modal */}
      <div
        className={`modal fade ${popup.show ? 'show d-block' : ''}`}
        tabIndex="-1"
        role="dialog"
        style={{ backgroundColor: 'rgba(0,0,0,0.3)' }}
      >
        <div className="modal-dialog modal-dialog-centered" role="document">
          <div className="modal-content">
            <div className={`modal-header bg-${popup.variant} text-white`}>
              <h5 className="modal-title">{popup.title}</h5>
              <button
                type="button"
                className="btn-close btn-close-white"
                onClick={() => setPopup({ ...popup, show: false })}
              ></button>
            </div>
            <div className="modal-body">
              <p>{popup.message}</p>
            </div>
            <div className="modal-footer">
              <button
                type="button"
                className={`btn btn-${popup.variant}`}
                onClick={() => setPopup({ ...popup, show: false })}
              >
                OK
              </button>
            </div>
          </div>
        </div>
      </div>
    </>
  )
}

export default Login

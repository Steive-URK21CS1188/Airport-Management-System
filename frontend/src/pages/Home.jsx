import React from 'react'
import { useNavigate } from 'react-router-dom'
import NavbarComponent from '../components/NavbarComponent'
import '../styles/custom.css'

const Home = () => {
  const navigate = useNavigate()

  const cards = [
    {
      title: 'Planes',
      description: 'Manage all planes in the system',
      icon: 'bi bi-airplane',
      path: '/planes',
    },
    {
      title: 'Pilots',
      description: 'View and manage pilot details',
      icon: 'bi bi-person-badge',
      path: '/pilots',
    },
    {
      title: 'Hangar',
      description: 'Manage hangar locations and statuses',
      icon: 'bi bi-building',
      path: '/hangar',
    },
    {
      title: 'Plane Allocation',
      description: 'Allocate planes to pilots and managers',
      icon: 'bi bi-card-checklist',
      path: '/allocation',
    },
    {
      title: 'Hangar Status',
      description: 'Track hangar allocations and availability',
      icon: 'bi bi-geo-alt',
      path: '/hangar-status',
    },
  ]

  return (
    <>
      {/* Navbar stays fixed */}
      <div
        style={{ position: 'fixed', top: 0, left: 0, right: 0, zIndex: 1000 }}
      >
        <NavbarComponent />
      </div>

      {/* Hero Section - scrolls full width */}
      <div
        style={{
          marginTop: '70px', // Push below navbar
          height: 'calc(100vh - 70px)',
          width: '100vw', // Full width of viewport
          marginLeft: 'calc(-50vw + 50%)', // Remove any Bootstrap container centering
          backgroundImage:
            'linear-gradient(rgba(0,0,0,0.5), rgba(0,0,0,0.5)), url("https://static.vecteezy.com/system/resources/thumbnails/020/141/626/small_2x/plane-in-the-sky-passenger-commercial-plane-flying-above-the-clouds-concept-of-fast-travel-vacation-and-business-photo.jpg")',
          backgroundSize: 'cover',
          backgroundRepeat: 'no-repeat',
          backgroundPosition: 'center',
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          justifyContent: 'center',
          color: 'white',
          textAlign: 'center',
        }}
      >
        <div>
          <h1 className="display-4 fw-bold">Welcome to AirMatrix</h1>
          <p className="lead">
            Your all-in-one solution for airport operations
          </p>
        </div>
      </div>

      {/* Cards Section */}
      <div
        style={{
          padding: '40px 10px',
          backgroundColor: '#f8f9fa',
        }}
      >
        <div
          className="d-flex justify-content-center gap-3 flex-wrap"
          style={{ maxWidth: '100%', overflowX: 'hidden' }}
        >
          {cards.map(({ title, description, icon, path }) => (
            <div
              key={title}
              className="card text-center shadow-sm border-0 hover-scale"
              onClick={() => navigate(path)}
              style={{
                transition: 'transform 0.3s ease',
                cursor: 'pointer',
                width: '170px',
                flex: '0 0 auto',
              }}
            >
              <div className="card-body d-flex flex-column justify-content-center align-items-center">
                <i className={`${icon} fs-1 text-primary mb-3`}></i>
                <h5 className="fw-bold">{title}</h5>
                <p className="text-muted small">{description}</p>
              </div>
            </div>
          ))}
        </div>
      </div>
    </>
  )
}

export default Home

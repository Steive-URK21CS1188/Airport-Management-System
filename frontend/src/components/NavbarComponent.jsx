import React, { useState, useRef, useEffect } from "react";
import "bootstrap/dist/css/bootstrap.min.css";
import { Link, useNavigate } from "react-router-dom";
import logo from "../assets/logo.png";

const NavbarComponent = () => {
  const navigate = useNavigate();
  const isLoggedIn = localStorage.getItem("isLoggedIn") === "true";
  const [showProfileDetails, setShowProfileDetails] = useState(false);
  const [expandedDetails, setExpandedDetails] = useState(false);
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const profileRef = useRef(null);

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("role");
    localStorage.removeItem("isLoggedIn");
    setUser(null);
    navigate("/login");
  };

  const fetchUserDetails = async () => {
    setLoading(true);
    setError(null);
    try {
      const token = localStorage.getItem("token");
      if (!token) throw new Error("User not authenticated");

      const response = await fetch("/api/users/dashboard", {
        headers: { Authorization: `Bearer ${token}` },
      });

      if (!response.ok) throw new Error("Failed to fetch user details");

      const data = await response.json();
      setUser(data);
    } catch (err) {
      setError(err.message || "Error fetching user details");
      setUser(null);
    } finally {
      setLoading(false);
    }
  };

  const toggleProfileDetails = () => {
    if (!showProfileDetails) {
      fetchUserDetails();
      setExpandedDetails(false);
    }
    setShowProfileDetails((prev) => !prev);
  };

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (profileRef.current && !profileRef.current.contains(event.target)) {
        setShowProfileDetails(false);
        setExpandedDetails(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  return (
    <>
      {/* Inline CSS for Navbar */}
      <style>{`
        .navbar-custom {
          background-color: #166ac9 !important; /* Blue background */
        }
        .navbar-custom .nav-link {
          color: #fff !important;
        }
        .navbar-custom .nav-link:hover {
          color: #cce4ff !important;
        }
        .navbar-custom .navbar-brand {
          color: #fff !important;
        }
        .navbar-custom .navbar-brand:hover {
          color: #cce4ff !important;
        }
      `}</style>

      <nav className="navbar navbar-expand-lg navbar-custom shadow-sm w-100">
        <div className="container-fluid px-3">
          {/* Brand */}
          <Link className="navbar-brand fw-bold d-flex align-items-center" to="/">
            <img
              src={logo}
              alt="AirManager Logo"
              className="me-2"
              style={{ width: "40px", height: "40px", objectFit: "contain" }}
            />
            AirMatrix
          </Link>

          {/* Hamburger */}
          <button
            className="navbar-toggler"
            type="button"
            data-bs-toggle="collapse"
            data-bs-target="#navbarNav"
            aria-controls="navbarNav"
            aria-expanded="false"
            aria-label="Toggle navigation"
          >
            <span className="navbar-toggler-icon"></span>
          </button>

          {/* Nav Links */}
          <div className="collapse navbar-collapse" id="navbarNav">
            <ul className="navbar-nav ms-auto align-items-lg-center">
              {["Planes", "Pilots", "Hangar", "Allocation", "Hangar-Status"].map(
                (item) => (
                  <li className="nav-item" key={item}>
                    <Link className="nav-link fw-medium" to={`/${item.toLowerCase()}`}>
                      {item}
                    </Link>
                  </li>
                )
              )}

              {!isLoggedIn ? (
                <>
                  <li className="nav-item">
                    <Link className="nav-link fw-medium" to="/login">
                      Sign In
                    </Link>
                  </li>
                  <li className="nav-item">
                    <Link className="nav-link fw-medium" to="/register">
                      Sign Up
                    </Link>
                  </li>
                </>
              ) : (
                <li
                  className="nav-item d-flex align-items-center"
                  ref={profileRef}
                  style={{ position: "relative" }}
                >
                  {/* Profile Icon */}
                  <div
                    onClick={toggleProfileDetails}
                    role="button"
                    tabIndex={0}
                    className="rounded-circle ms-3"
                    style={{
                      width: "40px",
                      height: "40px",
                      backgroundImage:
                        'url("https://lh3.googleusercontent.com/aida-public/AB6AXuAK7Wt-J1euGunxR33kKCqZWfYMY3v8C4Lg8JygBQvVEO7AbrP5JxaONfGrvK16HR4Euaae6ECd8jLYHc6US3eC-4szN_bzACrM1iaCjVjsVE4uXRdxdwhz1nKeTATrRAVEw-QsCtKNySpVoSWkAkOiFYloSJhiFlAkMnenryLFeR7wfQnOwKTMI7vw5B3JqIsaEqtbB5HKkAEfRUX-Vm0dSykSOMcagrV0vrkYrrdwBNVg-E_vMt9_SKcdepzRJazNtJ3kHjlWXzTE")',
                      backgroundSize: "cover",
                      backgroundPosition: "center",
                      cursor: "pointer",
                    }}
                  ></div>

                  {/* Profile Dropdown */}
                  {showProfileDetails && (
                    <div
                      className="shadow bg-white rounded p-3"
                      style={{
                        position: "absolute",
                        top: "50px",
                        right: 0,
                        width: "280px",
                        zIndex: 1000,
                        fontSize: "0.85rem",
                      }}
                    >
                      {loading && <p>Loading...</p>}
                      {error && <p className="text-danger">{error}</p>}
                      {!loading && !error && user && (
                        <>
                          <p className="mb-1 fw-bold">{user.name || user.username}</p>
                          <p className="mb-1 text-muted" style={{ fontSize: "0.9rem" }}>
                            {user.email}
                          </p>
                          <p style={{ fontSize: "0.8rem" }}>Role: {user.role}</p>

                          {!expandedDetails ? (
                            <p
                              className="text-primary"
                              style={{ cursor: "pointer", fontSize: "0.85rem" }}
                              onClick={() => setExpandedDetails(true)}
                            >
                              Expand ▼
                            </p>
                          ) : (
                            <>
                              <hr />
                              <p className="mb-0">
                                <strong>Phone:</strong> {user.phoneNo || "N/A"}
                              </p>
                              <p className="mb-0">
                                <strong>House No:</strong> {user.houseNo || "N/A"}
                              </p>
                              <p className="mb-0">
                                <strong>Street:</strong> {user.street || "N/A"}
                              </p>
                              <p className="mb-0">
                                <strong>City:</strong> {user.city || "N/A"}
                              </p>
                              <p className="mb-0">
                                <strong>State:</strong> {user.state || "N/A"}
                              </p>
                              <p className="mb-0">
                                <strong>Pincode:</strong> {user.pincode || "N/A"}
                              </p>

                              <p
                                className="text-primary mt-2"
                                style={{ cursor: "pointer", fontSize: "0.85rem" }}
                                onClick={() => setExpandedDetails(false)}
                              >
                                Collapse ▲
                              </p>
                            </>
                          )}
                        </>
                      )}
                      <button
                        className="btn btn-outline-danger btn-sm w-100 mt-3"
                        onClick={handleLogout}
                      >
                        Logout
                      </button>
                    </div>
                  )}
                </li>
              )}
            </ul>
          </div>
        </div>
      </nav>
    </>
  );
};

export default NavbarComponent;

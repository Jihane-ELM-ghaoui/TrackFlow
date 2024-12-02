import React from 'react';
import './Navbar.css';
import { Link } from 'react-router-dom';
import { useAuth0 } from '@auth0/auth0-react';

const Navbar = ({ handleSidebarToggle, isOpen }) => {
  const { loginWithRedirect, logout, isAuthenticated, user } = useAuth0();

  const handleLogout = () => {
    logout({
      returnTo: window.location.origin, // Redirect back to your site after logout
      federated: true, // Ensure logout from Identity Provider as well
    });
  };

  return (
    <>
      {isAuthenticated ? (
        <>
          <nav id="navbar" className={`${isOpen ? 'navbar-shifted' : ''}`}>
            <button className="sidebar-toggle" onClick={handleSidebarToggle}>
              <i className="bx bx-menu"></i>
            </button>

            <form action="#">
              <div className="form-input">
                <input type="search" placeholder="Search..." />
                <button type="submit" className="search-btn">
                  <i className="bx bx-search"></i>
                </button>
              </div>
            </form>

            <div className="right-items">
              <Link to="/user/notifications" className="notification-icon">
                <i className="bx bx-bell"></i>
              </Link>

              <Link to="/profile">
                <img
                    src={user.picture}
                    alt={user.name}
                    style={{ width: '30px', borderRadius: '50%', marginRight: '10px' }}
                />
              </Link>

              <button
                className="logout-button"
                onClick={handleLogout}
                aria-label="Log out"
              >
                Log Out
              </button>
            </div>
          </nav>
        </>
      ) : (


        <div class="navbarX">

      <Link to="/" className="brand">
        <i className='bx bxs-check-shield'></i>
        <span className="text">CyberLearn</span>
      </Link>

        <div class="navbar-nav">

          <div class="nav-item">
            <a class="nav-link" href="#">Home</a>
          </div>
          <div class="nav-item">
            <a class="nav-link" href="#">About</a>
          </div>
          <div class="nav-item">
            <a class="nav-link" href="#services-list">Services</a>
          </div>
          <div class="nav-item">
            <a class="nav-link" href="#">Contact</a>
          </div>
        </div>
    
  
            <button
              className="login-button"
              onClick={() => loginWithRedirect()}
              aria-label="Log in"
            >
              Log In
            </button>
          
    </div>


      )}
    </>
  );
};

export default Navbar;




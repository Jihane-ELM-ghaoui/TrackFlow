import React, { useEffect, useState } from 'react';
import './Navbar.css';
import { Link } from 'react-router-dom';
import { useAuth0 } from '@auth0/auth0-react';
import axios from 'axios';
import { Stomp } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

const Navbar = ({ handleSidebarToggle, isOpen }) => {
  const { loginWithRedirect, logout, isAuthenticated, getIdTokenClaims, user } = useAuth0();

  const handleLogout = () => {
    logout({
      returnTo: window.location.origin, // Redirect back to your site after logout
      federated: true, // Ensure logout from Identity Provider as well
    });
  };

  const [unseenCount, setUnseenCount] = useState(0);

  useEffect(() => {
    async function fetchUnseenCount() {
      try {
        const response = await fetch(`http://localhost:8000/notification/unseen-count`);
        if (response.ok) {
          const count = await response.json();
          setUnseenCount(count);
        } else {
          console.error("Failed to fetch unseen count:", response.statusText);
        }
      } catch (error) {
        console.error("Error fetching unseen count:", error);
      }
    }
    fetchUnseenCount();

    const stompClient = Stomp.over(() => new SockJS('http://localhost:8000/ws'));

    stompClient.connect({}, () => {
      stompClient.subscribe('/topic/notifications', (message) => {
        if (message.body) {
          const notification = JSON.parse(message.body);
          setUnseenCount((prevCount) => prevCount + 1);
          console.log("Received notification:", notification);
        }
      });
    }, (error) => {
      console.error("WebSocket connection error:", error);
    });

    return () => {
      if (stompClient && stompClient.connected) {
        stompClient.disconnect();
      }
    };
  }, []);

  const markAllAsRead = async () => {
    if (isAuthenticated) {
      try {
        const idTokenClaims = await getIdTokenClaims();
        const idToken = idTokenClaims.__raw;

        await axios.get('http://localhost:8000/notification/markAllAsRead', {
          headers: {
            Authorization: `Bearer ${idToken}`,
          },
        });

        setUnseenCount(0); 
      } catch (error) {
        console.error('Error marking notifications as read:', error);
      }
    }
  };

  return (
    <>
      {isAuthenticated ? (
        <>
          <nav id="navbarSB" className={`${isOpen ? 'navbar-shiftedSB' : ''}`}>
            <button className="sidebar-toggleSB" onClick={handleSidebarToggle}>
              <i className="bx bx-menu"></i>
            </button>

            <form action="#">
              <div className="form-inputSB">
                <input type="search" placeholder="Search..." />
                <button type="submit" className="search-btnSB">
                  <i className="bx bx-search"></i>
                </button>
              </div>
            </form>

            <div className="right-itemsSB">
              <Link to="/notifications" className="notification-iconSB" onClick={markAllAsRead}>
                <i className="bx bx-bell"></i>
                {unseenCount > 0 && (
                  <span className="notification-countSB">
                    {unseenCount}
                  </span>
                )}
              </Link>

              <Link to="/profile">
                <img
                  src={user.picture}
                  style={{ width: '30px', borderRadius: '50%' }}
                  alt="User Profile"
                />
              </Link>

              <button
                className="logout-buttonSB"
                onClick={handleLogout}
                aria-label="Log out"
              >
                Log Out
              </button>
            </div>
          </nav>
        </>
      ) : (
        <div className="navbarXSB">
          <Link to="/" className="brandSB">
            <i className="bx bxs-check-shield"></i>
            <span className="textSB">CyberLearn</span>
          </Link>

          <div className="navbar-navSB">
            <div className="nav-itemSB">
              <a className="nav-linkSB" href="#">Home</a>
            </div>
            <div className="nav-itemSB">
              <a className="nav-linkSB" href="#">About</a>
            </div>
            <div className="nav-itemSB">
              <a className="nav-linkSB" href="#services-list">Services</a>
            </div>
            <div className="nav-itemSB">
              <a className="nav-linkSB" href="#">Contact</a>
            </div>
          </div>

          <button
            className="login-buttonSB"
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
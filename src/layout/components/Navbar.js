import React, { useEffect, useState, useRef } from 'react';
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
  const [isPanelOpen, setIsPanelOpen] = useState(false); // State for showing/hiding panel
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [errorMessage, setErrorMessage] = useState('');
  const stompClientRef = useRef(null); // Persist WebSocket client across renders

  // Fetch unseen notification count
  const fetchUnseenCount = async () => {
    try {
      const response = await fetch('http://localhost:8888/notification-service/notification/unseen-count');
      if (response.ok) {
        const count = await response.json();
        setUnseenCount(count);
      } else {
        console.error('Failed to fetch unseen count:', response.statusText);
      }
    } catch (error) {
      console.error('Error fetching unseen count:', error);
    }
  };

  // WebSocket connection setup
  useEffect(() => {
    const stompClient = Stomp.over(() => new SockJS('http://localhost:8000/ws'));
    stompClientRef.current = stompClient;

    stompClient.connect({}, () => {
      stompClient.subscribe('/topic/notifications', (message) => {
        try {
          const notification = JSON.parse(message.body);
          console.log('Notification received:', notification);

          if (notification.userId && user?.sub && notification.userId === user.sub) {
            // Update unseen count and notification list
            setUnseenCount((prevCount) => prevCount + 1);
            setData((prevData) => [notification, ...prevData]);
          }
        } catch (error) {
          console.error('Error parsing notification:', error);
        }
      });
    }, (error) => {
      console.error('WebSocket connection error:', error);
    });

    return () => {
      if (stompClientRef.current?.connected) {
        stompClientRef.current.disconnect();
      }
    };
  }, [user?.sub]); // Reinitialize WebSocket connection only if user.sub changes

  // Fetch notification data
  const fetchData = async () => {
    if (isAuthenticated) {
      try {
        const idTokenClaims = await getIdTokenClaims();
        const idToken = idTokenClaims.__raw;

        const response = await axios.get('http://localhost:8888/notification-service/notification', {
          headers: {
            Authorization: `Bearer ${idToken}`,
          },
        });

        console.log('Fetched notifications:', response.data);
        setData(response.data);
        setErrorMessage('');
      } catch (error) {
        if (error.response?.status === 403) {
          setErrorMessage("You don't have permission.");
        } else {
          console.error('Error fetching data:', error);
          setErrorMessage('An error occurred while fetching data.');
        }
      } finally {
        setLoading(false);
      }
    }
  };

  useEffect(() => {
    fetchUnseenCount();
    fetchData();
  }, [isAuthenticated]);

  // Mark all notifications as read
  const markAllAsRead = async () => {
    if (isAuthenticated) {
      try {
        const idTokenClaims = await getIdTokenClaims();
        const idToken = idTokenClaims.__raw;

        await axios.get('http://localhost:8888/notification-service/notification/markAllAsRead', {
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

  // Toggle notification panel
  const togglePanel = async () => {
    if (!isPanelOpen) {
      await fetchData(); // Fetch notifications when opening the panel
      await markAllAsRead(); // Mark all notifications as read
    }
    setIsPanelOpen(!isPanelOpen);
  };

  // Helper function for relative time formatting
  const timeAgo = (timestamp) => {
    const now = Date.now();
    const diff = now - new Date(timestamp).getTime();

    const seconds = Math.floor(diff / 1000);
    const minutes = Math.floor(seconds / 60);
    const hours = Math.floor(minutes / 60);
    const days = Math.floor(hours / 24);
    const weeks = Math.floor(days / 7);

    if (seconds < 60) return `${seconds} sec ago`;
    if (minutes < 60) return `${minutes} min ago`;
    if (hours < 24) return `${hours} h ago`;
    if (days < 7) return `${days} day${days > 1 ? 's' : ''} ago`;
    return `${weeks} week${weeks > 1 ? 's' : ''} ago`;
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
              <div className="notification-icon-wrapper">
                <button
                  className="notification-iconSB"
                  onClick={togglePanel}
                  aria-label="Toggle notifications"
                >
                  <i className="bx bx-bell"></i>
                  {unseenCount > 0 && (
                    <span className="notification-countSB">{unseenCount}</span>
                  )}
                </button>
              </div>

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

          {isPanelOpen && (
            <div className="notification-panelSB">
              <div className="notification-headerSB">
                <h2>Notifications</h2>
                <button
                  className="close-panelSB"
                  onClick={togglePanel}
                  aria-label="Close notifications"
                >
                  <i className="bx bx-x"></i>
                </button>
              </div>
              <div className="notification-bodySB">
                {loading ? (
                  <p>Loading...</p>
                ) : errorMessage ? (
                  <p>{errorMessage}</p>
                ) : (
                  <div>
                    {data.length > 0 ? (
                      [...data]
                        .sort((a, b) => new Date(b.timestamp) - new Date(a.timestamp))
                        .map((notification) => (
                          <div key={notification.id} className="notification-itemSB">
                            <p className="notification-messageSB">{notification.message}</p>
                            <p className="notification-timestampSB">{timeAgo(notification.timestamp)}</p>
                          </div>
                        ))
                    ) : (
                      <p>No new notifications.</p>
                    )}
                  </div>
                )}
              </div>
            </div>
          )}
        </>
      ) : (
        <div className="navbarXSB">
          <Link to="/" className="brandSB">
            <i className="bx bxs-check-shield"></i>
            <span className="textSB">TrackFlow</span>
          </Link>

          <div className="navbar-navSB">
            <div className="nav-itemSB">
              <a className="nav-linkSB" href="#home">
                Home
              </a>
            </div>
            <div className="nav-itemSB">
              <a className="nav-linkSB" href="#services">
              Services
              </a>
            </div>
            <div className="nav-itemSB">
              <a className="nav-linkSB" href="#pricing">
              Pricing
              </a>
            </div>
            <div className="nav-itemSB">
              <a className="nav-linkSB" href="#testimonials">
              Testimonials
              </a>
            </div>
            <div className="nav-itemSB">
              <a className="nav-linkSB" href="#contact">
              Contact
              </a>
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

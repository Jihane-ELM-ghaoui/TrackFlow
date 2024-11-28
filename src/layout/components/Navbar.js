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
  const [isPanelOpen, setIsPanelOpen] = useState(false); // State for showing/hiding panel
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [errorMessage, setErrorMessage] = useState('');

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
  
          // Update unseen count
          setUnseenCount((prevCount) => prevCount + 1);
  
          // Dynamically add the new notification to the panel
          setData((prevData) => [notification, ...prevData]); // Add to the top of the list
          
  
          console.log("Received notification:", notification);
        }
      });
    }, (error) => {
      console.error("WebSocket connection error:", error);
    });
  
    // Cleanup on component unmount
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

  const togglePanel = async () => {
    markAllAsRead(); 
    setIsPanelOpen(!isPanelOpen);
  };

  useEffect(() => {
    const fetchData = async () => {
      if (isAuthenticated) {
        try {
          const idTokenClaims = await getIdTokenClaims();
          const idToken = idTokenClaims.__raw;

          const response = await axios.get('http://localhost:8000/notification', {
            headers: {
              Authorization: `Bearer ${idToken}`,
            },
          });

          console.log('Response Data:', response.data);
          setData(response.data);
          setErrorMessage('');
        } catch (error) {
          if (error.response && error.response.status === 403) {
            setErrorMessage("You don't have permission.");
          } else {
            console.error('Error fetching data:', error);
            setErrorMessage('An error occurred while fetching data.');
          }
        } finally {
          setLoading(false);
        }
      } else {
        setLoading(false);
      }
    };

    fetchData();
  }, [getIdTokenClaims, isAuthenticated]);



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
                    <span className="notification-countSB">
                      {unseenCount}
                    </span>
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


          {/* Notification Panel */}
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
                              .sort((a, b) => new Date(b.timestamp) - new Date(a.timestamp)) // Sort newest to oldest
                              .map((notification) => (
                                <div key={notification.id} className="notification-itemSB">
                                  <p className="notification-messageSB">{notification.message}</p>
                                  <p className="notification-timestampSB">
                                    {timeAgo(notification.timestamp)}
                                  </p>
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

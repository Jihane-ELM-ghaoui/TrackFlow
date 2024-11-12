import React, { useEffect, useState } from 'react';
import { useAuth0 } from '@auth0/auth0-react';
import axios from 'axios';

const Notification = () => {
  const { isAuthenticated, getIdTokenClaims } = useAuth0();
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [errorMessage, setErrorMessage] = useState('');

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

          console.log('Response Data:', response.data); // Debugging line
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

  return (
    <div>
      {loading ? (
        <p>Loading...</p>
      ) : errorMessage ? (
        <p>{errorMessage}</p>
      ) : (
        <div>
          <h2>Notifications</h2>
          {data.length > 0 ? (
            data.map((notification) => (
              <div key={notification.id}>
                <p>Message: {notification.message} Date: {new Date(notification.timestamp).toLocaleDateString()}</p>
              </div>
            ))
          ) : (
            <p>No notifications available.</p>
          )}
        </div>
      )}
    </div>
  );
};

export default Notification;

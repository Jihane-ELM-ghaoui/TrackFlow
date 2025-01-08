import React, { useState, useEffect } from 'react';
import './TaskCard.css'; 
import { useAuth0 } from '@auth0/auth0-react';
import axios from 'axios';

function TaskCard({ title, fetchUrl }) {
  const { isAuthenticated, getIdTokenClaims, getAccessTokenSilently } = useAuth0();
  const [userMetadata, setUserMetadata] = useState(null);
  const [data, setData] = useState('');
  const [idToken, setIdToken] = useState(''); 
  const [token, setToken] = useState('');
  const [errorMessage, setErrorMessage] = useState('');
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  
 useEffect(() => {
    if (!isAuthenticated) {
      return;
    }

    const getUserMetadataAndToken = async () => {
      try {
        // Fetch the ID token claims
        const idTokenClaims = await getIdTokenClaims();
        console.log('ID token claims:', idTokenClaims);

        if (idTokenClaims) {
          const metadata = idTokenClaims['https://demo.app.com/user_metadata'];
          console.log('User metadata:', metadata);
          setUserMetadata(metadata);
        } else {
          console.error('No ID token claims available');
        }

        // Fetch the access token silently
        const accessToken = await getAccessTokenSilently();
        console.log('Access Token:', accessToken);
        setToken(accessToken);  // Set token in state

      } catch (error) {
        console.error('Error getting token or metadata:', error);
        setError('Error fetching user metadata or token');
      } finally {
        setLoading(false);
      }
    };

    getUserMetadataAndToken();
  }, [getIdTokenClaims, getAccessTokenSilently, isAuthenticated]);


  useEffect(() => {
    const fetchData = async () => {
      
      try {
        const idTokenClaims = await getIdTokenClaims(); 
        const idToken = idTokenClaims.__raw;

        const response = await axios.get(fetchUrl, {
          headers: { Authorization: `Bearer ${idToken}` },
        });

        setItems(response.data);
      } catch (err) {
        console.error('Error fetching data:', err);
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [fetchUrl]);

  return (
    <div className="task-card-kh">
      <div className="task-card-header-kh">
        <h2>{title}</h2>
      </div>
      <div className="task-card-content-kh">
        {loading ? (
          <p>Loading...</p>
        ) : error ? (
          <p className="error-message">{error}</p>
        ) : items.length > 0 ? (
          <div className="tasks-kh">
            {items.map((item, index) => (
            <div className="project-kh" key={index}>
                <p><strong>{item.name}</strong></p> {/* Maps the 'name' field from the project API response */}
                <small>{item.description}</small> {/* Optionally map 'description' */}
            </div>
            ))}
          </div>
        ) : (
          <p>No data available</p>
        )}
      </div>
    </div>
  );
}

export default TaskCard;

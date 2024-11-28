import React, { useEffect, useState } from 'react';
import { useAuth0 } from '@auth0/auth0-react';

import './Profile.css'; // Import your custom CSS file

const Profile = () => {
  const { user, getIdTokenClaims, getAccessTokenSilently, isAuthenticated } = useAuth0();
  const [userMetadata, setUserMetadata] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [token, setToken] = useState(null);  


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

  if (!isAuthenticated) {
    return <div>Not authenticated. Please log in.</div>;
  }

  if (loading) {
    return <div>Loading user data...</div>;
  }

  if (error) {
    return <div>{error}</div>;
  }

  return (

    <div class="boxSB">

      <div class="cardSB">

        <div class="profileSB">
          <img className="card-imgSB" src={user.picture} alt="Profile" />
          <h2>{userMetadata?.Full_Name || "Not provided" }</h2>

          <a href="/profileUpdate" aria-label="Edit">
            <i className="bx bxs-edit"></i>
          </a>


        </div>

        <div class="infoSB">
          
          <h3>Information</h3>
            <div class="card2SB">
              <div>
                <p><strong>Email:</strong></p>
                <p> {user.email}</p>
              </div>
              <div>
                <p><strong>Phone:</strong></p>
                <p>{userMetadata?.Phone_Number || "Not provided"}</p>
              </div>
            </div>

          <h3 class="titleSB">Career</h3>
            <div class="card2SB">
              <div>
                  <p><strong>Organisation:</strong></p>
                  <p>{userMetadata?.Organisation || "Not provided" }</p>
              </div>
              <div>
                  <p><strong>Job:</strong></p>
                  <p>{userMetadata?.job || "Not provided" }</p>
              </div>
            </div>
      
        </div>


      </div>

    </div>

  );
};

export default Profile;

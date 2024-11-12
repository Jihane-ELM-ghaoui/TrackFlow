import React, { useEffect, useState } from 'react';
import { useAuth0 } from '@auth0/auth0-react';
import axios from 'axios';

const Home = () => {
  const { isAuthenticated, getIdTokenClaims } = useAuth0();
  const [data, setData] = useState('');
  const [idToken, setIdToken] = useState(''); 
  const [loading, setLoading] = useState(true);
  const [errorMessage, setErrorMessage] = useState('');

  useEffect(() => {
    const fetchData = async () => {
      if (isAuthenticated) {
        try {
          const idTokenClaims = await getIdTokenClaims(); 
          const idToken = idTokenClaims.__raw; 
          setIdToken(idToken); 

          const response = await axios.get('http://localhost:8080/api/protected', {
            headers: {
              Authorization: `Bearer ${idToken}`, 
            },
          });

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


    </div>
  );
};

export default Home;


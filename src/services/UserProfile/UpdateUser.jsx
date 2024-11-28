import React, { useEffect, useState } from 'react';
import { useAuth0 } from '@auth0/auth0-react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import PhoneInput from 'react-phone-input-2';
import 'react-phone-input-2/lib/style.css';
import './UpdateUser.css';

const UpdateUser = () => {
  const { user, getIdTokenClaims, getAccessTokenSilently, isAuthenticated, logout } = useAuth0();
  const [userMetadata, setUserMetadata] = useState({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [token, setToken] = useState(null);
  const [success, setSuccess] = useState(false);
  const [validationError, setValidationError] = useState(false);
  const [editedMetadata, setEditedMetadata] = useState({});

  const navigate = useNavigate();

  useEffect(() => {
    if (!isAuthenticated) {
      return;
    }

    const getUserMetadataAndToken = async () => {
      try {
        const idTokenClaims = await getIdTokenClaims();
        if (idTokenClaims) {
          const metadata = idTokenClaims['https://demo.app.com/user_metadata'];
          setUserMetadata(metadata);
          setEditedMetadata(metadata);
        }
        const accessToken = await getAccessTokenSilently({
          audience: 'https://dev-y4ownsl82b0t7zeb.us.auth0.com/api/v2/',
          scope: 'update:current_user_metadata',
        });
        setToken(accessToken);
      } catch (error) {
        setError('Error fetching user metadata or token');
      } finally {
        setLoading(false);
      }
    };

    getUserMetadataAndToken();
  }, [getIdTokenClaims, getAccessTokenSilently, isAuthenticated]);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setEditedMetadata({ ...editedMetadata, [name]: value });
  };

  const handlePhoneChange = (phone) => {
    setEditedMetadata((prevMetadata) => ({
      ...prevMetadata,
      Phone_Number: phone,
    }));
  };

  const validateInputs = () => {
    const requiredFields = ['Full_Name', 'Phone_Number', 'Organisation', 'job'];
    return requiredFields.every(field => editedMetadata[field] && editedMetadata[field].trim() !== '');
  };

  const handleSave = async (e) => {
    e.preventDefault();

    if (!validateInputs()) {
      setValidationError(true);
      return;
    }

    try {
      await axios.patch(
        `https://dev-y4ownsl82b0t7zeb.us.auth0.com/api/v2/users/${user.sub}`,
        {
          user_metadata: editedMetadata,
        },
        {
          headers: {
            Authorization: `Bearer ${token}`,
            'Content-Type': 'application/json',
          },
        }
      );
      setSuccess(true);
      setValidationError(false);
    } catch (error) {
      setError('Error updating metadata');
    }
  };

  const handleCancel = () => {
    navigate('/profile');
  };

  if (!isAuthenticated) {
    return <div>Not authenticated. Please log in.</div>;
  }

  if (loading) {
    return <div>Loading user data...</div>;
  }

  if (error) {
    return <div>{error}</div>;
  }

  const handlePasswordReset = () => {
    if (isAuthenticated && user) {
      window.location.href = `https://dev-y4ownsl82b0t7zeb.us.auth0.com/dbconnections/change_password?client_id=GtmdWzB9PrIS4oH01NjPAOZaF1zBJ2kp&email=${user.email}&connection=Username-Password-Authentication`;
    } else {
      console.log('You need to be logged in to reset your password.');
    }
  };

  return (
    <div className="update-user-containerSB">
      <div className="update-user-cardSB">
        <img className="profile-imgSB" src={user.picture} alt="Profile" />
        <div className="card-bodySB"></div>
        <form onSubmit={handleSave}>
          <ul className="list-groupSB">
            
            <li className="list-group-itemSB">
              <p><strong>Full Name :</strong></p>
              <input
                type="text"
                name="Full_Name"
                value={editedMetadata.Full_Name || "Not provided"}
                onChange={handleInputChange}
                className="input-fieldSB"
              />
            </li>

            <li className="list-group-itemSB">
              <p><strong>Phone :</strong></p>
              <PhoneInput
                country={'us'}
                value={editedMetadata.Phone_Number || ''}
                onChange={handlePhoneChange}
                enableSearch
                className="input-fieldSB"
              />
            </li>

            <li className="list-group-itemSB">
              <p><strong>Organisation :</strong></p>
              <input
                type="text"
                name="Organisation"
                value={editedMetadata.Organisation || "Not provided"}
                onChange={handleInputChange}
                className="input-fieldSB"
              />
            </li>

            <li className="list-group-itemSB">
              <p><strong>Job:</strong></p>
              <select
                name="job"
                value={editedMetadata.job || "Not provided"}
                onChange={handleInputChange}
                className="input-fieldSB"
              >
                <option value="Software Developer">Software Developer</option>
                <option value="UI/UX Designer">UI/UX Designer</option>
                <option value="Security Engineer">Security Engineer</option>
                <option value="Data Engineer">Data Engineer</option>
                <option value="DevOps Engineer">DevOps Engineer</option>
                <option value="Other">Other</option>
              </select>
            </li>
          </ul>
          <button className="save-btnSB" type="button" onClick={handlePasswordReset}>Reset Password</button>
          <button className="save-btnSB" type="submit">Save Changes</button>
          <button className="save-btnSB" type="button" onClick={handleCancel}>Cancel</button>
        </form>

        {validationError && (
          <div className="error-messageSB">
            Please make sure all information is complete before saving.
          </div>
        )}

        {success && (
          <div className="success-messageSB">
            Metadata updated successfully! Please <strong>log out</strong> and log in again to see the changes.
            <button className="logout-btnSB" onClick={() => logout({ returnTo: window.location.origin })}>
              Log out
            </button>
          </div>
        )}
      </div>
    </div>
  );
};

export default UpdateUser;

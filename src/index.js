import React from 'react';
import ReactDOM from 'react-dom';
import App from './App';
import { Auth0Provider } from '@auth0/auth0-react';
import { BrowserRouter as Router, useNavigate } from 'react-router-dom';

const domain = process.env.REACT_APP_AUTH0_DOMAIN;
const clientId = process.env.REACT_APP_AUTH0_CLIENT_ID;

const Auth0ProviderWithNavigate = ({ children }) => {
  const navigate = useNavigate();

  const onRedirectCallback = (appState) => {
    navigate(appState?.returnTo || '/home');
  };

  return (
    <Auth0Provider
      domain={domain}
      clientId={clientId}
      redirectUri={window.location.origin}
      onRedirectCallback={onRedirectCallback}
      useRefreshTokens={true}
      cacheLocation="localstorage" // Persisting token between pages!!

      
      authorizationParams={{
        audience: "https://dev-y4ownsl82b0t7zeb.us.auth0.com/api/v2/",
        scope: "openid profile email read:current_user update:current_user_metadata"
      }}
      
    >
      {children}
    </Auth0Provider>
  );
};


const AppWrapper = () => (
  <Router>
    <Auth0ProviderWithNavigate>
      <App />
    </Auth0ProviderWithNavigate>
  </Router>
);

ReactDOM.render(
  <AppWrapper />,
  document.getElementById('root')
);

// src/components/ProtectedRoute.js
import React from 'react';
import { useAuth0 } from '@auth0/auth0-react';

import NotFound from './Pages/NotFound';


const ProtectedRoute = ({ component: Component, roles, ...rest }) => {
  const { isAuthenticated, user } = useAuth0();
  const userHasRequiredRole = (user, roles) =>
    user && roles.some((role) => user['https://demo.app.com/roles']?.includes(role));

  if (!isAuthenticated) {
    return <NotFound/>;
  }

  if (roles && !userHasRequiredRole(user, roles)) {
    return <NotFound />;
  }

  return <Component {...rest} />;
};

export default ProtectedRoute;

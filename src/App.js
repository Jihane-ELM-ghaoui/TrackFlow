import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import React, { useState, useEffect } from 'react';
import { useAuth0 } from '@auth0/auth0-react';


import './App.css';


import Navbar from './layout/components/Navbar';
import Sidebar from './layout/components/Sidebar';
import Main from './layout/components/Main';
import Footer from './layout/components/Footer';


import Loading from './Pages/Loading';
import NotFound from './Pages/NotFound';
import ProtectedRoute from './ProtectedRoute';


import Profile from './UserProfile/Profile';
import UpdateUser from './UserProfile/UpdateUser';

import UserDashboard from './services/UserDashboard';
import AdminDashboard from './services/AdminDashboard';

import Home from './services/Home';
import Notification from './services/Notification';


function App() {

  const [isOpen, setIsOpen] = useState(false);

  const toggleSidebar = () => {
    setIsOpen(!isOpen);
  };



  const { isLoading, error } = useAuth0();

  if (isLoading) return <Loading />;
  if (error) return <div>Oops... {error.message}</div>;

  
  return (
    <div className="App">

      <Sidebar isOpen={isOpen} />



      <Navbar handleSidebarToggle={toggleSidebar} isOpen={isOpen} />



        <div className="main-content">

          <Routes>

              <Route exact path="/" element={<Main />} />



              <Route
                path="/profile"
                element={<ProtectedRoute component={Profile}/>}
              />

              <Route
                path="/notifications"
                element={<ProtectedRoute component={Notification}/>}
              />

              <Route
                path="/update"
                element={<ProtectedRoute component={UpdateUser}/>}
              />


              <Route
                path="/home"
                element={<ProtectedRoute component={Home}/>}
              />


              <Route
                path="/user/dashboard"
                element={<ProtectedRoute component={UserDashboard} roles={['User']} />}
              />

              <Route
                path="/admin-dashboard"
                element={<ProtectedRoute component={AdminDashboard} roles={['Admin']} />}
              />




              <Route path="*" element={<NotFound />} />


          </Routes>

        </div>


      <Footer /> 

    </div>
  );
}

export default App;

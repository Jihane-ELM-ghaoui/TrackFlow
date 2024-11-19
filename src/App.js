import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import React, { useState, useEffect } from 'react';

import './App.css';


import Navbar from './layout/components/Navbar';
import Sidebar from './layout/components/Sidebar';
import Main from './layout/components/Main';
import Footer from './layout/components/Footer';


import NotFound from './Pages/NotFound';
import ProtectedRoute from './ProtectedRoute';


import Profile from './UserProfile/Profile';
import UpdateUser from './UserProfile/UpdateUser';

import UserDashboard from './services/UserDashboard';
import AdminDashboard from './services/AdminDashboard';

import Home from './services/Home';


function App() {

  const [isOpen, setIsOpen] = useState(false);

  const toggleSidebar = () => {
    setIsOpen(!isOpen);
  };



  
  return (
    <div className="AppSB">

      <Sidebar isOpen={isOpen} />
      <Navbar handleSidebarToggle={toggleSidebar} isOpen={isOpen} />


        <div className="main-contentSB">

          <Routes>

              <Route exact path="/" element={<Main />} />


              <Route
                path="/profile"
                element={<ProtectedRoute component={Profile}/>}
              />

              <Route
                path="/profileUpdate"
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

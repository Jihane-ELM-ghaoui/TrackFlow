import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import React, { useState, useEffect } from 'react';

import './App.css';


import Navbar from './layout/components/Navbar';
import Sidebar from './layout/components/Sidebar';
import Main from './layout/components/Main';
import Footer from './layout/components/Footer';


import NotFound from './Pages/NotFound';
import ProtectedRoute from './ProtectedRoute';


import Profile from './services/UserProfile/Profile';
import UpdateUser from './services/UserProfile/UpdateUser';

import AdminDashboard from './services/AdminDashboard';

import Dashboard from './services/Dashboard/Dashboard';
import Storage from './services/Storage/Storage'

import Project from './services/project/project';
import AddProject from './services/project/AddProject';
import JoinProject from './services/project/JoinProject';


import Chat from './services/Chat/Chat'

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
                path="/chat"
                element={<ProtectedRoute component={Chat}/>}
              />

              <Route
                path="/profile"
                element={<ProtectedRoute component={Profile}/>}
              />

              <Route
                path="/profileUpdate"
                element={<ProtectedRoute component={UpdateUser}/>}
              />


              <Route
                path="/dashboard"
                element={<ProtectedRoute component={Dashboard}/>}
              />

              <Route
                path="/storage"
                element={<ProtectedRoute component={Storage}/>}
              />

              <Route
                  path="/user/add-project"
                  element={<ProtectedRoute component={AddProject} />}
              />


              <Route
                path="/admin-dashboard"
                element={<ProtectedRoute component={AdminDashboard} roles={['Admin']} />}
              />
                  
              <Route
                path="/Project/:projectId"
                element={<ProtectedRoute component={Project} />}
              />

              <Route path="/projects/join" element={<JoinProject />} />


              <Route path="*" element={<NotFound />} />


          </Routes>

        </div>


      <Footer /> 

    </div>
  );
}

export default App;

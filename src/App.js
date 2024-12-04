import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import React, { useState, useEffect } from 'react';
import { useAuth0 } from '@auth0/auth0-react';


import './App.css';


import Navbar from './layout/components/Navbar';
import Sidebar from './layout/components/Sidebar';
import Main from './layout/components/Main';
import Footer from './layout/components/Footer';

import Dashboard from './Services/Dashboard/Dashboard';
import Storage from './Services/Storage/Storage';

import Loading from './Pages/Loading';
import NotFound from './Pages/NotFound';
import ProtectedRoute from './ProtectedRoute';



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

              <Route exact path="/user/dashboard" element={<Dashboard />} />
              <Route exact path="/user/storage" element={<Storage />} />


              <Route path="*" element={<NotFound />} />


          </Routes>

        </div>


      <Footer /> 

    </div>
  );
}

export default App;

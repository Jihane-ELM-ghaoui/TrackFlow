import React, { useState, useEffect } from 'react';
import { NavLink, useLocation } from 'react-router-dom';
import './Sidebar.css';


import { useAuth0 } from '@auth0/auth0-react';

const Sidebar = ({ isOpen }) => {

  const {isAuthenticated} = useAuth0();


  const [activeItem, setActiveItem] = useState('Dashboard');
  const location = useLocation();

  useEffect(() => {
    // Set active item based on current pathname
    if (location.pathname === '/dashboard') {
      setActiveItem('Dashboard');
    } else if (location.pathname.includes('/user/add-project')) {
      setActiveItem('Services');
    } else if (location.pathname.includes('/storage')) {
      setActiveItem('Storage');
    } else if (location.pathname.includes('/user/todo')) {
      setActiveItem('Todo List');
    } else if (location.pathname.includes('/chat') || location.pathname.includes('/chat/:receiver')) {
      setActiveItem('Chat');
    } else {
      setActiveItem('');
    }
  }, [location.pathname]);

  const handleItemClick = (item) => {
    setActiveItem(item);
  };

  return (


    <>


{isAuthenticated ? (
          <>

<section id="sidebarSB" className={isOpen ? '' : 'hide'}>
      <NavLink to="/" className="brandSB" onClick={() => handleItemClick('')}>
        <i className='bx bxs-check-shield'></i>
        <span className="textSB">TrackFlow</span>
      </NavLink>
      <ul className="side-menuSB top">
        <li className={activeItem === 'Dashboard' ? 'active' : ''}>
          <NavLink to="/dashboard" onClick={() => handleItemClick('Dashboard')}>
            <i className='bx bxs-home'></i>
            <span className="textSB">Dashboard</span>
          </NavLink>
        </li>
        <li className={activeItem === 'Services' ? 'active' : ''}>
          <NavLink to="/user/add-project" onClick={() => handleItemClick('Services')}>
            <i className='bx bxs-dashboard'></i>
            <span className="textSB">Projects</span>
          </NavLink>
        </li>
        <li className={activeItem === 'Storage' ? 'active' : ''}>
          <NavLink to="/storage" onClick={() => handleItemClick('Storage')}>
            <i className='bx bxs-data'></i>
            <span className="textSB">Storage</span>
          </NavLink>
        </li>
        <li className={activeItem === 'Chat' ? 'active' : ''}>
          <NavLink to="/chat" onClick={() => handleItemClick('Chat')}>
            <i className='bx bxs-message-dots'></i>
            <span className="textSB">Chat Room</span>
          </NavLink>
        </li>
      </ul>

      </section>
      </>
        ) : (
          
          <></>

        )}



    </>
    
  );
};

export default Sidebar;

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
    } else if (location.pathname.includes('/user/cours') || location.pathname.includes('/user/cours/modules/:year/:semester') || location.pathname.includes('/user/cours/modules/list/:year/:semester/:module') ) {
      setActiveItem('Services');
    } else if (location.pathname.includes('/storage')) {
      setActiveItem('Storage');
    } else if (location.pathname.includes('/user/todo')) {
      setActiveItem('Todo List');
    } else if (location.pathname.includes('/user/community/articles')) {
      setActiveItem('Community');
    } else if (location.pathname.includes('/user/dashboard/:id') || location.pathname.includes('/user/dashboard') || location.pathname.includes('/user/dashboard/:id/edit') ) {
      setActiveItem('UserDashboard');
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
          <NavLink to="/projects" onClick={() => handleItemClick('Services')}>
            <i className='bx bxs-dashboard'></i>
            <span className="textSB">Services</span>
          </NavLink>
        </li>
        <li className={activeItem === 'Storage' ? 'active' : ''}>
          <NavLink to="/storage" onClick={() => handleItemClick('Storage')}>
            <i className='bx bxs-data'></i>
            <span className="textSB">Storage</span>
          </NavLink>
        </li>
        <li className={activeItem === 'Todo List' ? 'active' : ''}>
          <NavLink to="/user/todo" onClick={() => handleItemClick('Todo List')}>
            <i className='bx bx-list-check'></i>
            <span className="textSB">Todo List</span>
          </NavLink>
        </li>
        <li className={activeItem === 'Community' ? 'active' : ''}>
          <NavLink to="/user/community/articles" onClick={() => handleItemClick('Community')}>
            <i className='bx bxs-message-dots'></i>
            <span className="textSB">Community</span>
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

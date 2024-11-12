import React, { useState, useEffect } from 'react';
import { NavLink, useLocation } from 'react-router-dom';
import './Sidebar.css';


import { useAuth0 } from '@auth0/auth0-react';

const Sidebar = ({ isOpen }) => {

  const {isAuthenticated} = useAuth0();


  const [activeItem, setActiveItem] = useState('Home');
  const location = useLocation();

  useEffect(() => {
    // Set active item based on current pathname
    if (location.pathname === '/home') {
      setActiveItem('Home');
    } else if (location.pathname.includes('/user/cours') || location.pathname.includes('/user/cours/modules/:year/:semester') || location.pathname.includes('/user/cours/modules/list/:year/:semester/:module') ) {
      setActiveItem('Services');
    } else if (location.pathname.includes('/user/library')) {
      setActiveItem('Library');
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

<section id="sidebar" className={isOpen ? '' : 'hide'}>
      <NavLink to="/" className="brand" onClick={() => handleItemClick('')}>
        <i className='bx bxs-check-shield'></i>
        <span className="text">CyberLearn</span>
      </NavLink>
      <ul className="side-menu top">
        <li className={activeItem === 'Home' ? 'active' : ''}>
          <NavLink to="/home" onClick={() => handleItemClick('Home')}>
            <i className='bx bxs-home'></i>
            <span className="text">Home</span>
          </NavLink>
        </li>
        <li className={activeItem === 'Services' ? 'active' : ''}>
          <NavLink to="/user/cours" onClick={() => handleItemClick('Services')}>
            <i className='bx bxs-dashboard'></i>
            <span className="text">Services</span>
          </NavLink>
        </li>
        <li className={activeItem === 'Library' ? 'active' : ''}>
          <NavLink to="/user/library" onClick={() => handleItemClick('Library')}>
            <i className='bx bxs-book'></i>
            <span className="text">Library</span>
          </NavLink>
        </li>
        <li className={activeItem === 'Todo List' ? 'active' : ''}>
          <NavLink to="/user/todo" onClick={() => handleItemClick('Todo List')}>
            <i className='bx bx-list-check'></i>
            <span className="text">Todo List</span>
          </NavLink>
        </li>
        <li className={activeItem === 'Community' ? 'active' : ''}>
          <NavLink to="/user/community/articles" onClick={() => handleItemClick('Community')}>
            <i className='bx bxs-message-dots'></i>
            <span className="text">Community</span>
          </NavLink>
        </li>
        <li className={activeItem === 'UserDashboard' ? 'active' : ''}>
          <NavLink to="/user/dashboard" onClick={() => handleItemClick('UserDashboard')}>
            <i className='bx bxs-group'></i>
            <span className="text">UserDashboard</span>
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

import React from 'react';
import './Main.css';
import logo from '../img/logo_GS-rmv_bg.png';
import img1 from '../img/top001_01-768x768.png';

const Main = () => {
  return (
    
    <main>

    <div className="head-title">
        <div className="left">
            <h1>Welcome to <span> CyberLearn </span></h1>
            <p>Empower yourself with cutting-edge cybersecurity education.</p>
        </div>
        <img className="right-image" src={img1} alt="CyberLearn Illustration" />
    </div>

    <div className="centered-container">
        <div className="image-text-container">
            <div className="image-text-content">
                <h2>Who Are We?</h2>
                <p>
                    CyberLearn isn’t just an application; it's a dynamic educational platform tailored for those dedicated to safeguarding our digital landscapes. Designed for students and professionals within the realm of cyber defense, CyberEdu offers an immersive learning experience, equipping users with the knowledge and skills needed to navigate the complexities of cybersecurity.
                </p>
                <ul className="services-list" id="services-list">
                    <li><i className='bx bxs-book'></i> Courses</li>
                    <li><i className='bx bxs-book'></i> Library</li>
                    <li><i className='bx bxs-book'></i> Todo list</li>
                    <li><i className='bx bxs-book'></i> Community</li>
                </ul>
            </div>
            <img className="logo-image" src={logo} alt="GCDSTE Logo" />
        </div>
    </div>
</main>


  );
};

export default Main;

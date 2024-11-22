import React from 'react';
import './Footer.css';

const Footer = () => {
  return (
    <footer className="footer">
      <div className="social-icons">
        <a href="https://www.facebook.com" target="_blank" rel="noopener noreferrer" aria-label="Facebook">
          <i className="bx bxl-facebook"></i>
        </a>
        <a href="https://www.twitter.com" target="_blank" rel="noopener noreferrer" aria-label="Twitter">
          <i className="bx bxl-twitter"></i>
        </a>
        <a href="https://www.instagram.com" target="_blank" rel="noopener noreferrer" aria-label="Instagram">
          <i className="bx bxl-instagram"></i>
        </a>
        <a href="https://www.linkedin.com" target="_blank" rel="noopener noreferrer" aria-label="LinkedIn">
          <i className="bx bxl-linkedin"></i>
        </a>
      </div>
      <p>© 2024 CyberLearn. Tous droits réservés.</p>
    </footer>
  );
};

export default Footer;

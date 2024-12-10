import React from 'react';
import './Main.css';
import logo from '../img/logo_GS-rmv_bg.png';
import img1 from '../img/top001_01-768x768.png';

const Main = () => {
  return (
    <main>
      {/* Section Home */}
      <section id="home" className="section-home">
        <div className="head-title">
          <div className="left">
            <h1>Welcome to <span> TrackFlow </span></h1>
            <p>Empower yourself with cutting-edge management tools.</p>
          </div>
          <img className="right-image" src={img1} alt="CyberLearn Illustration" />
        </div>
      </section>

      {/* Section Services */}
      <section id="services" className="section-services">
        <div className="centered-container">
          <h2 className="section-title">Our Services</h2>
          <p className="section-description">
          TrackFlow provides essential tools to optimize your work and enhance collaboration within your team.
          </p>
          <div className="services-grid">
            <div className="service-box">
              <h3>Project Tracking Tools</h3>
              <p>Gain a comprehensive overview of your team's project progress and individual tasks.</p>
            </div>
            <div className="service-box">
              <h3>Shared Storage Space</h3>
              <p>Store and share documents securely with your entire team.</p>
            </div>
            <div className="service-box">
              <h3>Productivity Analytics Tools</h3>
              <p>Track your team's performance with detailed analytics tools.</p>
            </div>
          </div>
        </div>
      </section>

      {/* Section Pricing */}
<section id="pricing" className="section-pricing">
  <div className="centered-container">
    <h2 className="section-title">Our Pricing</h2>
    <p className="section-description">
      Choose the right plan that fits your needs and empower your team.
    </p>
    <div className="pricing-grid">
      <div className="pricing-box">
        <h3>Basic Plan</h3>
        <p className="price">$19/month</p>
        <ul>
          <li>Up to 5 team members</li>
          <li>Basic project tracking</li>
          <li>Shared storage space (10GB)</li>
        </ul>
        <button>Choose Plan</button>
      </div>
      <div className="pricing-box">
        <h3>Professional Plan</h3>
        <p className="price">$49/month</p>
        <ul>
          <li>Up to 20 team members</li>
          <li>Advanced project tracking</li>
          <li>Shared storage space (50GB)</li>
          <li>Productivity analytics tools</li>
        </ul>
        <button>Choose Plan</button>
      </div>
      <div className="pricing-box">
        <h3>Enterprise Plan</h3>
        <p className="price">$99/month</p>
        <ul>
          <li>Unlimited team members</li>
          <li>Custom project tracking features</li>
          <li>Shared storage space (100GB)</li>
          <li>Priority customer support</li>
        </ul>
        <button>Choose Plan</button>
      </div>
    </div>
  </div>
</section>


      {/* Section Testimonials */}
        <section id="testimonials" className="section-testimonials">
          <div className="centered-container">
            <h2 className="section-title">What Our Clients Say</h2>
            <div className="testimonial-grid">
              <div className="testimonial-box">
                <p>"TrackFlow's project tracking tools have transformed the way our team collaborates. Highly recommended!"</p>
                <h4>- Jihane El M'ghaoui, Group's main rememberer</h4>
            </div>
              <div className="testimonial-box">
                <p>"The shared storage space is a lifesaver. We can easily store, share, and collaborate on documents in real time."</p>
                <h4>- Nabila Bazar, Product Manager at TrackFlow</h4>
              </div>
            </div>
          </div>
        </section>



      {/* Section Contact */}
      <section id="contact" className="contact-section">
          <div className="contact-container">
            <h2>Contactez-Nous</h2>
            <form action="/submit" method="POST" className="contact-form">
              <div className="form-group">
                <label htmlFor="name">Nom</label>
                <input
                  type="text"
                  id="name"
                  name="name"
                  placeholder="Entrez votre nom"
                  required
                />
              </div>
              <div className="form-group">
                <label htmlFor="email">Email</label>
                <input
                  type="email"
                  id="email"
                  name="email"
                  placeholder="Entrez votre email"
                  required
                />
              </div>
              <div className="form-group">
                <label htmlFor="message">Message</label>
                <textarea
                  id="message"
                  name="message"
                  placeholder="Entrez votre message"
                  required
                ></textarea>
              </div>
              <button type="submit" className="submit-btn">Envoyer</button>
            </form>
          </div>
        </section>

    </main>
  );
};



export default Main;


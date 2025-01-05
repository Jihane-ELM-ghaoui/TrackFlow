import React, { useState } from 'react';
import './Main.css';


const Main = () => {

  const [selectedPlan, setSelectedPlan] = useState(null);
  const [showPaymentForm, setShowPaymentForm] = useState(false);
  const [paymentStatus, setPaymentStatus] = useState("");
  const [errorPopup, setErrorPopup] = useState(false);

  const handleOpenPaymentForm = (plan) => {
    setSelectedPlan(plan);
    setShowPaymentForm(true);
  };

  const handleClosePaymentForm = () => {
    setSelectedPlan(null);
    setShowPaymentForm(false);
  };

  const handlePayment = async (paymentData) => {
    try {
      const response = await fetch("https://mocky.io/v3/8cf2e4b2-13b2-4b78-a8d9-ea44e5693a72", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(paymentData),
      });

      const result = await response.json();
      if (response.ok) {
        setPaymentStatus(`Payment for ${selectedPlan.name}: ${result.message || "Payment successful!"}`);
        setShowPaymentForm(false);
      } else {
        throw new Error("Payment failed");
      }
    } catch (error) {
      setPaymentStatus("");
      setErrorPopup(true);
    }
  };






  return (
    <main>

      {/* Section Home */}
      <section id="home" className="section-home">

<div className="SB-app">
      <header className="SB-header">
        <div className="SB-hero">
          <h1 className="SB-hero-title">WELCOME TO <span> TrackFlow </span></h1>
          <p className="SB-hero-text">
                Empower yourself with cutting-edge management tools !!
          </p>
          <a href="#services" className="SB-btn">Get In Touch</a>
          <div className="SB-scroll-indicator">↓</div>
        </div>
      </header>
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
              <button onClick={() => handleOpenPaymentForm({ name: "Basic Plan", amount: "19.00" })}>
                Choose Plan
              </button>
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
              <button onClick={() => handleOpenPaymentForm({ name: "Professional Plan", amount: "49.00" })}>
                Choose Plan
              </button>
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
              <button onClick={() => handleOpenPaymentForm({ name: "Enterprise Plan", amount: "99.00" })}>
                Choose Plan
              </button>
            </div>
          </div>
          <p className="payment-status">{paymentStatus}</p>
        </div>
      </section>

      {/* Payment Form Modal */}
      {showPaymentForm && (
        <div className="modal">
          <div className="modal-content">
            <h2>Payment for {selectedPlan.name}</h2>
            <form
              onSubmit={(e) => {
                e.preventDefault();
                const formData = new FormData(e.target);
                const paymentData = {
                  card_number: formData.get("card_number"),
                  expiration_date: formData.get("expiration_date"),
                  cvv: formData.get("cvv"),
                  amount: selectedPlan.amount,
                };
                handlePayment(paymentData);
              }}
            >
              <div className="form-group">
                <label>Card Number</label>
                <input type="text" name="card_number" placeholder="4242 4242 4242 4242" required />
              </div>
              <div className="form-group">
                <label>Expiration Date</label>
                <input type="text" name="expiration_date" placeholder="MM/YY" required />
              </div>
              <div className="form-group">
                <label>CVV</label>
                <input type="text" name="cvv" placeholder="123" required />
              </div>
              <button className="Btn-JE" type="submit">Pay ${selectedPlan.amount}</button>
            </form>
            <button onClick={handleClosePaymentForm} className="close-btn">Close</button>
          </div>
        </div>
      )}

      {/* Error Popup */}
      {errorPopup && (
        <div className="error-popup">
          <div className="error-content">
            <p>Payment failed. Please try again.</p>
            <button onClick={() => setErrorPopup(false)} className="close-btn">Close</button>
          </div>
        </div>
      )}























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


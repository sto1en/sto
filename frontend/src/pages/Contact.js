import React from 'react';

function Contact() {
    return (
        <main className="main-content">
            <section className="page-section">
                <h1>Контакты</h1>
                <p>Свяжитесь с нами любым удобным способом</p>
                <div className="contact-grid">
                    <div className="contact-card">
                        <h3>📧 Email</h3>
                        <p>info@sto.com</p>
                    </div>
                    <div className="contact-card">
                        <h3>📞 Телефон</h3>
                        <p>+7 (999) 123-45-67</p>
                    </div>
                    <div className="contact-card">
                        <h3>📍 Адрес</h3>
                        <p>Москва, ул. Примерная, д. 123</p>
                    </div>
                </div>
            </section>
        </main>
    );
}

export default Contact;
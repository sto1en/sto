import React, { useState } from 'react';
import './App.css';

import Showcase from './components/Showcase';
import NavMenu from './components/NavMenu';

import BookingSection from './components/sections/BookingSection';
import ServicesSection from './components/sections/ServicesSection';
import PromoSection from './components/sections/PromoSection';
import ReviewsSection from './components/sections/ReviewsSection';
import AboutSection from './components/sections/AboutSection';
import FaqSection from './components/sections/FaqSection';

function App() {
    const [activeSection, setActiveSection] = useState('booking');

    const handleNav = (section) => {
        setActiveSection(section);
        setTimeout(() => {
            const el = document.getElementById('content');
            if (el) el.scrollIntoView({ behavior: 'smooth', block: 'start' });
        }, 50);
    };

    return (
        <div className="App">
            <main className="main-container">
                <div className="left-sidebar">
                    <div className="left-text">
                        <div className="left-text-main">СТО ВЯЗОВСКИЙ</div>
                        <div className="left-text-sub">AUTOSERVICE · SINCE 2023</div>
                    </div>
                </div>

                <NavMenu activeSection={activeSection} onNav={handleNav} />

                <Showcase />
            </main>

            <section className="content-section" id="content">
                {activeSection === 'booking' && <BookingSection />}
                {activeSection === 'services' && <ServicesSection />}
                {activeSection === 'promo' && <PromoSection />}
                {activeSection === 'reviews' && <ReviewsSection />}
                {activeSection === 'about' && <AboutSection />}
                {activeSection === 'faq' && <FaqSection />}
            </section>
        </div>
    );
}

export default App;
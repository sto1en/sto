import React from 'react';

function About() {
    return (
        <main className="main-content">
            <section className="page-section">
                <h1>О нас</h1>
                <p>Мы — команда профессионалов, создающих современные веб-решения.</p>
                <div className="about-grid">
                    <div className="about-card">
                        <h3>🎯 Миссия</h3>
                        <p>Помогать бизнесу расти через цифровые технологии</p>
                    </div>
                    <div className="about-card">
                        <h3>👥 Команда</h3>
                        <p>Опытные разработчики, дизайнеры и аналитики</p>
                    </div>
                    <div className="about-card">
                        <h3>🏆 Опыт</h3>
                        <p>Более 50 успешных проектов за 5 лет работы</p>
                    </div>
                </div>
            </section>
        </main>
    );
}

export default About;
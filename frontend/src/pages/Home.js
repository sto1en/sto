import React from 'react';

function Home() {
    return (
        <main className="main-content">
            <section className="hero">
                <h1>Добро пожаловать на Sto</h1>
                <p>Ваш надёжный партнёр в мире технологий</p>
                <div className="hero-buttons">
                    <a href="/services" className="btn btn-primary">Наши услуги</a>
                    <a href="/contact" className="btn btn-secondary">Связаться</a>
                </div>
            </section>

            <section className="features">
                <div className="feature-card">
                    <h3>🚀 Быстро</h3>
                    <p>Современные технологии для максимальной производительности</p>
                </div>
                <div className="feature-card">
                    <h3>🔒 Надёжно</h3>
                    <p>Гарантия качества и безопасности ваших данных</p>
                </div>
                <div className="feature-card">
                    <h3>💡 Инновации</h3>
                    <p>Используем передовые решения в разработке</p>
                </div>
            </section>
        </main>
    );
}

export default Home;
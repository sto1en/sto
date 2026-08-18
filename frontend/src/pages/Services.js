import React from 'react';

function Services() {
    return (
        <main className="main-content">
            <section className="page-section">
                <h1>Наши услуги</h1>
                <p>Мы предлагаем полный цикл разработки</p>
                <div className="services-grid">
                    <div className="service-card">
                        <h3>💻 Веб-разработка</h3>
                        <p>Создание сайтов и веб-приложений любой сложности</p>
                    </div>
                    <div className="service-card">
                        <h3>📱 Мобильные приложения</h3>
                        <p>Разработка под iOS и Android</p>
                    </div>
                    <div className="service-card">
                        <h3>⚡ IT-консалтинг</h3>
                        <p>Помощь в выборе технологий и оптимизации процессов</p>
                    </div>
                    <div className="service-card">
                        <h3>🚀 DevOps</h3>
                        <p>Настройка CI/CD, облачная инфраструктура</p>
                    </div>
                </div>
            </section>
        </main>
    );
}

export default Services;
import React, { useEffect, useState } from 'react';
import ApiTest from './components/ApiTest';
import './App.css';

import showcase1 from './assets/showcase1.jpg';
import showcase2 from './assets/showcase2.jpg';
import showcase3 from './assets/showcase3.jpg';
import showcase4 from './assets/showcase4.jpg';
import logo from './assets/logo.png';

/* ================= КАРУСЕЛЬ ================= */
const EngineCarousel = () => {
    const images = [showcase1, showcase2, showcase3, showcase4];
    const [currentIndex, setCurrentIndex] = useState(0);
    const [tick, setTick] = useState(0);

    useEffect(() => {
        const interval = setInterval(() => {
            setCurrentIndex((prev) => (prev + 1) % images.length);
        }, 6000);

        return () => clearInterval(interval);
    }, [images.length, tick]);

    const goTo = (index) => {
        setCurrentIndex(index);
        setTick((t) => t + 1);
    };

    const prev = () => {
        setCurrentIndex((i) => (i - 1 + images.length) % images.length);
        setTick((t) => t + 1);
    };

    const next = () => {
        setCurrentIndex((i) => (i + 1) % images.length);
        setTick((t) => t + 1);
    };

    return (
        <div className="right-sidebar">
            <div
                key={`img-${currentIndex}-${tick}`}
                className="carousel-image"
                style={{ backgroundImage: `url(${images[currentIndex]})` }}
            />
            <div className="carousel-overlay" />
            <div className="glass-layer" />

            <button
                className="carousel-arrow arrow-prev"
                onClick={prev}
                aria-label="Предыдущее фото"
            >
                <svg viewBox="0 0 24 24" width="24" height="24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
                    <polyline points="15 18 9 12 15 6" />
                </svg>
            </button>

            <button
                className="carousel-arrow arrow-next"
                onClick={next}
                aria-label="Следующее фото"
            >
                <svg viewBox="0 0 24 24" width="24" height="24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
                    <polyline points="9 18 15 12 9 6" />
                </svg>
            </button>

            <div className="carousel-progress">
                <div className="progress-track">
                    {images.map((_, i) => (
                        <div
                            key={i}
                            className={`progress-segment ${
                                i === currentIndex ? 'active' : ''
                            } ${i < currentIndex ? 'passed' : ''}`}
                            onClick={() => goTo(i)}
                        >
                            {i === currentIndex && (
                                <div
                                    key={`fill-${currentIndex}-${tick}`}
                                    className="segment-fill"
                                />
                            )}
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
};

/* ================= СЕКЦИЯ: ЗАПИСАТЬСЯ ================= */
const BookingSection = () => {
    const [form, setForm] = useState({ name: '', phone: '', service: '' });
    const [sent, setSent] = useState(false);

    const handleChange = (e) => {
        setForm({ ...form, [e.target.name]: e.target.value });
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        if (!form.name.trim() || !form.phone.trim()) return;
        setSent(true);
        setTimeout(() => setSent(false), 4000);
        setForm({ name: '', phone: '', service: '' });
    };

    return (
        <div className="section-inner">
            <div className="booking-inner">
                <div className="booking-left">
                    <h2>ЗАПИСАТЬСЯ НА СЕРВИС</h2>
                    <p className="booking-sub">
                        Оставьте заявку — перезвоним в течение 15 минут и подберём
                        удобное время.
                    </p>

                    <form className="booking-form" onSubmit={handleSubmit}>
                        <input
                            type="text"
                            name="name"
                            placeholder="Ваше имя"
                            value={form.name}
                            onChange={handleChange}
                            required
                        />
                        <input
                            type="tel"
                            name="phone"
                            placeholder="Телефон"
                            value={form.phone}
                            onChange={handleChange}
                            required
                        />
                        <input
                            type="text"
                            name="service"
                            placeholder="Услуга (необязательно)"
                            value={form.service}
                            onChange={handleChange}
                        />
                        <button type="submit" className="btn btn-primary">
                            ОТПРАВИТЬ ЗАЯВКУ
                        </button>

                        {sent && (
                            <p className="booking-success">
                                ✓ Заявка отправлена! Мы свяжемся с вами.
                            </p>
                        )}
                    </form>
                </div>

                <div className="booking-right">
                    <h3>КОНТАКТЫ</h3>

                    <div className="contact-item">
                        <span className="contact-label">ТЕЛЕФОН</span>
                        <a href="tel:+79001234567" className="contact-value">
                            +7 (900) 123-45-67
                        </a>
                    </div>

                    <div className="contact-item">
                        <span className="contact-label">АДРЕС</span>
                        <span className="contact-value">
              2-й Вязовский пр., 4А, стр. 4, Москва
            </span>
                    </div>

                    <div className="contact-item">
                        <span className="contact-label">ГРАФИК</span>
                        <span className="contact-value">
              Пн–Сб: 9:00 – 20:00<br />
              Вс: выходной
            </span>
                    </div>

                    <div className="contact-buttons">
                        <a
                            href="https://wa.me/79001234567"
                            target="_blank"
                            rel="noreferrer"
                            className="btn-contact whatsapp"
                        >
                            WHATSAPP
                        </a>
                        <a
                            href="https://t.me/your_profile"
                            target="_blank"
                            rel="noreferrer"
                            className="btn-contact telegram"
                        >
                            TELEGRAM
                        </a>
                        <a href="tel:+79001234567" className="btn-contact call">
                            ПОЗВОНИТЬ
                        </a>
                    </div>
                </div>
            </div>
        </div>
    );
};

/* ================= СЕКЦИЯ: УСЛУГИ ================= */
const ServicesSection = () => {
    const services = [
        {
            icon: '🛢️',
            title: 'Замена масла',
            desc: 'Замена моторного масла и фильтров. Работаем с любыми марками, используем только оригинальные расходники.',
            price: 'от 1 500 ₽',
            time: '30–40 мин',
        },
        {
            icon: '🔧',
            title: 'Диагностика',
            desc: 'Полная компьютерная диагностика двигателя, подвески, тормозной системы. Выявим причину неисправности за один визит.',
            price: 'от 1 000 ₽',
            time: '40–60 мин',
        },
        {
            icon: '🛞',
            title: 'Шиномонтаж',
            desc: 'Сезонная смена шин, балансировка, ремонт проколов и порезов. Работаем с R13–R22.',
            price: 'от 2 000 ₽',
            time: '40–90 мин',
        },
        {
            icon: '⚙️',
            title: 'Ремонт двигателя',
            desc: 'Капитальный и текущий ремонт ДВС, замена ГРМ, устранение течей и посторонних шумов.',
            price: 'от 8 000 ₽',
            time: 'от 1 дня',
        },
        {
            icon: '🛑',
            title: 'Тормозная система',
            desc: 'Замена колодок, дисков, суппортов, прокачка тормозов. Гарантия на работы — 1 год.',
            price: 'от 2 500 ₽',
            time: '1–2 часа',
        },
        {
            icon: '❄️',
            title: 'Кондиционер',
            desc: 'Заправка фреоном, диагностика утечек, замена компрессора. Работаем с R134a и R1234yf.',
            price: 'от 2 500 ₽',
            time: '1–2 часа',
        },
    ];

    return (
        <div className="section-inner">
            <div className="section-head">
                <h2>НАШИ УСЛУГИ</h2>
                <p className="section-sub">
                    Полный спектр работ по обслуживанию и ремонту автомобилей.
                    Гарантия на все виды работ — 12 месяцев.
                </p>
            </div>

            <div className="services-grid">
                {services.map((s, i) => (
                    <div className="service-card" key={i}>
                        <div className="service-icon">{s.icon}</div>
                        <h3 className="service-title">{s.title}</h3>
                        <p className="service-desc">{s.desc}</p>
                        <div className="service-footer">
                            <span className="service-price">{s.price}</span>
                            <span className="service-time">{s.time}</span>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
};

/* ================= СЕКЦИЯ: АКЦИИ ================= */
const PromoSection = () => {
    const promos = [
        {
            tag: '-20%',
            title: 'Скидка на первое посещение',
            desc: 'Для новых клиентов — скидка 20% на любую услугу при первой записи через сайт.',
            until: 'до 31 декабря',
        },
        {
            tag: '2 = 1',
            title: 'Диагностика в подарок',
            desc: 'При замене масла — компьютерная диагностика двигателя бесплатно.',
            until: 'постоянная акция',
        },
        {
            tag: '-15%',
            title: 'Скидка на шиномонтаж',
            desc: 'При записи на шиномонтаж до 15 ноября — скидка 15% на весь комплекс.',
            until: 'до 15 ноября',
        },
        {
            tag: '0 ₽',
            title: 'Выездная диагностика',
            desc: 'Бесплатный выезд мастера в пределах города при заказе ремонта от 10 000 ₽.',
            until: 'постоянная акция',
        },
    ];

    return (
        <div className="section-inner">
            <div className="section-head">
                <h2>АКЦИИ И ПРЕДЛОЖЕНИЯ</h2>
                <p className="section-sub">
                    Следите за нашими акциями — регулярно проводим скидки и дарим
                    полезные услуги.
                </p>
            </div>

            <div className="promo-grid">
                {promos.map((p, i) => (
                    <div className="promo-card" key={i}>
                        <div className="promo-tag">{p.tag}</div>
                        <h3 className="promo-title">{p.title}</h3>
                        <p className="promo-desc">{p.desc}</p>
                        <div className="promo-until">{p.until}</div>
                    </div>
                ))}
            </div>
        </div>
    );
};

/* ================= СЕКЦИЯ: ОТЗЫВЫ ================= */
const ReviewsSection = () => {
    const reviews = [
        {
            name: 'Алексей М.',
            car: 'Toyota Camry',
            rating: 5,
            text: 'Быстро и качественно заменили масло и фильтры. Мастер всё объяснил, показал старые детали. Цены адекватные, приеду ещё.',
            date: '2 недели назад',
        },
        {
            name: 'Дмитрий К.',
            car: 'Kia Rio',
            rating: 5,
            text: 'Долго искал причину стука в подвеске, в двух сервисах разводили руками. Здесь нашли за 20 минут, отремонтировали в тот же день.',
            date: 'месяц назад',
        },
        {
            name: 'Марина С.',
            car: 'Hyundai Solaris',
            rating: 5,
            text: 'Понравилось, что не навязывают лишнего. Сделали только то, что нужно, дали гарантию. Отдельное спасибо за вежливость администратора.',
            date: 'месяц назад',
        },
        {
            name: 'Сергей П.',
            car: 'Volkswagen Passat',
            rating: 5,
            text: 'Заправлял кондиционер, попутно проверили тормоза и охлаждайку. Работы выполнили быстро, по деньгам вышло дешевле, чем ожидал.',
            date: '2 месяца назад',
        },
    ];

    const renderStars = (n) => '★'.repeat(n) + '☆'.repeat(5 - n);

    return (
        <div className="section-inner">
            <div className="section-head">
                <h2>ОТЗЫВЫ КЛИЕНТОВ</h2>
                <p className="section-sub">
                    Более 250 отзывов от довольных клиентов. Средний рейтинг на
                    Яндекс Картах — 5.0.
                </p>
            </div>

            <div className="reviews-grid">
                {reviews.map((r, i) => (
                    <div className="review-card" key={i}>
                        <div className="review-top">
                            <div className="review-avatar">{r.name.charAt(0)}</div>
                            <div className="review-meta">
                                <div className="review-name">{r.name}</div>
                                <div className="review-car">{r.car}</div>
                            </div>
                            <div className="review-stars">{renderStars(r.rating)}</div>
                        </div>
                        <p className="review-text">{r.text}</p>
                        <div className="review-date">{r.date}</div>
                    </div>
                ))}
            </div>
        </div>
    );
};

/* ================= СЕКЦИЯ: О КОМПАНИИ ================= */
const AboutSection = () => {
    const stats = [
        { value: '12', label: 'лет на рынке' },
        { value: '8', label: 'мастеров в штате' },
        { value: '250+', label: 'отзывов клиентов' },
        { value: '5.0', label: 'рейтинг на картах' },
    ];

    const features = [
        {
            icon: '🎯',
            title: 'Честные цены',
            desc: 'Стоимость работ согласовываем до начала ремонта. Никаких скрытых доплат и навязанных услуг.',
        },
        {
            icon: '🔍',
            title: 'Прозрачная диагностика',
            desc: 'Показываем неисправные детали, объясняем причину поломки простым языком.',
        },
        {
            icon: '📜',
            title: 'Гарантия 12 месяцев',
            desc: 'На все виды работ и установленные запчасти. При повторной проблеме — устраняем бесплатно.',
        },
        {
            icon: '⚡',
            title: 'Ремонт в день обращения',
            desc: 'Большинство работ выполняем за один визит. Согласуем время, чтобы вы не ждали.',
        },
    ];

    return (
        <div className="section-inner">
            <div className="section-head">
                <h2>О КОМПАНИИ</h2>
                <p className="section-sub">
                    СТО «Вязовский» — независимый автосервис с 2012 года.
                    Специализируемся на обслуживании легковых автомобилей всех марок.
                </p>
            </div>

            <div className="stats-row">
                {stats.map((s, i) => (
                    <div className="stat-item" key={i}>
                        <div className="stat-value">{s.value}</div>
                        <div className="stat-label">{s.label}</div>
                    </div>
                ))}
            </div>

            <div className="features-grid">
                {features.map((f, i) => (
                    <div className="feature-card" key={i}>
                        <div className="feature-icon">{f.icon}</div>
                        <h3 className="feature-title">{f.title}</h3>
                        <p className="feature-desc">{f.desc}</p>
                    </div>
                ))}
            </div>
        </div>
    );
};

/* ================= СЕКЦИЯ: FAQ ================= */
const FaqSection = () => {
    const faqs = [
        {
            q: 'Сколько стоит диагностика?',
            a: 'Компьютерная диагностика — от 1 000 ₽. При заказе ремонта у нас — диагностика бесплатно.',
        },
        {
            q: 'Работаете ли вы с моей маркой авто?',
            a: 'Мы обслуживаем все популярные марки: Toyota, Kia, Hyundai, Volkswagen, BMW, Mercedes, Lada и другие. Уточните по телефону.',
        },
        {
            q: 'Даёте ли гарантию на работы?',
            a: 'Да, на все виды работ и установленные запчасти — 12 месяцев. При повторной проблеме устраняем бесплатно.',
        },
        {
            q: 'Можно ли приехать без записи?',
            a: 'Можно, но рекомендуем записаться заранее — так вы не будете ждать в очереди, а мастер подготовится к вашему визиту.',
        },
        {
            q: 'Сколько времени займёт ремонт?',
            a: 'Большинство работ (замена масла, диагностика, тормоза) выполняем за 1–2 часа. Сложный ремонт — от 1 дня.',
        },
        {
            q: 'Можно ли оплатить картой?',
            a: 'Да, принимаем наличные, банковские карты, а также переводы через СБП.',
        },
    ];

    const [openIndex, setOpenIndex] = useState(null);

    const toggle = (i) => {
        setOpenIndex(openIndex === i ? null : i);
    };

    return (
        <div className="section-inner">
            <div className="section-head">
                <h2>ЧАСТЫЕ ВОПРОСЫ</h2>
                <p className="section-sub">
                    Ответы на самые популярные вопросы. Если не нашли свой — позвоните.
                </p>
            </div>

            <div className="faq-list">
                {faqs.map((f, i) => (
                    <div
                        className={`faq-item ${openIndex === i ? 'open' : ''}`}
                        key={i}
                        onClick={() => toggle(i)}
                    >
                        <div className="faq-question">
                            <span>{f.q}</span>
                            <span className="faq-toggle">
                {openIndex === i ? '−' : '+'}
              </span>
                        </div>
                        {openIndex === i && (
                            <div className="faq-answer">{f.a}</div>
                        )}
                    </div>
                ))}
            </div>
        </div>
    );
};

/* ================= ОСНОВНОЙ APP ================= */
function App() {
    const [activeSection, setActiveSection] = useState('booking');

    const handleNav = (section) => {
        setActiveSection(section);
        setTimeout(() => {
            const el = document.getElementById('content');
            if (el) el.scrollIntoView({ behavior: 'smooth', block: 'start' });
        }, 50);
    };

    const navItems = [
        { key: 'booking', label: 'ЗАПИСАТЬСЯ', highlight: true },
        { key: 'services', label: 'УСЛУГИ' },
        { key: 'promo', label: 'АКЦИИ' },
        { key: 'reviews', label: 'ОТЗЫВЫ' },
        { key: 'about', label: 'О НАС' },
        { key: 'faq', label: 'ВОПРОСЫ' },
    ];

    return (
        <div className="App">
            <main className="main-container">
                <div className="left-sidebar">
                    <div className="left-text">
                        <div className="left-text-main">СТО ВЯЗОВСКИЙ</div>
                        <div className="left-text-sub">AUTOSERVICE · SINCE 2023</div>
                    </div>
                </div>

                <div className="center-content">
                    <img src={logo} alt="СТО Вязовский" className="service-logo" />

                    <h2>
                        АВТОСЕРВИС,
                        <br />
                        КОТОРОМУ ДОВЕРЯЮТ
                    </h2>

                    <p className="sto-info">
                        Более 250 отзывов · Рейтинг 5.0 на Яндекс Картах
                    </p>

                    <div className="buttons-group">
                        {navItems.map((item) => (
                            <button
                                key={item.key}
                                className={`btn ${
                                    item.highlight ? 'btn-highlight' : ''
                                } ${activeSection === item.key ? 'btn-primary' : 'btn-outline'}`}
                                onClick={() => handleNav(item.key)}
                            >
                                {item.label}
                            </button>
                        ))}
                    </div>

                    <ApiTest />
                </div>

                <EngineCarousel />
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
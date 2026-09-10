import React, { useState } from 'react';

const BookingSection = () => {
    const [form, setForm] = useState({
        name: '',
        phone: '',
        service: '',
        car: '',
    });
    const [sent, setSent] = useState(false);
    const [error, setError] = useState(false);

    const address = '2-й Вязовский пр., 4А, стр. 4, Москва';
    const yandexMapsUrl =
        'https://yandex.ru/maps/org/vyazovskiy/77363803301/?ll=37.764042%2C55.719237&z=17';

    const phoneDisplay = '+7 (933) 408-12-44';
    const phoneLink = 'tel:+79334081244';
    const whatsappLink = 'https://wa.me/79334081244';
    const maxLink = 'https://max.ru/';

    const handleChange = (e) => {
        setForm({ ...form, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!form.name.trim() || !form.phone.trim()) return;

        try {
            const response = await fetch('http://localhost:8080/api/booking', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(form),
            });

            if (response.ok) {
                setSent(true);
                setError(false);
                setTimeout(() => setSent(false), 5000);
                setForm({ name: '', phone: '', service: '', car: '' });
            } else {
                setError(true);
            }
        } catch (err) {
            console.error('Ошибка отправки:', err);
            setError(true);
        }
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
                            name="car"
                            placeholder="Автомобиль (марка, модель)"
                            value={form.car}
                            onChange={handleChange}
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

                        {error && (
                            <p
                                className="booking-success"
                                style={{
                                    background: 'rgba(255,107,107,0.12)',
                                    borderColor: 'rgba(255,107,107,0.4)',
                                    color: '#ff6b6b',
                                }}
                            >
                                ✕ Не удалось отправить. Позвоните нам.
                            </p>
                        )}
                    </form>
                </div>

                <div className="booking-right">
                    <h3>КОНТАКТЫ</h3>

                    <div className="contact-item">
                        <span className="contact-label">ТЕЛЕФОН</span>
                        <a href={phoneLink} className="contact-value">
                            {phoneDisplay}
                        </a>
                    </div>

                    <div className="contact-item">
                        <span className="contact-label">АДРЕС</span>
                        <a
                            href={yandexMapsUrl}
                            target="_blank"
                            rel="noreferrer"
                            className="contact-value contact-address"
                        >
                            {address}
                        </a>
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
                            href={maxLink}
                            target="_blank"
                            rel="noreferrer"
                            className="btn-contact max"
                        >
                            MAX
                        </a>
                        <a
                            href={whatsappLink}
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
                        <a href={phoneLink} className="btn-contact call">
                            ПОЗВОНИТЬ
                        </a>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default BookingSection;
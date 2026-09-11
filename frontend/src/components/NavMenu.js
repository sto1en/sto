import React from 'react';
import ApiTest from './ApiTest';
import logo from '../assets/logo.png';

const navItems = [
    { key: 'booking', label: 'ЗАПИСАТЬСЯ', highlight: true },
    { key: 'services', label: 'УСЛУГИ' },
    { key: 'promo', label: 'АКЦИИ' },
    { key: 'reviews', label: 'ОТЗЫВЫ' },
    { key: 'about', label: 'О НАС' },
    { key: 'faq', label: 'ВОПРОСЫ' },
];

const REVIEWS_URL =
    'https://yandex.ru/maps/org/vyazovskiy/77363803301/reviews/?ll=37.764042%2C55.719237&z=17';

const NavMenu = ({ activeSection, onNav }) => {
    return (
        <div className="center-content">
            <img src={logo} alt="СТО Вязовский" className="service-logo" />

            <h2 className="sto-title">
                АВТОСЕРВИС,
                <br />
                КОТОРОМУ ДОВЕРЯЮТ
            </h2>

            <a
                href={REVIEWS_URL}
                target="_blank"
                rel="noreferrer"
                className="sto-info-link"
            >
                Более 250 отзывов <span className="sto-info-sep">•</span> Рейтинг 5.0 на Яндекс Картах
            </a>

            <div className="buttons-group">
                {navItems.map((item) => (
                    <button
                        key={item.key}
                        className={`btn ${
                            item.highlight ? 'btn-highlight' : ''
                        } ${activeSection === item.key ? 'btn-primary' : 'btn-outline'}`}
                        onClick={() => onNav(item.key)}
                    >
                        {item.label}
                    </button>
                ))}
            </div>

            <ApiTest />
        </div>
    );
};

export default NavMenu;
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

const NavMenu = ({ activeSection, onNav }) => {
    return (
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
import React, { useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import './Header.css';

function Header() {
    const [menuOpen, setMenuOpen] = useState(false);
    const location = useLocation();

    const isActive = (path) => {
        return location.pathname === path ? 'active' : '';
    };

    return (
        <header className="header">
            <div className="logo">
                <Link to="/">🚀 Sto</Link>
            </div>

            <button
                className="menu-toggle"
                onClick={() => setMenuOpen(!menuOpen)}
                aria-label="Toggle menu"
            >
                ☰
            </button>

            <nav className={`nav ${menuOpen ? 'open' : ''}`}>
                <Link to="/" className={isActive('/')}>Главная</Link>
                <Link to="/about" className={isActive('/about')}>О нас</Link>
                <Link to="/services" className={isActive('/services')}>Услуги</Link>
                <Link to="/contact" className={isActive('/contact')}>Контакты</Link>
                <Link to="/api-test" className={isActive('/api-test')}>API Тест</Link>
                <Link to="/login" className={`login-btn ${isActive('/login')}`}>Войти</Link>
            </nav>
        </header>
    );
}

export default Header;
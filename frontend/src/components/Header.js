import React from 'react';
import logo from '../logo.svg'; // Или ваша картинка

const Header = () => {
    return (
        <header className="header">
            <img src={logo} alt="Logo" className="logo" />
            <div className="logo-text">СТО Вязовский</div>
        </header>
    );
};

export default Header;
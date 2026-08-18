import React from 'react';

function Login() {
    return (
        <main className="main-content">
            <section className="page-section login-section">
                <h1>Вход в систему</h1>
                <form className="login-form">
                    <div className="form-group">
                        <label>Email</label>
                        <input type="email" placeholder="Введите email" />
                    </div>
                    <div className="form-group">
                        <label>Пароль</label>
                        <input type="password" placeholder="Введите пароль" />
                    </div>
                    <button type="submit" className="btn btn-primary">Войти</button>
                </form>
            </section>
        </main>
    );
}

export default Login;
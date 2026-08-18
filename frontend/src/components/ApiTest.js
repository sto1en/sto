import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './ApiTest.css';

function ApiTest() {
    const [message, setMessage] = useState('');
    const [info, setInfo] = useState(null);
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(false);

    // Запрос к /api/hello
    const fetchHello = async () => {
        try {
            setLoading(true);
            const response = await axios.get('http://localhost:8080/api/hello');
            setMessage(response.data);
        } catch (error) {
            console.error('Ошибка:', error);
            setMessage('Ошибка подключения к серверу');
        } finally {
            setLoading(false);
        }
    };

    // Запрос к /api/info
    const fetchInfo = async () => {
        try {
            setLoading(true);
            const response = await axios.get('http://localhost:8080/api/info');
            setInfo(response.data);
        } catch (error) {
            console.error('Ошибка:', error);
        } finally {
            setLoading(false);
        }
    };

    // Запрос к /api/users
    const fetchUsers = async () => {
        try {
            setLoading(true);
            const response = await axios.get('http://localhost:8080/api/users');
            setUsers(response.data);
        } catch (error) {
            console.error('Ошибка:', error);
        } finally {
            setLoading(false);
        }
    };

    // Загружаем данные при монтировании компонента
    useEffect(() => {
        fetchHello();
        fetchInfo();
        fetchUsers();
    }, []);

    return (
        <div className="api-test">
            <h2>📡 Данные с сервера</h2>

            {loading && <p>⏳ Загрузка...</p>}

            <div className="api-section">
                <h3>📨 Приветствие</h3>
                <p className="response">{message}</p>
                <button onClick={fetchHello}>Обновить</button>
            </div>

            <div className="api-section">
                <h3>📊 Информация</h3>
                {info && (
                    <pre className="response">{JSON.stringify(info, null, 2)}</pre>
                )}
                <button onClick={fetchInfo}>Обновить</button>
            </div>

            <div className="api-section">
                <h3>👥 Пользователи</h3>
                {users.length > 0 ? (
                    <ul className="users-list">
                        {users.map(user => (
                            <li key={user.id}>
                                <strong>{user.name}</strong> — {user.email}
                            </li>
                        ))}
                    </ul>
                ) : (
                    <p>Нет пользователей</p>
                )}
                <button onClick={fetchUsers}>Обновить</button>
            </div>
        </div>
    );
}

export default ApiTest;
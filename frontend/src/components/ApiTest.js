import React, { useEffect, useState } from 'react';

const ApiTest = () => {
    const [message, setMessage] = useState("Загрузка...");

    useEffect(() => {
        // Проверка связи с бэкендом
        fetch('http://localhost:8080/api/services')
            .then(res => res.json())
            .then(data => setMessage(`Сервер работает. Услуги: ${data.join(', ')}`))
            .catch(() => setMessage("Сервер не отвечает"));
    }, []);

    return (
        <div style={{marginTop: '20px', color: '#fff', fontSize: '12px'}}>
            Статус API: {message}
        </div>
    );
};

export default ApiTest;
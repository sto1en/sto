import React, { useEffect, useState } from 'react';

const ApiTest = () => {
    const [message, setMessage] = useState('Загрузка...');

    useEffect(() => {
        fetch('/api/services')
            .then((res) => res.json())
            .then((data) =>
                setMessage(`Сервер работает. Категорий: ${data.length}`)
            )
            .catch(() => setMessage('Сервер не отвечает'));
    }, []);

    return (
        <div style={{ marginTop: '20px', color: '#fff', fontSize: '12px' }}>
            Статус API: {message}
        </div>
    );
};

export default ApiTest;
import React from 'react';

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

const AboutSection = () => {
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

export default AboutSection;
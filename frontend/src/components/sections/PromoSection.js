import React from 'react';

const promos = [
    {
        tag: '-20%',
        title: 'Скидка на первое посещение',
        desc: 'Для новых клиентов — скидка 20% на любую услугу при первой записи через сайт.',
        until: 'до 31 декабря',
    },
    {
        tag: '2 = 1',
        title: 'Диагностика в подарок',
        desc: 'При замене масла — компьютерная диагностика двигателя бесплатно.',
        until: 'постоянная акция',
    },
    {
        tag: '-15%',
        title: 'Скидка на шиномонтаж',
        desc: 'При записи на шиномонтаж до 15 ноября — скидка 15% на весь комплекс.',
        until: 'до 15 ноября',
    },
    {
        tag: '0 ₽',
        title: 'Выездная диагностика',
        desc: 'Бесплатный выезд мастера в пределах города при заказе ремонта от 10 000 ₽.',
        until: 'постоянная акция',
    },
];

const PromoSection = () => {
    return (
        <div className="section-inner">
            <div className="section-head">
                <h2>АКЦИИ И ПРЕДЛОЖЕНИЯ</h2>
                <p className="section-sub">
                    Следите за нашими акциями — регулярно проводим скидки и дарим
                    полезные услуги.
                </p>
            </div>

            <div className="promo-grid">
                {promos.map((p, i) => (
                    <div className="promo-card" key={i}>
                        <div className="promo-tag">{p.tag}</div>
                        <h3 className="promo-title">{p.title}</h3>
                        <p className="promo-desc">{p.desc}</p>
                        <div className="promo-until">{p.until}</div>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default PromoSection;
import React from 'react';

const reviews = [
    {
        name: 'Алексей М.',
        car: 'Toyota Camry',
        rating: 5,
        text: 'Быстро и качественно заменили масло и фильтры. Мастер всё объяснил, показал старые детали. Цены адекватные, приеду ещё.',
        date: '2 недели назад',
    },
    {
        name: 'Дмитрий К.',
        car: 'Kia Rio',
        rating: 5,
        text: 'Долго искал причину стука в подвеске, в двух сервисах разводили руками. Здесь нашли за 20 минут, отремонтировали в тот же день.',
        date: 'месяц назад',
    },
    {
        name: 'Марина С.',
        car: 'Hyundai Solaris',
        rating: 5,
        text: 'Понравилось, что не навязывают лишнего. Сделали только то, что нужно, дали гарантию. Отдельное спасибо за вежливость администратора.',
        date: 'месяц назад',
    },
    {
        name: 'Сергей П.',
        car: 'Volkswagen Passat',
        rating: 5,
        text: 'Заправлял кондиционер, попутно проверили тормоза и охлаждайку. Работы выполнили быстро, по деньгам вышло дешевле, чем ожидал.',
        date: '2 месяца назад',
    },
];

const renderStars = (n) => '★'.repeat(n) + '☆'.repeat(5 - n);

const ReviewsSection = () => {
    return (
        <div className="section-inner">
            <div className="section-head">
                <h2>ОТЗЫВЫ КЛИЕНТОВ</h2>
                <p className="section-sub">
                    Более 250 отзывов от довольных клиентов. Средний рейтинг на
                    Яндекс Картах — 5.0.
                </p>
            </div>

            <div className="reviews-grid">
                {reviews.map((r, i) => (
                    <div className="review-card" key={i}>
                        <div className="review-top">
                            <div className="review-avatar">{r.name.charAt(0)}</div>
                            <div className="review-meta">
                                <div className="review-name">{r.name}</div>
                                <div className="review-car">{r.car}</div>
                            </div>
                            <div className="review-stars">{renderStars(r.rating)}</div>
                        </div>
                        <p className="review-text">{r.text}</p>
                        <div className="review-date">{r.date}</div>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default ReviewsSection;
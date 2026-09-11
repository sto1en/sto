import React, { useEffect, useState } from 'react';
import ServiceModal from './ServiceModal';

const ServicesSection = () => {
    const [categories, setCategories] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [activeCategory, setActiveCategory] = useState(null);

    useEffect(() => {
        fetch('/api/services')
            .then((res) => {
                if (!res.ok) throw new Error('Ошибка загрузки');
                return res.json();
            })
            .then((data) => {
                setCategories(data);
                setLoading(false);
            })
            .catch((err) => {
                console.error(err);
                setError('Не удалось загрузить услуги');
                setLoading(false);
            });
    }, []);

    return (
        <div className="section-inner">
            <div className="section-head">
                <h2>НАШИ УСЛУГИ</h2>
                <p className="section-sub">
                    Полный спектр работ по обслуживанию и ремонту автомобилей.
                    Выберите категорию, чтобы посмотреть цены.
                </p>
            </div>

            {loading && <p className="services-loading">Загрузка услуг...</p>}
            {error && <p className="services-error">{error}</p>}

            {!loading && !error && (
                <div className="services-categories">
                    {categories.map((cat) => (
                        <button
                            key={cat.key}
                            className="service-category-btn"
                            onClick={() => setActiveCategory(cat)}
                        >
                            <span className="service-category-title">{cat.title}</span>
                            <span className="service-category-count">
                {cat.items.length} услуг
              </span>
                        </button>
                    ))}
                </div>
            )}

            {activeCategory && (
                <ServiceModal
                    category={activeCategory}
                    onClose={() => setActiveCategory(null)}
                />
            )}
        </div>
    );
};

export default ServicesSection;
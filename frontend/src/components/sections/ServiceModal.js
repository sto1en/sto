import React, { useEffect } from 'react';

const ServiceModal = ({ category, onClose }) => {
    useEffect(() => {
        const handleEsc = (e) => {
            if (e.key === 'Escape') onClose();
        };
        document.addEventListener('keydown', handleEsc);
        document.body.style.overflow = 'hidden';
        return () => {
            document.removeEventListener('keydown', handleEsc);
            document.body.style.overflow = '';
        };
    }, [onClose]);

    if (!category) return null;

    return (
        <div className="modal-overlay" onClick={onClose}>
            <div className="modal-window" onClick={(e) => e.stopPropagation()}>
                <button className="modal-close" onClick={onClose} aria-label="Закрыть">
                    ×
                </button>

                <div className="modal-head">
                    <h3>{category.title}</h3>
                    <p className="modal-sub">
                        {category.items.length} услуг · актуальные цены
                    </p>
                </div>

                <div className="modal-body">
                    {category.items.map((item, i) => (
                        <div className="modal-item" key={i}>
                            <div className="modal-item-info">
                                <span className="modal-item-name">{item.name}</span>
                                <span className="modal-item-duration">{item.duration}</span>
                            </div>
                            <span className="modal-item-price">{item.price}</span>
                        </div>
                    ))}
                </div>

                <div className="modal-foot">
          <span className="modal-foot-text">
            Точную стоимость уточняйте по телефону
          </span>
                    <button className="modal-foot-btn" onClick={onClose}>
                        ПОНЯТНО
                    </button>
                </div>
            </div>
        </div>
    );
};

export default ServiceModal;
import React, { useEffect, useState } from 'react';

const ServiceModal = ({ category, onClose }) => {
    const [expandedIndex, setExpandedIndex] = useState(null);

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

    const toggle = (i) => {
        setExpandedIndex(expandedIndex === i ? null : i);
    };

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
                    {category.items.map((item, i) => {
                        const hasDescription =
                            item.description && item.description.trim().length > 0;
                        const isOpen = expandedIndex === i;

                        return (
                            <div
                                className={`modal-item-wrapper ${
                                    hasDescription ? 'clickable' : ''
                                } ${isOpen ? 'open' : ''}`}
                                key={i}
                            >
                                <div
                                    className="modal-item"
                                    onClick={() => hasDescription && toggle(i)}
                                >
                                    <span className="modal-item-name">{item.name}</span>
                                    <div className="modal-item-right">
                                        <span className="modal-item-price">{item.price}</span>
                                        {hasDescription && (
                                            <span
                                                className={`modal-item-arrow ${
                                                    isOpen ? 'rotated' : ''
                                                }`}
                                            >
                        ▼
                      </span>
                                        )}
                                    </div>
                                </div>

                                {hasDescription && isOpen && (
                                    <div className="modal-item-description">
                                        {item.description}
                                    </div>
                                )}
                            </div>
                        );
                    })}
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
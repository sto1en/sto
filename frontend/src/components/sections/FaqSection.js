import React, { useState } from 'react';

const faqs = [
    {
        q: 'Сколько стоит диагностика?',
        a: 'Компьютерная диагностика — от 1 000 ₽. При заказе ремонта у нас — диагностика бесплатно.',
    },
    {
        q: 'Работаете ли вы с моей маркой авто?',
        a: 'Мы обслуживаем все популярные марки: Toyota, Kia, Hyundai, Volkswagen, BMW, Mercedes, Lada и другие. Уточните по телефону.',
    },
    {
        q: 'Даёте ли гарантию на работы?',
        a: 'Да, на все виды работ и установленные запчасти — 12 месяцев. При повторной проблеме устраняем бесплатно.',
    },
    {
        q: 'Можно ли приехать без записи?',
        a: 'Можно, но рекомендуем записаться заранее — так вы не будете ждать в очереди, а мастер подготовится к вашему визиту.',
    },
    {
        q: 'Сколько времени займёт ремонт?',
        a: 'Большинство работ (замена масла, диагностика, тормоза) выполняем за 1–2 часа. Сложный ремонт — от 1 дня.',
    },
    {
        q: 'Можно ли оплатить картой?',
        a: 'Да, принимаем наличные, банковские карты, а также переводы через СБП.',
    },
];

const FaqSection = () => {
    const [openIndex, setOpenIndex] = useState(null);

    const toggle = (i) => {
        setOpenIndex(openIndex === i ? null : i);
    };

    return (
        <div className="section-inner">
            <div className="section-head">
                <h2>ЧАСТЫЕ ВОПРОСЫ</h2>
                <p className="section-sub">
                    Ответы на самые популярные вопросы. Если не нашли свой — позвоните.
                </p>
            </div>

            <div className="faq-list">
                {faqs.map((f, i) => (
                    <div
                        className={`faq-item ${openIndex === i ? 'open' : ''}`}
                        key={i}
                        onClick={() => toggle(i)}
                    >
                        <div className="faq-question">
                            <span>{f.q}</span>
                            <span className="faq-toggle">
                {openIndex === i ? '−' : '+'}
              </span>
                        </div>
                        {openIndex === i && (
                            <div className="faq-answer">{f.a}</div>
                        )}
                    </div>
                ))}
            </div>
        </div>
    );
};

export default FaqSection;
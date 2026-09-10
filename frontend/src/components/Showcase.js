import React, { useEffect, useState } from 'react';

import showcase1 from '../assets/showcase1.jpg';
import showcase2 from '../assets/showcase2.jpg';
import showcase3 from '../assets/showcase3.jpg';
import showcase4 from '../assets/showcase4.jpg';

const EngineCarousel = () => {
    const images = [showcase1, showcase2, showcase3, showcase4];
    const [currentIndex, setCurrentIndex] = useState(0);
    const [tick, setTick] = useState(0);

    useEffect(() => {
        const interval = setInterval(() => {
            setCurrentIndex((prev) => (prev + 1) % images.length);
        }, 6000);

        return () => clearInterval(interval);
    }, [images.length, tick]);

    const goTo = (index) => {
        setCurrentIndex(index);
        setTick((t) => t + 1);
    };

    const prev = () => {
        setCurrentIndex((i) => (i - 1 + images.length) % images.length);
        setTick((t) => t + 1);
    };

    const next = () => {
        setCurrentIndex((i) => (i + 1) % images.length);
        setTick((t) => t + 1);
    };

    return (
        <div className="right-sidebar">
            <div
                key={`img-${currentIndex}-${tick}`}
                className="carousel-image"
                style={{ backgroundImage: `url(${images[currentIndex]})` }}
            />
            <div className="carousel-overlay" />
            <div className="glass-layer" />

            <button
                className="carousel-arrow arrow-prev"
                onClick={prev}
                aria-label="Предыдущее фото"
            >
                <svg viewBox="0 0 24 24" width="24" height="24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
                    <polyline points="15 18 9 12 15 6" />
                </svg>
            </button>

            <button
                className="carousel-arrow arrow-next"
                onClick={next}
                aria-label="Следующее фото"
            >
                <svg viewBox="0 0 24 24" width="24" height="24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
                    <polyline points="9 18 15 12 9 6" />
                </svg>
            </button>

            <div className="carousel-progress">
                <div className="progress-track">
                    {images.map((_, i) => (
                        <div
                            key={i}
                            className={`progress-segment ${
                                i === currentIndex ? 'active' : ''
                            } ${i < currentIndex ? 'passed' : ''}`}
                            onClick={() => goTo(i)}
                        >
                            {i === currentIndex && (
                                <div
                                    key={`fill-${currentIndex}-${tick}`}
                                    className="segment-fill"
                                />
                            )}
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
};

export default EngineCarousel;
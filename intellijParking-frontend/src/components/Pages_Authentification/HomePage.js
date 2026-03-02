import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import "../../styles/home.css";

import imageLocalVip from "../../assets/slider/imageLocalVip.jpg";
import imageParking1 from "../../assets/slider/imageParking1.jpg";
import imageParking2 from "../../assets/slider/imageParking2.jpg";
import imageNiveau1 from "../../assets/slider/imageNiveau1.jpg";


const images = [imageLocalVip, imageNiveau1, imageParking1, imageParking2];

export default function HomePage() {
    const [index, setIndex] = useState(0);
    const navigate = useNavigate();


    useEffect(() => {
        const interval = setInterval(() => {
            setIndex(prev => (prev + 1) % images.length);
        }, 3000); // 3 secondes

        return () => clearInterval(interval);
    }, []);

    return (
        <div className="home-page">

            {/* le slide */}
            <div className="slider-container">
                <img
                    src={images[index]}
                    alt="slide"
                    className="home-image"
                />
            </div>

            {/* les petits points */}
            <div className="points">
                {images.map((_, i) => (
                    <span
                        key={i}
                        className={`point ${i === index ? "actif" : ""}`}
                    />
                ))}
            </div>

            {/* les boutons */}
            <div className="home-buttons">
                <button onClick={() => navigate("/ParkingPage")}>
                    Voir parking
                </button>
                <button onClick={() => navigate("/Dashboard")}>
                    Dashboard
                </button>
            </div>

        </div>
    );
}

import { useState, useEffect } from "react";
import Niveau1 from "./Niveau1";
import Niveau2 from "./Niveau2";
import Niveau3 from "./Niveau3";


export default function ParkingPage() {
    const [level, setLevel] = useState(0);
    const [now, setNow] = useState(new Date());

    useEffect(() => {
        const timer = setInterval(() => setNow(new Date()), 1000);
        return () => clearInterval(timer);
    }, []);

    const time = now.toLocaleTimeString('fr-FR', {
        hour: '2-digit', minute: '2-digit', second: '2-digit'
    });
    const date = now.toLocaleDateString('fr-FR', {
        weekday: 'long', day: 'numeric', month: 'long', year: 'numeric'
    });

    return (
        <div style={{ padding: 20 }}>

            <h2>Niveaux du parking</h2>

            {/* Boutons niveaux */}
            <div style={{ display: 'flex', alignItems: 'center', gap: 24, flexWrap: 'wrap', marginBottom: 20 }}>
            <div style={{ display: 'flex', gap: 8 }}>
                <button onClick={() => setLevel(0)}>RDC</button>
                <button onClick={() => setLevel(1)}>Niveau 1</button>
                <button onClick={() => setLevel(2)}>Niveau 2</button>
            </div>
            <div style={{ lineHeight: 1.2, textAlign: 'right', marginLeft: 'auto' }}>
                <div style={{ fontSize: 28, fontWeight: 500, fontVariantNumeric: 'tabular-nums' }}>
                    {time}
                </div>
                <div style={{ fontSize: 15, color: '#888', marginTop: 2 }}>
                    {date.charAt(0).toUpperCase() + date.slice(1)}
                </div>
            </div>
            </div>

            {/* Affichage du niveau */}
            {level === 0 && <Niveau1 />}
            {level === 1 && <Niveau2 />}
            {level === 2 && <Niveau3 />}

        </div>
    );
}

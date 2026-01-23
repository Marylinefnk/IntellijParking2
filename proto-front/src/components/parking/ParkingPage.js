import { useState } from "react";
import Niveau1 from "./Niveau1";


export default function ParkingPage() {
    const [level, setLevel] = useState(0);

    return (
        <div style={{ padding: 20 }}>

            <h2>Niveaux du parking</h2>

            {/* Boutons niveaux */}
            <div style={{ marginBottom: 20 }}>
                <button onClick={() => setLevel(0)}>RDC</button>
                <button onClick={() => setLevel(1)}>Niveau -1</button>
                <button onClick={() => setLevel(2)}>Niveau -2</button>
            </div>

            {/* Affichage du niveau */}
            {level === 0 && <Niveau1 />}
            {level === 1 && <Niveau1 />}
            {level === 2 && <Niveau1 />}

        </div>
    );
}

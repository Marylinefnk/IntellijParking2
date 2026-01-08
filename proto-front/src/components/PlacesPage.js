import React, { useEffect, useState } from "react";

export default function PlacesPage() {
    const [places, setPlaces] = useState([]);
    const [msg, setMsg] = useState("Chargement...");

    async function loadPlaces() {
        try {
            setMsg("Chargement...");
            const res = await fetch("/api/places");
            if (!res.ok) throw new Error("HTTP " + res.status);
            const data = await res.json();
            setPlaces(data);
            setMsg(`OK — ${data.length} places`);
        } catch (e) {
            setMsg("Erreur : " + e.message);
        }
    }

    useEffect(() => {
        loadPlaces();
        // optionnel: refresh automatique
        // const t = setInterval(loadPlaces, 2000);
        // return () => clearInterval(t);
    }, []);

    return (
        <div style={{ padding: 24, fontFamily: "Arial, sans-serif" }}>
            <h1>Places du parking</h1>
            <button onClick={loadPlaces}>Rafraîchir</button>
            <span style={{ marginLeft: 12, color: "#666" }}>{msg}</span>

            <table style={{ width: "100%", borderCollapse: "collapse", marginTop: 12 }}>
                <thead>
                <tr>
                    <th style={th}>ID place</th>
                    <th style={th}>Numéro</th>
                    <th style={th}>Type</th>
                    <th style={th}>Statut</th>
                </tr>
                </thead>
                <tbody>
                {places.map((p) => (
                    <tr key={p.idPlace}>
                        <td style={td}>{p.idPlace}</td>
                        <td style={td}>{p.numero}</td>
                        <td style={td}>{p.typePlace}</td>
                        <td style={td}>{p.etat}</td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
}

const th = { border: "1px solid #ddd", padding: 8, background: "#f4f4f4", textAlign: "left" };
const td = { border: "1px solid #ddd", padding: 8 };

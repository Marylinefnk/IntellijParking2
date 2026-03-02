import React from 'react';
import {Link} from "react-router-dom";

export default function Navbar(){
    return (
            <ul className="nav justify-content-center my-3" style={{ background: "#f8f9fa", padding: "10px" }}>
                <li className="nav-item">
                    <Link className="nav-link" to="/app/places">Places</Link>
                </li>
                <li className="nav-item">
                    <Link className="nav-link" to="/app/reservations">Reservations</Link>
                </li>
                <li className="nav-item">
                    <Link className="nav-link" to="/app/personnes">Personnes</Link>
                </li>
                <li className="nav-item">
                    <Link className="nav-link" to="/app/vehicules">Vehicules</Link>
                </li>
            </ul>
    );
};
import React from "react";
import { NavLink, Outlet, useNavigate } from "react-router-dom";
import { useUser } from "../context/UserContext";
import { useNotificationWebSocket } from "../WebHooks/useNotificationWebSocket";

/**
 * Layout principal de l'application pour les utilisateurs connectes.
 * Inclut la sidebar de navigation et les notifications temps reel.
 */
export default function Layout() {
    const { user, logout } = useUser();
    const navigate = useNavigate();

    // Connexion WebSocket pour les notifications
    useNotificationWebSocket();

    const isAdmin = user?.typePersonne === "SUPERVISEUR";

    const handleLogout = () => {
        logout();
        navigate("/");
    };

    const getRoleColor = (type) => {
        const colors = {
            "SUPERVISEUR": "#f59e0b",
            "ABONNE": "#3b82f6",
            "VISITEUR": "#22c55e"
        };
        return colors[type] || "#64748b";
    };

    const getRoleLabel = (type) => {
        const labels = {
            "SUPERVISEUR": "Superviseur",
            "ABONNE": "Abonne",
            "VISITEUR": "Visiteur"
        };
        return labels[type] || type;
    };

    return (
        <div className="app-container">
            <aside className="sidebar">
                <div className="sidebar-header">
                    <div className="sidebar-logo">
                        <div className="sidebar-logo-icon">P</div>
                        <span>Intellij Parking</span>
                    </div>
                </div>

                {user && (
                    <div style={{
                        padding: "16px 24px",
                        borderBottom: "1px solid rgba(255,255,255,0.1)"
                    }}>
                        <div style={{ display: "flex", alignItems: "center", gap: 12 }}>
                            <div style={{
                                width: 40,
                                height: 40,
                                borderRadius: "50%",
                                background: getRoleColor(user.typePersonne),
                                display: "flex",
                                alignItems: "center",
                                justifyContent: "center",
                                color: "white",
                                fontWeight: 600
                            }}>
                                {user.nom?.charAt(0) || "U"}
                            </div>
                            <div>
                                <div style={{ color: "white", fontWeight: 500 }}>
                                    {user.nom} {user.prenom}
                                </div>
                                <div style={{
                                    fontSize: "0.75rem",
                                    color: getRoleColor(user.typePersonne),
                                    fontWeight: 500
                                }}>
                                    {getRoleLabel(user.typePersonne)}
                                </div>
                            </div>
                        </div>
                    </div>
                )}

                <nav className="sidebar-nav">
                    <NavLink to="/app" className={({ isActive }) => `nav-item ${isActive ? "active" : ""}`} end>
                        <span className="nav-icon">D</span>
                        Tableau de bord
                    </NavLink>

                    <NavLink to="/app/places" className={({ isActive }) => `nav-item ${isActive ? "active" : ""}`}>
                        <span className="nav-icon">P</span>
                        {isAdmin ? "Gestion Places" : "Places"}
                    </NavLink>

                    <NavLink to="/app/reservations" className={({ isActive }) => `nav-item ${isActive ? "active" : ""}`}>
                        <span className="nav-icon">R</span>
                        {isAdmin ? "Reservations" : "Mes Reservations"}
                    </NavLink>

                    <NavLink to="/app/vehicules" className={({ isActive }) => `nav-item ${isActive ? "active" : ""}`}>
                        <span className="nav-icon">V</span>
                        {isAdmin ? "Vehicules" : "Mes Vehicules"}
                    </NavLink>

                    {isAdmin && (
                        <NavLink to="/app/personnes" className={({ isActive }) => `nav-item ${isActive ? "active" : ""}`}>
                            <span className="nav-icon">U</span>
                            Clients
                        </NavLink>
                    )}

                    {isAdmin && (
                        <NavLink to="/app/simulation" className={({ isActive }) => `nav-item ${isActive ? "active" : ""}`}>
                            <span className="nav-icon">S</span>
                            Simulation
                        </NavLink>
                    )}
                </nav>

                <div style={{ padding: "16px 24px", marginTop: "auto" }}>
                    <button
                        onClick={handleLogout}
                        style={{
                            width: "100%",
                            padding: "12px",
                            background: "rgba(255,255,255,0.1)",
                            border: "none",
                            borderRadius: 8,
                            color: "rgba(255,255,255,0.7)",
                            cursor: "pointer",
                            display: "flex",
                            alignItems: "center",
                            justifyContent: "center",
                            gap: 8
                        }}
                    >
                        <span>X</span> Deconnexion
                    </button>
                </div>
            </aside>
            <main className="main-content">
                <Outlet />
            </main>
        </div>
    );
}

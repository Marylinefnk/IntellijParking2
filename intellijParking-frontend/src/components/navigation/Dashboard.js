import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { useUser } from "../../context/UserContext";
import { API_PLACES, API_RESERVATIONS_PLACE, API_PERSONNES, API_VEHICULES } from "../../constants/back";

export default function Dashboard() {
    const { user } = useUser();
    const [stats, setStats] = useState({
        totalPlaces: 0,
        placesLibres: 0,
        placesOccupees: 0,
        placesReservees: 0,
        reservationsActives: 0,
        mesReservations: 0,
        totalPersonnes: 0,
        totalVehicules: 0
    });
    const [recentReservations, setRecentReservations] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        loadDashboardData();
    }, [user]);

    async function loadDashboardData() {
        try {
            const [placesRes, reservationsRes, personnesRes, vehiculesRes] = await Promise.all([
                fetch(API_PLACES),
                fetch(API_RESERVATIONS_PLACE),
                fetch(API_PERSONNES),
                fetch(API_VEHICULES)
            ]);

            const places = placesRes.ok ? await placesRes.json() : [];
            const reservations = reservationsRes.ok ? await reservationsRes.json() : [];
            const personnes = personnesRes.ok ? await personnesRes.json() : [];
            const vehicules = vehiculesRes.ok ? await vehiculesRes.json() : [];

            const mesReservations = user ? reservations.filter(r => r.personne?.id === user.id) : [];

            setStats({
                totalPlaces: places.length,
                placesLibres: places.filter(p => p.statut === "LIBRE").length,
                placesOccupees: places.filter(p => p.statut === "OCCUPEE").length,
                placesReservees: places.filter(p => p.statut === "RESERVEE").length,
                reservationsActives: reservations.filter(r => r.statut === "EN_COURS" || r.statut === "EN_ATTENTE").length,
                mesReservations: mesReservations.filter(r => r.statut === "EN_COURS" || r.statut === "EN_ATTENTE").length,
                totalPersonnes: personnes.length,
                totalVehicules: vehicules.length
            });

            if (user?.typePersonne === "SUPERVISEUR") {
                setRecentReservations(reservations.slice(0, 5));
            } else {
                setRecentReservations(mesReservations.slice(0, 5));
            }
        } catch (e) {
            console.error("Erreur chargement dashboard:", e);
        } finally {
            setLoading(false);
        }
    }

    const formatDate = (dateStr) => {
        if (!dateStr) return "-";
        return new Date(dateStr).toLocaleString("fr-FR", {
            day: "2-digit",
            month: "short",
            hour: "2-digit",
            minute: "2-digit"
        });
    };

    const getStatusClass = (statut) => {
        const classes = {
            "EN_ATTENTE": "status-en-attente",
            "EN_COURS": "status-en-cours",
            "TERMINEE": "status-terminee",
            "ANNULEE": "status-annulee"
        };
        return classes[statut] || "";
    };

    const getRoleTitle = () => {
        if (!user) return "Tableau de bord";
        switch (user.typePersonne) {
            case "SUPERVISEUR": return "Supervision du Parking";
            case "ABONNE": return `Bienvenue ${user.prenom}`;
            case "VISITEUR": return `Bienvenue ${user.prenom}`;
            default: return "Tableau de bord";
        }
    };

    const getRoleSubtitle = () => {
        if (!user) return "";
        switch (user.typePersonne) {
            case "SUPERVISEUR": return "Surveillez les anomalies et gérez le parking";
            case "ABONNE": return "Gérez votre place dédiée et vos réservations";
            case "VISITEUR": return "Trouvez une place temporaire pour votre véhicule";
            default: return "";
        }
    };

    if (loading) {
        return (
            <div className="empty-state">
                <div className="empty-state-icon">⏳</div>
                <h3>Chargement...</h3>
            </div>
        );
    }

    return (
        <div>
            <div className="page-header">
                <h1 className="page-title">{getRoleTitle()}</h1>
                <p className="page-subtitle">{getRoleSubtitle()}</p>
            </div>

            {user?.typePersonne === "SUPERVISEUR" && (
                <>
                    <div className="stats-grid">
                        <div className="stat-card">
                            <div className="stat-icon blue">🅿️</div>
                            <div className="stat-info">
                                <h3>{stats.totalPlaces}</h3>
                                <p>Places totales</p>
                            </div>
                        </div>
                        <div className="stat-card">
                            <div className="stat-icon green">✓</div>
                            <div className="stat-info">
                                <h3>{stats.placesLibres}</h3>
                                <p>Places libres</p>
                            </div>
                        </div>
                        <div className="stat-card">
                            <div className="stat-icon red">🚗</div>
                            <div className="stat-info">
                                <h3>{stats.placesOccupees}</h3>
                                <p>Places occupées</p>
                            </div>
                        </div>
                        <div className="stat-card">
                            <div className="stat-icon orange">📅</div>
                            <div className="stat-info">
                                <h3>{stats.reservationsActives}</h3>
                                <p>Réservations actives</p>
                            </div>
                        </div>
                    </div>

                    <div className="quick-actions">
                        <Link to="/app/places" className="quick-action-card">
                            <div className="quick-action-icon stat-icon blue">🅿️</div>
                            <h3>Gérer les Places</h3>
                            <p>Voir et modifier les places</p>
                        </Link>
                        <Link to="/app/reservations" className="quick-action-card">
                            <div className="quick-action-icon stat-icon orange">📅</div>
                            <h3>Réservations</h3>
                            <p>{stats.reservationsActives} actives</p>
                        </Link>
                        <Link to="/app/personnes" className="quick-action-card">
                            <div className="quick-action-icon stat-icon cyan">👥</div>
                            <h3>Clients</h3>
                            <p>{stats.totalPersonnes} enregistrés</p>
                        </Link>
                        <Link to="/app/vehicules" className="quick-action-card">
                            <div className="quick-action-icon stat-icon green">🚙</div>
                            <h3>Véhicules</h3>
                            <p>{stats.totalVehicules} enregistrés</p>
                        </Link>
                    </div>
                </>
            )}

            {user?.typePersonne === "ABONNE" && (
                <>
                    <div className="stats-grid">
                        <div className="stat-card">
                            <div className="stat-icon blue">🎫</div>
                            <div className="stat-info">
                                <h3>Abonné</h3>
                                <p>Votre statut</p>
                            </div>
                        </div>
                        <div className="stat-card">
                            <div className="stat-icon green">📅</div>
                            <div className="stat-info">
                                <h3>{stats.mesReservations}</h3>
                                <p>Mes réservations</p>
                            </div>
                        </div>
                        <div className="stat-card">
                            <div className="stat-icon cyan">✓</div>
                            <div className="stat-info">
                                <h3>{stats.placesLibres}</h3>
                                <p>Places disponibles</p>
                            </div>
                        </div>
                    </div>

                    <div className="quick-actions">
                        <Link to="/app/places" className="quick-action-card">
                            <div className="quick-action-icon stat-icon blue">🅿️</div>
                            <h3>Voir les Places</h3>
                            <p>Consultez les places disponibles</p>
                        </Link>
                        <Link to="/app/reservations" className="quick-action-card">
                            <div className="quick-action-icon stat-icon orange">📅</div>
                            <h3>Mes Réservations</h3>
                            <p>Gérer vos réservations</p>
                        </Link>
                    </div>
                </>
            )}

            {user?.typePersonne === "VISITEUR" && (
                <>
                    <div className="stats-grid">
                        <div className="stat-card">
                            <div className="stat-icon green">✓</div>
                            <div className="stat-info">
                                <h3>{stats.placesLibres}</h3>
                                <p>Places disponibles</p>
                            </div>
                        </div>
                        <div className="stat-card">
                            <div className="stat-icon orange">📅</div>
                            <div className="stat-info">
                                <h3>{stats.mesReservations}</h3>
                                <p>Mes réservations</p>
                            </div>
                        </div>
                    </div>

                    <div className="quick-actions">
                        <Link to="/app/places" className="quick-action-card">
                            <div className="quick-action-icon stat-icon green">🅿️</div>
                            <h3>Trouver une Place</h3>
                            <p>{stats.placesLibres} places libres</p>
                        </Link>
                        <Link to="/app/reservations" className="quick-action-card">
                            <div className="quick-action-icon stat-icon orange">📅</div>
                            <h3>Réserver</h3>
                            <p>Réserver une place temporaire</p>
                        </Link>
                    </div>
                </>
            )}

            <div className="card" style={{ marginTop: 24 }}>
                <div className="card-header">
                    <h2 className="card-title">
                        {user?.typePersonne === "SUPERVISEUR" ? "Réservations récentes" : "Mes réservations"}
                    </h2>
                    <Link to="/app/reservations" className="btn btn-outline btn-sm">Voir tout</Link>
                </div>
                <div className="card-body" style={{ padding: 0 }}>
                    {recentReservations.length === 0 ? (
                        <div className="empty-state">
                            <div className="empty-state-icon">📅</div>
                            <h3>Aucune réservation</h3>
                            <p>Les réservations apparaîtront ici</p>
                        </div>
                    ) : (
                        <div className="table-container">
                            <table className="table">
                                <thead>
                                <tr>
                                    {user?.typePersonne === "SUPERVISEUR" && <th>Client</th>}
                                    <th>Place</th>
                                    <th>Véhicule</th>
                                    <th>Début</th>
                                    <th>Fin</th>
                                    <th>Statut</th>
                                </tr>
                                </thead>
                                <tbody>
                                {recentReservations.map(r => (
                                    <tr key={r.id}>
                                        {user?.typePersonne === "SUPERVISEUR" && (
                                            <td>
                                                <div style={{ display: "flex", alignItems: "center", gap: 12 }}>
                                                    <div className="user-avatar">
                                                        {r.personne?.nom?.charAt(0) || "?"}
                                                    </div>
                                                    <span>{r.personne?.nom} {r.personne?.prenom}</span>
                                                </div>
                                            </td>
                                        )}
                                        <td><strong>{r.place?.numero}</strong></td>
                                        <td>{r.vehicule?.immatriculation || "-"}</td>
                                        <td>{formatDate(r.dateDebut)}</td>
                                        <td>{formatDate(r.dateFin)}</td>
                                        <td>
                                                <span className={`place-status ${getStatusClass(r.statut)}`}>
                                                    {r.statut}
                                                </span>
                                        </td>
                                    </tr>
                                ))}
                                </tbody>
                            </table>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
}
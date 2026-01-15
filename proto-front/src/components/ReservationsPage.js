import React, { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";
import { API_RESERVATIONS_PLACE, API_PLACES_DISPONIBLES, API_PERSONNES, API_VEHICULES } from "../constants/back";
import { useUser } from "../context/UserContext";

export default function ReservationsPage() {
    const [reservations, setReservations] = useState([]);
    const [places, setPlaces] = useState([]);
    const [personnes, setPersonnes] = useState([]);
    const [vehicules, setVehicules] = useState([]);
    const [filter, setFilter] = useState("TOUS");
    const [showModal, setShowModal] = useState(false);
    const [form, setForm] = useState({ personneId: "", placeId: "", vehiculeId: "", dateDebut: "", dateFin: "" });
    const [loading, setLoading] = useState(true);

    const { user, authFetch } = useUser();
    const location = useLocation();
    const isAdmin = user?.typePersonne === "SUPERVISEUR";

    useEffect(() => {
        loadReservations();
        loadData();
    }, []);

    // Handle pre-selected place from PlacesPage
    useEffect(() => {
        if (location.state?.placeToReserve) {
            const place = location.state.placeToReserve;
            setForm(prev => ({
                ...prev,
                placeId: place.id.toString(),
                personneId: user?.id?.toString() || ""
            }));
            setShowModal(true);
        }
    }, [location.state, user]);

    async function loadReservations() {
        try {
            setLoading(true);
            // Admin sees all, users see only their own
            const url = isAdmin
                ? API_RESERVATIONS_PLACE
                : `${API_RESERVATIONS_PLACE}/personne/${user?.id}`;
            const res = await authFetch(url);
            if (res.ok) setReservations(await res.json());
        } catch (e) {
            console.error(e);
        } finally {
            setLoading(false);
        }
    }

    async function loadData() {
        try {
            const requests = [
                fetch(API_PLACES_DISPONIBLES),
                authFetch(`${API_VEHICULES}/personne/${user?.id}`)
            ];

            // Admin can see all persons and vehicles
            if (isAdmin) {
                requests.push(authFetch(API_PERSONNES));
                requests.push(authFetch(API_VEHICULES));
            }

            const results = await Promise.all(requests);

            if (results[0].ok) setPlaces(await results[0].json());

            if (isAdmin) {
                if (results[2]?.ok) setPersonnes(await results[2].json());
                if (results[3]?.ok) setVehicules(await results[3].json());
            } else {
                // For regular users, only show their vehicles
                if (results[1]?.ok) setVehicules(await results[1].json());
                setPersonnes([user]);
            }
        } catch (e) {
            console.error(e);
        }
    }

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const body = {
                personneId: isAdmin ? parseInt(form.personneId) : user.id,
                placeId: parseInt(form.placeId),
                vehiculeId: parseInt(form.vehiculeId),
                dateDebut: form.dateDebut,
                dateFin: form.dateFin
            };
            const res = await authFetch(API_RESERVATIONS_PLACE, {
                method: "POST",
                body: JSON.stringify(body)
            });
            if (!res.ok) {
                const err = await res.json();
                throw new Error(err.message || "Erreur " + res.status);
            }
            closeModal();
            loadReservations();
            loadData();
        } catch (e) {
            alert("Erreur: " + e.message);
        }
    };

    const handleAction = async (id, action) => {
        try {
            const res = await authFetch(`${API_RESERVATIONS_PLACE}/${id}/${action}`, { method: "POST" });
            if (!res.ok) throw new Error("Erreur " + res.status);
            loadReservations();
            loadData();
        } catch (e) {
            alert("Erreur: " + e.message);
        }
    };

    const handleDelete = async (id) => {
        if (!window.confirm("Supprimer cette reservation?")) return;
        try {
            await authFetch(`${API_RESERVATIONS_PLACE}/${id}`, { method: "DELETE" });
            loadReservations();
            loadData();
        } catch (e) {
            alert("Erreur: " + e.message);
        }
    };

    const closeModal = () => {
        setShowModal(false);
        setForm({ personneId: "", placeId: "", vehiculeId: "", dateDebut: "", dateFin: "" });
    };

    const formatDate = (dateStr) => {
        if (!dateStr) return "-";
        return new Date(dateStr).toLocaleString("fr-FR", {
            day: "2-digit",
            month: "short",
            year: "numeric",
            hour: "2-digit",
            minute: "2-digit"
        });
    };

    const getStatusClass = (statut) => {
        const classes = {
            "EN_ATTENTE": "en-attente",
            "CONFIRMEE": "en-attente",
            "EN_COURS": "en-cours",
            "TERMINEE": "terminee",
            "ANNULEE": "annulee"
        };
        return classes[statut] || "";
    };

    const getStatusBadgeClass = (statut) => {
        const classes = {
            "EN_ATTENTE": "status-en-attente",
            "CONFIRMEE": "status-en-attente",
            "EN_COURS": "status-en-cours",
            "TERMINEE": "status-terminee",
            "ANNULEE": "status-annulee"
        };
        return classes[statut] || "";
    };

    const filteredReservations = filter === "TOUS"
        ? reservations
        : reservations.filter(r => r.statut === filter);

    const stats = {
        total: reservations.length,
        enAttente: reservations.filter(r => r.statut === "EN_ATTENTE" || r.statut === "CONFIRMEE").length,
        enCours: reservations.filter(r => r.statut === "EN_COURS").length,
        terminee: reservations.filter(r => r.statut === "TERMINEE").length
    };

    return (
        <div>
            <div className="page-header">
                <h1 className="page-title">
                    {isAdmin ? "Gestion des Reservations" : "Mes Reservations"}
                </h1>
                <p className="page-subtitle">{stats.total} reservations - {stats.enCours} en cours</p>
            </div>

            <div className="stats-grid" style={{ marginBottom: 24 }}>
                <div className="stat-card">
                    <div className="stat-icon blue">...</div>
                    <div className="stat-info">
                        <h3>{stats.enAttente}</h3>
                        <p>En attente</p>
                    </div>
                </div>
                <div className="stat-card">
                    <div className="stat-icon green">&gt;</div>
                    <div className="stat-info">
                        <h3>{stats.enCours}</h3>
                        <p>En cours</p>
                    </div>
                </div>
                <div className="stat-card">
                    <div className="stat-icon cyan">ok</div>
                    <div className="stat-info">
                        <h3>{stats.terminee}</h3>
                        <p>Terminees</p>
                    </div>
                </div>
            </div>

            <div className="card">
                <div className="card-header">
                    <div className="filter-bar" style={{ margin: 0 }}>
                        {["TOUS", "EN_ATTENTE", "EN_COURS", "TERMINEE", "ANNULEE"].map(f => (
                            <button
                                key={f}
                                className={`filter-btn ${filter === f ? "active" : ""}`}
                                onClick={() => setFilter(f)}
                            >
                                {f === "TOUS" ? "Toutes" : f.replace("_", " ")}
                            </button>
                        ))}
                    </div>
                    <button className="btn btn-primary" onClick={() => setShowModal(true)}>
                        + Nouvelle Reservation
                    </button>
                </div>
                <div className="card-body" style={{ padding: 16 }}>
                    {loading ? (
                        <div className="empty-state">
                            <div className="empty-state-icon">...</div>
                            <h3>Chargement...</h3>
                        </div>
                    ) : filteredReservations.length === 0 ? (
                        <div className="empty-state">
                            <div className="empty-state-icon">R</div>
                            <h3>Aucune reservation</h3>
                            <p>Creez une nouvelle reservation</p>
                        </div>
                    ) : (
                        filteredReservations.map(r => (
                            <div key={r.id} className={`reservation-card ${getStatusClass(r.statut)}`}>
                                <div className="reservation-info">
                                    <h4>
                                        <span className="user-avatar" style={{ marginRight: 12, display: "inline-flex", width: 32, height: 32, fontSize: "0.9rem" }}>
                                            {r.personne?.nom?.charAt(0) || "?"}
                                        </span>
                                        {r.personne?.nom} {r.personne?.prenom}
                                        <span className={`place-status ${getStatusBadgeClass(r.statut)}`} style={{ marginLeft: 12 }}>
                                            {r.statut?.replace("_", " ")}
                                        </span>
                                    </h4>
                                    <div className="reservation-details">
                                        <div className="reservation-detail">
                                            <span>P</span> Place {r.place?.numero}
                                        </div>
                                        <div className="reservation-detail">
                                            <span>V</span> {r.vehicule?.immatriculation || "-"}
                                        </div>
                                        <div className="reservation-detail">
                                            <span>D</span> {formatDate(r.dateDebut)} - {formatDate(r.dateFin)}
                                        </div>
                                    </div>
                                </div>
                                <div className="table-actions">
                                    {(r.statut === "EN_ATTENTE" || r.statut === "CONFIRMEE") && (
                                        <>
                                            <button
                                                className="btn btn-success btn-sm"
                                                onClick={() => handleAction(r.id, "commencer")}
                                            >
                                                Demarrer
                                            </button>
                                            <button
                                                className="btn btn-warning btn-sm"
                                                onClick={() => handleAction(r.id, "annuler")}
                                            >
                                                Annuler
                                            </button>
                                        </>
                                    )}
                                    {r.statut === "EN_COURS" && (
                                        <button
                                            className="btn btn-primary btn-sm"
                                            onClick={() => handleAction(r.id, "terminer")}
                                        >
                                            Terminer
                                        </button>
                                    )}
                                    {isAdmin && (
                                        <button
                                            className="btn btn-outline btn-sm"
                                            onClick={() => handleDelete(r.id)}
                                        >
                                            Supprimer
                                        </button>
                                    )}
                                </div>
                            </div>
                        ))
                    )}
                </div>
            </div>

            {showModal && (
                <div className="modal-overlay" onClick={closeModal}>
                    <div className="modal" onClick={e => e.stopPropagation()}>
                        <div className="modal-header">
                            <h2 className="modal-title">Nouvelle Reservation</h2>
                            <button className="modal-close" onClick={closeModal}>&times;</button>
                        </div>
                        <form onSubmit={handleSubmit}>
                            <div className="modal-body">
                                {isAdmin && (
                                    <div className="form-group">
                                        <label className="form-label">Client</label>
                                        <select
                                            className="form-control"
                                            value={form.personneId}
                                            onChange={e => setForm({...form, personneId: e.target.value})}
                                            required
                                        >
                                            <option value="">-- Selectionner un client --</option>
                                            {personnes.map(p => (
                                                <option key={p.id} value={p.id}>{p.nom} {p.prenom}</option>
                                            ))}
                                        </select>
                                    </div>
                                )}
                                <div className="form-group">
                                    <label className="form-label">Place de parking</label>
                                    <select
                                        className="form-control"
                                        value={form.placeId}
                                        onChange={e => setForm({...form, placeId: e.target.value})}
                                        required
                                    >
                                        <option value="">-- Selectionner une place --</option>
                                        {places.map(p => (
                                            <option key={p.id} value={p.id}>{p.numero} ({p.type})</option>
                                        ))}
                                    </select>
                                </div>
                                <div className="form-group">
                                    <label className="form-label">Vehicule</label>
                                    <select
                                        className="form-control"
                                        value={form.vehiculeId}
                                        onChange={e => setForm({...form, vehiculeId: e.target.value})}
                                        required
                                    >
                                        <option value="">-- Selectionner un vehicule --</option>
                                        {vehicules.map(v => (
                                            <option key={v.id} value={v.id}>{v.immatriculation} ({v.typeVehicule})</option>
                                        ))}
                                    </select>
                                    {vehicules.length === 0 && !isAdmin && (
                                        <p style={{ color: "#64748b", fontSize: "0.85rem", marginTop: 8 }}>
                                            Vous n'avez pas de vehicule enregistre. Ajoutez-en un dans la section Vehicules.
                                        </p>
                                    )}
                                </div>
                                <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 16 }}>
                                    <div className="form-group">
                                        <label className="form-label">Date de debut</label>
                                        <input
                                            type="datetime-local"
                                            className="form-control"
                                            value={form.dateDebut}
                                            onChange={e => setForm({...form, dateDebut: e.target.value})}
                                            required
                                        />
                                    </div>
                                    <div className="form-group">
                                        <label className="form-label">Date de fin</label>
                                        <input
                                            type="datetime-local"
                                            className="form-control"
                                            value={form.dateFin}
                                            onChange={e => setForm({...form, dateFin: e.target.value})}
                                            required
                                        />
                                    </div>
                                </div>
                            </div>
                            <div className="modal-footer">
                                <button type="button" className="btn btn-secondary" onClick={closeModal}>
                                    Annuler
                                </button>
                                <button type="submit" className="btn btn-primary">
                                    Creer la reservation
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
}

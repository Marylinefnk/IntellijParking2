import React, { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";
import { API_RESERVATIONS_PLACE, API_PLACES_AVAILABILITY, API_PERSONNES, API_VEHICULES } from "../constants/back";
import { useUser } from "../context/UserContext";
import { useNotification } from "../context/NotificationContext";

export default function ReservationsPage() {
    const [reservations, setReservations] = useState([]);
    const [places, setPlaces] = useState([]);
    const [personnes, setPersonnes] = useState([]);
    const [vehicules, setVehicules] = useState([]);
    const [filter, setFilter] = useState("TOUS");
    const [search, setSearch] = useState("");
    const [filterFrom, setFilterFrom] = useState("");
    const [filterTo, setFilterTo] = useState("");
    const [page, setPage] = useState(0);
    const PAGE_SIZE = 10;
    const [showModal, setShowModal] = useState(false);
    const [form, setForm] = useState({ personneId: "", placeId: "", vehiculeId: "", dateDebut: "", dateFin: "" });
    const [loading, setLoading] = useState(true);

    const { user, authFetch, loading: userLoading } = useUser();
    const { success, error, warning } = useNotification();
    const location = useLocation();
    const isAdmin = user?.typePersonne === "SUPERVISEUR";

    useEffect(() => {
        if (!userLoading && user) {
            loadReservations();
            loadData();
        }
    }, [userLoading, user]);

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
                fetch(API_PLACES_AVAILABILITY),
                authFetch(`${API_VEHICULES}/personne/${user?.id}`)
            ];

            if (isAdmin) {
                requests.push(authFetch(API_PERSONNES));
                requests.push(authFetch(API_VEHICULES));
            }

            const results = await Promise.all(requests);

            if (results[0].ok) {
                const allPlaces = await results[0].json();
                const selectedPlaceId = location.state?.placeToReserve?.id;
                const availablePlaces = allPlaces.filter(p =>
                    p.statutActuel === "LIBRE" || p.id === selectedPlaceId
                );
                setPlaces(availablePlaces);
            }

            if (isAdmin) {
                if (results[2]?.ok) setPersonnes(await results[2].json());
                if (results[3]?.ok) setVehicules(await results[3].json());
            } else {
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
                let errorMsg = "Erreur " + res.status;
                try {
                    const err = await res.json();
                    errorMsg = err.message || err.error || errorMsg;
                } catch {}
                throw new Error(errorMsg);
            }
            closeModal();
            loadReservations();
            loadData();
            success("Reservation creee avec succes");
        } catch (err) {
            error("Erreur: " + err.message);
        }
    };

    const handleAction = async (id, action) => {
        try {
            const res = await authFetch(`${API_RESERVATIONS_PLACE}/${id}/${action}`, { method: "POST" });
            if (!res.ok) throw new Error("Erreur " + res.status);
            loadReservations();
            loadData();
            const actionMessages = {
                commencer: "Reservation demarree",
                terminer: "Reservation terminee",
                annuler: "Reservation annulee"
            };
            if (action === "annuler") {
                warning(actionMessages[action]);
            } else {
                success(actionMessages[action] || "Action effectuee");
            }
        } catch (err) {
            error("Erreur: " + err.message);
        }
    };

    const handleDelete = async (id) => {
        if (!window.confirm("Supprimer cette reservation?")) return;
        try {
            await authFetch(`${API_RESERVATIONS_PLACE}/${id}`, { method: "DELETE" });
            loadReservations();
            loadData();
            success("Reservation supprimee avec succes");
        } catch (err) {
            error("Erreur: " + err.message);
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

    const filteredReservations = reservations.filter(r => {
        if (filter !== "TOUS") {
            if (filter === "EN_ATTENTE") {
                if (r.statut !== "CONFIRMEE" && r.statut !== "EN_ATTENTE") return false;
            } else if (r.statut !== filter) {
                return false;
            }
        }
        if (search.trim()) {
            const s = search.trim().toLowerCase();
            const persName = `${r.personne?.nom || ""} ${r.personne?.prenom || ""}`.toLowerCase();
            const placeNum = (r.place?.numero || "").toLowerCase();
            const vehicleImmat = (r.vehicule?.immatriculation || "").toLowerCase();
            if (!persName.includes(s) && !placeNum.includes(s) && !vehicleImmat.includes(s)) return false;
        }
        if (filterFrom && new Date(r.dateDebut) < new Date(filterFrom)) return false;
        if (filterTo) {
            const endOfDay = new Date(filterTo);
            endOfDay.setHours(23, 59, 59, 999);
            if (new Date(r.dateDebut) > endOfDay) return false;
        }
        return true;
    });

    useEffect(() => { setPage(0); }, [filter, search, filterFrom, filterTo]);

    const totalPages = Math.ceil(filteredReservations.length / PAGE_SIZE);
    const paginatedReservations = filteredReservations.slice(page * PAGE_SIZE, (page + 1) * PAGE_SIZE);

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
                                {f === "TOUS" ? "Toutes" : f === "EN_ATTENTE" ? "En attente" : f.replace("_", " ")}
                            </button>
                        ))}
                    </div>
                    <button className="btn btn-primary" onClick={() => setShowModal(true)}>
                        + Nouvelle Reservation
                    </button>
                </div>
                <div style={{ padding: "12px 16px", borderBottom: "1px solid #e2e8f0", display: "flex", gap: 12, flexWrap: "wrap", alignItems: "center", background: "#f8fafc" }}>
                    <input
                        type="text"
                        className="form-control"
                        placeholder="Rechercher par nom, immatriculation, place..."
                        value={search}
                        onChange={e => setSearch(e.target.value)}
                        style={{ flex: 2, minWidth: 180, maxWidth: 320 }}
                    />
                    <div style={{ display: "flex", alignItems: "center", gap: 8 }}>
                        <span style={{ fontSize: "0.85rem", color: "#64748b", whiteSpace: "nowrap" }}>Du</span>
                        <input
                            type="date"
                            className="form-control"
                            value={filterFrom}
                            onChange={e => setFilterFrom(e.target.value)}
                            style={{ width: 150 }}
                        />
                    </div>
                    <div style={{ display: "flex", alignItems: "center", gap: 8 }}>
                        <span style={{ fontSize: "0.85rem", color: "#64748b", whiteSpace: "nowrap" }}>Au</span>
                        <input
                            type="date"
                            className="form-control"
                            value={filterTo}
                            onChange={e => setFilterTo(e.target.value)}
                            style={{ width: 150 }}
                        />
                    </div>
                    {(search || filterFrom || filterTo) && (
                        <button
                            className="btn btn-outline btn-sm"
                            onClick={() => { setSearch(""); setFilterFrom(""); setFilterTo(""); }}
                        >
                            Effacer
                        </button>
                    )}
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
                            <p>{(filter !== "TOUS" || search || filterFrom || filterTo)
                                ? "Aucune reservation ne correspond aux filtres selectionnes"
                                : "Creez une nouvelle reservation"}</p>
                        </div>
                    ) : (
                        paginatedReservations.map(r => (
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
                    {!loading && totalPages > 1 && (
                        <div style={{ display: "flex", justifyContent: "center", alignItems: "center", gap: 8, padding: "16px 0 4px" }}>
                            <button
                                className="btn btn-outline btn-sm"
                                onClick={() => setPage(0)}
                                disabled={page === 0}
                            >
                                «
                            </button>
                            <button
                                className="btn btn-outline btn-sm"
                                onClick={() => setPage(p => p - 1)}
                                disabled={page === 0}
                            >
                                ‹
                            </button>
                            {Array.from({ length: totalPages }, (_, i) => i)
                                .filter(i => i === 0 || i === totalPages - 1 || Math.abs(i - page) <= 2)
                                .reduce((acc, i, idx, arr) => {
                                    if (idx > 0 && i - arr[idx - 1] > 1) acc.push("...");
                                    acc.push(i);
                                    return acc;
                                }, [])
                                .map((item, idx) =>
                                    item === "..." ? (
                                        <span key={`ellipsis-${idx}`} style={{ padding: "0 4px", color: "#64748b" }}>…</span>
                                    ) : (
                                        <button
                                            key={item}
                                            className={`btn btn-sm ${page === item ? "btn-primary" : "btn-outline"}`}
                                            onClick={() => setPage(item)}
                                        >
                                            {item + 1}
                                        </button>
                                    )
                                )
                            }
                            <button
                                className="btn btn-outline btn-sm"
                                onClick={() => setPage(p => p + 1)}
                                disabled={page === totalPages - 1}
                            >
                                ›
                            </button>
                            <button
                                className="btn btn-outline btn-sm"
                                onClick={() => setPage(totalPages - 1)}
                                disabled={page === totalPages - 1}
                            >
                                »
                            </button>
                            <span style={{ fontSize: "0.85rem", color: "#64748b", marginLeft: 8 }}>
                                {page * PAGE_SIZE + 1}–{Math.min((page + 1) * PAGE_SIZE, filteredReservations.length)} / {filteredReservations.length}
                            </span>
                        </div>
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
                                            <option key={p.id} value={p.id}>
                                                {p.numero} - {p.type} ({p.statutActuel || p.statut})
                                            </option>
                                        ))}
                                    </select>
                                    {places.length === 0 && (
                                        <p style={{ color: "#64748b", fontSize: "0.85rem", marginTop: 8 }}>
                                            Aucune place disponible pour le moment.
                                        </p>
                                    )}
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

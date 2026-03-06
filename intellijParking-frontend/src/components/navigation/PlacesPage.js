import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { API_PLACES, API_PLACES_AVAILABILITY_PAGE, API_PLACES_STATS, API_ZONES } from "../../constants/back";
import { useUser } from "../../context/UserContext";
import { useNotification } from "../../context/NotificationContext";
import { useSSEPlaces } from "../../hooks/useSSEPlaces";

export default function PlacesPage() {
    const [places, setPlaces] = useState([]);
    const [zones, setZones] = useState([]);
    const [filter, setFilter] = useState("TOUS");
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [totalElements, setTotalElements] = useState(0);
    const PAGE_SIZE = 12;
    const [showModal, setShowModal] = useState(false);
    const [editingPlace, setEditingPlace] = useState(null);
    const [selectedPlace, setSelectedPlace] = useState(null);
    const [filterDate, setFilterDate] = useState("");
    const [form, setForm] = useState({ numero: "", type: "STANDARD", statut: "LIBRE", positionX: 0, positionY: 0, zoneId: "" });
    const [stats, setStats] = useState({ total: 0, libre: 0, occupee: 0, reservee: 0 });
    const [loading, setLoading] = useState(true);

    const { user, authFetch } = useUser();
    const { success, error, info } = useNotification();
    const navigate = useNavigate();
    const isAdmin = user?.typePersonne === "SUPERVISEUR";

    const ssePlaces = useSSEPlaces();

    useEffect(() => {
        if (!ssePlaces.lastEvent) return;
        const evt = ssePlaces.lastEvent;
        info(`Place ${evt.numero}: ${evt.ancienStatut} → ${evt.nouveauStatut}`);
        setPlaces(prev => prev.map(p => {
            if (p.id === evt.idPlace) {
                return { ...p, statutActuel: evt.nouveauStatut };
            }
            return p;
        }));
        if (evt.statsTotal != null) {
            setStats({
                total: evt.statsTotal,
                libre: evt.statsLibre,
                occupee: evt.statsOccupee,
                reservee: evt.statsReservee
            });
        }
    }, [ssePlaces.lastEvent]);

    useEffect(() => {
        loadPlaces();
        loadZones();
        loadStats();
    }, [page, filterDate]);

    async function loadPlaces() {
        try {
            setLoading(true);
            let url = `${API_PLACES_AVAILABILITY_PAGE}?page=${page}&size=${PAGE_SIZE}`;
            if (filterDate) url += `&date=${filterDate}`;
            const res = await fetch(url);
            if (res.ok) {
                const data = await res.json();
                setPlaces(data.content);
                setTotalPages(data.totalPages);
                setTotalElements(data.totalElements);
            }
        } catch (e) {
            console.error(e);
        } finally {
            setLoading(false);
        }
    }

    async function loadStats() {
        try {
            const res = await fetch(API_PLACES_STATS);
            if (res.ok) setStats(await res.json());
        } catch (e) {}
    }

    async function loadZones() {
        try {
            const res = await fetch(API_ZONES);
            if (res.ok) setZones(await res.json());
        } catch (e) {}
    }

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!isAdmin) return;

        try {
            const url = editingPlace ? `${API_PLACES}/${editingPlace.id}` : API_PLACES;
            const method = editingPlace ? "PUT" : "POST";
            const body = {
                numero: form.numero,
                type: form.type,
                statut: form.statut,
                positionX: parseFloat(form.positionX) || 0,
                positionY: parseFloat(form.positionY) || 0,
                zoneId: form.zoneId ? parseInt(form.zoneId) : null
            };
            const res = await authFetch(url, {
                method,
                body: JSON.stringify(body)
            });
            if (!res.ok) throw new Error("Erreur " + res.status);
            closeModal();
            loadPlaces();
            success(editingPlace
                ? `Place ${form.numero} modifiee avec succes`
                : `Place ${form.numero} ajoutee avec succes`
            );
        } catch (err) {
            error("Erreur: " + err.message);
        }
    };

    const handleEdit = (place) => {
        if (!isAdmin) return;
        setEditingPlace(place);
        setForm({
            numero: place.numero || "",
            type: place.type || "STANDARD",
            statut: place.statutActuel || "LIBRE",
            positionX: place.positionX || 0,
            positionY: place.positionY || 0,
            zoneId: place.zoneId || ""
        });
        setShowModal(true);
    };

    const handleDelete = async (id) => {
        if (!isAdmin) return;
        if (!window.confirm("Supprimer cette place?")) return;
        try {
            await authFetch(`${API_PLACES}/${id}`, { method: "DELETE" });
            loadPlaces();
            success("Place supprimee avec succes");
        } catch (err) {
            error("Erreur: " + err.message);
        }
    };

    const handleReserve = (place) => {
        if (!user) {
            navigate("/login");
            return;
        }
        navigate("/app/reservations", { state: { placeToReserve: place } });
    };

    const handleShowDetails = (place) => {
        setSelectedPlace(place);
    };

    const closeModal = () => {
        setShowModal(false);
        setEditingPlace(null);
        setForm({ numero: "", type: "STANDARD", statut: "LIBRE", positionX: 0, positionY: 0, zoneId: "" });
    };

    const getStatusClass = (statut) => {
        const classes = {
            "LIBRE": "libre",
            "OCCUPEE": "occupee",
            "RESERVEE": "reservee",
            "HORS_SERVICE": "hors-service"
        };
        return classes[statut] || "";
    };

    const getStatusBadgeClass = (statut) => {
        const classes = {
            "LIBRE": "status-libre",
            "OCCUPEE": "status-occupee",
            "RESERVEE": "status-reservee",
            "HORS_SERVICE": "status-hors-service"
        };
        return classes[statut] || "";
    };

    const formatDate = (dateStr) => {
        if (!dateStr) return "-";
        return new Date(dateStr).toLocaleString("fr-FR", {
            day: "2-digit",
            month: "short",
            hour: "2-digit",
            minute: "2-digit"
        });
    };

    const filteredPlaces = filter === "TOUS" ? places : places.filter(p => p.statutActuel === filter);

    return (
        <div>
            <div className="page-header">
                <div style={{ display: "flex", alignItems: "center", gap: 12 }}>
                    <h1 className="page-title">
                        {isAdmin ? "Gestion des Places" : "Places de Parking"}
                    </h1>
                    <span style={{
                        display: "inline-flex",
                        alignItems: "center",
                        gap: 6,
                        padding: "4px 10px",
                        borderRadius: 20,
                        fontSize: "0.75rem",
                        fontWeight: 500,
                        background: ssePlaces.connected ? "#dcfce7" : "#fef2f2",
                        color: ssePlaces.connected ? "#166534" : "#dc2626"
                    }}>
                        <span style={{
                            width: 8,
                            height: 8,
                            borderRadius: "50%",
                            background: ssePlaces.connected ? "#22c55e" : "#ef4444"
                        }}></span>
                        {ssePlaces.connected ? "Temps reel" : "Hors ligne"}
                    </span>
                </div>
                <p className="page-subtitle">{stats.total} places au total - {stats.libre} disponibles maintenant</p>
            </div>

            <div className="stats-grid" style={{ marginBottom: 24 }}>
                <div className="stat-card">
                    <div className="stat-icon green">P</div>
                    <div className="stat-info">
                        <h3>{stats.libre}</h3>
                        <p>Libres</p>
                    </div>
                </div>
                <div className="stat-card">
                    <div className="stat-icon red">P</div>
                    <div className="stat-info">
                        <h3>{stats.occupee}</h3>
                        <p>Occupees</p>
                    </div>
                </div>
                <div className="stat-card">
                    <div className="stat-icon orange">P</div>
                    <div className="stat-info">
                        <h3>{stats.reservee}</h3>
                        <p>Reservees</p>
                    </div>
                </div>
            </div>

            <div className="card">
                <div className="card-header">
                    <div className="filter-bar" style={{ margin: 0 }}>
                        {["TOUS", "LIBRE", "OCCUPEE", "RESERVEE", "HORS_SERVICE"].map(f => (
                            <button
                                key={f}
                                className={`filter-btn ${filter === f ? "active" : ""}`}
                                onClick={() => setFilter(f)}
                            >
                                {f === "TOUS" ? "Toutes" : f.replace("_", " ")}
                            </button>
                        ))}
                    </div>
                    <div style={{ display: "flex", alignItems: "center", gap: 8 }}>
                        <input
                            type="date"
                            className="form-control"
                            value={filterDate}
                            onChange={e => { setFilterDate(e.target.value); setPage(0); }}
                            style={{ width: 160 }}
                        />
                        {filterDate && (
                            <button
                                className="btn btn-outline btn-sm"
                                onClick={() => { setFilterDate(""); setPage(0); }}
                            >
                                Effacer
                            </button>
                        )}
                        {isAdmin && (
                            <button className="btn btn-primary" onClick={() => setShowModal(true)}>
                                + Nouvelle Place
                            </button>
                        )}
                    </div>
                </div>
                <div className="card-body">
                    {loading ? (
                        <div className="empty-state">
                            <div className="empty-state-icon">...</div>
                            <h3>Chargement...</h3>
                        </div>
                    ) : filteredPlaces.length === 0 ? (
                        <div className="empty-state">
                            <div className="empty-state-icon">P</div>
                            <h3>Aucune place trouvee</h3>
                            <p>Changez le filtre pour voir d'autres places</p>
                        </div>
                    ) : (
                        <div className="places-grid">
                            {filteredPlaces.map(place => (
                                <div
                                    key={place.id}
                                    className={`place-card ${getStatusClass(place.statutActuel)}`}
                                    onClick={() => isAdmin ? handleEdit(place) : handleShowDetails(place)}
                                    style={{ cursor: "pointer" }}
                                >
                                    <div className="place-number">{place.numero}</div>
                                    <div className="place-type">{place.type}</div>
                                    <span className={`place-status ${getStatusBadgeClass(place.statutActuel)}`}>
                                        {place.statutActuel}
                                    </span>
                                    {place.zoneNom && (
                                        <div style={{ marginTop: 8, fontSize: "0.8rem", color: "#64748b" }}>
                                            Zone: {place.zoneNom}
                                        </div>
                                    )}

                                    {place.reservationEnCours && (
                                        <div style={{
                                            marginTop: 8,
                                            padding: 8,
                                            background: "rgba(0,0,0,0.05)",
                                            borderRadius: 4,
                                            fontSize: "0.75rem"
                                        }}>
                                            <div style={{ fontWeight: 500 }}>En cours:</div>
                                            <div>Jusqu'a {formatDate(place.reservationEnCours.dateFin)}</div>
                                        </div>
                                    )}

                                    {place.prochainesReservations?.length > 0 && !place.reservationEnCours && (
                                        <div style={{
                                            marginTop: 8,
                                            padding: 8,
                                            background: "rgba(251, 191, 36, 0.1)",
                                            borderRadius: 4,
                                            fontSize: "0.75rem",
                                            color: "#92400e"
                                        }}>
                                            <div style={{ fontWeight: 500 }}>Prochaine resa:</div>
                                            <div>{formatDate(place.prochainesReservations[0].dateDebut)}</div>
                                        </div>
                                    )}

                                    {!isAdmin && place.disponiblePourReservation && place.statutActuel !== "HORS_SERVICE" && (
                                        <button
                                            className="btn btn-primary"
                                            style={{ marginTop: 12, width: "100%", padding: "8px" }}
                                            onClick={(e) => { e.stopPropagation(); handleReserve(place); }}
                                        >
                                            {place.statutActuel === "LIBRE" ? "Reserver maintenant" : "Reserver autre creneau"}
                                        </button>
                                    )}
                                </div>
                            ))}
                        </div>
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
                                {page * PAGE_SIZE + 1}–{Math.min((page + 1) * PAGE_SIZE, totalElements)} / {totalElements}
                            </span>
                        </div>
                    )}
                </div>
            </div>

            {!user && (
                <div style={{
                    marginTop: 24,
                    padding: 20,
                    background: "#dbeafe",
                    borderRadius: 8,
                    textAlign: "center"
                }}>
                    <p style={{ margin: 0, color: "#1e40af" }}>
                        Connectez-vous pour reserver une place de parking
                    </p>
                    <button
                        className="btn btn-primary"
                        style={{ marginTop: 12 }}
                        onClick={() => navigate("/login")}
                    >
                        Se connecter
                    </button>
                </div>
            )}

            {selectedPlace && !isAdmin && (
                <div className="modal-overlay" onClick={() => setSelectedPlace(null)}>
                    <div className="modal" onClick={e => e.stopPropagation()}>
                        <div className="modal-header">
                            <h2 className="modal-title">Place {selectedPlace.numero}</h2>
                            <button className="modal-close" onClick={() => setSelectedPlace(null)}>&times;</button>
                        </div>
                        <div className="modal-body">
                            <div style={{ marginBottom: 16 }}>
                                <strong>Type:</strong> {selectedPlace.type}
                            </div>
                            <div style={{ marginBottom: 16 }}>
                                <strong>Statut actuel:</strong>{" "}
                                <span className={`place-status ${getStatusBadgeClass(selectedPlace.statutActuel)}`}>
                                    {selectedPlace.statutActuel}
                                </span>
                            </div>
                            {selectedPlace.zoneNom && (
                                <div style={{ marginBottom: 16 }}>
                                    <strong>Zone:</strong> {selectedPlace.zoneNom}
                                </div>
                            )}

                            {selectedPlace.reservationEnCours && (
                                <div style={{
                                    padding: 12,
                                    background: "#fef2f2",
                                    borderRadius: 8,
                                    marginBottom: 16
                                }}>
                                    <strong style={{ color: "#dc2626" }}>Reservation en cours:</strong>
                                    <div>De {formatDate(selectedPlace.reservationEnCours.dateDebut)}</div>
                                    <div>A {formatDate(selectedPlace.reservationEnCours.dateFin)}</div>
                                </div>
                            )}

                            {selectedPlace.prochainesReservations?.length > 0 && (
                                <div>
                                    <strong>Prochaines reservations:</strong>
                                    <div style={{ marginTop: 8 }}>
                                        {selectedPlace.prochainesReservations.map((r, i) => (
                                            <div key={i} style={{
                                                padding: 8,
                                                background: "#fffbeb",
                                                borderRadius: 4,
                                                marginBottom: 8,
                                                fontSize: "0.9rem"
                                            }}>
                                                {formatDate(r.dateDebut)} - {formatDate(r.dateFin)}
                                            </div>
                                        ))}
                                    </div>
                                </div>
                            )}

                            {selectedPlace.prochainesReservations?.length === 0 && !selectedPlace.reservationEnCours && (
                                <div style={{
                                    padding: 12,
                                    background: "#ecfdf5",
                                    borderRadius: 8,
                                    color: "#059669"
                                }}>
                                    Cette place n'a aucune reservation a venir!
                                </div>
                            )}
                        </div>
                        <div className="modal-footer">
                            <button className="btn btn-secondary" onClick={() => setSelectedPlace(null)}>
                                Fermer
                            </button>
                            {selectedPlace.disponiblePourReservation && selectedPlace.statutActuel !== "HORS_SERVICE" && (
                                <button
                                    className="btn btn-primary"
                                    onClick={() => { setSelectedPlace(null); handleReserve(selectedPlace); }}
                                >
                                    Reserver cette place
                                </button>
                            )}
                        </div>
                    </div>
                </div>
            )}

            {showModal && isAdmin && (
                <div className="modal-overlay" onClick={closeModal}>
                    <div className="modal" onClick={e => e.stopPropagation()}>
                        <div className="modal-header">
                            <h2 className="modal-title">
                                {editingPlace ? "Modifier la place" : "Nouvelle place"}
                            </h2>
                            <button className="modal-close" onClick={closeModal}>&times;</button>
                        </div>
                        <form onSubmit={handleSubmit}>
                            <div className="modal-body">
                                <div className="form-group">
                                    <label className="form-label">Numero de place</label>
                                    <input
                                        className="form-control"
                                        value={form.numero}
                                        onChange={e => setForm({...form, numero: e.target.value})}
                                        placeholder="Ex: A01, B12..."
                                        required
                                    />
                                </div>
                                <div className="form-group">
                                    <label className="form-label">Type de place</label>
                                    <select
                                        className="form-control"
                                        value={form.type}
                                        onChange={e => setForm({...form, type: e.target.value})}
                                    >
                                        <option value="STANDARD">Standard</option>
                                        <option value="PMR">PMR (Handicape)</option>
                                        <option value="ELECTRIQUE">Electrique</option>
                                        <option value="MOTO">Moto</option>
                                    </select>
                                </div>
                                <div className="form-group">
                                    <label className="form-label">Statut (manuel)</label>
                                    <select
                                        className="form-control"
                                        value={form.statut}
                                        onChange={e => setForm({...form, statut: e.target.value})}
                                    >
                                        <option value="LIBRE">Libre</option>
                                        <option value="HORS_SERVICE">Hors service</option>
                                    </select>
                                    <p style={{ fontSize: "0.8rem", color: "#64748b", marginTop: 4 }}>
                                        Note: Les statuts OCCUPEE et RESERVEE sont calcules automatiquement selon les reservations
                                    </p>
                                </div>
                                <div className="form-group">
                                    <label className="form-label">Zone</label>
                                    <select
                                        className="form-control"
                                        value={form.zoneId}
                                        onChange={e => setForm({...form, zoneId: e.target.value})}
                                    >
                                        <option value="">-- Aucune zone --</option>
                                        {zones.map(z => (
                                            <option key={z.id} value={z.id}>{z.nom}</option>
                                        ))}
                                    </select>
                                </div>
                                <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 16 }}>
                                    <div className="form-group">
                                        <label className="form-label">Position X</label>
                                        <input
                                            type="number"
                                            className="form-control"
                                            value={form.positionX}
                                            onChange={e => setForm({...form, positionX: e.target.value})}
                                        />
                                    </div>
                                    <div className="form-group">
                                        <label className="form-label">Position Y</label>
                                        <input
                                            type="number"
                                            className="form-control"
                                            value={form.positionY}
                                            onChange={e => setForm({...form, positionY: e.target.value})}
                                        />
                                    </div>
                                </div>
                            </div>
                            <div className="modal-footer">
                                {editingPlace && (
                                    <button
                                        type="button"
                                        className="btn btn-danger"
                                        onClick={() => { handleDelete(editingPlace.id); closeModal(); }}
                                    >
                                        Supprimer
                                    </button>
                                )}
                                <button type="button" className="btn btn-secondary" onClick={closeModal}>
                                    Annuler
                                </button>
                                <button type="submit" className="btn btn-primary">
                                    {editingPlace ? "Enregistrer" : "Creer"}
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
}

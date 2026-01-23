import React, { useEffect, useState } from "react";
import { API_VEHICULES, API_PERSONNES } from "../constants/back";
import { useUser } from "../context/UserContext";
import { useNotification } from "../context/NotificationContext";

export default function VehiculesPage() {
    const [vehicules, setVehicules] = useState([]);
    const [personnes, setPersonnes] = useState([]);
    const [showModal, setShowModal] = useState(false);
    const [editingVehicule, setEditingVehicule] = useState(null);
    const [form, setForm] = useState({ immatriculation: "", typeVehicule: "VOITURE", personneId: "" });
    const [loading, setLoading] = useState(true);
    const [search, setSearch] = useState("");

    const { user, authFetch } = useUser();
    const { success, error } = useNotification();
    const isAdmin = user?.typePersonne === "SUPERVISEUR";

    useEffect(() => {
        loadVehicules();
        if (isAdmin) {
            loadPersonnes();
        }
    }, []);

    async function loadVehicules() {
        try {
            setLoading(true);
            // Admin sees all vehicles, users see only their own
            const url = isAdmin ? API_VEHICULES : `${API_VEHICULES}/personne/${user?.id}`;
            const res = await authFetch(url);
            if (res.ok) setVehicules(await res.json());
        } catch (e) {
            console.error(e);
        } finally {
            setLoading(false);
        }
    }

    async function loadPersonnes() {
        try {
            const res = await authFetch(API_PERSONNES);
            if (res.ok) setPersonnes(await res.json());
        } catch (e) {}
    }

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const url = editingVehicule ? `${API_VEHICULES}/${editingVehicule.id}` : API_VEHICULES;
            const method = editingVehicule ? "PUT" : "POST";
            const body = {
                immatriculation: form.immatriculation,
                typeVehicule: form.typeVehicule,
                personneId: isAdmin ? (form.personneId ? parseInt(form.personneId) : null) : user.id
            };
            const res = await authFetch(url, {
                method,
                body: JSON.stringify(body)
            });
            if (!res.ok) throw new Error("Erreur " + res.status);
            closeModal();
            loadVehicules();
            success(editingVehicule
                ? `Vehicule ${form.immatriculation} modifie avec succes`
                : `Vehicule ${form.immatriculation} ajoute avec succes`
            );
        } catch (err) {
            error("Erreur: " + err.message);
        }
    };

    const handleEdit = (vehicule) => {
        setEditingVehicule(vehicule);
        setForm({
            immatriculation: vehicule.immatriculation || "",
            typeVehicule: vehicule.typeVehicule || "VOITURE",
            personneId: vehicule.personneId || ""
        });
        setShowModal(true);
    };

    const handleDelete = async (id) => {
        if (!window.confirm("Supprimer ce vehicule?")) return;
        try {
            await authFetch(`${API_VEHICULES}/${id}`, { method: "DELETE" });
            loadVehicules();
            success("Vehicule supprime avec succes");
        } catch (err) {
            error("Erreur: " + err.message);
        }
    };

    const closeModal = () => {
        setShowModal(false);
        setEditingVehicule(null);
        setForm({ immatriculation: "", typeVehicule: "VOITURE", personneId: "" });
    };

    const getPersonneName = (personneId) => {
        if (!isAdmin) return `${user?.nom} ${user?.prenom}`;
        const p = personnes.find(per => per.id === personneId);
        return p ? `${p.nom} ${p.prenom}` : "-";
    };

    const getVehicleIcon = (type) => {
        const icons = {
            "VOITURE": "V",
            "MOTO": "M",
            "CAMION": "C",
            "VELO": "B"
        };
        return icons[type] || "V";
    };

    const filteredVehicules = vehicules.filter(v =>
        `${v.immatriculation} ${v.typeVehicule}`.toLowerCase().includes(search.toLowerCase())
    );

    const stats = {
        total: vehicules.length,
        voiture: vehicules.filter(v => v.typeVehicule === "VOITURE").length,
        moto: vehicules.filter(v => v.typeVehicule === "MOTO").length,
        autres: vehicules.filter(v => !["VOITURE", "MOTO"].includes(v.typeVehicule)).length
    };

    return (
        <div>
            <div className="page-header">
                <h1 className="page-title">
                    {isAdmin ? "Gestion des Vehicules" : "Mes Vehicules"}
                </h1>
                <p className="page-subtitle">{vehicules.length} vehicules enregistres</p>
            </div>

            <div className="stats-grid" style={{ marginBottom: 24 }}>
                <div className="stat-card">
                    <div className="stat-icon blue">V</div>
                    <div className="stat-info">
                        <h3>{stats.voiture}</h3>
                        <p>Voitures</p>
                    </div>
                </div>
                <div className="stat-card">
                    <div className="stat-icon orange">M</div>
                    <div className="stat-info">
                        <h3>{stats.moto}</h3>
                        <p>Motos</p>
                    </div>
                </div>
                <div className="stat-card">
                    <div className="stat-icon green">+</div>
                    <div className="stat-info">
                        <h3>{stats.autres}</h3>
                        <p>Autres</p>
                    </div>
                </div>
            </div>

            <div className="card">
                <div className="card-header">
                    <input
                        type="text"
                        className="form-control"
                        placeholder="Rechercher par immatriculation..."
                        value={search}
                        onChange={e => setSearch(e.target.value)}
                        style={{ maxWidth: 300 }}
                    />
                    <button className="btn btn-primary" onClick={() => setShowModal(true)}>
                        + Nouveau Vehicule
                    </button>
                </div>
                <div className="card-body" style={{ padding: 0 }}>
                    {loading ? (
                        <div className="empty-state">
                            <div className="empty-state-icon">...</div>
                            <h3>Chargement...</h3>
                        </div>
                    ) : filteredVehicules.length === 0 ? (
                        <div className="empty-state">
                            <div className="empty-state-icon">V</div>
                            <h3>Aucun vehicule trouve</h3>
                            <p>Ajoutez un nouveau vehicule</p>
                        </div>
                    ) : (
                        <div className="table-container">
                            <table className="table">
                                <thead>
                                    <tr>
                                        <th>Vehicule</th>
                                        <th>Type</th>
                                        {isAdmin && <th>Proprietaire</th>}
                                        <th>Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {filteredVehicules.map(v => (
                                        <tr key={v.id}>
                                            <td>
                                                <div style={{ display: "flex", alignItems: "center", gap: 12 }}>
                                                    <div className="stat-icon blue" style={{ width: 40, height: 40, fontSize: "1.2rem" }}>
                                                        {getVehicleIcon(v.typeVehicule)}
                                                    </div>
                                                    <div>
                                                        <strong>{v.immatriculation}</strong>
                                                        <div style={{ fontSize: "0.85rem", color: "#64748b" }}>
                                                            ID: {v.id}
                                                        </div>
                                                    </div>
                                                </div>
                                            </td>
                                            <td>
                                                <span className="place-status status-en-attente">
                                                    {v.typeVehicule}
                                                </span>
                                            </td>
                                            {isAdmin && <td>{getPersonneName(v.personneId)}</td>}
                                            <td>
                                                <div className="table-actions">
                                                    <button
                                                        className="btn btn-outline btn-sm"
                                                        onClick={() => handleEdit(v)}
                                                    >
                                                        Modifier
                                                    </button>
                                                    <button
                                                        className="btn btn-danger btn-sm"
                                                        onClick={() => handleDelete(v.id)}
                                                    >
                                                        Supprimer
                                                    </button>
                                                </div>
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                    )}
                </div>
            </div>

            {showModal && (
                <div className="modal-overlay" onClick={closeModal}>
                    <div className="modal" onClick={e => e.stopPropagation()}>
                        <div className="modal-header">
                            <h2 className="modal-title">
                                {editingVehicule ? "Modifier le vehicule" : "Nouveau vehicule"}
                            </h2>
                            <button className="modal-close" onClick={closeModal}>&times;</button>
                        </div>
                        <form onSubmit={handleSubmit}>
                            <div className="modal-body">
                                <div className="form-group">
                                    <label className="form-label">Immatriculation</label>
                                    <input
                                        className="form-control"
                                        value={form.immatriculation}
                                        onChange={e => setForm({...form, immatriculation: e.target.value})}
                                        placeholder="AA-123-BB"
                                        required
                                    />
                                </div>
                                <div className="form-group">
                                    <label className="form-label">Type de vehicule</label>
                                    <select
                                        className="form-control"
                                        value={form.typeVehicule}
                                        onChange={e => setForm({...form, typeVehicule: e.target.value})}
                                    >
                                        <option value="VOITURE">Voiture</option>
                                        <option value="MOTO">Moto</option>
                                        <option value="CAMION">Camion</option>
                                        <option value="VELO">Velo</option>
                                    </select>
                                </div>
                                {isAdmin && (
                                    <div className="form-group">
                                        <label className="form-label">Proprietaire</label>
                                        <select
                                            className="form-control"
                                            value={form.personneId}
                                            onChange={e => setForm({...form, personneId: e.target.value})}
                                        >
                                            <option value="">-- Aucun proprietaire --</option>
                                            {personnes.map(p => (
                                                <option key={p.id} value={p.id}>{p.nom} {p.prenom}</option>
                                            ))}
                                        </select>
                                    </div>
                                )}
                            </div>
                            <div className="modal-footer">
                                <button type="button" className="btn btn-secondary" onClick={closeModal}>
                                    Annuler
                                </button>
                                <button type="submit" className="btn btn-primary">
                                    {editingVehicule ? "Enregistrer" : "Creer"}
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
}

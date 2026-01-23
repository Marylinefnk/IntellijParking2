import React, { useEffect, useState } from "react";
import { API_PERSONNES } from "../constants/back";
import { useUser } from "../context/UserContext";

export default function PersonnesPage() {
    const [personnes, setPersonnes] = useState([]);
    const [showModal, setShowModal] = useState(false);
    const [editingPersonne, setEditingPersonne] = useState(null);
    const [form, setForm] = useState({ nom: "", prenom: "", mail: "", password: "", typePersonne: "VISITEUR" });
    const [loading, setLoading] = useState(true);
    const [search, setSearch] = useState("");

    const { authFetch } = useUser();

    useEffect(() => {
        loadPersonnes();
    }, []);

    async function loadPersonnes() {
        try {
            setLoading(true);
            const res = await authFetch(API_PERSONNES);
            if (res.ok) setPersonnes(await res.json());
        } catch (e) {
            console.error(e);
        } finally {
            setLoading(false);
        }
    }

    const handleSubmit = async (e) => {
        e.preventDefault();

        // Validate password for new users
        if (!editingPersonne && (!form.password || form.password.length < 6)) {
            alert("Le mot de passe doit contenir au moins 6 caracteres");
            return;
        }

        try {
            const url = editingPersonne ? `${API_PERSONNES}/${editingPersonne.id}` : API_PERSONNES;
            const method = editingPersonne ? "PUT" : "POST";

            // Only include password for new users
            const body = editingPersonne
                ? { nom: form.nom, prenom: form.prenom, mail: form.mail, typePersonne: form.typePersonne }
                : form;

            const res = await authFetch(url, {
                method,
                body: JSON.stringify(body)
            });
            if (!res.ok) {
                const err = await res.json();
                throw new Error(err.error || "Erreur " + res.status);
            }
            closeModal();
            loadPersonnes();
        } catch (e) {
            alert("Erreur: " + e.message);
        }
    };

    const handleEdit = (personne) => {
        setEditingPersonne(personne);
        setForm({
            nom: personne.nom || "",
            prenom: personne.prenom || "",
            mail: personne.mail || "",
            password: "",
            typePersonne: personne.typePersonne || "VISITEUR"
        });
        setShowModal(true);
    };

    const handleDelete = async (id) => {
        if (!window.confirm("Supprimer ce client?")) return;
        try {
            const res = await authFetch(`${API_PERSONNES}/${id}`, { method: "DELETE" });
            if (!res.ok) {
                const err = await res.json();
                throw new Error(err.error || "Erreur lors de la suppression");
            }
            loadPersonnes();
        } catch (e) {
            alert("Erreur: " + e.message);
        }
    };

    const closeModal = () => {
        setShowModal(false);
        setEditingPersonne(null);
        setForm({ nom: "", prenom: "", mail: "", password: "", typePersonne: "VISITEUR" });
    };

    const filteredPersonnes = personnes.filter(p =>
        `${p.nom} ${p.prenom} ${p.mail}`.toLowerCase().includes(search.toLowerCase())
    );

    const getRoleBadgeClass = (type) => {
        const classes = {
            "SUPERVISEUR": "status-en-attente",
            "ABONNE": "status-en-cours",
            "VISITEUR": "status-libre"
        };
        return classes[type] || "";
    };

    const getRoleLabel = (type) => {
        const labels = {
            "SUPERVISEUR": "Superviseur",
            "ABONNE": "Abonne",
            "VISITEUR": "Visiteur"
        };
        return labels[type] || type || "Visiteur";
    };

    const stats = {
        total: personnes.length,
        superviseurs: personnes.filter(p => p.typePersonne === "SUPERVISEUR").length,
        abonnes: personnes.filter(p => p.typePersonne === "ABONNE").length,
        visiteurs: personnes.filter(p => p.typePersonne === "VISITEUR" || !p.typePersonne).length
    };

    return (
        <div>
            <div className="page-header">
                <h1 className="page-title">Gestion des Clients</h1>
                <p className="page-subtitle">{personnes.length} clients enregistres</p>
            </div>

            <div className="stats-grid" style={{ marginBottom: 24 }}>
                <div className="stat-card">
                    <div className="stat-icon orange">S</div>
                    <div className="stat-info">
                        <h3>{stats.superviseurs}</h3>
                        <p>Superviseurs</p>
                    </div>
                </div>
                <div className="stat-card">
                    <div className="stat-icon blue">A</div>
                    <div className="stat-info">
                        <h3>{stats.abonnes}</h3>
                        <p>Abonnes</p>
                    </div>
                </div>
                <div className="stat-card">
                    <div className="stat-icon green">V</div>
                    <div className="stat-info">
                        <h3>{stats.visiteurs}</h3>
                        <p>Visiteurs</p>
                    </div>
                </div>
            </div>

            <div className="card">
                <div className="card-header">
                    <input
                        type="text"
                        className="form-control"
                        placeholder="Rechercher un client..."
                        value={search}
                        onChange={e => setSearch(e.target.value)}
                        style={{ maxWidth: 300 }}
                    />
                    <button className="btn btn-primary" onClick={() => setShowModal(true)}>
                        + Nouveau Client
                    </button>
                </div>
                <div className="card-body" style={{ padding: 0 }}>
                    {loading ? (
                        <div className="empty-state">
                            <div className="empty-state-icon">...</div>
                            <h3>Chargement...</h3>
                        </div>
                    ) : filteredPersonnes.length === 0 ? (
                        <div className="empty-state">
                            <div className="empty-state-icon">U</div>
                            <h3>Aucun client trouve</h3>
                            <p>Ajoutez un nouveau client</p>
                        </div>
                    ) : (
                        <div className="table-container">
                            <table className="table">
                                <thead>
                                    <tr>
                                        <th>Client</th>
                                        <th>Email</th>
                                        <th>Role</th>
                                        <th>Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {filteredPersonnes.map(p => (
                                        <tr key={p.id}>
                                            <td>
                                                <div style={{ display: "flex", alignItems: "center", gap: 12 }}>
                                                    <div className="user-avatar">
                                                        {p.nom?.charAt(0) || "?"}
                                                    </div>
                                                    <div>
                                                        <strong>{p.nom} {p.prenom}</strong>
                                                        <div style={{ fontSize: "0.85rem", color: "#64748b" }}>
                                                            ID: {p.id}
                                                        </div>
                                                    </div>
                                                </div>
                                            </td>
                                            <td>{p.mail}</td>
                                            <td>
                                                <span className={`place-status ${getRoleBadgeClass(p.typePersonne)}`}>
                                                    {getRoleLabel(p.typePersonne)}
                                                </span>
                                            </td>
                                            <td>
                                                <div className="table-actions">
                                                    <button
                                                        className="btn btn-outline btn-sm"
                                                        onClick={() => handleEdit(p)}
                                                    >
                                                        Modifier
                                                    </button>
                                                    <button
                                                        className="btn btn-danger btn-sm"
                                                        onClick={() => handleDelete(p.id)}
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
                                {editingPersonne ? "Modifier le client" : "Nouveau client"}
                            </h2>
                            <button className="modal-close" onClick={closeModal}>&times;</button>
                        </div>
                        <form onSubmit={handleSubmit}>
                            <div className="modal-body">
                                <div className="form-group">
                                    <label className="form-label">Nom</label>
                                    <input
                                        className="form-control"
                                        value={form.nom}
                                        onChange={e => setForm({...form, nom: e.target.value})}
                                        placeholder="Entrez le nom"
                                        required
                                    />
                                </div>
                                <div className="form-group">
                                    <label className="form-label">Prenom</label>
                                    <input
                                        className="form-control"
                                        value={form.prenom}
                                        onChange={e => setForm({...form, prenom: e.target.value})}
                                        placeholder="Entrez le prenom"
                                        required
                                    />
                                </div>
                                <div className="form-group">
                                    <label className="form-label">Email</label>
                                    <input
                                        type="email"
                                        className="form-control"
                                        value={form.mail}
                                        onChange={e => setForm({...form, mail: e.target.value})}
                                        placeholder="email@exemple.com"
                                        required
                                    />
                                </div>
                                {!editingPersonne && (
                                    <div className="form-group">
                                        <label className="form-label">Mot de passe</label>
                                        <input
                                            type="password"
                                            className="form-control"
                                            value={form.password}
                                            onChange={e => setForm({...form, password: e.target.value})}
                                            placeholder="Minimum 6 caracteres"
                                            required
                                            minLength={6}
                                        />
                                    </div>
                                )}
                                <div className="form-group">
                                    <label className="form-label">Role</label>
                                    <select
                                        className="form-control"
                                        value={form.typePersonne}
                                        onChange={e => setForm({...form, typePersonne: e.target.value})}
                                    >
                                        <option value="VISITEUR">Visiteur</option>
                                        <option value="ABONNE">Abonne</option>
                                        <option value="SUPERVISEUR">Superviseur</option>
                                    </select>
                                </div>
                            </div>
                            <div className="modal-footer">
                                <button type="button" className="btn btn-secondary" onClick={closeModal}>
                                    Annuler
                                </button>
                                <button type="submit" className="btn btn-primary">
                                    {editingPersonne ? "Enregistrer" : "Creer"}
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
}

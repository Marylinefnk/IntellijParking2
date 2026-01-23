import React, { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { useUser } from "../context/UserContext";
import { API_AUTH } from "../constants/back";

export default function RegisterPage() {
    const [formData, setFormData] = useState({
        nom: "",
        prenom: "",
        email: "",
        password: "",
        confirmPassword: ""
    });
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);
    const { login } = useUser();
    const navigate = useNavigate();

    const handleChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });
    };

    const handleRegister = async (e) => {
        e.preventDefault();
        setError("");

        // Validate passwords match
        if (formData.password !== formData.confirmPassword) {
            setError("Les mots de passe ne correspondent pas");
            return;
        }

        // Validate password length
        if (formData.password.length < 6) {
            setError("Le mot de passe doit contenir au moins 6 caracteres");
            return;
        }

        setLoading(true);

        try {
            const res = await fetch(`${API_AUTH}/register`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    nom: formData.nom,
                    prenom: formData.prenom,
                    email: formData.email,
                    password: formData.password
                })
            });

            const data = await res.json();

            if (!res.ok) {
                setError(data.error || "Erreur lors de l'inscription");
                setLoading(false);
                return;
            }

            login(data);
            navigate("/app");
        } catch (e) {
            setError("Erreur de connexion. Verifiez que le serveur est en marche.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div style={{
            minHeight: "100vh",
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            background: "linear-gradient(135deg, #1e293b 0%, #0f172a 100%)"
        }}>
            <div style={{
                background: "white",
                borderRadius: 16,
                padding: 40,
                width: "100%",
                maxWidth: 450,
                boxShadow: "0 25px 50px -12px rgba(0,0,0,0.25)"
            }}>
                <div style={{ textAlign: "center", marginBottom: 32 }}>
                    <div style={{
                        width: 70,
                        height: 70,
                        background: "#10b981",
                        borderRadius: 16,
                        display: "flex",
                        alignItems: "center",
                        justifyContent: "center",
                        margin: "0 auto 16px",
                        fontSize: "2rem",
                        color: "white"
                    }}>
                        +
                    </div>
                    <h1 style={{ margin: 0, fontSize: "1.8rem", color: "#1e293b" }}>Creer un compte</h1>
                    <p style={{ color: "#64748b", marginTop: 8 }}>Rejoignez Intellij Parking</p>
                </div>

                <form onSubmit={handleRegister}>
                    <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 12, marginBottom: 20 }}>
                        <div>
                            <label style={{ display: "block", marginBottom: 8, fontWeight: 500, color: "#1e293b" }}>
                                Nom
                            </label>
                            <input
                                type="text"
                                name="nom"
                                className="form-control"
                                value={formData.nom}
                                onChange={handleChange}
                                placeholder="Dupont"
                                required
                                style={{ width: "100%", padding: "14px 16px" }}
                            />
                        </div>
                        <div>
                            <label style={{ display: "block", marginBottom: 8, fontWeight: 500, color: "#1e293b" }}>
                                Prenom
                            </label>
                            <input
                                type="text"
                                name="prenom"
                                className="form-control"
                                value={formData.prenom}
                                onChange={handleChange}
                                placeholder="Jean"
                                required
                                style={{ width: "100%", padding: "14px 16px" }}
                            />
                        </div>
                    </div>

                    <div style={{ marginBottom: 20 }}>
                        <label style={{ display: "block", marginBottom: 8, fontWeight: 500, color: "#1e293b" }}>
                            Adresse email
                        </label>
                        <input
                            type="email"
                            name="email"
                            className="form-control"
                            value={formData.email}
                            onChange={handleChange}
                            placeholder="votre@email.com"
                            required
                            style={{ width: "100%", padding: "14px 16px" }}
                        />
                    </div>

                    <div style={{ marginBottom: 20 }}>
                        <label style={{ display: "block", marginBottom: 8, fontWeight: 500, color: "#1e293b" }}>
                            Mot de passe
                        </label>
                        <input
                            type="password"
                            name="password"
                            className="form-control"
                            value={formData.password}
                            onChange={handleChange}
                            placeholder="Minimum 6 caracteres"
                            required
                            minLength={6}
                            style={{ width: "100%", padding: "14px 16px" }}
                        />
                    </div>

                    <div style={{ marginBottom: 20 }}>
                        <label style={{ display: "block", marginBottom: 8, fontWeight: 500, color: "#1e293b" }}>
                            Confirmer le mot de passe
                        </label>
                        <input
                            type="password"
                            name="confirmPassword"
                            className="form-control"
                            value={formData.confirmPassword}
                            onChange={handleChange}
                            placeholder="Confirmez votre mot de passe"
                            required
                            style={{ width: "100%", padding: "14px 16px" }}
                        />
                    </div>

                    {error && (
                        <div style={{
                            background: "#fef2f2",
                            color: "#dc2626",
                            padding: 12,
                            borderRadius: 8,
                            marginBottom: 20,
                            fontSize: "0.9rem"
                        }}>
                            {error}
                        </div>
                    )}

                    <button
                        type="submit"
                        className="btn btn-primary"
                        disabled={loading}
                        style={{
                            width: "100%",
                            padding: "14px",
                            fontSize: "1rem",
                            background: "#10b981",
                            border: "none"
                        }}
                    >
                        {loading ? "Inscription..." : "S'inscrire"}
                    </button>
                </form>

                <div style={{ margin: "24px 0", textAlign: "center", color: "#64748b" }}>
                    <span style={{ background: "white", padding: "0 12px" }}>ou</span>
                </div>

                <div style={{ textAlign: "center" }}>
                    <p style={{ color: "#64748b", marginBottom: 12 }}>
                        Deja un compte ?
                    </p>
                    <Link
                        to="/login"
                        className="btn"
                        style={{
                            width: "100%",
                            padding: 14,
                            background: "#f1f5f9",
                            color: "#1e293b",
                            border: "2px solid #e2e8f0",
                            display: "block",
                            textDecoration: "none"
                        }}
                    >
                        Se connecter
                    </Link>
                </div>
            </div>
        </div>
    );
}

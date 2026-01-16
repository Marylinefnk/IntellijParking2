import React, { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { useUser } from "../context/UserContext";
import { API_AUTH } from "../constants/back";

export default function LoginPage() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);
    const { login } = useUser();
    const navigate = useNavigate();

    const handleLogin = async (e) => {
        e.preventDefault();
        setError("");
        setLoading(true);

        try {
            const res = await fetch(`${API_AUTH}/login`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ email, password })
            });

            const data = await res.json();

            if (!res.ok) {
                setError(data.error || "Email ou mot de passe incorrect");
                setLoading(false);
                return;
            }

            login(data);
            navigate("/");
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
                        background: "#3b82f6",
                        borderRadius: 16,
                        display: "flex",
                        alignItems: "center",
                        justifyContent: "center",
                        margin: "0 auto 16px",
                        fontSize: "2rem",
                        color: "white"
                    }}>
                        P
                    </div>
                    <h1 style={{ margin: 0, fontSize: "1.8rem", color: "#1e293b" }}>Intellij Parking</h1>
                    <p style={{ color: "#64748b", marginTop: 8 }}>Systeme de gestion de parking</p>
                </div>

                <form onSubmit={handleLogin}>
                    <div style={{ marginBottom: 20 }}>
                        <label style={{ display: "block", marginBottom: 8, fontWeight: 500, color: "#1e293b" }}>
                            Adresse email
                        </label>
                        <input
                            type="email"
                            className="form-control"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
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
                            className="form-control"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            placeholder="Votre mot de passe"
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
                        style={{ width: "100%", padding: "14px", fontSize: "1rem" }}
                    >
                        {loading ? "Connexion..." : "Se connecter"}
                    </button>
                </form>

                <div style={{ margin: "24px 0", textAlign: "center", color: "#64748b" }}>
                    <span style={{ background: "white", padding: "0 12px" }}>ou</span>
                </div>

                <div style={{ textAlign: "center" }}>
                    <p style={{ color: "#64748b", marginBottom: 12 }}>
                        Pas encore de compte ?
                    </p>
                    <Link
                        to="/register"
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
                        Creer un compte
                    </Link>
                </div>

                <div style={{
                    marginTop: 24,
                    padding: 16,
                    background: "#f8fafc",
                    borderRadius: 8,
                    fontSize: "0.85rem",
                    color: "#64748b"
                }}>
                    <strong>Comptes de test :</strong>
                    <div style={{ marginTop: 8 }}>
                        <div>Admin: admin@parking.com / admin123</div>
                        <div>Abonne: marie@parking.com / abonne123</div>
                        <div>Visiteur: pierre@parking.com / visiteur123</div>
                    </div>
                </div>
            </div>
        </div>
    );
}

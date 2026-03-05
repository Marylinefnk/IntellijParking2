import React from "react";
import { Outlet, Link, useNavigate } from "react-router-dom";
import { useUser } from "../context/UserContext";

export default function PublicLayout() {
    const { user } = useUser();
    const navigate = useNavigate();

    return (
        <div style={{ minHeight: "100vh", background: "#f0f4f8" }}>
            <header style={{
                background: "white",
                padding: "16px 24px",
                boxShadow: "0 1px 3px rgba(0,0,0,0.1)",
                display: "flex",
                justifyContent: "space-between",
                alignItems: "center"
            }}>
                <div style={{ display: "flex", alignItems: "center", gap: 12 }}>
                    <div style={{
                        width: 40,
                        height: 40,
                        background: "#3b82f6",
                        borderRadius: 8,
                        display: "flex",
                        alignItems: "center",
                        justifyContent: "center",
                        color: "white",
                        fontWeight: "bold"
                    }}>
                        P
                    </div>
                    <span style={{ fontSize: "1.25rem", fontWeight: 600, color: "#1e293b" }}>
                        Intellij Parking
                    </span>
                </div>
                <nav style={{ display: "flex", gap: 16, alignItems: "center" }}>
                    <Link to="/HomePage" style={{ color: "#3b82f6", textDecoration: "none", fontWeight: 500 }}>
                        Voir le parking
                    </Link>
                    {user ? (
                        <button
                            className="btn btn-primary"
                            onClick={() => navigate("/app")}
                        >
                            Mon espace
                        </button>
                    ) : (
                        <>
                            <Link
                                to="/login"
                                className="btn"
                                style={{ background: "#f1f5f9", color: "#1e293b", textDecoration: "none" }}
                            >
                                Connexion
                            </Link>
                            <Link
                                to="/register"
                                className="btn btn-primary"
                                style={{ textDecoration: "none" }}
                            >
                                Inscription
                            </Link>
                        </>
                    )}
                </nav>
            </header>
            <main style={{ padding: 24, maxWidth: 1200, margin: "0 auto" }}>
                <Outlet />
            </main>
        </div>
    );
}

import React, { useState, useEffect } from "react";
import { useSimulation } from "../../hooks/useSimulation";
import { useSSEPlaces } from "../../hooks/useSSEPlaces";
import { useSSEReservations } from "../../hooks/useSSEReservations";
import { useNotification } from "../../context/NotificationContext";

export default function SimulationPanel() {
    const { addNotification } = useNotification();
    const simulation = useSimulation();

    const [nbPersonnes, setNbPersonnes] = useState(20);
    const [date, setDate] = useState(new Date().toISOString().split("T")[0]);
    const [pasSecondes, setPasSecondes] = useState(1);
    const [intervalle, setIntervalle] = useState(3);
    const [probabilite, setProbabilite] = useState(0.5);
    const [nbCapteurs, setNbCapteurs] = useState(1);

    const [resaEnCours, setResaEnCours] = useState(false);
    const [capteursActifs, setCapteursActifs] = useState(false);

    const ssePlaces = useSSEPlaces();
    const sseReservations = useSSEReservations(resaEnCours);

    useEffect(() => {
        simulation.refreshStatut().then((s) => {
            if (s) {
                setCapteursActifs(s.simulationActive);
                setResaEnCours(s.reservationActive);
            }
        });
    }, []);

    const handleInitialiser = async () => {
        try {
            const result = await simulation.initialiser(nbPersonnes);
            addNotification(
                `Initialisation terminee : ${result.nbPersonnesCreees} personnes, ${result.nbVehiculesCreees} vehicules`,
                "success"
            );
        } catch (e) {
            addNotification("Erreur lors de l'initialisation", "error");
        }
    };

    const handleGenererReservations = async () => {
        try {
            sseReservations.reset();
            setResaEnCours(true);
            await simulation.genererReservations(date, pasSecondes);
            addNotification("Generation des reservations lancee", "success");
        } catch (e) {
            addNotification("Erreur lors de la generation", "error");
            setResaEnCours(false);
        }
    };

    const handleArreterReservations = async () => {
        try {
            await simulation.arreterReservations();
            setResaEnCours(false);
            addNotification("Arret de la generation des reservations demande", "info");
        } catch (e) {
            addNotification("Erreur lors de l'arret des reservations", "error");
        }
    };

    const handleDemarrerCapteurs = async () => {
        try {
            await simulation.demarrerCapteurs(intervalle, probabilite, nbCapteurs);
            setCapteursActifs(true);
            addNotification("Simulation capteurs demarree", "success");
        } catch (e) {
            addNotification("Erreur lors du demarrage", "error");
        }
    };

    const handleArreterCapteurs = async () => {
        try {
            await simulation.arreterCapteurs();
            setCapteursActifs(false);
            addNotification("Simulation capteurs arretee", "info");
        } catch (e) {
            addNotification("Erreur lors de l'arret", "error");
        }
    };

    const formatDateTime = (arr) => {
        if (!arr || !Array.isArray(arr)) return "-";
        const [y, m, d, h, min] = arr;
        return `${String(d).padStart(2, "0")}/${String(m).padStart(2, "0")}/${y} ${String(h).padStart(2, "0")}:${String(min).padStart(2, "0")}`;
    };

    return (
        <div>
            <div className="page-header">
                <h1 className="page-title">Simulation IoT</h1>
                <p className="page-subtitle">
                    Panneau de controle de la simulation capteurs et reservations
                </p>
            </div>

            <div className="stats-grid">
                <div className="stat-card">
                    <div className="stat-icon" style={{ background: capteursActifs ? "#22c55e" : "#6b7280" }}>
                        {capteursActifs ? "ON" : "OFF"}
                    </div>
                    <div className="stat-info">
                        <h3>{capteursActifs ? "Active" : "Inactive"}</h3>
                        <p>Simulation capteurs</p>
                    </div>
                </div>
                <div className="stat-card">
                    <div className="stat-icon green">{ssePlaces.stats.libres}</div>
                    <div className="stat-info">
                        <h3>Libres</h3>
                        <p>Places SSE</p>
                    </div>
                </div>
                <div className="stat-card">
                    <div className="stat-icon red">{ssePlaces.stats.occupees}</div>
                    <div className="stat-info">
                        <h3>Occupees</h3>
                        <p>Places SSE</p>
                    </div>
                </div>
                <div className="stat-card">
                    <div className="stat-icon orange">{sseReservations.compteur}</div>
                    <div className="stat-info">
                        <h3>Reservations</h3>
                        <p>Generees</p>
                    </div>
                </div>
            </div>

            <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 24, marginTop: 24 }}>

                <div className="card">
                    <div className="card-header">
                        <h2 className="card-title">1. Initialisation</h2>
                    </div>
                    <div className="card-body">
                        <div className="form-group">
                            <label className="form-label">Nombre de personnes</label>
                            <input
                                type="number"
                                className="form-control"
                                min="1"
                                max="100"
                                value={nbPersonnes}
                                onChange={(e) => setNbPersonnes(parseInt(e.target.value) || 20)}
                            />
                        </div>
                        <button
                            className="btn btn-primary"
                            onClick={handleInitialiser}
                            disabled={simulation.loading}
                            style={{ width: "100%", marginTop: 12 }}
                        >
                            {simulation.loading ? "Chargement..." : "Initialiser"}
                        </button>
                        {simulation.initResult && (
                            <div style={{
                                marginTop: 12,
                                padding: 12,
                                background: "#f0fdf4",
                                borderRadius: 8,
                                fontSize: "0.85rem"
                            }}>
                                <div>{simulation.initResult.nbPersonnesCreees} personnes creees</div>
                                <div>{simulation.initResult.nbVehiculesCreees} vehicules crees</div>
                                <div>{simulation.initResult.nbCapteursCreees} capteurs crees</div>
                            </div>
                        )}
                    </div>
                </div>

                <div className="card">
                    <div className="card-header">
                        <h2 className="card-title">2. Reservations</h2>
                        <span className={`place-status ${sseReservations.connected ? "status-en-cours" : "status-annulee"}`}>
                            {sseReservations.connected ? "SSE connecte" : "SSE deconnecte"}
                        </span>
                    </div>
                    <div className="card-body">
                        <div className="form-group">
                            <label className="form-label">Date</label>
                            <input
                                type="date"
                                className="form-control"
                                value={date}
                                onChange={(e) => setDate(e.target.value)}
                            />
                        </div>
                        <div className="form-group">
                            <label className="form-label">
                                Intervalle de generation : {pasSecondes}s
                            </label>
                            <input
                                type="range"
                                min="1"
                                max="10"
                                value={pasSecondes}
                                onChange={(e) => setPasSecondes(parseInt(e.target.value))}
                                style={{ width: "100%" }}
                            />
                        </div>
                        {!resaEnCours ? (
                            <button
                                className="btn btn-warning"
                                onClick={handleGenererReservations}
                                disabled={simulation.loading}
                                style={{ width: "100%", marginTop: 8 }}
                            >
                                {simulation.loading ? "Chargement..." : "Generer les reservations"}
                            </button>
                        ) : (
                            <button
                                className="btn btn-danger"
                                onClick={handleArreterReservations}
                                disabled={simulation.loading}
                                style={{ width: "100%", marginTop: 8 }}
                            >
                                {simulation.loading ? "Chargement..." : "Arreter la generation"}
                            </button>
                        )}
                    </div>
                </div>

                <div className="card">
                    <div className="card-header">
                        <h2 className="card-title">3. Simulation Capteurs</h2>
                        <span className={`place-status ${ssePlaces.connected ? "status-en-cours" : "status-annulee"}`}>
                            {ssePlaces.connected ? "SSE connecte" : "SSE deconnecte"}
                        </span>
                    </div>
                    <div className="card-body">
                        <div className="form-group">
                            <label className="form-label">
                                Intervalle : {intervalle}s
                            </label>
                            <input
                                type="range"
                                min="1"
                                max="10"
                                value={intervalle}
                                onChange={(e) => setIntervalle(parseInt(e.target.value))}
                                style={{ width: "100%" }}
                            />
                        </div>
                        <div className="form-group">
                            <label className="form-label">
                                Probabilite de presence : {(probabilite * 100).toFixed(0)}%
                            </label>
                            <input
                                type="range"
                                min="0"
                                max="100"
                                value={probabilite * 100}
                                onChange={(e) => setProbabilite(parseInt(e.target.value) / 100)}
                                style={{ width: "100%" }}
                            />
                        </div>
                        <div className="form-group">
                            <label className="form-label">
                                Capteurs par cycle : {nbCapteurs}
                            </label>
                            <input
                                type="range"
                                min="1"
                                max="50"
                                value={nbCapteurs}
                                onChange={(e) => setNbCapteurs(parseInt(e.target.value))}
                                style={{ width: "100%" }}
                            />
                        </div>
                        {!capteursActifs ? (
                            <button
                                className="btn btn-success"
                                onClick={handleDemarrerCapteurs}
                                disabled={simulation.loading}
                                style={{ width: "100%", marginTop: 8 }}
                            >
                                Demarrer la simulation
                            </button>
                        ) : (
                            <button
                                className="btn btn-danger"
                                onClick={handleArreterCapteurs}
                                disabled={simulation.loading}
                                style={{ width: "100%", marginTop: 8 }}
                            >
                                Arreter la simulation
                            </button>
                        )}
                    </div>
                </div>

                <div className="card">
                    <div className="card-header">
                        <h2 className="card-title">Flux temps reel</h2>
                    </div>
                    <div className="card-body" style={{ maxHeight: 300, overflowY: "auto", padding: 0 }}>
                        {ssePlaces.places.length === 0 && sseReservations.reservations.length === 0 ? (
                            <div className="empty-state">
                                <div className="empty-state-icon">~</div>
                                <h3>En attente d'evenements</h3>
                                <p>Les evenements SSE apparaitront ici</p>
                            </div>
                        ) : (
                            <div style={{ padding: 12 }}>
                                {ssePlaces.lastEvent && (
                                    <div style={{
                                        padding: 8,
                                        marginBottom: 8,
                                        borderRadius: 6,
                                        background: ssePlaces.lastEvent.nouveauStatut === "OCCUPEE" ? "#fef2f2" : "#f0fdf4",
                                        fontSize: "0.85rem"
                                    }}>
                                        <strong>Place {ssePlaces.lastEvent.numero}</strong>
                                        {" "}{ssePlaces.lastEvent.ancienStatut} → {ssePlaces.lastEvent.nouveauStatut}
                                        {ssePlaces.lastEvent.immatriculation && (
                                            <span style={{ color: "#6b7280" }}> | {ssePlaces.lastEvent.immatriculation}</span>
                                        )}
                                    </div>
                                )}
                                {sseReservations.reservations.slice(0, 10).map((r, i) => (
                                    <div key={i} style={{
                                        padding: 8,
                                        marginBottom: 4,
                                        borderRadius: 6,
                                        background: "#fffbeb",
                                        fontSize: "0.8rem"
                                    }}>
                                        <strong>#{r.compteur}</strong> {r.numeroPlace}
                                        {" | "}{r.immatriculation}
                                        {" | "}{formatDateTime(r.dateDebut)} - {formatDateTime(r.dateFin)}
                                    </div>
                                ))}
                            </div>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
}

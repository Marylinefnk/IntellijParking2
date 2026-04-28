import { useState, useCallback } from "react";
import { useUser } from "../context/UserContext";
import {
    SIMULATION_INITIALISER,
    SIMULATION_RESERVATIONS,
    SIMULATION_RESERVATIONS_ARRETER,
    SIMULATION_DEMARRER,
    SIMULATION_ARRETER,
    SIMULATION_STATUT
} from "../constants/back";

export function useSimulation() {
    const { authFetch } = useUser();
    const [loading, setLoading] = useState(false);
    const [statut, setStatut] = useState({ simulationActive: false, nbClientsSSE: 0 });
    const [initResult, setInitResult] = useState(null);

    const refreshStatut = useCallback(async () => {
        try {
            const res = await authFetch(SIMULATION_STATUT);
            if (res.ok) {
                const data = await res.json();
                setStatut(data);
                return data;
            }
        } catch (e) {
            console.error("Erreur statut simulation:", e);
        }
        return null;
    }, [authFetch]);

    const initialiser = useCallback(async (nbPersonnes = 20, vehiculesMax = 2) => {
        setLoading(true);
        try {
            const url = `${SIMULATION_INITIALISER}?nbPersonnes=${nbPersonnes}&vehiculesParPersonneMax=${vehiculesMax}`;
            const res = await authFetch(url, { method: "POST" });
            if (res.ok) {
                const data = await res.json();
                setInitResult(data);
                return data;
            }
            throw new Error(`HTTP ${res.status}`);
        } finally {
            setLoading(false);
        }
    }, [authFetch]);

    const genererReservations = useCallback(async (date, pasSecondes = 1) => {
        setLoading(true);
        try {
            const url = `${SIMULATION_RESERVATIONS}?date=${date}&pasGenerationSecondes=${pasSecondes}`;
            const res = await authFetch(url, { method: "POST" });
            if (res.ok) {
                return await res.json();
            }
            throw new Error(`HTTP ${res.status}`);
        } finally {
            setLoading(false);
        }
    }, [authFetch]);

    const arreterReservations = useCallback(async () => {
        setLoading(true);
        try {
            const res = await authFetch(SIMULATION_RESERVATIONS_ARRETER, { method: "POST" });
            if (res.ok) {
                return await res.json();
            }
            throw new Error(`HTTP ${res.status}`);
        } finally {
            setLoading(false);
        }
    }, [authFetch]);

    const demarrerCapteurs = useCallback(async (intervalle = 3, probabilite = 0.5, nbCapteurs = 1) => {
        setLoading(true);
        try {
            const url = `${SIMULATION_DEMARRER}?intervalleSecondes=${intervalle}&probabilitePresence=${probabilite}&nbCapteursParCycle=${nbCapteurs}`;
            const res = await authFetch(url, { method: "POST" });
            if (res.ok) {
                const data = await res.json();
                await refreshStatut();
                return data;
            }
            throw new Error(`HTTP ${res.status}`);
        } finally {
            setLoading(false);
        }
    }, [authFetch, refreshStatut]);

    const arreterCapteurs = useCallback(async () => {
        setLoading(true);
        try {
            const res = await authFetch(SIMULATION_ARRETER, { method: "POST" });
            if (res.ok) {
                const data = await res.json();
                await refreshStatut();
                return data;
            }
            throw new Error(`HTTP ${res.status}`);
        } finally {
            setLoading(false);
        }
    }, [authFetch, refreshStatut]);

    return {
        loading,
        statut,
        initResult,
        refreshStatut,
        initialiser,
        genererReservations,
        arreterReservations,
        demarrerCapteurs,
        arreterCapteurs
    };
}

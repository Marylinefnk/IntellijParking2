import { useState, useEffect, useRef, useCallback } from "react";
import { SSE_FLUX_RESERVATIONS } from "../constants/back";

export function useSSEReservations(active) {
    const [reservations, setReservations] = useState([]);
    const [lastEvent, setLastEvent] = useState(null);
    const [connected, setConnected] = useState(false);
    const [compteur, setCompteur] = useState(0);
    const eventSourceRef = useRef(null);
    const reconnectTimeoutRef = useRef(null);

    const connect = useCallback(() => {
        if (eventSourceRef.current) {
            eventSourceRef.current.close();
        }

        const es = new EventSource(SSE_FLUX_RESERVATIONS);
        eventSourceRef.current = es;

        es.onopen = () => {
            setConnected(true);
        };

        es.addEventListener("reservation-created", (event) => {
            try {
                const data = JSON.parse(event.data);
                setLastEvent(data);
                setCompteur(data.compteur || 0);
                setReservations((prev) => [data, ...prev].slice(0, 50));
            } catch (e) {
                console.error("Erreur parsing SSE reservation:", e);
            }
        });

        es.onerror = () => {
            setConnected(false);
            es.close();
            reconnectTimeoutRef.current = setTimeout(() => {
                if (active) connect();
            }, 5000);
        };
    }, [active]);

    useEffect(() => {
        if (active) {
            connect();
        }
        return () => {
            if (eventSourceRef.current) {
                eventSourceRef.current.close();
            }
            if (reconnectTimeoutRef.current) {
                clearTimeout(reconnectTimeoutRef.current);
            }
        };
    }, [active, connect]);

    const reset = useCallback(() => {
        setReservations([]);
        setCompteur(0);
        setLastEvent(null);
    }, []);

    return {
        reservations,
        lastEvent,
        connected,
        compteur,
        reset
    };
}

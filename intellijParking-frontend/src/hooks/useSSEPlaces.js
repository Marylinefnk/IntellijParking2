import { useState, useEffect, useRef, useCallback } from "react";
import { SSE_FLUX_PLACES } from "../constants/back";

export function useSSEPlaces(niveau) {
    const [placesMap, setPlacesMap] = useState({});
    const [lastEvent, setLastEvent] = useState(null);
    const [connected, setConnected] = useState(false);
    const eventSourceRef = useRef(null);
    const reconnectTimeoutRef = useRef(null);

    const connect = useCallback(() => {
        if (eventSourceRef.current) {
            eventSourceRef.current.close();
        }

        let url = SSE_FLUX_PLACES;
        if (niveau) {
            url += `?niveau=${encodeURIComponent(niveau)}`;
        }

        const es = new EventSource(url);
        eventSourceRef.current = es;

        es.onopen = () => {
            setConnected(true);
        };

        es.addEventListener("place-update", (event) => {
            try {
                const data = JSON.parse(event.data);
                setLastEvent(data);
                setPlacesMap((prev) => ({
                    ...prev,
                    [data.idPlace]: data
                }));
            } catch (e) {
                console.error("Erreur parsing SSE place:", e);
            }
        });

        es.onerror = () => {
            setConnected(false);
            es.close();
            reconnectTimeoutRef.current = setTimeout(() => {
                connect();
            }, 5000);
        };
    }, [niveau]);

    useEffect(() => {
        connect();
        return () => {
            if (eventSourceRef.current) {
                eventSourceRef.current.close();
            }
            if (reconnectTimeoutRef.current) {
                clearTimeout(reconnectTimeoutRef.current);
            }
        };
    }, [connect]);

    const stats = Object.values(placesMap).reduce(
        (acc, p) => {
            if (p.nouveauStatut === "LIBRE") acc.libres++;
            else if (p.nouveauStatut === "OCCUPEE") acc.occupees++;
            else if (p.nouveauStatut === "RESERVEE") acc.reservees++;
            return acc;
        },
        { libres: 0, occupees: 0, reservees: 0 }
    );

    return {
        places: Object.values(placesMap),
        lastEvent,
        connected,
        stats
    };
}

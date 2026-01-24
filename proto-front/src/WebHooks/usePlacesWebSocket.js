import { useEffect, useRef, useCallback, useState } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

const WS_URL = (process.env.REACT_APP_API_URL || 'http://localhost:8080') + '/ws';

/**
 * webHook pour la mise a jour des places en temps reel via WebSocket.
 * @param {Function} onPlacesUpdate - Callback lors de la maj de toutes les places
 * @param {Function} onPlaceUpdate - Callback lors de la maj d'une place
 * @param {Function} onPlaceDeleted - Callback lors de la suppression d'une place
 */
export function usePlacesWebSocket(onPlacesUpdate, onPlaceUpdate, onPlaceDeleted) {
    const clientRef = useRef(null);
    const [connected, setConnected] = useState(false);
    const [error, setError] = useState(null);

    const connect = useCallback(() => {
        if (clientRef.current?.active) {
            return;
        }

        const client = new Client({
            webSocketFactory: () => new SockJS(WS_URL),
            reconnectDelay: 5000,
            heartbeatIncoming: 4000,
            heartbeatOutgoing: 4000,
            debug: (str) => {
                console.log('[WebSocket]', str);
            },
            onConnect: () => {
                console.log('[WebSocket] Connecte');
                setConnected(true);
                setError(null);

                // Abonnement aux mises a jour de toutes les places
                client.subscribe('/topic/places', (message) => {
                    try {
                        const places = JSON.parse(message.body);
                        if (onPlacesUpdate) {
                            onPlacesUpdate(places);
                        }
                    } catch (e) {
                        console.error('[WebSocket] Erreur parsing:', e);
                    }
                });

                // Abonnement aux mises a jour individuelles
                client.subscribe('/topic/place-update', (message) => {
                    try {
                        const data = JSON.parse(message.body);

                        // Verifie si c'est une suppression
                        if (data.action === 'DELETED') {
                            if (onPlaceDeleted) {
                                onPlaceDeleted(data.id);
                            }
                        } else {
                            if (onPlaceUpdate) {
                                onPlaceUpdate(data);
                            }
                        }
                    } catch (e) {
                        console.error('[WebSocket] Erreur parsing:', e);
                    }
                });
            },
            onDisconnect: () => {
                console.log('[WebSocket] Deconnecte');
                setConnected(false);
            },
            onStompError: (frame) => {
                console.error('[WebSocket] Erreur STOMP:', frame.headers['message']);
                setError(frame.headers['message']);
            },
            onWebSocketError: (event) => {
                console.error('[WebSocket] Erreur connexion:', event);
                setError('Erreur de connexion');
            }
        });

        client.activate();
        clientRef.current = client;
    }, [onPlacesUpdate, onPlaceUpdate, onPlaceDeleted]);

    const disconnect = useCallback(() => {
        if (clientRef.current) {
            clientRef.current.deactivate();
            clientRef.current = null;
            setConnected(false);
        }
    }, []);

    useEffect(() => {
        connect();
        return () => disconnect();
    }, [connect, disconnect]);

    return { connected, error, reconnect: connect };
}

export default usePlacesWebSocket;

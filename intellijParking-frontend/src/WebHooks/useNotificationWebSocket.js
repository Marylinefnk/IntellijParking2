import { useEffect, useRef, useCallback, useState } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { useNotification } from '../context/NotificationContext';

const WS_URL = (process.env.REACT_APP_API_URL || 'http://localhost:8080') + '/ws';

/**
 * Hook pour recevoir les notifications en temps reel via WebSocket.
 * Affiche automatiquement des toasts lors de la reception d'evenements.
 */
export function useNotificationWebSocket() {
    const clientRef = useRef(null);
    const [connected, setConnected] = useState(false);
    const [error, setError] = useState(null);
    const { success, info, warning } = useNotification();

    // Determine le type de notification selon l'evenement
    const getNotificationType = useCallback((eventType) => {
        if (eventType.includes('CREATED')) return 'success';
        if (eventType.includes('DELETED') || eventType.includes('CANCELLED')) return 'warning';
        return 'info';
    }, []);

    // Affiche la notification avec le bon style
    const showNotification = useCallback((notification) => {
        const type = getNotificationType(notification.type);
        const message = notification.message;

        switch (type) {
            case 'success':
                success(message);
                break;
            case 'warning':
                warning(message);
                break;
            default:
                info(message);
        }
    }, [success, info, warning, getNotificationType]);

    // Connetion au serveur WebSocket
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

                // Abonnement au topic des notifications
                client.subscribe('/topic/notifications', (message) => {
                    try {
                        const notification = JSON.parse(message.body);
                        showNotification(notification);
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
                setError('Erreur de connexion WebSocket');
            }
        });

        client.activate();
        clientRef.current = client;
    }, [showNotification]);

    // Deconnexion propre
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

export default useNotificationWebSocket;
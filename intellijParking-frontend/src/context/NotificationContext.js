import React, { createContext, useContext, useState, useCallback } from "react";

/**
 * Contexte pour la gestion des notifications pop up(toast) dans l'application.
 * Fournit des methodes pour afficher des messages de succes, erreur, warning et info.
 */
const NotificationContext = createContext(null);

let notificationId = 0;

export function NotificationProvider({ children }) {
    const [notifications, setNotifications] = useState([]);

    // Ajoute une notification avec suppression automatique
    const addNotification = useCallback((message, type = "info", duration = 5000) => {
        const id = ++notificationId;
        const notification = { id, message, type, duration };

        setNotifications(prev => [...prev, notification]);

        // Suppression auto apres la duree specifiee
        if (duration > 0) {
            setTimeout(() => {
                removeNotification(id);
            }, duration);
        }

        return id;
    }, []);

    const removeNotification = useCallback((id) => {
        setNotifications(prev => prev.filter(n => n.id !== id));
    }, []);

    // Methodes raccourcis pour chaque type
    const success = useCallback((message, duration) => {
        return addNotification(message, "success", duration);
    }, [addNotification]);

    const error = useCallback((message, duration) => {
        return addNotification(message, "error", duration);
    }, [addNotification]);

    const warning = useCallback((message, duration) => {
        return addNotification(message, "warning", duration);
    }, [addNotification]);

    const info = useCallback((message, duration) => {
        return addNotification(message, "info", duration);
    }, [addNotification]);

    return (
        <NotificationContext.Provider value={{
            notifications,
            addNotification,
            removeNotification,
            success,
            error,
            warning,
            info
        }}>
            {children}
            <NotificationContainer
                notifications={notifications}
                onClose={removeNotification}
            />
        </NotificationContext.Provider>
    );
}

export function useNotification() {
    const context = useContext(NotificationContext);
    if (!context) {
        throw new Error("useNotification doit etre utilise dans un NotificationProvider");
    }
    return context;
}

// Conteneur des notifications (coin superieur droit)
function NotificationContainer({ notifications, onClose }) {
    return (
        <div className="notification-container">
            {notifications.map(notification => (
                <Toast
                    key={notification.id}
                    notification={notification}
                    onClose={() => onClose(notification.id)}
                />
            ))}
        </div>
    );
}

// Composant Toast individuel
function Toast({ notification, onClose }) {
    const { message, type } = notification;

    const icons = {
        success: "\u2713",
        error: "\u2717",
        warning: "!",
        info: "i"
    };

    return (
        <div className={`toast toast-${type}`}>
            <div className="toast-icon">{icons[type]}</div>
            <div className="toast-message">{message}</div>
            <button className="toast-close" onClick={onClose}>&times;</button>
        </div>
    );
}

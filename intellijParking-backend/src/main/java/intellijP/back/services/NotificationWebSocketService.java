package intellijP.back.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Service de diffusion des notifications en temps reel via WebSocket.
 * Permet d'informer tous les clients connectes des changements sur les entites.
 */
@Service
public class NotificationWebSocketService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationWebSocketService.class);
    private static final String TOPIC_NOTIFICATIONS = "/topic/notifications";

    private final SimpMessagingTemplate messagingTemplate;

    public NotificationWebSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Diffuse une notification a tous les clients connectes.
     */
    public void broadcastNotification(String type, String message, String entityType, Long entityId) {
        try {
            NotificationEvent event = new NotificationEvent(type, message, entityType, entityId);
            messagingTemplate.convertAndSend(TOPIC_NOTIFICATIONS, event);
            logger.info("Notification envoyee: {} - {}", type, message);
        } catch (Exception e) {
            logger.error("Erreur lors de l'envoi de la notification: {}", e.getMessage());
        }
    }

    // Notifications pour les vehicules
    public void notifyVehiculeCreated(String immatriculation, Long vehiculeId) {
        broadcastNotification("VEHICULE_CREE",
                "Nouveau vehicule ajoute: " + immatriculation,
                "VEHICULE", vehiculeId);
    }

    public void notifyVehiculeUpdated(String immatriculation, Long vehiculeId) {
        broadcastNotification("VEHICULE_UPDATED",
                "Vehicule modifie: " + immatriculation,
                "VEHICULE", vehiculeId);
    }

    public void notifyVehiculeDeleted(Long vehiculeId) {
        broadcastNotification("VEHICULE_DELETED",
                "Vehicule supprime",
                "VEHICULE", vehiculeId);
    }

    // Notifications pour les places
    public void notifyPlaceCreated(String numero, Long placeId) {
        broadcastNotification("PLACE_CREE",
                "Nouvelle place ajoutee: " + numero,
                "PLACE", placeId);
    }

    public void notifyPlaceUpdated(String numero, Long placeId) {
        broadcastNotification("PLACE_UPDATED",
                "Place modifiee: " + numero,
                "PLACE", placeId);
    }

    public void notifyPlaceDeleted(Long placeId) {
        broadcastNotification("PLACE_DELETED",
                "Place supprimee",
                "PLACE", placeId);
    }

    // Notifications pour les reservations
    public void notifyReservationCreated(Long reservationId, String placeNumero) {
        broadcastNotification("RESERVATION_CREE",
                "Nouvelle reservation pour la place " + placeNumero,
                "RESERVATION", reservationId);
    }

    public void notifyReservationUpdated(Long reservationId, String placeNumero) {
        broadcastNotification("RESERVATION_MODIFIEE",
                "Reservation modifiee - Place " + placeNumero,
                "RESERVATION", reservationId);
    }

    public void notifyReservationStarted(Long reservationId, String placeNumero) {
        broadcastNotification("RESERVATION_DEMARREE",
                "Reservation demarree - Place " + placeNumero,
                "RESERVATION", reservationId);
    }

    public void notifyReservationEnded(Long reservationId, String placeNumero) {
        broadcastNotification("RESERVATION_TERMINEE",
                "Reservation terminee - Place " + placeNumero + " maintenant libre",
                "RESERVATION", reservationId);
    }

    public void notifyReservationCancelled(Long reservationId, String placeNumero) {
        broadcastNotification("RESERVATION_ANNULEE",
                "Reservation annulee - Place " + placeNumero,
                "RESERVATION", reservationId);
    }

    public void notifyReservationDeleted(Long reservationId) {
        broadcastNotification("RESERVATION_SUPPRIMEE",
                "Reservation supprimee",
                "RESERVATION", reservationId);
    }

    /**
     * Classe representant un evenement de notification.
     */
    public static class NotificationEvent {
        private String type;
        private String message;
        private String entityType;
        private Long entityId;
        private String timestamp;

        public NotificationEvent(String type, String message, String entityType, Long entityId) {
            this.type = type;
            this.message = message;
            this.entityType = entityType;
            this.entityId = entityId;
            this.timestamp = LocalDateTime.now().toString();
        }

        // Getters et Setters
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getEntityType() { return entityType; }
        public void setEntityType(String entityType) { this.entityType = entityType; }
        public Long getEntityId() { return entityId; }
        public void setEntityId(Long entityId) { this.entityId = entityId; }
        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    }
}

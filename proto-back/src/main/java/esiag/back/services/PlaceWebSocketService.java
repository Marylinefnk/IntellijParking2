package esiag.back.services;

import esiag.back.dto.PlaceAvailabilityDTO;
import esiag.back.events.PlaceCreatedEvent;
import esiag.back.events.PlaceDeletedEvent;
import esiag.back.events.PlaceUpdatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

/**
 * Service de diffusion des mises a jour des places en temps reel via WebSocket.
 * Permet aux clients de recevoir les changements de disponibilite instantanement.
 */
@Service
public class PlaceWebSocketService {

    private static final Logger logger = LoggerFactory.getLogger(PlaceWebSocketService.class);
    private static final String TOPIC_PLACES = "/topic/places";
    private static final String TOPIC_PLACE_UPDATE = "/topic/place-update";

    private final SimpMessagingTemplate messagingTemplate;
    private final PlaceService placeService;

    public PlaceWebSocketService(SimpMessagingTemplate messagingTemplate, PlaceService placeService) {
        this.messagingTemplate = messagingTemplate;
        this.placeService = placeService;
    }

    // ---- Event listeners (AFTER COMMIT) ----

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPlaceCreated(PlaceCreatedEvent e) {
        // Au besoin, tu peux aussi faire broadcastPlaceUpdate(e.placeId())
        broadcastAllPlaces();
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPlaceUpdated(PlaceUpdatedEvent e) {
        broadcastPlaceUpdate(e.placeId());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPlaceDeleted(PlaceDeletedEvent e) {
        broadcastPlaceDeleted(e.placeId());
    }

    /**
     * Diffuse la liste complete des places avec leur disponibilite.
     * Appelee lors d'un ajout ou suppression de place.
     */
    public void broadcastAllPlaces() {
        try {
            List<PlaceAvailabilityDTO> places = placeService.findAllWithAvailability();
            messagingTemplate.convertAndSend(TOPIC_PLACES, places);
            logger.info("Diffusion de {} places vers {}", places.size(), TOPIC_PLACES);
        } catch (Exception e) {
            logger.error("Erreur lors de la diffusion des places: {}", e.getMessage());
        }
    }

    /**
     * Diffuse la mise a jour d'une place specifique.
     */
    public void broadcastPlaceUpdate(Long placeId) {
        try {
            placeService.findByIdWithAvailability(placeId).ifPresent(place -> {
                messagingTemplate.convertAndSend(TOPIC_PLACE_UPDATE, place);
                logger.info("Mise a jour diffusee pour la place {}", placeId);
            });
        } catch (Exception e) {
            logger.error("Erreur lors de la diffusion de la mise a jour: {}", e.getMessage());
        }
    }

    /**
     * Diffuse un evenement de suppression de place.
     */
    public void broadcastPlaceDeleted(Long placeId) {
        try {
            messagingTemplate.convertAndSend(TOPIC_PLACE_UPDATE,
                    new PlaceDeletedPayload(placeId, "DELETED"));
            logger.info("Suppression diffusee pour la place {}", placeId);
        } catch (Exception e) {
            logger.error("Erreur lors de la diffusion de la suppression: {}", e.getMessage());
        }
    }

    /**
     * Payload WebSocket pour signaler une suppression au front.
     */
    public static class PlaceDeletedPayload {
        private Long id;
        private String action;

        public PlaceDeletedPayload(Long id, String action) {
            this.id = id;
            this.action = action;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }
    }
}

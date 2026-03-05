package intellijP.back.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import intellijP.back.dto.ChangementPlaceSSEDTO;
import intellijP.back.dto.ReservationSSEDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class FluxSSEService {

    private static final Logger logger = LoggerFactory.getLogger(FluxSSEService.class);

    private final List<SseEmitter> placesEmitters = new CopyOnWriteArrayList<>();
    private final List<SseEmitter> reservationsEmitters = new CopyOnWriteArrayList<>();

    private final ObjectMapper mapper;

    public FluxSSEService() {
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
    }

    public SseEmitter abonnerPlaces(String niveau, Long idPlace) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        placesEmitters.add(emitter);
        logger.info("Nouveau client SSE /flux/places - total connectés: {}", placesEmitters.size());

        emitter.onCompletion(() -> {
            placesEmitters.remove(emitter);
            logger.debug("Client SSE déconnecté - reste: {}", placesEmitters.size());
        });

        emitter.onTimeout(() -> {
            placesEmitters.remove(emitter);
            logger.debug("Timeout SSE - client retiré, reste: {}", placesEmitters.size());
        });

        emitter.onError(e -> {
            placesEmitters.remove(emitter);
            logger.debug("Erreur SSE client: {}", e.getMessage());
        });

        return emitter;
    }

    public void diffuserChangementPlace(ChangementPlaceSSEDTO dto) {
        if (placesEmitters.isEmpty()) {
            return;
        }

        String jsonPayload;
        try {
            jsonPayload = mapper.writeValueAsString(dto);
        } catch (Exception e) {
            logger.error("Erreur sérialisation SSE: {}", e.getMessage());
            return;
        }

        List<SseEmitter> emittersASupprimer = new CopyOnWriteArrayList<>();

        for (SseEmitter emitter : placesEmitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("place-update")
                        .data(jsonPayload));
            } catch (IOException e) {
                emittersASupprimer.add(emitter);
                logger.debug("Client SSE tombé pendant diffusion - supprimé de la liste");
            }
        }

        placesEmitters.removeAll(emittersASupprimer);
    }

    public int getNbClientsConnectes() {
        return placesEmitters.size();
    }

    public SseEmitter abonnerReservations() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        reservationsEmitters.add(emitter);
        logger.info("Nouveau client SSE /flux/reservations - total connectés: {}", reservationsEmitters.size());

        emitter.onCompletion(() -> {
            reservationsEmitters.remove(emitter);
            logger.debug("Client SSE reservations déconnecté - reste: {}", reservationsEmitters.size());
        });

        emitter.onTimeout(() -> {
            reservationsEmitters.remove(emitter);
            logger.debug("Timeout SSE reservations - client retiré, reste: {}", reservationsEmitters.size());
        });

        emitter.onError(e -> {
            reservationsEmitters.remove(emitter);
            logger.debug("Erreur SSE reservations client: {}", e.getMessage());
        });

        return emitter;
    }

    public void diffuserReservation(ReservationSSEDTO dto) {
        if (reservationsEmitters.isEmpty()) {
            return;
        }

        String jsonPayload;
        try {
            jsonPayload = mapper.writeValueAsString(dto);
        } catch (Exception e) {
            logger.error("Erreur sérialisation SSE reservation: {}", e.getMessage());
            return;
        }

        List<SseEmitter> emittersASupprimer = new CopyOnWriteArrayList<>();

        for (SseEmitter emitter : reservationsEmitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("reservation-created")
                        .data(jsonPayload));
            } catch (IOException e) {
                emittersASupprimer.add(emitter);
                logger.debug("Client SSE reservations tombé pendant diffusion - supprimé de la liste");
            }
        }

        reservationsEmitters.removeAll(emittersASupprimer);
    }

    public int getNbClientsReservations() {
        return reservationsEmitters.size();
    }

}

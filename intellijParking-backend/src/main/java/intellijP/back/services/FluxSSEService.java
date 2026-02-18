package intellijP.back.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import intellijP.back.dto.ChangementPlaceSSEDTO;
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

    // liste thread-safe des clients connectés au flux /flux/places
    private final List<SseEmitter> placesEmitters = new CopyOnWriteArrayList<>();

    // on réutilise le même mapper pour pas en créer à chaque fois
    private final ObjectMapper mapper;

    public FluxSSEService() {
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
    }

    public SseEmitter abonnerPlaces(String niveau, Long idPlace) {
        // timeout Long.MAX_VALUE = connexion maintenue indéfiniment
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        placesEmitters.add(emitter);
        logger.info("Nouveau client SSE /flux/places - total connectés: {}", placesEmitters.size());

        // on retire l'emitter quand le client se déco
        emitter.onCompletion(() -> {
            placesEmitters.remove(emitter);
            logger.debug("Client SSE déconnecté - reste: {}", placesEmitters.size());
        });

        emitter.onTimeout(() -> {
            placesEmitters.remove(emitter);
            logger.debug("Timeout SSE client - reste: {}", placesEmitters.size());
        });

        emitter.onError(e -> {
            placesEmitters.remove(emitter);
            logger.debug("Erreur SSE client: {}", e.getMessage());
        });

        return emitter;
    }

    public void diffuserChangementPlace(ChangementPlaceSSEDTO dto) {
        if (placesEmitters.isEmpty()) {
            // pas de clients connectés, on fait rien
            return;
        }

        // on filtre par niveau si précisé dans le DTO
        String jsonPayload;
        try {
            jsonPayload = mapper.writeValueAsString(dto);
        } catch (Exception e) {
            // TODO: mieux gérer cette erreur
            logger.error("Erreur sérialisation SSE: {}", e.getMessage());
            return;
        }

        // on copie la liste pour éviter les problèmes de concurrence pendant l'itération
        List<SseEmitter> emittersASupprimer = new CopyOnWriteArrayList<>();

        for (SseEmitter emitter : placesEmitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("place-update")
                        .data(jsonPayload));
            } catch (IOException e) {
                // le client s'est déco, on l'enlève
                emittersASupprimer.add(emitter);
                logger.debug("Client SSE tombé pendant diffusion, supprimé");
            }
        }

        placesEmitters.removeAll(emittersASupprimer);
    }

    public int getNbClientsConnectes() {
        return placesEmitters.size();
    }

}

package intellijP.back.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import intellijP.back.dto.ChangementPlaceSSEDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class FluxSSEService {

    private static final Logger logger = LoggerFactory.getLogger(FluxSSEService.class);

    private final List<SseEmitter> placesEmitters = new CopyOnWriteArrayList<>();

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
            logger.debug("Timeout SSE client - reste: {}", placesEmitters.size());
        });
        emitter.onError(e -> {
            placesEmitters.remove(emitter);
            logger.debug("Erreur SSE client: {}", e.getMessage());
        });
        return emitter;
    }

    public void diffuserChangementPlace(ChangementPlaceSSEDTO dto) {
        // TODO: implement diffusion à tous les clients
    }

    public int getNbClientsConnectes() {
        return placesEmitters.size();
    }

}

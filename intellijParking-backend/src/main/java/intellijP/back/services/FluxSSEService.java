package intellijP.back.services;

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

    public SseEmitter abonnerPlaces(String niveau, Long idPlace) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        placesEmitters.add(emitter);
        logger.info("Nouveau client SSE - total: {}", placesEmitters.size());
        emitter.onCompletion(() -> placesEmitters.remove(emitter));
        return emitter;
    }

    public void diffuserChangementPlace(ChangementPlaceSSEDTO dto) {
        // TODO: implement diffusion
    }

    public int getNbClientsConnectes() {
        return placesEmitters.size();
    }

}

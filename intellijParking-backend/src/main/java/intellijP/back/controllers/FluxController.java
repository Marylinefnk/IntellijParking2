package intellijP.back.controllers;

import intellijP.back.services.FluxSSEService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/flux")
public class FluxController {

    private static final Logger logger = LoggerFactory.getLogger(FluxController.class);

    private final FluxSSEService fluxSSEService;

    public FluxController(FluxSSEService fluxSSEService) {
        this.fluxSSEService = fluxSSEService;
    }

    @GetMapping(value = "/places", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter fluxPlaces(
            @RequestParam(required = false) String niveau,
            @RequestParam(required = false) Long idPlace) {

        logger.info("Nouvelle connexion SSE /flux/places - niveau={}, idPlace={}", niveau, idPlace);
        return fluxSSEService.abonnerPlaces(niveau, idPlace);
    }

    @GetMapping(value = "/reservations", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter fluxReservations() {
        logger.info("Nouvelle connexion SSE /flux/reservations");
        return fluxSSEService.abonnerReservations();
    }

}

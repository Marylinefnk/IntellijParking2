package esiag.back.controllers;

import esiag.back.models.Noeud;
import esiag.back.models.ReservationPlace;
import esiag.back.services.GuidageService;
import esiag.back.services.ReservationPlaceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/guidage")
@CrossOrigin(origins = "http://localhost:3000")
public class GuidageController {

    private final GuidageService guidageService;
    private final ReservationPlaceService reservationPlaceService;

    public GuidageController(GuidageService guidageService,
                             ReservationPlaceService reservationPlaceService) {
        this.guidageService = guidageService;
        this.reservationPlaceService = reservationPlaceService;
    }

    @GetMapping("/itineraire/{idReservation}")
    public ResponseEntity<?> getItineraire(@PathVariable Long idReservation) {
        try {
            ReservationPlace reservation = reservationPlaceService
                    .findById(idReservation)
                    .orElse(null);

            if (reservation == null) {
                return ResponseEntity.notFound().build();
            }

            Map<Long, Double> distances = new HashMap<>();
            List<Noeud> chemin = guidageService.calculerChemin(
                    reservation.getPlace(), distances
            );

            List<Map<String, Object>> cheminSimple = new ArrayList<>();
            for (Noeud n : chemin) {
                Map<String, Object> point = new HashMap<>();
                point.put("id", n.getId());
                point.put("nom", n.getNom() != null ? n.getNom() : "Inconnu");
                // Si positionX/Y null, mettre 0 ou -1
                point.put("x", n.getPositionX() != null ? n.getPositionX() : -1);
                point.put("y", n.getPositionY() != null ? n.getPositionY() : -1);
                point.put("type", n.getNoeudType() != null ? n.getNoeudType().toString() : "inconnu");
                cheminSimple.add(point);
            }

            return ResponseEntity.ok(cheminSimple);

        } catch (Exception e) {
            // On log l'erreur pour debugging
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("Erreur : " + e.getMessage());
        }
    }

}
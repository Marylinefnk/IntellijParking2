package esiag.back.controllers;

import esiag.back.models.ReservationPlace;
import esiag.back.models.StatutReservation;
import esiag.back.services.ReservationPlaceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations-place")
public class ReservationPlaceController {

    private final ReservationPlaceService reservationService;

    public ReservationPlaceController(ReservationPlaceService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    public List<ReservationPlace> getAllReservations() {
        return reservationService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationPlace> getReservationById(@PathVariable Long id) {
        return reservationService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/personne/{personneId}")
    public List<ReservationPlace> getReservationsByPersonne(@PathVariable Long personneId) {
        return reservationService.findByPersonne(personneId);
    }

    @GetMapping("/place/{placeId}")
    public List<ReservationPlace> getReservationsByPlace(@PathVariable Long placeId) {
        return reservationService.findByPlace(placeId);
    }

    @GetMapping("/statut/{statut}")
    public List<ReservationPlace> getReservationsByStatut(@PathVariable StatutReservation statut) {
        return reservationService.findByStatut(statut);
    }

    @PostMapping
    public ResponseEntity<ReservationPlace> createReservation(@RequestBody ReservationPlace reservation) {
        try {
            ReservationPlace created = reservationService.create(reservation);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservationPlace> updateReservation(@PathVariable Long id,
                                                              @RequestBody ReservationPlace reservation) {
        try {
            ReservationPlace updated = reservationService.update(id, reservation);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/annuler")
    public ResponseEntity<Void> annulerReservation(@PathVariable Long id) {
        try {
            reservationService.annuler(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/commencer")
    public ResponseEntity<Void> commencerReservation(@PathVariable Long id) {
        try {
            reservationService.commencer(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/terminer")
    public ResponseEntity<Void> terminerReservation(@PathVariable Long id) {
        try {
            reservationService.terminer(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        reservationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

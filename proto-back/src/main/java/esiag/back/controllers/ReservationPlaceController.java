package esiag.back.controllers;

import esiag.back.dto.DtoMapper;
import esiag.back.dto.ReservationPlaceCreateDTO;
import esiag.back.dto.ReservationPlaceResponseDTO;
import esiag.back.models.*;
import esiag.back.services.ReservationPlaceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reservations-place")
public class ReservationPlaceController {

    private final ReservationPlaceService reservationService;
    private final DtoMapper dtoMapper;

    public ReservationPlaceController(ReservationPlaceService reservationService, DtoMapper dtoMapper) {
        this.reservationService = reservationService;
        this.dtoMapper = dtoMapper;
    }

    @GetMapping
    public List<ReservationPlaceResponseDTO> getAllReservations() {
        return reservationService.findAll().stream()
                .map(dtoMapper::toReservationPlaceResponseDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationPlaceResponseDTO> getReservationById(@PathVariable Long id) {
        return reservationService.findById(id)
                .map(dtoMapper::toReservationPlaceResponseDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/personne/{personneId}")
    public List<ReservationPlaceResponseDTO> getReservationsByPersonne(@PathVariable Long personneId) {
        return reservationService.findByPersonne(personneId).stream()
                .map(dtoMapper::toReservationPlaceResponseDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/place/{placeId}")
    public List<ReservationPlaceResponseDTO> getReservationsByPlace(@PathVariable Long placeId) {
        return reservationService.findByPlace(placeId).stream()
                .map(dtoMapper::toReservationPlaceResponseDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/statut/{statut}")
    public List<ReservationPlaceResponseDTO> getReservationsByStatut(@PathVariable StatutReservation statut) {
        return reservationService.findByStatut(statut).stream()
                .map(dtoMapper::toReservationPlaceResponseDTO)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<ReservationPlaceResponseDTO> createReservation(@RequestBody ReservationPlaceCreateDTO createDTO) {
        try {
            ReservationPlace reservation = new ReservationPlace();

            Personne personne = new Personne();
            personne.setId(createDTO.getPersonneId());
            reservation.setPersonne(personne);

            Place place = new Place();
            place.setId(createDTO.getPlaceId());
            reservation.setPlace(place);

            Vehicule vehicule = new Vehicule();
            vehicule.setId(createDTO.getVehiculeId());
            reservation.setVehicule(vehicule);

            reservation.setDateDebut(createDTO.getDateDebut());
            reservation.setDateFin(createDTO.getDateFin());

            ReservationPlace created = reservationService.create(reservation);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(dtoMapper.toReservationPlaceResponseDTO(created));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservationPlaceResponseDTO> updateReservation(@PathVariable Long id,
                                                                          @RequestBody ReservationPlaceCreateDTO updateDTO) {
        try {
            ReservationPlace reservationDetails = new ReservationPlace();
            reservationDetails.setDateDebut(updateDTO.getDateDebut());
            reservationDetails.setDateFin(updateDTO.getDateFin());

            ReservationPlace updated = reservationService.update(id, reservationDetails);
            return ResponseEntity.ok(dtoMapper.toReservationPlaceResponseDTO(updated));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/annuler")
    public ResponseEntity<ReservationPlaceResponseDTO> annulerReservation(@PathVariable Long id) {
        try {
            reservationService.annuler(id);
            return reservationService.findById(id)
                    .map(dtoMapper::toReservationPlaceResponseDTO)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/commencer")
    public ResponseEntity<ReservationPlaceResponseDTO> commencerReservation(@PathVariable Long id) {
        try {
            reservationService.commencer(id);
            return reservationService.findById(id)
                    .map(dtoMapper::toReservationPlaceResponseDTO)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/terminer")
    public ResponseEntity<ReservationPlaceResponseDTO> terminerReservation(@PathVariable Long id) {
        try {
            reservationService.terminer(id);
            return reservationService.findById(id)
                    .map(dtoMapper::toReservationPlaceResponseDTO)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
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

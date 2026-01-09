package esiag.back.controllers;

import esiag.back.dto.DtoMapper;
import esiag.back.dto.ReservationServiceCreateDTO;
import esiag.back.dto.ReservationServiceResponseDTO;
import esiag.back.models.Personne;
import esiag.back.models.ReservationService;
import esiag.back.models.ServiceEntity;
import esiag.back.models.StatutReservation;
import esiag.back.services.ReservationServiceService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reservations-service")
public class ReservationServiceController {

    private final ReservationServiceService reservationServiceService;
    private final DtoMapper dtoMapper;

    public ReservationServiceController(ReservationServiceService reservationServiceService, DtoMapper dtoMapper) {
        this.reservationServiceService = reservationServiceService;
        this.dtoMapper = dtoMapper;
    }

    @GetMapping
    public List<ReservationServiceResponseDTO> getAllReservations() {
        return reservationServiceService.findAll().stream()
                .map(dtoMapper::toReservationServiceResponseDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationServiceResponseDTO> getReservationById(@PathVariable Long id) {
        return reservationServiceService.findById(id)
                .map(dtoMapper::toReservationServiceResponseDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/personne/{personneId}")
    public List<ReservationServiceResponseDTO> getReservationsByPersonne(@PathVariable Long personneId) {
        return reservationServiceService.findByPersonne(personneId).stream()
                .map(dtoMapper::toReservationServiceResponseDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/service/{serviceId}")
    public List<ReservationServiceResponseDTO> getReservationsByService(@PathVariable Long serviceId) {
        return reservationServiceService.findByService(serviceId).stream()
                .map(dtoMapper::toReservationServiceResponseDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/statut/{statut}")
    public List<ReservationServiceResponseDTO> getReservationsByStatut(@PathVariable StatutReservation statut) {
        return reservationServiceService.findByStatut(statut).stream()
                .map(dtoMapper::toReservationServiceResponseDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/periode")
    public List<ReservationServiceResponseDTO> getReservationsByPeriode(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return reservationServiceService.findByDateDebutBetween(debut, fin).stream()
                .map(dtoMapper::toReservationServiceResponseDTO)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<ReservationServiceResponseDTO> createReservation(@RequestBody ReservationServiceCreateDTO createDTO) {
        try {
            ReservationService reservation = new ReservationService();

            Personne personne = new Personne();
            personne.setId(createDTO.getPersonneId());
            reservation.setPersonne(personne);

            ServiceEntity service = new ServiceEntity();
            service.setId(createDTO.getServiceId());
            reservation.setService(service);

            reservation.setDateDebut(createDTO.getDateDebut());
            reservation.setDateFin(createDTO.getDateFin());

            ReservationService created = reservationServiceService.create(reservation);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(dtoMapper.toReservationServiceResponseDTO(created));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservationServiceResponseDTO> updateReservation(@PathVariable Long id,
                                                                            @RequestBody ReservationServiceCreateDTO updateDTO) {
        try {
            ReservationService reservationDetails = new ReservationService();
            reservationDetails.setDateDebut(updateDTO.getDateDebut());
            reservationDetails.setDateFin(updateDTO.getDateFin());

            ReservationService updated = reservationServiceService.update(id, reservationDetails);
            return ResponseEntity.ok(dtoMapper.toReservationServiceResponseDTO(updated));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/annuler")
    public ResponseEntity<ReservationServiceResponseDTO> annulerReservation(@PathVariable Long id) {
        try {
            reservationServiceService.annuler(id);
            return reservationServiceService.findById(id)
                    .map(dtoMapper::toReservationServiceResponseDTO)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/commencer")
    public ResponseEntity<ReservationServiceResponseDTO> commencerReservation(@PathVariable Long id) {
        try {
            reservationServiceService.commencer(id);
            return reservationServiceService.findById(id)
                    .map(dtoMapper::toReservationServiceResponseDTO)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/terminer")
    public ResponseEntity<ReservationServiceResponseDTO> terminerReservation(@PathVariable Long id) {
        try {
            reservationServiceService.terminer(id);
            return reservationServiceService.findById(id)
                    .map(dtoMapper::toReservationServiceResponseDTO)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        reservationServiceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

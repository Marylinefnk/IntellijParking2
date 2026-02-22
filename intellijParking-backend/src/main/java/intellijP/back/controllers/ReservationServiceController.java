package intellijP.back.controllers;

import intellijP.back.dto.DtoMapper;
import intellijP.back.dto.ReservationServiceCreateDTO;
import intellijP.back.dto.ReservationServiceResponseDTO;
import intellijP.back.models.Personne;
import intellijP.back.models.ReservationService;
import intellijP.back.models.ServiceEntity;
import intellijP.back.models.StatutReservation;
import intellijP.back.services.ReservationServiceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(ReservationServiceController.class);

    private final ReservationServiceService reservationServiceService;
    private final DtoMapper dtoMapper;

    public ReservationServiceController(ReservationServiceService reservationServiceService, DtoMapper dtoMapper) {
        this.reservationServiceService = reservationServiceService;
        this.dtoMapper = dtoMapper;
        logger.info("ReservationServiceController initialise");
    }

    @GetMapping
    public List<ReservationServiceResponseDTO> getAllReservations() {
        logger.debug("GET /api/reservations-service");
        List<ReservationServiceResponseDTO> result = reservationServiceService.findAll().stream()
                .map(dtoMapper::toReservationServiceResponseDTO)
                .collect(Collectors.toList());
        logger.info("GET /api/reservations-service - {} reservations retournees", result.size());
        return result;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationServiceResponseDTO> getReservationById(@PathVariable Long id) {
        logger.debug("GET /api/reservations-service/{}", id);
        return reservationServiceService.findById(id)
                .map(reservation -> {
                    logger.debug("GET /api/reservations-service/{} - reservation trouvee", id);
                    return ResponseEntity.ok(dtoMapper.toReservationServiceResponseDTO(reservation));
                })
                .orElseGet(() -> {
                    logger.warn("GET /api/reservations-service/{} - reservation non trouvee", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @GetMapping("/personne/{personneId}")
    public List<ReservationServiceResponseDTO> getReservationsByPersonne(@PathVariable Long personneId) {
        logger.debug("GET /api/reservations-service/personne/{}", personneId);
        List<ReservationServiceResponseDTO> result = reservationServiceService.findByPersonne(personneId).stream()
                .map(dtoMapper::toReservationServiceResponseDTO)
                .collect(Collectors.toList());
        logger.debug("GET /api/reservations-service/personne/{} - {} reservations trouvees", personneId, result.size());
        return result;
    }

    @GetMapping("/service/{serviceId}")
    public List<ReservationServiceResponseDTO> getReservationsByService(@PathVariable Long serviceId) {
        logger.debug("GET /api/reservations-service/service/{}", serviceId);
        List<ReservationServiceResponseDTO> result = reservationServiceService.findByService(serviceId).stream()
                .map(dtoMapper::toReservationServiceResponseDTO)
                .collect(Collectors.toList());
        logger.debug("GET /api/reservations-service/service/{} - {} reservations trouvees", serviceId, result.size());
        return result;
    }

    @GetMapping("/statut/{statut}")
    public List<ReservationServiceResponseDTO> getReservationsByStatut(@PathVariable StatutReservation statut) {
        logger.debug("GET /api/reservations-service/statut/{}", statut);
        List<ReservationServiceResponseDTO> result = reservationServiceService.findByStatut(statut).stream()
                .map(dtoMapper::toReservationServiceResponseDTO)
                .collect(Collectors.toList());
        logger.debug("GET /api/reservations-service/statut/{} - {} reservations trouvees", statut, result.size());
        return result;
    }

    @GetMapping("/periode")
    public List<ReservationServiceResponseDTO> getReservationsByPeriode(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        logger.debug("GET /api/reservations-service/periode - debut={}, fin={}", debut, fin);
        List<ReservationServiceResponseDTO> result = reservationServiceService.findByDateDebutBetween(debut, fin).stream()
                .map(dtoMapper::toReservationServiceResponseDTO)
                .collect(Collectors.toList());
        logger.debug("GET /api/reservations-service/periode - {} reservations trouvees", result.size());
        return result;
    }

    @PostMapping
    public ResponseEntity<ReservationServiceResponseDTO> createReservation(@RequestBody ReservationServiceCreateDTO createDTO) {
        logger.debug("POST /api/reservations-service - personne={}, service={}",
            createDTO.getPersonneId(), createDTO.getServiceId());
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
            logger.info("POST /api/reservations-service - reservation creee id={}", created.getId());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(dtoMapper.toReservationServiceResponseDTO(created));
        } catch (RuntimeException e) {
            logger.warn("POST /api/reservations-service - echec: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservationServiceResponseDTO> updateReservation(@PathVariable Long id,
                                                                            @RequestBody ReservationServiceCreateDTO updateDTO) {
        logger.debug("PUT /api/reservations-service/{}", id);
        try {
            ReservationService reservationDetails = new ReservationService();
            reservationDetails.setDateDebut(updateDTO.getDateDebut());
            reservationDetails.setDateFin(updateDTO.getDateFin());

            ReservationService updated = reservationServiceService.update(id, reservationDetails);
            logger.info("PUT /api/reservations-service/{} - reservation mise a jour", id);
            return ResponseEntity.ok(dtoMapper.toReservationServiceResponseDTO(updated));
        } catch (RuntimeException e) {
            logger.warn("PUT /api/reservations-service/{} - echec: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/annuler")
    public ResponseEntity<ReservationServiceResponseDTO> annulerReservation(@PathVariable Long id) {
        logger.debug("POST /api/reservations-service/{}/annuler", id);
        try {
            reservationServiceService.annuler(id);
            logger.info("POST /api/reservations-service/{}/annuler - reservation annulee", id);
            return reservationServiceService.findById(id)
                    .map(dtoMapper::toReservationServiceResponseDTO)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (RuntimeException e) {
            logger.warn("POST /api/reservations-service/{}/annuler - echec: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/commencer")
    public ResponseEntity<ReservationServiceResponseDTO> commencerReservation(@PathVariable Long id) {
        logger.debug("POST /api/reservations-service/{}/commencer", id);
        try {
            reservationServiceService.commencer(id);
            logger.info("POST /api/reservations-service/{}/commencer - reservation demarree", id);
            return reservationServiceService.findById(id)
                    .map(dtoMapper::toReservationServiceResponseDTO)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (RuntimeException e) {
            logger.warn("POST /api/reservations-service/{}/commencer - echec: {}", id, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/terminer")
    public ResponseEntity<ReservationServiceResponseDTO> terminerReservation(@PathVariable Long id) {
        logger.debug("POST /api/reservations-service/{}/terminer", id);
        try {
            reservationServiceService.terminer(id);
            logger.info("POST /api/reservations-service/{}/terminer - reservation terminee", id);
            return reservationServiceService.findById(id)
                    .map(dtoMapper::toReservationServiceResponseDTO)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (RuntimeException e) {
            logger.warn("POST /api/reservations-service/{}/terminer - echec: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        logger.debug("DELETE /api/reservations-service/{}", id);
        try {
            reservationServiceService.delete(id);
            logger.info("DELETE /api/reservations-service/{} - reservation supprimee", id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            logger.warn("DELETE /api/reservations-service/{} - echec: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}

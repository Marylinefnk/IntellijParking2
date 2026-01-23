package esiag.back.controllers;

import esiag.back.dto.ReservationPlaceCreateDTO;
import esiag.back.dto.ReservationPlaceResponseDTO;
import esiag.back.exceptions.ResourceNotFoundException;
import esiag.back.models.StatutReservation;
import esiag.back.services.ReservationPlaceService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/reservations-place")
public class ReservationPlaceController {

    private final ReservationPlaceService reservationService;

    public ReservationPlaceController(ReservationPlaceService reservationService) {
        this.reservationService = reservationService;
    }

    // ENDPOINTS DE LECTURE

    @GetMapping
    public List<ReservationPlaceResponseDTO> getAllReservations() {
        return reservationService.findAllDTO();
    }

    @GetMapping("/{id}")
    public ReservationPlaceResponseDTO getReservationById(@PathVariable Long id) {
        return reservationService.findByIdDTO(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", id));
    }

    @GetMapping("/personne/{personneId}")
    public List<ReservationPlaceResponseDTO> getReservationsByPersonne(@PathVariable Long personneId) {
        return reservationService.findByPersonneDTO(personneId);
    }

    @GetMapping("/place/{placeId}")
    public List<ReservationPlaceResponseDTO> getReservationsByPlace(@PathVariable Long placeId) {
        return reservationService.findByPlaceDTO(placeId);
    }

    @GetMapping("/statut/{statut}")
    public List<ReservationPlaceResponseDTO> getReservationsByStatut(@PathVariable StatutReservation statut) {
        return reservationService.findByStatutDTO(statut);
    }

    // ENDPOINTS DE CREATION/MODIFICATION

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationPlaceResponseDTO createReservation(@RequestBody ReservationPlaceCreateDTO createDTO) {
        return reservationService.createDTO(createDTO);
    }

    @PutMapping("/{id}")
    public ReservationPlaceResponseDTO updateReservation(@PathVariable Long id,
                                                          @RequestBody ReservationPlaceCreateDTO updateDTO) {
        return reservationService.updateDTO(id, updateDTO);
    }

    // ==================== ENDPOINTS D'ACTION SUR LE CYCLE DE VIE ====================

    @PostMapping("/{id}/annuler")
    public ReservationPlaceResponseDTO annulerReservation(@PathVariable Long id) {
        return reservationService.annulerDTO(id);
    }

    @PostMapping("/{id}/commencer")
    public ReservationPlaceResponseDTO commencerReservation(@PathVariable Long id) {
        return reservationService.commencerDTO(id);
    }

    @PostMapping("/{id}/terminer")
    public ReservationPlaceResponseDTO terminerReservation(@PathVariable Long id) {
        return reservationService.terminerDTO(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReservation(@PathVariable Long id) {
        reservationService.deleteDTO(id);
    }
}

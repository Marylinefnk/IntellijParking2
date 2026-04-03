package intellijP.back.controllers;

import intellijP.back.dto.ReservationPlaceCreateDTO;
import intellijP.back.dto.ReservationPlaceResponseDTO;
import intellijP.back.exceptions.ResourceNotFoundException;
import intellijP.back.models.StatutReservation;
import intellijP.back.services.ReservationPlaceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/reservations-place")
public class ReservationPlaceController {

    private final ReservationPlaceService reservationService;

    public ReservationPlaceController(ReservationPlaceService reservationService) {
        this.reservationService = reservationService;
    }
    @GetMapping
    public List<ReservationPlaceResponseDTO> getAllReservations() {
        return reservationService.findAllDTO();
    }

    @GetMapping("/page")
    public Page<ReservationPlaceResponseDTO> getAllReservationsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return reservationService.findAllDTOPaged(
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dateDebut")));
    }

    @GetMapping("/personne/{personneId}/page")
    public Page<ReservationPlaceResponseDTO> getReservationsByPersonnePaged(
            @PathVariable Long personneId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return reservationService.findByPersonneDTOPaged(personneId,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dateDebut")));
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

    //le endpoint pour la carte du p

    @GetMapping("/statut-carte")
    @CrossOrigin(origins = "http://localhost:3000")
    public Map<String, String> getStatutCarte() {
        List<ReservationPlaceResponseDTO> actives = reservationService
                .findByStatutDTO(StatutReservation.EN_COURS);

        Map<String, String> result = new HashMap<>();
        for (ReservationPlaceResponseDTO r : actives) {
            result.put(r.getPlace().getNumero(), r.getStatut().name());
        }
        return result;
    }

    @GetMapping("/ma-place/{personneId}")
    public ResponseEntity<Map<String, String>> getMaPlace(@PathVariable Long personneId) {
        List<ReservationPlaceResponseDTO> reservations = reservationService
                .findByPersonneDTO(personneId);

        return reservations.stream()
                .filter(r -> r.getStatut() == StatutReservation.EN_COURS)
                .findFirst()
                .map(r -> {
                    Map<String, String> result = new HashMap<>();
                    result.put("placeId", r.getPlace().getNumero());
                    return ResponseEntity.ok(result);
                })
                .orElse(ResponseEntity.noContent().build());
    }
}


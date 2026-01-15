package esiag.back.dto;

import esiag.back.models.StatutPlace;
import esiag.back.models.TypePlace;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlaceAvailabilityDTO {
    private Long id;
    private String numero;
    private TypePlace type;
    private StatutPlace statutActuel; // Computed status based on current time
    private Double positionX;
    private Double positionY;
    private Long zoneId;
    private String zoneNom;

    // Current reservation info (if place is reserved NOW)
    private ReservationInfo reservationEnCours;

    // Upcoming reservations (so users know when it's booked)
    private List<ReservationInfo> prochainesReservations;

    // Is the place available for reservation (even if it has future bookings)?
    private boolean disponiblePourReservation;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReservationInfo {
        private Long id;
        private LocalDateTime dateDebut;
        private LocalDateTime dateFin;
        private String personneNom;
        private String vehiculeImmatriculation;
    }
}

package intellijP.back.dto;

import intellijP.back.models.StatutPlace;
import intellijP.back.models.TypePlace;
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
    private StatutPlace statutActuel;
    private Double positionX;
    private Double positionY;
    private Long zoneId;
    private String zoneNom;


    private ReservationInfo reservationEnCours;
    private List<ReservationInfo> prochainesReservations;
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

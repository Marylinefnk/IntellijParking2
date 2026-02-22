package intellijP.back.dto;

import intellijP.back.models.StatutReservation;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationPlaceResponseDTO {
    private Long id;
    private PersonneDTO personne;
    private PlaceDTO place;
    private VehiculeDTO vehicule;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private StatutReservation statut;
}

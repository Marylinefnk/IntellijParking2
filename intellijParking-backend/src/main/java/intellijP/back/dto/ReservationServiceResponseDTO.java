package intellijP.back.dto;

import intellijP.back.models.StatutReservation;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationServiceResponseDTO {
    private Long id;
    private PersonneDTO personne;
    private ServiceDTO service;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private StatutReservation statut;
}

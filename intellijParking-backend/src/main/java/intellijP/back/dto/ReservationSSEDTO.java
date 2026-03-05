package intellijP.back.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationSSEDTO {

    private String type = "nouvelle_reservation";
    private LocalDateTime dateEvenement;

    private Long idReservation;

    private String numeroPlace;
    private String niveauPlace;

    private String immatriculation;
    private String typeVehicule;

    private String nomPersonne;
    private String prenomPersonne;

    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private String statut;

    private int compteur;

}

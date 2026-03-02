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
public class ChangementPlaceSSEDTO {

    private String type = "changement_statut_place";
    private LocalDateTime dateEvenement;

    private Long idPlace;
    private String numero;
    private String niveau;

    private String ancienStatut;
    private String nouveauStatut;

    private String cause;

    private Long idVehicule;
    private String immatriculation;
    private String typeVehicule;

    private Long idReservation;
    private String typeReservation;
    private String statutResaAvant;
    private String statutResaApres;

}

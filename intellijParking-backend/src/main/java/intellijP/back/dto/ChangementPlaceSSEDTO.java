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

    // CAPTEUR, RESERVATION ou STATIONNEMENT
    private String cause;

    // infos vehicule - null si pas de vehicule identifié
    private Long idVehicule;
    private String immatriculation;
    private String typeVehicule;

    // infos reservation - null si pas de resa liee
    private Long idReservation;
    // PREEXISTANTE ou CREE_A_LA_VOLEE
    private String typeReservation;
    private String statutResaAvant;
    private String statutResaApres;

}

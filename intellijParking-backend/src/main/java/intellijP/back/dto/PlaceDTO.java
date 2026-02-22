package intellijP.back.dto;

import intellijP.back.models.StatutPlace;
import intellijP.back.models.TypePlace;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlaceDTO {
    private Long id;
    private String numero;
    private TypePlace type;
    private StatutPlace statut;
    private Double positionX;
    private Double positionY;
    private ZoneDTO zone;
}

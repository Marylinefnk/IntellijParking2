package esiag.back.dto;

import esiag.back.models.StatutPlace;
import esiag.back.models.TypePlace;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlaceCreateDTO {
    private String numero;
    private TypePlace type;
    private StatutPlace statut;
    private Double positionX;
    private Double positionY;
    private Long zoneId;
}

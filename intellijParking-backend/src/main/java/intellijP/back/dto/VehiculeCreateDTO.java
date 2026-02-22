package intellijP.back.dto;

import intellijP.back.models.TypeVehicule;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VehiculeCreateDTO {
    private String immatriculation;
    private TypeVehicule typeVehicule;
    private Long personneId;
}

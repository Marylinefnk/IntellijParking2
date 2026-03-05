package intellijP.back.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InitialisationResultatDTO {

    private int nbPersonnesCreees;
    private int nbVehiculesCreees;
    private int nbCapteursCreees;

}

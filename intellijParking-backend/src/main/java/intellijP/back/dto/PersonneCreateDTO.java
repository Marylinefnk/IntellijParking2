package intellijP.back.dto;

import intellijP.back.models.TypePersonne;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PersonneCreateDTO {
    private String nom;
    private String prenom;
    private String mail;
    private TypePersonne typePersonne;
}

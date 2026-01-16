package esiag.back.dto;

import esiag.back.models.TypePersonne;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PersonneCreateDTO {
    private String nom;
    private String prenom;
    private String mail;
    private String password;
    private TypePersonne typePersonne;
}

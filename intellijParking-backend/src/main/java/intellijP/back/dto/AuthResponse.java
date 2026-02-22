package intellijP.back.dto;

import intellijP.back.models.TypePersonne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String token;
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private TypePersonne typePersonne;
}

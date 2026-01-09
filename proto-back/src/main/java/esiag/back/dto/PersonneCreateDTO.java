package esiag.back.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PersonneCreateDTO {
    private String nom;
    private String prenom;
    private String mail;
}

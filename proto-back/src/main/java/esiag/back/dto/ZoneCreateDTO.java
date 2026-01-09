package esiag.back.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ZoneCreateDTO {
    private String nom;
    private String description;
}

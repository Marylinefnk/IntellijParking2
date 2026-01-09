package esiag.back.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ZoneDTO {
    private Long id;
    private String nom;
    private String description;
}

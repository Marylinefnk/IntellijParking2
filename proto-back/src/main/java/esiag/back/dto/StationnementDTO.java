package esiag.back.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StationnementDTO {
    private Long id;
    private LocalDateTime dateEntree;
    private LocalDateTime dateSortie;
    private Double tarif;
    private Integer dureeMin;
    private VehiculeDTO vehicule;
    private PlaceDTO place;
}

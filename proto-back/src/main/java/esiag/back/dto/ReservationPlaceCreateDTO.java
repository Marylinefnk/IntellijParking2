package esiag.back.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationPlaceCreateDTO {
    private Long personneId;
    private Long placeId;
    private Long vehiculeId;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
}

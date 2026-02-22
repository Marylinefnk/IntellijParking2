package intellijP.back.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationServiceCreateDTO {
    private Long personneId;
    private Long serviceId;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
}

package esiag.back.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StationnementCreateDTO {
    private Long vehiculeId;
    private Long placeId;
    private LocalDateTime dateEntree;
}

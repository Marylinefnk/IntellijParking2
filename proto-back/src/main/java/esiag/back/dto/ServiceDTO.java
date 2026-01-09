package esiag.back.dto;

import esiag.back.models.TypeService;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceDTO {
    private Long id;
    private TypeService typeService;
    private String description;
}

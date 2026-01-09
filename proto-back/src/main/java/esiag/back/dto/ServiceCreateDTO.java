package esiag.back.dto;

import esiag.back.models.TypeService;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceCreateDTO {
    private TypeService typeService;
    private String description;
}

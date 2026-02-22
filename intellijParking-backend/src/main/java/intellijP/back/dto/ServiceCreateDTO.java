package intellijP.back.dto;

import intellijP.back.models.TypeService;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceCreateDTO {
    private TypeService typeService;
    private String description;
}

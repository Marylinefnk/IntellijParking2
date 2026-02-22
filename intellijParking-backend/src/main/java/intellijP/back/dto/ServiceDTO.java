package intellijP.back.dto;

import intellijP.back.models.TypeService;
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

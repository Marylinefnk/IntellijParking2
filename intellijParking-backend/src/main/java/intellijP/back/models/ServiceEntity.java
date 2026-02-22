package intellijP.back.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "service")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_service")
    private Long id;

    @Enumerated(EnumType.STRING)
    private TypeService typeService;

    private String description;

    @OneToMany(mappedBy = "service")
    @JsonManagedReference("service-reservations")
    private List<ReservationService> reservations;

}


package intellijP.back.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "intersection")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Intersection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_intersection")
    private Long id;

    private Double positionX;
    private Double positionY;

    @Enumerated(EnumType.STRING)
    private TypeIntersection typeIntersection;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_zone")
    @JsonBackReference("zone-intersections")
    private Zone zone;
}


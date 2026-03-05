package intellijP.back.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "place")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"zone", "stationnements", "reservations"})
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_place")
    private Long id;

    private String numero;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_place")
    private TypePlace type;

    @Enumerated(EnumType.STRING)
    private StatutPlace statut;

    private Double positionX;
    private Double positionY;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_zone")
    @JsonBackReference("zone-places")
    private Zone zone;

    @OneToMany(mappedBy = "place")
    @JsonManagedReference("place-stationnements")
    private List<Stationnement> stationnements;

    @OneToMany(mappedBy = "place")
    @JsonManagedReference("place-reservations")
    private List<ReservationPlace> reservations;
}


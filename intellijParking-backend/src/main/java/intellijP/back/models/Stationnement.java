package intellijP.back.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "stationnement")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Stationnement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_stationnement")
    private Long id;

    private LocalDateTime dateEntree;
    private LocalDateTime dateSortie;
    private Double tarif;
    private Integer dureeMin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_vehicule")
    @JsonBackReference("vehicule-stationnements")
    private Vehicule vehicule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_place")
    @JsonBackReference("place-stationnements")
    private Place place;


}


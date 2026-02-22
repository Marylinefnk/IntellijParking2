package intellijP.back.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "vehicule")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Vehicule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vehicule")
    private Long id;

    private String immatriculation;

    @Enumerated(EnumType.STRING)
    private TypeVehicule typeVehicule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_personne")
    @JsonBackReference("personne-vehicules")
    private Personne personne;

    @OneToMany(mappedBy = "vehicule")
    @JsonManagedReference("vehicule-stationnements")
    private List<Stationnement> stationnements;

    @OneToMany(mappedBy = "vehicule")
    @JsonManagedReference("vehicule-reservations")
    private List<ReservationPlace> reservations;


}


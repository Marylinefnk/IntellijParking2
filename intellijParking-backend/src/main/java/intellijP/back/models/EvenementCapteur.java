package intellijP.back.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "evenement_capteur")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EvenementCapteur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evenement_capteur")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_capteur")
    @JsonBackReference("capteur-evenements")
    private Capteur capteur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_place")
    @JsonBackReference("place-evenements")
    private Place place;

    // le vehicule detecte par la plaque - peut etre null dans certains cas limites
    // TODO: gérer le cas où la plaque n'est pas reconnue
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_vehicule")
    @JsonBackReference("vehicule-evenements")
    private Vehicule vehicule;

    // la reservation liée - null si le vehicule n'avait pas de reservation (créée à la volée)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_reservation")
    @JsonBackReference("reservation-evenements")
    private ReservationPlace reservation;

    @Column(name = "presence_detectee")
    private boolean presenceDetectee;

    @Column(name = "date_evenement")
    private LocalDateTime dateEvenement;

    @Enumerated(EnumType.STRING)
    private SourceEvenement source = SourceEvenement.SIMULATION;

}

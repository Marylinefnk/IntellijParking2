package intellijP.back.models;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "reservation_place", indexes = {
    @Index(name = "idx_resa_place_id", columnList = "id_place"),
    @Index(name = "idx_resa_personne_id", columnList = "id_personne"),
    @Index(name = "idx_resa_vehicule_id", columnList = "id_vehicule"),
    @Index(name = "idx_resa_statut", columnList = "statut"),
    @Index(name = "idx_resa_dates", columnList = "date_debut, date_fin"),
    @Index(name = "idx_resa_place_statut_dates", columnList = "id_place, statut, date_debut, date_fin")
})
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationPlace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reservation_place")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_personne")
    @JsonBackReference("personne-reservations")
    private Personne personne;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_place")
    @JsonBackReference("place-reservations")
    private Place place;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_vehicule")
    @JsonBackReference("vehicule-reservations")
    private Vehicule vehicule;

    @Column(name = "date_debut", nullable = false)
    private LocalDateTime dateDebut;

    @Column(name = "date_fin", nullable = false)
    private LocalDateTime dateFin;

    @Enumerated(EnumType.STRING)
    private StatutReservation statut;

}

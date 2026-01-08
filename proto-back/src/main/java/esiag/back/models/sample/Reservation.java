package esiag.back.models.sample;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reservation")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reservation")
    private Integer id_reservation;

    @ManyToOne
    @JoinColumn(name = "id_personne")
    private Personne personne;

    @ManyToOne
    @JoinColumn(name = "id_place")
    private Place place;

    @ManyToOne
    @JoinColumn(name = "id_vehicule")
    private Vehicule vehicule;

    @Column(name = "date_debut")
    LocalDateTime date_debut;
    @Column(name = "date_fin")
    private LocalDateTime date_fin;

    @Enumerated(EnumType.STRING)
    @Column(name = "typeReservation")
    private ReservationType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut")
    private ReservationStatut statut;







}

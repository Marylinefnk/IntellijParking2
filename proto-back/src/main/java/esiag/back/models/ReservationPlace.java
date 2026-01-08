package esiag.back.models;


import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "reservation_place")
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

    public ReservationPlace() {
    }

    public ReservationPlace(Personne personne, Place place, Vehicule vehicule,
                           LocalDateTime dateDebut, LocalDateTime dateFin, StatutReservation statut) {
        this.personne = personne;
        this.place = place;
        this.vehicule = vehicule;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.statut = statut;
    }

}

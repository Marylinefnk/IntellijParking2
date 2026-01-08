package esiag.back.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "reservation_service")
public class ReservationService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reservation_service")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_personne")
    @JsonBackReference("personne-reservations-service")
    private Personne personne;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_service")
    @JsonBackReference("service-reservations")
    private ServiceEntity service;

    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;

    @Enumerated(EnumType.STRING)
    private StatutReservation statut;

    public ReservationService() {
    }

    public ReservationService(Personne personne, ServiceEntity service,
                            LocalDateTime dateDebut, LocalDateTime dateFin, StatutReservation statut) {
        this.personne = personne;
        this.service = service;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.statut = statut;
    }

}


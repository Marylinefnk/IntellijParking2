package esiag.back.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "stationnement")
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

    public Stationnement() {
    }

    public Stationnement(LocalDateTime dateEntree, Vehicule vehicule, Place place) {
        this.dateEntree = dateEntree;
        this.vehicule = vehicule;
        this.place = place;
    }

}


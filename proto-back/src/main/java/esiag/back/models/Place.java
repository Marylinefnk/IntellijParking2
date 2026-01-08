package esiag.back.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "place")
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

    public Place() {
    }

    public Place(String numero, TypePlace type, StatutPlace statut, Double positionX, Double positionY) {
        this.numero = numero;
        this.type = type;
        this.statut = statut;
        this.positionX = positionX;
        this.positionY = positionY;
    }

}


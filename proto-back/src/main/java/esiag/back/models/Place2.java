package esiag.back.models;

import javax.persistence.*;

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
    private Zone zone;

    @OneToMany(mappedBy = "place")
    private List<Stationnement> stationnements;

    @OneToMany(mappedBy = "place")
    private List<ReservationPlace> reservations;
}


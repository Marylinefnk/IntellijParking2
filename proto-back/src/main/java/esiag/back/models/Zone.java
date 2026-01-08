package esiag.back.models;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "zone")
public class Zone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_zone")
    private Long id;

    private String nom;
    private Integer etage;

    @OneToMany(mappedBy = "zone")
    private List<Place> places;

    @OneToMany(mappedBy = "zone")
    private List<Intersection> intersections;
}

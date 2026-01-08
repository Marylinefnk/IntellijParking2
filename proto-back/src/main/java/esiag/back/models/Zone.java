package esiag.back.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "zone")
public class Zone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_zone")
    private Long id;

    @Column(name = "nom_zone")
    private String nom;

    private String description;

    @OneToMany(mappedBy = "zone")
    @JsonManagedReference("zone-places")
    private List<Place> places;

    @OneToMany(mappedBy = "zone")
    @JsonManagedReference("zone-intersections")
    private List<Intersection> intersections;

    public Zone() {
    }

    public Zone(String nom, String description) {
        this.nom = nom;
        this.description = description;
    }

}

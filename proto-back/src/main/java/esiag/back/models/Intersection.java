package esiag.back.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "intersection")
public class Intersection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_intersection")
    private Long id;

    private Double positionX;
    private Double positionY;

    @Enumerated(EnumType.STRING)
    private TypeIntersection typeIntersection;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_zone")
    @JsonBackReference("zone-intersections")
    private Zone zone;

    public Intersection() {
    }

    public Intersection(Double positionX, Double positionY, TypeIntersection typeIntersection) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.typeIntersection = typeIntersection;
    }

}


package esiag.back.models.sample;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "allee")
public class Allee {
    @Id
    @Column(name = "id_allee")
    private Integer id_allee;

    @ManyToOne
    @JoinColumn(name = "id_intersection_debut")
    private Intersection id_intersection_debut;

    @ManyToOne
    @JoinColumn(name = "id_intersection_fin")
    private Intersection id_intersection_fin;

    @Column(name = "distance")
    private double distance;
    @Column(name = "largeur")
    private double largeur;

    @Enumerated(EnumType.STRING)
    @Column(name = "sens_circulation")
    private SensCirculation sens_circulation;

    @Column(name = "nom_allee")
    private String nom_allee;
}
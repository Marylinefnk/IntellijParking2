package esiag.back.models;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "arete")
public class Arete {
    @Id
    @Column(name = "id_arete")
    private Integer id_arete;

    @ManyToOne
    @JoinColumn(name = "noeud_source")
    private Noeud noeudSource;

    @ManyToOne
    @JoinColumn(name = "noeud_destination")
    private Noeud noeudDestination;

    //ajouter poids de l'arete




}
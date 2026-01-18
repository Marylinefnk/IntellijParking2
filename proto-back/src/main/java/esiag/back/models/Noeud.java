package esiag.back.models;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "Noeud")
public class Noeud {

    @Id
    @Column(name = "id_noeud")
    private Integer id_zone;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_noeud")
    private NoeudType noeudType;

    @Column(name = "numero_noeud")
    private int numeroNoeud;

    @Column(name = "niveau_noeud")
    private int niveauNoeud;

    @Enumerated(EnumType.STRING)
    @Column(name = "sens_noeud")
    private SensNoeud sensNoeud;

    @Column(name = "nom")
    private int nomNoeud;

    //ajouter position_x et position_y
    //id_place;
    //id_zone;
}
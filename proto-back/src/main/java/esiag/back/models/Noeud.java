package esiag.back.models;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Noeud")
public class Noeud {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_noeud")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_noeud")
    private NoeudType noeudType;

    @Column(name = "niveau_noeud")
    private int niveauNoeud;

    @Enumerated(EnumType.STRING)
    @Column(name = "sens_noeud")
    private SensNoeud sensNoeud;

    @Column(name = "nom")
    private String nom;

    @Column(name = "position_x")
    private Double positionX;

    @Column(name = "position_y")
    private Double positionY;

}
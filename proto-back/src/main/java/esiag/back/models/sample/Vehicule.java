package esiag.back.models.sample;

import java.time.LocalDateTime;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "vehicule")
public class Vehicule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idVehicule")
    private Integer id_vehicule;

    @Column(name = "immatriculation")
    private String immatriculation;

    @Column(name = "typeVehicule")
    private String type_vehicule;

    @ManyToOne
    @JoinColumn(name = "id_personne")
    private Personne personne;





}
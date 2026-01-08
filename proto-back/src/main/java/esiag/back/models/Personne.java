package esiag.back.models;

import javax.persistence.*;

@Entity
@Table(name = "personne")
public class Personne {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_personne")
    private Long id;

    @Column(name = "nom_personne")
    private String nom;

    @Column(name = "prenom_personne")
    private String prenom;

    @Column(name = "mail")
    private String mail;

    @OneToMany(mappedBy = "personne")
    private List<Vehicule> vehicules;

    @OneToMany(mappedBy = "personne")
    private List<ReservationPlace> reservationsPlace;

    @OneToMany(mappedBy = "personne")
    private List<ReservationService> reservationsService;
}


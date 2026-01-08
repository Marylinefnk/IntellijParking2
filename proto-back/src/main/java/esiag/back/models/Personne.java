package esiag.back.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Setter
@Getter
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
    @JsonManagedReference("personne-vehicules")
    private List<Vehicule> vehicules;

    @OneToMany(mappedBy = "personne")
    @JsonManagedReference("personne-reservations")
    private List<ReservationPlace> reservationsPlace;


    @OneToMany(mappedBy = "personne")
    @JsonManagedReference("personne-reservations-service")
    private List<ReservationService> reservationsService;

    public Personne() {
    }

    public Personne(String nom, String prenom, String mail) {
        this.nom = nom;
        this.prenom = prenom;
        this.mail = mail;
    }

}


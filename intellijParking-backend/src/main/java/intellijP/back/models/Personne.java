package intellijP.back.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "personne")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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

    @Column(name = "password")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_personne")
    private TypePersonne typePersonne = TypePersonne.VISITEUR;

    @OneToMany(mappedBy = "personne")
    @JsonManagedReference("personne-vehicules")
    private List<Vehicule> vehicules;

    @OneToMany(mappedBy = "personne")
    @JsonManagedReference("personne-reservations")
    private List<ReservationPlace> reservationsPlace;


    @OneToMany(mappedBy = "personne")
    @JsonManagedReference("personne-reservations-service")
    private List<ReservationService> reservationsService;


}


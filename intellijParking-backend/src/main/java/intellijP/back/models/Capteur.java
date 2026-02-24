package intellijP.back.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "capteur")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Capteur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_capteur")
    private Long id;

    // une place = un seul capteur
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_place", unique = true)
    @JsonBackReference("place-capteur")
    private Place place;

    @Enumerated(EnumType.STRING)
    @Column(name = "etat_capteur")
    private EtatCapteur etatCapteur = EtatCapteur.ACTIF;

    @Column(name = "presence_detectee")
    private boolean presenceDetectee = false;

    @Column(name = "date_dernier_signal")
    private LocalDateTime dateDernierSignal;

    // le vehicule dont on a lu la plaque, null si aucun vehicule present
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_vehicule_detecte")
    @JsonBackReference("vehicule-capteurs")
    private Vehicule vehiculeDetecte;

}

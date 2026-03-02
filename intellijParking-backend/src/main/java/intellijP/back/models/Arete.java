package intellijP.back.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_arete")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "noeud_source")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Noeud noeudSource;

    @ManyToOne
    @JoinColumn(name = "noeud_destination")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Noeud noeudDestination;

    @Column(name = "poids")
    private Double poids;
}
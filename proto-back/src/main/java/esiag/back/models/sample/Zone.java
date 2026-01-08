package esiag.back.models.sample;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "zone")
public class Zone {

    @Id
    @Column(name = "id_zone")
    private Integer id_zone;


    @Column(name = "nom")
    private String nom_zone;

    @Column(name = "etage")
    private int etage;
}
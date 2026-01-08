package esiag.back.models.sample;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "place")
public class Place {

    @Id
    @Column(name="id_place")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_place;

    @Column(name= "numero")
    private String numero;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_place")
    private PlaceType type_place;

    @Enumerated(EnumType.STRING)
    @Column(name = "etat")
    private EtatPlaceType etat_place;

    @Column(name= "position_x")
    private double position_x;
    @Column(name= "position_y")
    private double position_y;

    @ManyToOne
    @JoinColumn(name = "id_zone")
    private Zone id_zone;


    public int getId_place() {
        return id_place;
    }
    public void setId_place(int id_place) {}
    public String getNumero() {
        return numero;
    }
    public void setNumero(String numero) {}

   /* public String getEtat() {
        return etat;
    }
    public void setEtat(String etat) {}
    public String getType() {
        return type;
    }
    public void setType(String type) {} */




}
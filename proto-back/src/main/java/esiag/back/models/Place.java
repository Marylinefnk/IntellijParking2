package esiag.back.models;

import javax.persistence.*;

@Entity
@Table(name = "place")
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_place")
    private Integer idPlace;

    @Column(name = "numero")
    private String numero;

    @Column(name = "type_place")
    private String typePlace;

    @Column(name = "etat")
    private String etat;

    // getters/setters
    public Integer getIdPlace() { return idPlace; }
    public void setIdPlace(Integer idPlace) { this.idPlace = idPlace; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getTypePlace() { return typePlace; }
    public void setTypePlace(String typePlace) { this.typePlace = typePlace; }

    public String getEtat() { return etat; }
    public void setEtat(String etat) { this.etat = etat; }
}

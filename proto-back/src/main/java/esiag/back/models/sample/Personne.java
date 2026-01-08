package esiag.back.models.sample;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "personne")
public class Personne {

    @Id
    @Column(name="id_personne")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id_personne;

    @Column(name= "nom_personne")
    private String nom;

    @Column(name = "prenom_personne")
    private String prenom;

    @Column(name = "mail")
    private String email;

    /*public Personne(int id_personne, String nom, String prenom, String email) {
            this.id_personne = id_personne;
            this.nom = nom;
            this.prenom = prenom;
            this.email = email;
    } */

    public int getId_personne() {
        return id_personne;
    }

    public String getNom() {
        return nom;
    }
    public String getPrenom() {
        return prenom;
    }
    public String getEmail() {return  email;}

    public void setId_personne(int id_personne) {}
    public void setNom(String nom) {this.nom = nom;}
    public void setPrenom(String prenom) {this.prenom = prenom;}
    public void setEmail(String email) {this.email = email;}
}
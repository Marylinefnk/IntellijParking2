package esiag.back.models;

import javax.persistence.*;


    @Entity
    @Table(name = "allee")
    public class Allee {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id_allee")
        private Long id;

        @ManyToOne
        @JoinColumn(name = "id_intersection_debut")
        private Intersection debut;

        @ManyToOne
        @JoinColumn(name = "id_intersection_fin")
        private Intersection fin;

        private Double distance;
        private Double largeur;

        @Enumerated(EnumType.STRING)
        private SensCirculation sensCirculation;

        private String nomAllee;

    public Allee() {
    }

    public Allee(Intersection debut, Intersection fin, Double distance, Double largeur,
                SensCirculation sensCirculation, String nomAllee) {
        this.debut = debut;
        this.fin = fin;
        this.distance = distance;
        this.largeur = largeur;
        this.sensCirculation = sensCirculation;
        this.nomAllee = nomAllee;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Intersection getDebut() {
        return debut;
    }

    public void setDebut(Intersection debut) {
        this.debut = debut;
    }

    public Intersection getFin() {
        return fin;
    }

    public void setFin(Intersection fin) {
        this.fin = fin;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Double getLargeur() {
        return largeur;
    }

    public void setLargeur(Double largeur) {
        this.largeur = largeur;
    }

    public SensCirculation getSensCirculation() {
        return sensCirculation;
    }

    public void setSensCirculation(SensCirculation sensCirculation) {
        this.sensCirculation = sensCirculation;
    }

    public String getNomAllee() {
        return nomAllee;
    }

    public void setNomAllee(String nomAllee) {
        this.nomAllee = nomAllee;
    }
    }


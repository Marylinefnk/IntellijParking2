package esiag.back.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;


    @Entity
    @Table(name = "allee")
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
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


    }


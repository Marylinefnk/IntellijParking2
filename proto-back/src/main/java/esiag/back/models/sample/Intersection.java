package esiag.back.models.sample;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "intersection")
public class Intersection {

    @Id
    @Column(name = "id_intersection")
    private Integer id_intersection;

    @Column(name = "position_x")
    private double position_x;

    @Column(name = "position_y")
    private double position_y;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_intersection")
    private InteractionType type_interaction;

    /*@Column(name = "id_zone")
    Private Zone id_zone; */ //mis en comm pour tester compilation

}
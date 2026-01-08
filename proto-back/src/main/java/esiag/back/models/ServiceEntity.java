package esiag.back.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "service")
public class ServiceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_service")
    private Long id;

    @Enumerated(EnumType.STRING)
    private TypeService typeService;

    private String description;

    @OneToMany(mappedBy = "service")
    @JsonManagedReference("service-reservations")
    private List<ReservationService> reservations;

    public ServiceEntity() {
    }

    public ServiceEntity(TypeService typeService, String description) {
        this.typeService = typeService;
        this.description = description;
    }

}


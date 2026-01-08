package esiag.back.repositories;

import esiag.back.models.ReservationService;
import esiag.back.models.StatutReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface ReservationServiceRepository extends JpaRepository<ReservationService, Long> {
    List<ReservationService> findByPersonneId(Long personneId);
    List<ReservationService> findByServiceId(Long serviceId);
    List<ReservationService> findByStatut(StatutReservation statut);
    List<ReservationService> findByDateDebutBetween(LocalDateTime debut, LocalDateTime fin);
}

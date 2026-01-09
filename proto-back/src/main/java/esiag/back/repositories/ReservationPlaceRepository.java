package esiag.back.repositories;

import esiag.back.models.ReservationPlace;
import esiag.back.models.StatutReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.List;

public interface ReservationPlaceRepository extends JpaRepository<ReservationPlace, Long> {
    List<ReservationPlace> findByPersonneId(Long personneId);
    List<ReservationPlace> findByPlaceId(Long placeId);
    List<ReservationPlace> findByStatut(StatutReservation statut);
    List<ReservationPlace> findByDateDebutBetween(LocalDateTime debut, LocalDateTime fin);
    
    @Query("SELECT r FROM ReservationPlace r WHERE r.place.id = :placeId AND r.statut = :statut AND " +
           "((r.dateDebut <= :dateFin AND r.dateFin >= :dateDebut))")
    List<ReservationPlace> findConflictingReservations(Long placeId, LocalDateTime dateDebut, 
                                                       LocalDateTime dateFin, StatutReservation statut);
}

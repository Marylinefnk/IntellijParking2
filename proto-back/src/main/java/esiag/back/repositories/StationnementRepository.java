package esiag.back.repositories;

import esiag.back.models.Stationnement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface StationnementRepository extends JpaRepository<Stationnement, Long> {
    List<Stationnement> findByPlaceId(Long placeId);
    List<Stationnement> findByVehiculeId(Long vehiculeId);
    
    @Query("SELECT s FROM Stationnement s WHERE s.dateSortie IS NULL AND s.vehicule.id = :vehiculeId")
    Optional<Stationnement> findActiveStationnementByVehicule(Long vehiculeId);
    
    @Query("SELECT s FROM Stationnement s WHERE s.dateSortie IS NULL AND s.place.id = :placeId")
    Optional<Stationnement> findActiveStationnementByPlace(Long placeId);
    
    List<Stationnement> findByDateEntreeBetween(LocalDateTime debut, LocalDateTime fin);
}

package esiag.back.repositories;

import esiag.back.models.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PlaceRepository extends JpaRepository<Place, Long> {
    List<Place> findByStatut(esiag.back.models.StatutPlace statut);
    List<Place> findByType(esiag.back.models.TypePlace type);
    List<Place> findByZoneId(Long zoneId);
}

package intellijP.back.repositories;

import intellijP.back.models.EvenementCapteur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EvenementCapteurRepository extends JpaRepository<EvenementCapteur, Long> {

    List<EvenementCapteur> findByPlaceId(Long placeId);

    List<EvenementCapteur> findByCapteurId(Long capteurId);

    List<EvenementCapteur> findByDateEvenementBetween(LocalDateTime debut, LocalDateTime fin);

    List<EvenementCapteur> findByPlaceIdAndDateEvenementBetween(Long placeId, LocalDateTime debut, LocalDateTime fin);

}

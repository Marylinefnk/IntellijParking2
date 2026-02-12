package intellijP.back.repositories;

import intellijP.back.models.Capteur;
import intellijP.back.models.EtatCapteur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CapteurRepository extends JpaRepository<Capteur, Long> {

    Optional<Capteur> findByPlaceId(Long placeId);

    boolean existsByPlaceId(Long placeId);

    List<Capteur> findByEtatCapteur(EtatCapteur etat);

    // on filtre par zone - utile pour la simulation par niveau
    @Query("SELECT c FROM Capteur c WHERE c.place.zone.nom = :nomZone AND c.etatCapteur = 'ACTIF'")
    List<Capteur> findActifsByZoneNom(@Param("nomZone") String nomZone);

    @Query("SELECT c FROM Capteur c WHERE c.etatCapteur = 'ACTIF'")
    List<Capteur> findAllActifs();

}

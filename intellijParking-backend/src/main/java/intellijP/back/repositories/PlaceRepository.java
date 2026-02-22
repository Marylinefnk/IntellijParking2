package intellijP.back.repositories;

import intellijP.back.models.Place;
import intellijP.back.models.StatutPlace;
import intellijP.back.models.TypePlace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour la gestion des places de parking.
 * Fournit les méthodes d'accès aux données pour l'entité Place.
 */
public interface PlaceRepository extends JpaRepository<Place, Long> {

    /**
     * Recherche les places par statut (LIBRE, OCCUPEE, RESERVEE).
     */
    List<Place> findByStatut(StatutPlace statut);

    /**
     * Recherche les places par type (STANDARD, PMR, ELECTRIQUE, MOTO, FAMILIALE).
     */
    List<Place> findByType(TypePlace type);

    /**
     * Recherche les places appartenant à une zone spécifique.
     */
    List<Place> findByZoneId(Long zoneId);

    /**
     * Recherche une place par son numéro.
     */
    Optional<Place> findByNumero(String numero);

    /**
     * Vérifie si une place existe avec ce numéro.
     */
    boolean existsByNumero(String numero);

    /**
     * Vérifie si une place existe à cette position exacte.
     */
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Place p " +
           "WHERE p.positionX = :positionX AND p.positionY = :positionY")
    boolean existsByPosition(@Param("positionX") Double positionX, @Param("positionY") Double positionY);

    /**
     * Vérifie si une autre place (différente de l'id donné) existe avec ce numéro.
     */
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Place p " +
           "WHERE p.numero = :numero AND p.id != :id")
    boolean existsByNumeroAndIdNot(@Param("numero") String numero, @Param("id") Long id);

    /**
     * Vérifie si une autre place (différente de l'id donné) existe à cette position.
     */
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Place p " +
           "WHERE p.positionX = :positionX AND p.positionY = :positionY AND p.id != :id")
    boolean existsByPositionAndIdNot(@Param("positionX") Double positionX,
                                      @Param("positionY") Double positionY,
                                      @Param("id") Long id);
}

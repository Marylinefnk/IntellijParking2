package intellijP.back.repositories;

import intellijP.back.models.ReservationPlace;
import intellijP.back.models.StatutReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository pour la gestion des réservations de places.
 * Fournit les méthodes d'accès aux données pour l'entité ReservationPlace.
 */
public interface ReservationPlaceRepository extends JpaRepository<ReservationPlace, Long> {

    /**
     * Recherche les réservations d'une personne.
     */
    List<ReservationPlace> findByPersonneId(Long personneId);

    /**
     * Recherche les réservations d'une place.
     */
    List<ReservationPlace> findByPlaceId(Long placeId);

    /**
     * Recherche les réservations par statut.
     */
    List<ReservationPlace> findByStatut(StatutReservation statut);

    /**
     * Recherche les réservations dans une période donnée.
     */
    List<ReservationPlace> findByDateDebutBetween(LocalDateTime debut, LocalDateTime fin);

    /**
     * Recherche les réservations en conflit pour une place donnée.
     * Une réservation est en conflit si elle chevauche la période demandée.
     * Formule : conflit si (dateDebut1 <= dateFin2) ET (dateFin1 >= dateDebut2)
     *
     * @param placeId Identifiant de la place
     * @param dateDebut Date de début de la période à vérifier
     * @param dateFin Date de fin de la période à vérifier
     * @param statut Statut des réservations à considérer (généralement CONFIRMEE ou EN_COURS)
     * @return Liste des réservations en conflit
     */
    @Query("SELECT r FROM ReservationPlace r WHERE r.place.id = :placeId " +
           "AND r.statut IN (:statuts) " +
           "AND r.dateDebut <= :dateFin AND r.dateFin >= :dateDebut")
    List<ReservationPlace> findConflictingReservations(
            @Param("placeId") Long placeId,
            @Param("dateDebut") LocalDateTime dateDebut,
            @Param("dateFin") LocalDateTime dateFin,
            @Param("statuts") List<StatutReservation> statuts);

    /**
     * Recherche les réservations en conflit, en excluant une réservation spécifique.
     * Utilisé lors de la modification d'une réservation existante.
     *
     * @param placeId Identifiant de la place
     * @param dateDebut Date de début de la période à vérifier
     * @param dateFin Date de fin de la période à vérifier
     * @param statuts Statuts des réservations à considérer
     * @param excludeId Identifiant de la réservation à exclure
     * @return Liste des réservations en conflit
     */
    @Query("SELECT r FROM ReservationPlace r WHERE r.place.id = :placeId " +
           "AND r.statut IN (:statuts) " +
           "AND r.dateDebut <= :dateFin AND r.dateFin >= :dateDebut " +
           "AND r.id != :excludeId")
    List<ReservationPlace> findConflictingReservationsExcluding(
            @Param("placeId") Long placeId,
            @Param("dateDebut") LocalDateTime dateDebut,
            @Param("dateFin") LocalDateTime dateFin,
            @Param("statuts") List<StatutReservation> statuts,
            @Param("excludeId") Long excludeId);

    /**
     * Recherche les réservations d'un véhicule.
     */
    List<ReservationPlace> findByVehiculeId(Long vehiculeId);

    /**
     * Compte le nombre de réservations actives d'une personne.
     */
    @Query("SELECT COUNT(r) FROM ReservationPlace r WHERE r.personne.id = :personneId " +
           "AND r.statut IN ('CONFIRMEE', 'EN_COURS')")
    long countActiveReservationsByPersonne(@Param("personneId") Long personneId);

    /**
     * Trouve la reservation active en cours pour une place a un moment donne.
     * Une reservation est "en cours" si le moment est entre dateDebut et dateFin
     * et le statut est CONFIRMEE ou EN_COURS.
     */
    @Query("SELECT r FROM ReservationPlace r WHERE r.place.id = :placeId " +
           "AND r.statut IN ('CONFIRMEE', 'EN_COURS') " +
           "AND r.dateDebut <= :moment AND r.dateFin >= :moment")
    List<ReservationPlace> findActiveReservationsAtMoment(
            @Param("placeId") Long placeId,
            @Param("moment") LocalDateTime moment);

    /**
     * Trouve toutes les reservations futures pour une place.
     */
    @Query("SELECT r FROM ReservationPlace r WHERE r.place.id = :placeId " +
           "AND r.statut IN ('CONFIRMEE', 'EN_COURS') " +
           "AND r.dateDebut > :moment " +
           "ORDER BY r.dateDebut ASC")
    List<ReservationPlace> findFutureReservations(
            @Param("placeId") Long placeId,
            @Param("moment") LocalDateTime moment);
}

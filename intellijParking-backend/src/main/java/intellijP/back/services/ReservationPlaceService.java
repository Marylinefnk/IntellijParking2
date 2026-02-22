package intellijP.back.services;

import intellijP.back.models.*;
import intellijP.back.repositories.ReservationPlaceRepository;
import intellijP.back.repositories.PlaceRepository;
import intellijP.back.repositories.PersonneRepository;
import intellijP.back.repositories.VehiculeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Service métier pour la gestion des réservations de places.
 * Implémente les règles métier suivantes :
 * - Pas de réservation sur un créneau déjà réservé
 * - La place doit exister et ne pas être occupée
 * - La personne et le véhicule doivent exister
 * - La date de fin doit être postérieure à la date de début
 * - Limite du nombre de réservations actives par personne
 */
@Service
@Transactional
public class ReservationPlaceService {

    private final ReservationPlaceRepository reservationRepository;
    private final PlaceRepository placeRepository;
    private final PersonneRepository personneRepository;
    private final VehiculeRepository vehiculeRepository;

    /** Nombre maximum de réservations actives par personne */
    private static final int MAX_RESERVATIONS_ACTIVES = 3;

    public ReservationPlaceService(ReservationPlaceRepository reservationRepository,
                                   PlaceRepository placeRepository,
                                   PersonneRepository personneRepository,
                                   VehiculeRepository vehiculeRepository) {
        this.reservationRepository = reservationRepository;
        this.placeRepository = placeRepository;
        this.personneRepository = personneRepository;
        this.vehiculeRepository = vehiculeRepository;
    }

    /**
     * Récupère toutes les réservations de places.
     * @return Liste de toutes les réservations
     */
    public List<ReservationPlace> findAll() {
        return reservationRepository.findAll();
    }

    /**
     * Recherche une réservation par son identifiant.
     * @param id Identifiant de la réservation
     * @return La réservation trouvée ou Optional vide
     */
    public Optional<ReservationPlace> findById(Long id) {
        return reservationRepository.findById(id);
    }

    /**
     * Recherche les réservations d'une personne.
     * @param personneId Identifiant de la personne
     * @return Liste des réservations de la personne
     */
    public List<ReservationPlace> findByPersonne(Long personneId) {
        return reservationRepository.findByPersonneId(personneId);
    }

    /**
     * Recherche les réservations d'une place.
     * @param placeId Identifiant de la place
     * @return Liste des réservations de la place
     */
    public List<ReservationPlace> findByPlace(Long placeId) {
        return reservationRepository.findByPlaceId(placeId);
    }

    /**
     * Recherche les réservations par statut.
     * @param statut Statut recherché
     * @return Liste des réservations correspondantes
     */
    public List<ReservationPlace> findByStatut(StatutReservation statut) {
        return reservationRepository.findByStatut(statut);
    }

    /**
     * Crée une nouvelle réservation de place.
     * Règles métier appliquées :
     * - La place, la personne et le véhicule doivent exister
     * - La date de fin doit être postérieure à la date de début
     * - La place ne doit pas être actuellement occupée
     * - Le créneau ne doit pas chevaucher une réservation existante
     * - La personne ne doit pas avoir atteint la limite de réservations actives
     *
     * @param reservation La réservation à créer
     * @return La réservation créée
     * @throws RuntimeException si une règle métier est violée
     */
    public ReservationPlace create(ReservationPlace reservation) {
        // Validation de la place
        Place place = placeRepository.findById(reservation.getPlace().getId())
                .orElseThrow(() -> new RuntimeException("Place non trouvée avec l'id: " +
                        reservation.getPlace().getId()));

        // Validation de la personne
        Personne personne = personneRepository.findById(reservation.getPersonne().getId())
                .orElseThrow(() -> new RuntimeException("Personne non trouvée avec l'id: " +
                        reservation.getPersonne().getId()));

        // Validation du véhicule
        Vehicule vehicule = vehiculeRepository.findById(reservation.getVehicule().getId())
                .orElseThrow(() -> new RuntimeException("Véhicule non trouvé avec l'id: " +
                        reservation.getVehicule().getId()));

        // Vérification que le véhicule appartient à la personne
        if (!vehicule.getPersonne().getId().equals(personne.getId())) {
            throw new RuntimeException("Le véhicule n'appartient pas à cette personne");
        }

        // Vérification des dates
        if (reservation.getDateDebut() == null || reservation.getDateFin() == null) {
            throw new RuntimeException("Les dates de début et de fin sont obligatoires");
        }
        if (reservation.getDateFin().isBefore(reservation.getDateDebut())) {
            throw new RuntimeException("La date de fin doit être postérieure à la date de début");
        }
        if (reservation.getDateDebut().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("La date de début ne peut pas être dans le passé");
        }

        // Vérification que la place n'est pas occupée
        if (place.getStatut() == StatutPlace.OCCUPEE) {
            throw new RuntimeException("Cette place est actuellement occupée");
        }

        // Vérification des conflits de créneaux
        List<StatutReservation> statutsActifs = Arrays.asList(
                StatutReservation.CONFIRMEE, StatutReservation.EN_COURS);
        List<ReservationPlace> conflits = reservationRepository.findConflictingReservations(
                place.getId(),
                reservation.getDateDebut(),
                reservation.getDateFin(),
                statutsActifs);

        if (!conflits.isEmpty()) {
            throw new RuntimeException("Cette place est déjà réservée sur ce créneau. " +
                    "Conflit avec " + conflits.size() + " réservation(s) existante(s).");
        }

        // Vérification de la limite de réservations actives
        long nbReservationsActives = reservationRepository.countActiveReservationsByPersonne(personne.getId());
        if (nbReservationsActives >= MAX_RESERVATIONS_ACTIVES) {
            throw new RuntimeException("Limite de " + MAX_RESERVATIONS_ACTIVES +
                    " réservations actives atteinte pour cette personne");
        }

        // Configuration de la réservation
        reservation.setPersonne(personne);
        reservation.setPlace(place);
        reservation.setVehicule(vehicule);
        reservation.setStatut(StatutReservation.CONFIRMEE);

        // NOTE: On ne change plus le statut de la place ici.
        // Le statut sera calcule dynamiquement en fonction de l'heure actuelle.

        return reservationRepository.save(reservation);
    }

    /**
     * Met à jour une réservation existante.
     * Seules les dates peuvent être modifiées.
     * Règles métier :
     * - La réservation doit exister
     * - Les nouvelles dates ne doivent pas créer de conflit
     *
     * @param id Identifiant de la réservation
     * @param reservationDetails Nouvelles données
     * @return La réservation mise à jour
     */
    public ReservationPlace update(Long id, ReservationPlace reservationDetails) {
        ReservationPlace reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée avec l'id: " + id));

        // Vérification des nouvelles dates
        if (reservationDetails.getDateDebut() != null && reservationDetails.getDateFin() != null) {
            if (reservationDetails.getDateFin().isBefore(reservationDetails.getDateDebut())) {
                throw new RuntimeException("La date de fin doit être postérieure à la date de début");
            }

            // Vérification des conflits (en excluant cette réservation)
            List<StatutReservation> statutsActifs = Arrays.asList(
                    StatutReservation.CONFIRMEE, StatutReservation.EN_COURS);
            List<ReservationPlace> conflits = reservationRepository.findConflictingReservationsExcluding(
                    reservation.getPlace().getId(),
                    reservationDetails.getDateDebut(),
                    reservationDetails.getDateFin(),
                    statutsActifs,
                    id);

            if (!conflits.isEmpty()) {
                throw new RuntimeException("Le nouveau créneau entre en conflit avec " +
                        conflits.size() + " réservation(s) existante(s)");
            }

            reservation.setDateDebut(reservationDetails.getDateDebut());
            reservation.setDateFin(reservationDetails.getDateFin());
        }

        if (reservationDetails.getStatut() != null) {
            reservation.setStatut(reservationDetails.getStatut());
        }

        return reservationRepository.save(reservation);
    }

    /**
     * Annule une réservation.
     * @param id Identifiant de la réservation
     */
    public void annuler(Long id) {
        ReservationPlace reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée avec l'id: " + id));

        if (reservation.getStatut() == StatutReservation.EN_COURS) {
            throw new RuntimeException("Impossible d'annuler une réservation en cours. Veuillez la terminer.");
        }

        reservation.setStatut(StatutReservation.ANNULEE);
        reservationRepository.save(reservation);

        // NOTE: Le statut de la place sera recalcule dynamiquement
    }

    /**
     * Démarre une réservation (passage en cours).
     * @param id Identifiant de la réservation
     */
    public void commencer(Long id) {
        ReservationPlace reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée avec l'id: " + id));

        if (reservation.getStatut() != StatutReservation.CONFIRMEE) {
            throw new RuntimeException("Seule une réservation confirmée peut être commencée. " +
                    "Statut actuel: " + reservation.getStatut());
        }

        reservation.setStatut(StatutReservation.EN_COURS);
        reservationRepository.save(reservation);

        // NOTE: Le statut de la place (OCCUPEE) sera calcule dynamiquement
    }

    /**
     * Termine une réservation.
     * @param id Identifiant de la réservation
     */
    public void terminer(Long id) {
        ReservationPlace reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée avec l'id: " + id));

        reservation.setStatut(StatutReservation.TERMINEE);
        reservationRepository.save(reservation);

        // NOTE: Le statut de la place sera recalcule dynamiquement
    }

    /**
     * Supprime une réservation.
     * @param id Identifiant de la réservation
     */
    public void delete(Long id) {
        ReservationPlace reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée avec l'id: " + id));

        // NOTE: Le statut de la place sera recalcule dynamiquement apres suppression
        reservationRepository.deleteById(id);
    }
}

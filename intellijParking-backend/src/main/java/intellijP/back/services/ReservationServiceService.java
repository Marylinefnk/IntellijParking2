package intellijP.back.services;

import intellijP.back.models.Personne;
import intellijP.back.models.ReservationService;
import intellijP.back.models.ServiceEntity;
import intellijP.back.models.StatutReservation;
import intellijP.back.repositories.PersonneRepository;
import intellijP.back.repositories.ReservationServiceRepository;
import intellijP.back.repositories.ServiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service métier pour la gestion des réservations de services.
 * Implémente les règles métier suivantes :
 * - La personne et le service doivent exister
 * - La date de fin doit être postérieure à la date de début
 * - La date de début ne peut pas être dans le passé
 */
@Service
@Transactional
public class ReservationServiceService {

    private final ReservationServiceRepository reservationServiceRepository;
    private final PersonneRepository personneRepository;
    private final ServiceRepository serviceRepository;

    public ReservationServiceService(ReservationServiceRepository reservationServiceRepository,
                                     PersonneRepository personneRepository,
                                     ServiceRepository serviceRepository) {
        this.reservationServiceRepository = reservationServiceRepository;
        this.personneRepository = personneRepository;
        this.serviceRepository = serviceRepository;
    }

    /**
     * Récupère toutes les réservations de services.
     * @return Liste de toutes les réservations
     */
    public List<ReservationService> findAll() {
        return reservationServiceRepository.findAll();
    }

    /**
     * Recherche une réservation par son identifiant.
     * @param id Identifiant de la réservation
     * @return La réservation trouvée ou Optional vide
     */
    public Optional<ReservationService> findById(Long id) {
        return reservationServiceRepository.findById(id);
    }

    /**
     * Recherche les réservations d'une personne.
     * @param personneId Identifiant de la personne
     * @return Liste des réservations de la personne
     */
    public List<ReservationService> findByPersonne(Long personneId) {
        return reservationServiceRepository.findByPersonneId(personneId);
    }

    /**
     * Recherche les réservations d'un service.
     * @param serviceId Identifiant du service
     * @return Liste des réservations du service
     */
    public List<ReservationService> findByService(Long serviceId) {
        return reservationServiceRepository.findByServiceId(serviceId);
    }

    /**
     * Recherche les réservations par statut.
     * @param statut Statut recherché
     * @return Liste des réservations correspondantes
     */
    public List<ReservationService> findByStatut(StatutReservation statut) {
        return reservationServiceRepository.findByStatut(statut);
    }

    /**
     * Recherche les réservations dans une période donnée.
     * @param debut Date de début
     * @param fin Date de fin
     * @return Liste des réservations correspondantes
     */
    public List<ReservationService> findByDateDebutBetween(LocalDateTime debut, LocalDateTime fin) {
        return reservationServiceRepository.findByDateDebutBetween(debut, fin);
    }

    /**
     * Crée une nouvelle réservation de service.
     * Règles métier :
     * - La personne et le service doivent exister
     * - La date de fin doit être postérieure à la date de début
     * - La date de début ne peut pas être dans le passé
     *
     * @param reservation La réservation à créer
     * @return La réservation créée
     * @throws RuntimeException si une règle métier est violée
     */
    public ReservationService create(ReservationService reservation) {
        // Validation de la personne
        Personne personne = personneRepository.findById(reservation.getPersonne().getId())
                .orElseThrow(() -> new RuntimeException("Personne non trouvée avec l'id: " +
                        reservation.getPersonne().getId()));

        // Validation du service
        ServiceEntity service = serviceRepository.findById(reservation.getService().getId())
                .orElseThrow(() -> new RuntimeException("Service non trouvé avec l'id: " +
                        reservation.getService().getId()));

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

        // Configuration de la réservation
        reservation.setPersonne(personne);
        reservation.setService(service);
        reservation.setStatut(StatutReservation.CONFIRMEE);

        return reservationServiceRepository.save(reservation);
    }

    /**
     * Met à jour une réservation existante.
     * @param id Identifiant de la réservation
     * @param reservationDetails Nouvelles données
     * @return La réservation mise à jour
     */
    public ReservationService update(Long id, ReservationService reservationDetails) {
        ReservationService reservation = reservationServiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation de service non trouvée avec l'id: " + id));

        // Vérification des nouvelles dates
        if (reservationDetails.getDateDebut() != null && reservationDetails.getDateFin() != null) {
            if (reservationDetails.getDateFin().isBefore(reservationDetails.getDateDebut())) {
                throw new RuntimeException("La date de fin doit être postérieure à la date de début");
            }
            reservation.setDateDebut(reservationDetails.getDateDebut());
            reservation.setDateFin(reservationDetails.getDateFin());
        }

        if (reservationDetails.getStatut() != null) {
            reservation.setStatut(reservationDetails.getStatut());
        }

        return reservationServiceRepository.save(reservation);
    }

    /**
     * Annule une réservation de service.
     * @param id Identifiant de la réservation
     */
    public void annuler(Long id) {
        ReservationService reservation = reservationServiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation de service non trouvée avec l'id: " + id));

        if (reservation.getStatut() == StatutReservation.EN_COURS) {
            throw new RuntimeException("Impossible d'annuler une réservation en cours");
        }

        reservation.setStatut(StatutReservation.ANNULEE);
        reservationServiceRepository.save(reservation);
    }

    /**
     * Démarre une réservation de service.
     * @param id Identifiant de la réservation
     */
    public void commencer(Long id) {
        ReservationService reservation = reservationServiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation de service non trouvée avec l'id: " + id));

        if (reservation.getStatut() != StatutReservation.CONFIRMEE) {
            throw new RuntimeException("Seule une réservation confirmée peut être commencée. " +
                    "Statut actuel: " + reservation.getStatut());
        }

        reservation.setStatut(StatutReservation.EN_COURS);
        reservationServiceRepository.save(reservation);
    }

    /**
     * Termine une réservation de service.
     * @param id Identifiant de la réservation
     */
    public void terminer(Long id) {
        ReservationService reservation = reservationServiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation de service non trouvée avec l'id: " + id));

        reservation.setStatut(StatutReservation.TERMINEE);
        reservationServiceRepository.save(reservation);
    }

    /**
     * Supprime une réservation de service.
     * @param id Identifiant de la réservation
     */
    public void delete(Long id) {
        if (!reservationServiceRepository.existsById(id)) {
            throw new RuntimeException("Réservation de service non trouvée avec l'id: " + id);
        }
        reservationServiceRepository.deleteById(id);
    }
}

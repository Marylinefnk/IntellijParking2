package intellijP.back.services;

import intellijP.back.models.Personne;
import intellijP.back.models.ReservationService;
import intellijP.back.models.ServiceEntity;
import intellijP.back.models.StatutReservation;
import intellijP.back.repositories.PersonneRepository;
import intellijP.back.repositories.ReservationServiceRepository;
import intellijP.back.repositories.ServiceRepository;
import intellijP.back.models.*;
import intellijP.back.repositories.PersonneRepository;
import intellijP.back.repositories.ReservationServiceRepository;
import intellijP.back.repositories.ServiceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service metier pour la gestion des reservations de services.
 */
@Service
@Transactional
public class ReservationServiceService {

    private static final Logger logger = LoggerFactory.getLogger(ReservationServiceService.class);

    private final ReservationServiceRepository reservationServiceRepository;
    private final PersonneRepository personneRepository;
    private final ServiceRepository serviceRepository;

    public ReservationServiceService(ReservationServiceRepository reservationServiceRepository,
                                     PersonneRepository personneRepository,
                                     ServiceRepository serviceRepository) {
        this.reservationServiceRepository = reservationServiceRepository;
        this.personneRepository = personneRepository;
        this.serviceRepository = serviceRepository;
        logger.info("ReservationServiceService initialise");
    }

    /**
     * Recupere toutes les reservations de services.
     * @return Liste de toutes les reservations
     */
    public List<ReservationService> findAll() {
        logger.debug("Recuperation de toutes les reservations de services");
        List<ReservationService> reservations = reservationServiceRepository.findAll();
        logger.info("Nombre de reservations de services recuperees: {}", reservations.size());
        return reservations;
    }

    /**
     * Recherche une reservation par son identifiant.
     * @param id Identifiant de la reservation
     * @return La reservation trouvee ou Optional vide
     */
    public Optional<ReservationService> findById(Long id) {
        logger.debug("Recherche de la reservation de service avec id: {}", id);
        Optional<ReservationService> reservation = reservationServiceRepository.findById(id);
        if (reservation.isPresent()) {
            logger.debug("Reservation de service trouvee: id={}, statut={}", id, reservation.get().getStatut());
        } else {
            logger.warn("Reservation de service non trouvee avec id: {}", id);
        }
        return reservation;
    }

    /**
     * Recherche les reservations d'une personne.
     * @param personneId Identifiant de la personne
     * @return Liste des reservations de la personne
     */
    public List<ReservationService> findByPersonne(Long personneId) {
        logger.debug("Recherche des reservations de services pour la personne id: {}", personneId);
        return reservationServiceRepository.findByPersonneId(personneId);
    }

    /**
     * Recherche les reservations d'un service.
     * @param serviceId Identifiant du service
     * @return Liste des reservations du service
     */
    public List<ReservationService> findByService(Long serviceId) {
        logger.debug("Recherche des reservations pour le service id: {}", serviceId);
        return reservationServiceRepository.findByServiceId(serviceId);
    }

    /**
     * Recherche les reservations par statut.
     * @param statut Statut recherche
     * @return Liste des reservations correspondantes
     */
    public List<ReservationService> findByStatut(StatutReservation statut) {
        logger.debug("Recherche des reservations de services avec statut: {}", statut);
        return reservationServiceRepository.findByStatut(statut);
    }

    /**
     * Recherche les reservations dans une periode donnee.
     * @param debut Date de debut
     * @param fin Date de fin
     * @return Liste des reservations correspondantes
     */
    public List<ReservationService> findByDateDebutBetween(LocalDateTime debut, LocalDateTime fin) {
        logger.debug("Recherche des reservations de services entre {} et {}", debut, fin);
        return reservationServiceRepository.findByDateDebutBetween(debut, fin);
    }

    /**
     * Cree une nouvelle reservation de service.

     * @param reservation La reservation a creer
     * @return La reservation creee
     * @throws RuntimeException si une regle metier est violee
     */
    public ReservationService create(ReservationService reservation) {
        logger.info("Tentative de creation d'une reservation de service - personne={}, service={}",
            reservation.getPersonne().getId(), reservation.getService().getId());

        // Validation de la personne
        Personne personne = personneRepository.findById(reservation.getPersonne().getId())
                .orElseThrow(() -> {
                    logger.error("Echec creation: personne non trouvee avec id={}",
                        reservation.getPersonne().getId());
                    return new RuntimeException("Personne non trouvee avec l'id: " +
                            reservation.getPersonne().getId());
                });

        // Validation du service
        ServiceEntity service = serviceRepository.findById(reservation.getService().getId())
                .orElseThrow(() -> {
                    logger.error("Echec creation: service non trouve avec id={}",
                        reservation.getService().getId());
                    return new RuntimeException("Service non trouve avec l'id: " +
                            reservation.getService().getId());
                });

        // Verification des dates
        if (reservation.getDateDebut() == null || reservation.getDateFin() == null) {
            logger.error("Echec creation: dates manquantes");
            throw new RuntimeException("Les dates de debut et de fin sont obligatoires");
        }
        if (reservation.getDateFin().isBefore(reservation.getDateDebut())) {
            logger.error("Echec creation: date fin {} avant date debut {}",
                reservation.getDateFin(), reservation.getDateDebut());
            throw new RuntimeException("La date de fin doit etre posterieure a la date de debut");
        }
        if (reservation.getDateDebut().isBefore(LocalDateTime.now())) {
            logger.error("Echec creation: date debut {} dans le passe", reservation.getDateDebut());
            throw new RuntimeException("La date de debut ne peut pas etre dans le passe");
        }

        // Configuration de la reservation
        reservation.setPersonne(personne);
        reservation.setService(service);
        reservation.setStatut(StatutReservation.CONFIRMEE);

        ReservationService created = reservationServiceRepository.save(reservation);
        logger.info("Reservation de service creee avec succes: id={}, service={}, personne={}, debut={}, fin={}",
            created.getId(), service.getTypeService(), personne.getMail(),
            created.getDateDebut(), created.getDateFin());

        return created;
    }

    /**
     * Met a jour une reservation existante.
     * @param id Identifiant de la reservation
     * @param reservationDetails Nouvelles donnees
     * @return La reservation mise a jour
     */
    public ReservationService update(Long id, ReservationService reservationDetails) {
        logger.info("Tentative de mise a jour de la reservation de service id={}", id);

        ReservationService reservation = reservationServiceRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Echec mise a jour: reservation de service non trouvee avec id={}", id);
                    return new RuntimeException("Reservation de service non trouvee avec l'id: " + id);
                });

        // Verification des nouvelles dates
        if (reservationDetails.getDateDebut() != null && reservationDetails.getDateFin() != null) {
            if (reservationDetails.getDateFin().isBefore(reservationDetails.getDateDebut())) {
                logger.error("Echec mise a jour: date fin avant date debut");
                throw new RuntimeException("La date de fin doit etre posterieure a la date de debut");
            }
            reservation.setDateDebut(reservationDetails.getDateDebut());
            reservation.setDateFin(reservationDetails.getDateFin());
        }

        if (reservationDetails.getStatut() != null) {
            reservation.setStatut(reservationDetails.getStatut());
        }

        ReservationService updated = reservationServiceRepository.save(reservation);
        logger.info("Reservation de service mise a jour avec succes: id={}, statut={}",
            id, updated.getStatut());
        return updated;
    }

    /**
     * Annule une reservation de service.
     * @param id Identifiant de la reservation
     */
    public void annuler(Long id) {
        logger.info("Tentative d'annulation de la reservation de service id={}", id);

        ReservationService reservation = reservationServiceRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Echec annulation: reservation de service non trouvee avec id={}", id);
                    return new RuntimeException("Reservation de service non trouvee avec l'id: " + id);
                });

        if (reservation.getStatut() == StatutReservation.EN_COURS) {
            logger.error("Echec annulation: reservation {} est en cours", id);
            throw new RuntimeException("Impossible d'annuler une reservation en cours");
        }

        reservation.setStatut(StatutReservation.ANNULEE);
        reservationServiceRepository.save(reservation);
        logger.info("Reservation de service annulee avec succes: id={}", id);
    }

    /**
     * Demarre une reservation de service.
     * @param id Identifiant de la reservation
     */
    public void commencer(Long id) {
        logger.info("Tentative de demarrage de la reservation de service id={}", id);

        ReservationService reservation = reservationServiceRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Echec demarrage: reservation de service non trouvee avec id={}", id);
                    return new RuntimeException("Reservation de service non trouvee avec l'id: " + id);
                });

        if (reservation.getStatut() != StatutReservation.CONFIRMEE) {
            logger.error("Echec demarrage: reservation {} a le statut {} (attendu: CONFIRMEE)",
                id, reservation.getStatut());
            throw new RuntimeException("Seule une reservation confirmee peut etre commencee. " +
                    "Statut actuel: " + reservation.getStatut());
        }

        reservation.setStatut(StatutReservation.EN_COURS);
        reservationServiceRepository.save(reservation);
        logger.info("Reservation de service demarree avec succes: id={}", id);
    }

    /**
     * Termine une reservation de service.
     * @param id Identifiant de la reservation
     */
    public void terminer(Long id) {
        logger.info("Tentative de terminaison de la reservation de service id={}", id);

        ReservationService reservation = reservationServiceRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Echec terminaison: reservation de service non trouvee avec id={}", id);
                    return new RuntimeException("Reservation de service non trouvee avec l'id: " + id);
                });

        reservation.setStatut(StatutReservation.TERMINEE);
        reservationServiceRepository.save(reservation);
        logger.info("Reservation de service terminee avec succes: id={}", id);
    }

    /**
     * Supprime une reservation de service.
     * @param id Identifiant de la reservation
     */
    public void delete(Long id) {
        logger.info("Tentative de suppression de la reservation de service id={}", id);

        if (!reservationServiceRepository.existsById(id)) {
            logger.error("Echec suppression: reservation de service non trouvee avec id={}", id);
            throw new RuntimeException("Reservation de service non trouvee avec l'id: " + id);
        }
        reservationServiceRepository.deleteById(id);
        logger.info("Reservation de service supprimee avec succes: id={}", id);
    }
}

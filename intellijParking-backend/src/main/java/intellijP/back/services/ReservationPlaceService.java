package intellijP.back.services;

import intellijP.back.models.*;
import intellijP.back.repositories.ReservationPlaceRepository;
import intellijP.back.repositories.PlaceRepository;
import intellijP.back.repositories.PersonneRepository;
import intellijP.back.repositories.VehiculeRepository;
import intellijP.back.dto.DtoMapper;
import intellijP.back.dto.ReservationPlaceCreateDTO;
import intellijP.back.dto.ReservationPlaceResponseDTO;
import intellijP.back.exceptions.BusinessException;
import intellijP.back.exceptions.ConflictException;
import intellijP.back.exceptions.OperationNotAllowedException;
import intellijP.back.exceptions.ResourceNotFoundException;
import intellijP.back.exceptions.ValidationException;
import intellijP.back.models.*;
import intellijP.back.repositories.ReservationPlaceRepository;
import intellijP.back.repositories.PlaceRepository;
import intellijP.back.repositories.PersonneRepository;
import intellijP.back.repositories.VehiculeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Service
@Transactional
public class ReservationPlaceService {

    private static final Logger logger = LoggerFactory.getLogger(ReservationPlaceService.class);

    private final ReservationPlaceRepository reservationRepository;
    private final PlaceRepository placeRepository;
    private final PersonneRepository personneRepository;
    private final VehiculeRepository vehiculeRepository;
    private final DtoMapper dtoMapper;
    private final PlaceWebSocketService placeWebSocketService;
    private final NotificationWebSocketService notificationService;
    private static final int MAX_RESERVATIONS_ACTIVES = 3;

    public ReservationPlaceService(ReservationPlaceRepository reservationRepository,
                                   PlaceRepository placeRepository,
                                   PersonneRepository personneRepository,
                                   VehiculeRepository vehiculeRepository,
                                   DtoMapper dtoMapper,
                                   PlaceWebSocketService placeWebSocketService,
                                   NotificationWebSocketService notificationService) {
        this.reservationRepository = reservationRepository;
        this.placeRepository = placeRepository;
        this.personneRepository = personneRepository;
        this.vehiculeRepository = vehiculeRepository;
        this.dtoMapper = dtoMapper;
        this.placeWebSocketService = placeWebSocketService;
        this.notificationService = notificationService;
        logger.info("ReservationPlaceService initialise");
    }
    public List<ReservationPlaceResponseDTO> findAllDTO() {
        logger.debug("Recuperation de toutes les reservations de places (DTO)");
        List<ReservationPlaceResponseDTO> result = reservationRepository.findAll().stream()
                .map(dtoMapper::toReservationPlaceResponseDTO)
                .collect(Collectors.toList());
        logger.info("Nombre de reservations de places recuperees: {}", result.size());
        return result;
    }

    public Optional<ReservationPlaceResponseDTO> findByIdDTO(Long id) {
        logger.debug("Recherche de la reservation avec id: {} (DTO)", id);
        Optional<ReservationPlaceResponseDTO> result = reservationRepository.findById(id)
                .map(dtoMapper::toReservationPlaceResponseDTO);
        if (result.isEmpty()) {
            logger.warn("Reservation non trouvee avec id: {}", id);
        }
        return result;
    }

    public List<ReservationPlaceResponseDTO> findByPersonneDTO(Long personneId) {
        logger.debug("Recherche des reservations pour la personne id: {} (DTO)", personneId);
        return reservationRepository.findByPersonneId(personneId).stream()
                .map(dtoMapper::toReservationPlaceResponseDTO)
                .collect(Collectors.toList());
    }

    public List<ReservationPlaceResponseDTO> findByPlaceDTO(Long placeId) {
        logger.debug("Recherche des reservations pour la place id: {} (DTO)", placeId);
        return reservationRepository.findByPlaceId(placeId).stream()
                .map(dtoMapper::toReservationPlaceResponseDTO)
                .collect(Collectors.toList());
    }

    public List<ReservationPlaceResponseDTO> findByStatutDTO(StatutReservation statut) {
        logger.debug("Recherche des reservations avec statut: {} (DTO)", statut);
        return reservationRepository.findByStatut(statut).stream()
                .map(dtoMapper::toReservationPlaceResponseDTO)
                .collect(Collectors.toList());
    }

    public ReservationPlaceResponseDTO createDTO(ReservationPlaceCreateDTO createDTO) {
        logger.info("Tentative de creation d'une reservation - place={}, personne={}, vehicule={}",
            createDTO.getPlaceId(), createDTO.getPersonneId(), createDTO.getVehiculeId());

        ReservationPlace reservation = new ReservationPlace();

        Personne personne = new Personne();
        personne.setId(createDTO.getPersonneId());
        reservation.setPersonne(personne);

        Place place = new Place();
        place.setId(createDTO.getPlaceId());
        reservation.setPlace(place);

        Vehicule vehicule = new Vehicule();
        vehicule.setId(createDTO.getVehiculeId());
        reservation.setVehicule(vehicule);

        reservation.setDateDebut(createDTO.getDateDebut());
        reservation.setDateFin(createDTO.getDateFin());

        ReservationPlace created = create(reservation);
        ReservationPlaceResponseDTO result = dtoMapper.toReservationPlaceResponseDTO(created);

        placeWebSocketService.broadcastPlaceUpdate(createDTO.getPlaceId());
        notificationService.notifyReservationCreated(result.getId(), result.getPlace().getNumero());

        return result;
    }

    public ReservationPlaceResponseDTO updateDTO(Long id, ReservationPlaceCreateDTO updateDTO) {
        logger.info("Tentative de mise a jour de la reservation id={}", id);

        ReservationPlace reservationDetails = new ReservationPlace();
        reservationDetails.setDateDebut(updateDTO.getDateDebut());
        reservationDetails.setDateFin(updateDTO.getDateFin());

        ReservationPlace updated = update(id, reservationDetails);
        ReservationPlaceResponseDTO result = dtoMapper.toReservationPlaceResponseDTO(updated);

        placeWebSocketService.broadcastPlaceUpdate(updated.getPlace().getId());
        notificationService.notifyReservationUpdated(result.getId(), result.getPlace().getNumero());

        return result;
    }

    public ReservationPlaceResponseDTO annulerDTO(Long id) {
        ReservationPlace reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", id));
        Long placeId = reservation.getPlace().getId();
        String placeNumero = reservation.getPlace().getNumero();

        annuler(id);
        ReservationPlaceResponseDTO result = findByIdDTO(id).orElse(null);

        placeWebSocketService.broadcastPlaceUpdate(placeId);
        notificationService.notifyReservationCancelled(id, placeNumero);

        return result;
    }

    public ReservationPlaceResponseDTO commencerDTO(Long id) {
        ReservationPlace reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", id));
        Long placeId = reservation.getPlace().getId();
        String placeNumero = reservation.getPlace().getNumero();

        commencer(id);
        ReservationPlaceResponseDTO result = findByIdDTO(id).orElse(null);

        placeWebSocketService.broadcastPlaceUpdate(placeId);
        notificationService.notifyReservationStarted(id, placeNumero);

        return result;
    }

    public ReservationPlaceResponseDTO terminerDTO(Long id) {
        ReservationPlace reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", id));
        Long placeId = reservation.getPlace().getId();
        String placeNumero = reservation.getPlace().getNumero();

        terminer(id);
        ReservationPlaceResponseDTO result = findByIdDTO(id).orElse(null);

        placeWebSocketService.broadcastPlaceUpdate(placeId);
        notificationService.notifyReservationEnded(id, placeNumero);

        return result;
    }

    /**
     * Recupere toutes les reservations de places.
     * @return Liste de toutes les reservations
     */
    public List<ReservationPlace> findAll() {
        logger.debug("Recuperation de toutes les reservations de places");
        List<ReservationPlace> reservations = reservationRepository.findAll();
        logger.info("Nombre de reservations de places recuperees: {}", reservations.size());
        return reservations;
    }

    /**
     * Recherche une reservation par son identifiant.
     * @param id Identifiant de la reservation
     * @return La reservation trouvee ou Optional vide
     */
    public Optional<ReservationPlace> findById(Long id) {
        logger.debug("Recherche de la reservation avec id: {}", id);
        Optional<ReservationPlace> reservation = reservationRepository.findById(id);
        if (reservation.isPresent()) {
            logger.debug("Reservation trouvee: id={}, statut={}", id, reservation.get().getStatut());
        } else {
            logger.warn("Reservation non trouvee avec id: {}", id);
        }
        return reservation;
    }

    /**
     * Recherche les reservations d'une personne.
     * @param personneId Identifiant de la personne
     * @return Liste des reservations de la personne
     */
    public List<ReservationPlace> findByPersonne(Long personneId) {
        logger.debug("Recherche des reservations pour la personne id: {}", personneId);
        return reservationRepository.findByPersonneId(personneId);
    }

    /**
     * Recherche les reservations d'une place.
     * @param placeId Identifiant de la place
     * @return Liste des reservations de la place
     */
    public List<ReservationPlace> findByPlace(Long placeId) {
        logger.debug("Recherche des reservations pour la place id: {}", placeId);
        return reservationRepository.findByPlaceId(placeId);
    }

    /**
     * Recherche les reservations par statut.
     * @param statut Statut recherche
     * @return Liste des reservations correspondantes
     */
    public List<ReservationPlace> findByStatut(StatutReservation statut) {
        logger.debug("Recherche des reservations avec statut: {}", statut);
        return reservationRepository.findByStatut(statut);
    }
    public ReservationPlace create(ReservationPlace reservation) {
        logger.info("Tentative de creation d'une reservation - place={}, personne={}, vehicule={}",
            reservation.getPlace().getId(), reservation.getPersonne().getId(), reservation.getVehicule().getId());

        Place place = placeRepository.findById(reservation.getPlace().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Place", reservation.getPlace().getId()));

        Personne personne = personneRepository.findById(reservation.getPersonne().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Personne", reservation.getPersonne().getId()));

        Vehicule vehicule = vehiculeRepository.findById(reservation.getVehicule().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicule", reservation.getVehicule().getId()));

        if (!vehicule.getPersonne().getId().equals(personne.getId())) {
            throw new ValidationException("Le vehicule n'appartient pas a cette personne");
        }

        if (reservation.getDateDebut() == null || reservation.getDateFin() == null) {
            throw new ValidationException("Les dates de debut et de fin sont obligatoires");
        }
        if (reservation.getDateFin().isBefore(reservation.getDateDebut())) {
            throw new ValidationException("La date de fin doit etre posterieure a la date de debut");
        }
        if (reservation.getDateDebut().isBefore(LocalDateTime.now())) {
            throw new ValidationException("La date de debut ne peut pas etre dans le passe");
        }

        if (place.getStatut() == StatutPlace.OCCUPEE) {
            throw new OperationNotAllowedException("Cette place est actuellement occupee");
        }

        List<StatutReservation> statutsActifs = Arrays.asList(
                StatutReservation.CONFIRMEE, StatutReservation.EN_COURS);
        List<ReservationPlace> conflits = reservationRepository.findConflictingReservations(
                place.getId(),
                reservation.getDateDebut(),
                reservation.getDateFin(),
                statutsActifs);

        if (!conflits.isEmpty()) {
            throw new ConflictException("Cette place est deja reservee sur ce creneau. " +
                    "Conflit avec " + conflits.size() + " reservation(s) existante(s).");
        }

        long nbReservationsActives = reservationRepository.countActiveReservationsByPersonne(personne.getId());
        if (nbReservationsActives >= MAX_RESERVATIONS_ACTIVES) {
            throw new BusinessException("LIMIT_REACHED", "Limite de " + MAX_RESERVATIONS_ACTIVES +
                    " reservations actives atteinte pour cette personne");
        }

        reservation.setPersonne(personne);
        reservation.setPlace(place);
        reservation.setVehicule(vehicule);
        reservation.setStatut(StatutReservation.CONFIRMEE);

        ReservationPlace created = reservationRepository.save(reservation);
        logger.info("Reservation creee avec succes: id={}, place={}, personne={}, debut={}, fin={}",
            created.getId(), place.getNumero(), personne.getMail(),
            created.getDateDebut(), created.getDateFin());

        return created;
    }

    public ReservationPlace update(Long id, ReservationPlace reservationDetails) {
        logger.info("Tentative de mise a jour de la reservation id={}", id);

        ReservationPlace reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", id));

        if (reservationDetails.getDateDebut() != null && reservationDetails.getDateFin() != null) {
            if (reservationDetails.getDateFin().isBefore(reservationDetails.getDateDebut())) {
                throw new ValidationException("La date de fin doit etre posterieure a la date de debut");
            }

            List<StatutReservation> statutsActifs = Arrays.asList(
                    StatutReservation.CONFIRMEE, StatutReservation.EN_COURS);
            List<ReservationPlace> conflits = reservationRepository.findConflictingReservationsExcluding(
                    reservation.getPlace().getId(),
                    reservationDetails.getDateDebut(),
                    reservationDetails.getDateFin(),
                    statutsActifs,
                    id);

            if (!conflits.isEmpty()) {
                throw new ConflictException("Le nouveau creneau entre en conflit avec " +
                        conflits.size() + " reservation(s) existante(s)");
            }

            reservation.setDateDebut(reservationDetails.getDateDebut());
            reservation.setDateFin(reservationDetails.getDateFin());
        }

        if (reservationDetails.getStatut() != null) {
            reservation.setStatut(reservationDetails.getStatut());
        }

        ReservationPlace updated = reservationRepository.save(reservation);
        logger.info("Reservation mise a jour avec succes: id={}, statut={}", id, updated.getStatut());
        return updated;
    }

    /**
     * Annule une reservation.
     * @param id Identifiant de la reservation
     */
    public void annuler(Long id) {
        logger.info("Tentative d'annulation de la reservation id={}", id);

        ReservationPlace reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", id));

        if (reservation.getStatut() == StatutReservation.EN_COURS) {
            throw new OperationNotAllowedException("Impossible d'annuler une reservation en cours. Veuillez la terminer.");
        }

        reservation.setStatut(StatutReservation.ANNULEE);
        reservationRepository.save(reservation);
        logger.info("Reservation annulee avec succes: id={}", id);
    }

    /**
     * Demarre une reservation (passage en cours).
     * @param id Identifiant de la reservation
     */
    public void commencer(Long id) {
        logger.info("Tentative de demarrage de la reservation id={}", id);

        ReservationPlace reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", id));

        if (reservation.getStatut() != StatutReservation.CONFIRMEE) {
            throw new OperationNotAllowedException("Seule une reservation confirmee peut etre commencee. " +
                    "Statut actuel: " + reservation.getStatut());
        }

        reservation.setStatut(StatutReservation.EN_COURS);
        reservationRepository.save(reservation);
        logger.info("Reservation demarree avec succes: id={}", id);
    }

    /**
     * Termine une reservation.
     * @param id Identifiant de la reservation
     */
    public void terminer(Long id) {
        logger.info("Tentative de terminaison de la reservation id={}", id);

        ReservationPlace reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", id));

        reservation.setStatut(StatutReservation.TERMINEE);
        reservationRepository.save(reservation);
        logger.info("Reservation terminee avec succes: id={}", id);
    }

    /**
     * Supprime une reservation avec notifications.
     * @param id Identifiant de la reservation
     */
    public void deleteDTO(Long id) {
        ReservationPlace reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", id));
        Long placeId = reservation.getPlace().getId();

        reservationRepository.deleteById(id);
        logger.info("Reservation supprimee avec succes: id={}", id);

        placeWebSocketService.broadcastPlaceUpdate(placeId);
        notificationService.notifyReservationDeleted(id);
    }

    /**
     * Supprime une reservation (methode interne sans notifications).
     * @param id Identifiant de la reservation
     */
    public void delete(Long id) {
        if (!reservationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Reservation", id);
        }
        reservationRepository.deleteById(id);
    }
}

package esiag.back.services;

import esiag.back.dto.DtoMapper;
import esiag.back.dto.PlaceAvailabilityDTO;
import esiag.back.dto.PlaceCreateDTO;
import esiag.back.dto.PlaceDTO;
import esiag.back.events.PlaceCreatedEvent;
import esiag.back.events.PlaceDeletedEvent;
import esiag.back.events.PlaceUpdatedEvent;
import esiag.back.exceptions.ConflictException;
import esiag.back.exceptions.OperationNotAllowedException;
import esiag.back.exceptions.ResourceNotFoundException;
import esiag.back.models.Place;
import esiag.back.models.ReservationPlace;
import esiag.back.models.StatutPlace;
import esiag.back.models.TypePlace;
import esiag.back.models.Zone;
import esiag.back.repositories.PlaceRepository;
import esiag.back.repositories.ReservationPlaceRepository;
import esiag.back.repositories.ZoneRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service metier pour la gestion des places de parking.
 *
 * ARCHITECTURE MVC:
 * - Contient TOUTE la logique metier (validation, regles de gestion)
 * - Gere le mapping DTO <-> Entite
 * - Publie des evenements (WebSocket decouple via listeners)
 * - Le Controller ne fait que deleguer et retourner la reponse HTTP
 */
@Service
@Transactional
public class PlaceService {

    private static final Logger logger = LoggerFactory.getLogger(PlaceService.class);

    private final PlaceRepository placeRepository;
    private final ReservationPlaceRepository reservationPlaceRepository;
    private final ZoneRepository zoneRepository;
    private final DtoMapper dtoMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final NotificationWebSocketService notificationService;

    public PlaceService(PlaceRepository placeRepository,
                        ReservationPlaceRepository reservationPlaceRepository,
                        ZoneRepository zoneRepository,
                        DtoMapper dtoMapper,
                        ApplicationEventPublisher eventPublisher,
                        NotificationWebSocketService notificationService) {
        this.placeRepository = placeRepository;
        this.reservationPlaceRepository = reservationPlaceRepository;
        this.zoneRepository = zoneRepository;
        this.dtoMapper = dtoMapper;
        this.eventPublisher = eventPublisher;
        this.notificationService = notificationService;
    }

    // ==================== Methodes DTO ====================

    public List<PlaceDTO> findAllDTO() {
        logger.debug("Recuperation de toutes les places (DTO)");
        List<PlaceDTO> result = placeRepository.findAll().stream()
                .map(dtoMapper::toPlaceDTO)
                .collect(Collectors.toList());
        logger.info("Nombre de places recuperees: {}", result.size());
        return result;
    }

    public Optional<PlaceDTO> findByIdDTO(Long id) {
        logger.debug("Recherche de la place avec id: {} (DTO)", id);
        Optional<PlaceDTO> result = placeRepository.findById(id)
                .map(dtoMapper::toPlaceDTO);
        if (result.isEmpty()) {
            logger.warn("Place non trouvee avec id: {}", id);
        }
        return result;
    }

    public List<PlaceDTO> findByStatutDTO(StatutPlace statut) {
        logger.debug("Recherche des places avec statut: {} (DTO)", statut);
        return placeRepository.findByStatut(statut).stream()
                .map(dtoMapper::toPlaceDTO)
                .collect(Collectors.toList());
    }

    public List<PlaceDTO> findByTypeDTO(TypePlace type) {
        logger.debug("Recherche des places de type: {} (DTO)", type);
        return placeRepository.findByType(type).stream()
                .map(dtoMapper::toPlaceDTO)
                .collect(Collectors.toList());
    }

    public List<PlaceDTO> findByZoneDTO(Long zoneId) {
        logger.debug("Recherche des places pour la zone id: {} (DTO)", zoneId);
        return placeRepository.findByZoneId(zoneId).stream()
                .map(dtoMapper::toPlaceDTO)
                .collect(Collectors.toList());
    }

    public List<PlaceDTO> findPlacesDisponiblesDTO() {
        logger.debug("Recherche des places disponibles (DTO)");
        return placeRepository.findByStatut(StatutPlace.LIBRE).stream()
                .map(dtoMapper::toPlaceDTO)
                .collect(Collectors.toList());
    }

    public PlaceDTO createDTO(PlaceCreateDTO createDTO) {
        Place place = new Place();
        place.setNumero(createDTO.getNumero());
        place.setType(createDTO.getType());
        place.setStatut(createDTO.getStatut());
        place.setPositionX(createDTO.getPositionX());
        place.setPositionY(createDTO.getPositionY());

        if (createDTO.getZoneId() != null) {
            Zone zone = zoneRepository.findById(createDTO.getZoneId())
                    .orElseThrow(() -> new ResourceNotFoundException("Zone", createDTO.getZoneId()));
            place.setZone(zone);
        }

        Place created = create(place);
        PlaceDTO result = dtoMapper.toPlaceDTO(created);

        // Notifications temps reel (decouplees)
        eventPublisher.publishEvent(new PlaceCreatedEvent(result.getId()));
        notificationService.notifyPlaceCreated(result.getNumero(), result.getId());

        return result;
    }

    public PlaceDTO updateDTO(Long id, PlaceCreateDTO updateDTO) {
        Place placeDetails = new Place();
        placeDetails.setNumero(updateDTO.getNumero());
        placeDetails.setType(updateDTO.getType());
        placeDetails.setStatut(updateDTO.getStatut());
        placeDetails.setPositionX(updateDTO.getPositionX());
        placeDetails.setPositionY(updateDTO.getPositionY());

        if (updateDTO.getZoneId() != null) {
            Zone zone = zoneRepository.findById(updateDTO.getZoneId())
                    .orElseThrow(() -> new ResourceNotFoundException("Zone", updateDTO.getZoneId()));
            placeDetails.setZone(zone);
        }

        Place updated = update(id, placeDetails);
        PlaceDTO result = dtoMapper.toPlaceDTO(updated);

        // Notifications temps reel (decouplees)
        eventPublisher.publishEvent(new PlaceUpdatedEvent(id));
        notificationService.notifyPlaceUpdated(result.getNumero(), result.getId());

        return result;
    }

    // ==================== Methodes internes (entites) ====================

    public List<Place> findAll() {
        return placeRepository.findAll();
    }

    public Optional<Place> findById(Long id) {
        return placeRepository.findById(id);
    }

    public List<Place> findByStatut(StatutPlace statut) {
        return placeRepository.findByStatut(statut);
    }

    public List<Place> findByType(TypePlace type) {
        return placeRepository.findByType(type);
    }

    public List<Place> findByZone(Long zoneId) {
        return placeRepository.findByZoneId(zoneId);
    }

    public List<Place> findPlacesDisponibles() {
        return placeRepository.findByStatut(StatutPlace.LIBRE);
    }

    public Place create(Place place) {
        logger.info("Creation d'une place: numero={}", place.getNumero());

        if (place.getNumero() != null && placeRepository.existsByNumero(place.getNumero())) {
            throw new ConflictException("Une place avec le numero '" + place.getNumero() + "' existe deja");
        }

        if (place.getPositionX() != null && place.getPositionY() != null) {
            if (placeRepository.existsByPosition(place.getPositionX(), place.getPositionY())) {
                throw new ConflictException("Une place existe deja a la position (" +
                        place.getPositionX() + ", " + place.getPositionY() + ")");
            }
        }

        if (place.getStatut() == null) {
            place.setStatut(StatutPlace.LIBRE);
        }

        Place created = placeRepository.save(place);
        logger.info("Place creee avec succes: id={}, numero={}", created.getId(), created.getNumero());
        return created;
    }

    public Place update(Long id, Place placeDetails) {
        logger.info("Mise a jour de la place id={}", id);

        Place place = placeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Place", id));

        if (placeDetails.getNumero() != null && !placeDetails.getNumero().equals(place.getNumero())) {
            if (placeRepository.existsByNumeroAndIdNot(placeDetails.getNumero(), id)) {
                throw new ConflictException("Une autre place avec le numero '" +
                        placeDetails.getNumero() + "' existe deja");
            }
        }

        if (placeDetails.getPositionX() != null && placeDetails.getPositionY() != null) {
            boolean positionChanged = !placeDetails.getPositionX().equals(place.getPositionX()) ||
                    !placeDetails.getPositionY().equals(place.getPositionY());
            if (positionChanged && placeRepository.existsByPositionAndIdNot(
                    placeDetails.getPositionX(), placeDetails.getPositionY(), id)) {
                throw new ConflictException("Une autre place existe deja a la position (" +
                        placeDetails.getPositionX() + ", " + placeDetails.getPositionY() + ")");
            }
        }

        place.setNumero(placeDetails.getNumero());
        place.setType(placeDetails.getType());
        place.setStatut(placeDetails.getStatut());
        place.setPositionX(placeDetails.getPositionX());
        place.setPositionY(placeDetails.getPositionY());
        place.setZone(placeDetails.getZone());

        return placeRepository.save(place);
    }

    public PlaceDTO updateStatutDTO(Long id, StatutPlace statut) {
        Place place = placeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Place", id));
        place.setStatut(statut);
        placeRepository.save(place);

        // Notification temps reel (decouplee)
        eventPublisher.publishEvent(new PlaceUpdatedEvent(id));

        return dtoMapper.toPlaceDTO(place);
    }

    public void deleteDTO(Long id) {
        Place place = placeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Place", id));

        if (place.getStatut() == StatutPlace.OCCUPEE) {
            throw new OperationNotAllowedException("Impossible de supprimer une place actuellement occupee");
        }
        if (place.getStatut() == StatutPlace.RESERVEE) {
            throw new OperationNotAllowedException("Impossible de supprimer une place actuellement reservee");
        }

        placeRepository.deleteById(id);

        // Notifications temps reel (decouplees)
        eventPublisher.publishEvent(new PlaceDeletedEvent(id));
        notificationService.notifyPlaceDeleted(id);
    }

    // ==================== Methodes disponibilite ====================

    public List<PlaceAvailabilityDTO> findAllWithAvailability() {
        LocalDateTime now = LocalDateTime.now();
        List<Place> places = placeRepository.findAll();

        return places.stream()
                .map(place -> computeAvailability(place, now))
                .collect(Collectors.toList());
    }

    public Optional<PlaceAvailabilityDTO> findByIdWithAvailability(Long id) {
        LocalDateTime now = LocalDateTime.now();
        return placeRepository.findById(id)
                .map(place -> computeAvailability(place, now));
    }

    private PlaceAvailabilityDTO computeAvailability(Place place, LocalDateTime moment) {
        if (place.getStatut() == StatutPlace.HORS_SERVICE) {
            return buildDTO(place, StatutPlace.HORS_SERVICE, null, List.of(), false);
        }

        List<ReservationPlace> activeNow = reservationPlaceRepository
                .findActiveReservationsAtMoment(place.getId(), moment);

        List<ReservationPlace> futureReservations = reservationPlaceRepository
                .findFutureReservations(place.getId(), moment);

        StatutPlace statutActuel;
        PlaceAvailabilityDTO.ReservationInfo reservationEnCours = null;

        if (!activeNow.isEmpty()) {
            ReservationPlace current = activeNow.get(0);
            if (current.getStatut().name().equals("EN_COURS")) {
                statutActuel = StatutPlace.OCCUPEE;
            } else {
                statutActuel = StatutPlace.RESERVEE;
            }
            reservationEnCours = buildReservationInfo(current);
        } else {
            statutActuel = StatutPlace.LIBRE;
        }

        List<PlaceAvailabilityDTO.ReservationInfo> prochainesReservations = futureReservations.stream()
                .limit(5)
                .map(this::buildReservationInfo)
                .collect(Collectors.toList());

        boolean disponible = statutActuel != StatutPlace.HORS_SERVICE;

        return buildDTO(place, statutActuel, reservationEnCours, prochainesReservations, disponible);
    }

    private PlaceAvailabilityDTO buildDTO(Place place, StatutPlace statut,
                                          PlaceAvailabilityDTO.ReservationInfo reservationEnCours,
                                          List<PlaceAvailabilityDTO.ReservationInfo> prochainesReservations,
                                          boolean disponible) {
        return PlaceAvailabilityDTO.builder()
                .id(place.getId())
                .numero(place.getNumero())
                .type(place.getType())
                .statutActuel(statut)
                .positionX(place.getPositionX())
                .positionY(place.getPositionY())
                .zoneId(place.getZone() != null ? place.getZone().getId() : null)
                .zoneNom(place.getZone() != null ? place.getZone().getNom() : null)
                .reservationEnCours(reservationEnCours)
                .prochainesReservations(prochainesReservations)
                .disponiblePourReservation(disponible)
                .build();
    }

    private PlaceAvailabilityDTO.ReservationInfo buildReservationInfo(ReservationPlace reservation) {
        return PlaceAvailabilityDTO.ReservationInfo.builder()
                .id(reservation.getId())
                .dateDebut(reservation.getDateDebut())
                .dateFin(reservation.getDateFin())
                .personneNom(reservation.getPersonne() != null ?
                        reservation.getPersonne().getNom() + " " + reservation.getPersonne().getPrenom() : null)
                .vehiculeImmatriculation(reservation.getVehicule() != null ?
                        reservation.getVehicule().getImmatriculation() : null)
                .build();
    }
}

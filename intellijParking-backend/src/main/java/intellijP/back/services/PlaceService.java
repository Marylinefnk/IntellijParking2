package intellijP.back.services;

import intellijP.back.dto.PlaceAvailabilityDTO;
import intellijP.back.models.Place;
import intellijP.back.models.ReservationPlace;
import intellijP.back.models.StatutPlace;
import intellijP.back.models.TypePlace;
import intellijP.back.repositories.PlaceRepository;
import intellijP.back.repositories.ReservationPlaceRepository;
import intellijP.back.dto.DtoMapper;
import intellijP.back.dto.PlaceAvailabilityDTO;
import intellijP.back.dto.PlaceCreateDTO;
import intellijP.back.dto.PlaceDTO;
import intellijP.back.events.PlaceCreatedEvent;
import intellijP.back.events.PlaceDeletedEvent;
import intellijP.back.events.PlaceUpdatedEvent;
import intellijP.back.exceptions.ConflictException;
import intellijP.back.exceptions.OperationNotAllowedException;
import intellijP.back.exceptions.ResourceNotFoundException;
import intellijP.back.models.Place;
import intellijP.back.models.ReservationPlace;
import intellijP.back.models.StatutPlace;
import intellijP.back.models.TypePlace;
import intellijP.back.models.Zone;
import intellijP.back.repositories.PlaceRepository;
import intellijP.back.repositories.ReservationPlaceRepository;
import intellijP.back.repositories.ZoneRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

        eventPublisher.publishEvent(new PlaceUpdatedEvent(id));
        notificationService.notifyPlaceUpdated(result.getNumero(), result.getId());

        return result;
    }
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

        eventPublisher.publishEvent(new PlaceDeletedEvent(id));
        notificationService.notifyPlaceDeleted(id);
    }
    public Map<String, Long> getStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("total", placeRepository.count());
        stats.put("libre", placeRepository.countByStatut(StatutPlace.LIBRE));
        stats.put("occupee", placeRepository.countByStatut(StatutPlace.OCCUPEE));
        stats.put("reservee", placeRepository.countByStatut(StatutPlace.RESERVEE));
        stats.put("horsService", placeRepository.countByStatut(StatutPlace.HORS_SERVICE));
        return stats;
    }

    public List<PlaceAvailabilityDTO> findAllWithAvailability() {
        LocalDateTime now = LocalDateTime.now();
        List<Place> places = placeRepository.findAll();

        return places.stream()
                .map(place -> computeAvailability(place, now))
                .collect(Collectors.toList());
    }

    public Page<PlaceAvailabilityDTO> findAllWithAvailabilityPaged(Pageable pageable) {
        LocalDateTime now = LocalDateTime.now();
        return placeRepository.findAll(pageable)
                .map(place -> computeAvailability(place, now));
    }

    public Page<PlaceAvailabilityDTO> findAllWithAvailabilityByDate(Pageable pageable, LocalDate date) {
        LocalDateTime debutJournee = date.atStartOfDay();
        LocalDateTime finJournee = date.atTime(LocalTime.MAX);
        return placeRepository.findAll(pageable)
                .map(place -> computeAvailabilityForDay(place, debutJournee, finJournee));
    }

    private PlaceAvailabilityDTO computeAvailabilityForDay(Place place, LocalDateTime debut, LocalDateTime fin) {
        if (place.getStatut() == StatutPlace.HORS_SERVICE) {
            return buildDTO(place, StatutPlace.HORS_SERVICE, null, List.of(), false);
        }

        List<ReservationPlace> reservationsJour = reservationPlaceRepository
                .findReservationsForPlaceOnDay(place.getId(), debut, fin);

        StatutPlace statutActuel = place.getStatut();
        PlaceAvailabilityDTO.ReservationInfo reservationEnCours = null;

        LocalDateTime now = LocalDateTime.now();
        for (ReservationPlace r : reservationsJour) {
            if (!r.getDateDebut().isAfter(now) && !r.getDateFin().isBefore(now)) {
                if (r.getStatut().name().equals("EN_COURS")) {
                    statutActuel = StatutPlace.OCCUPEE;
                } else {
                    statutActuel = StatutPlace.RESERVEE;
                }
                reservationEnCours = buildReservationInfo(r);
                break;
            }
        }

        List<PlaceAvailabilityDTO.ReservationInfo> toutesReservations = reservationsJour.stream()
                .map(this::buildReservationInfo)
                .collect(Collectors.toList());

        boolean disponible = statutActuel != StatutPlace.HORS_SERVICE;

        return buildDTO(place, statutActuel, reservationEnCours, toutesReservations, disponible);
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

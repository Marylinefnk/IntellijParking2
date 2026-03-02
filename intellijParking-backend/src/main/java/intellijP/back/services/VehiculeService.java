package intellijP.back.services;

import intellijP.back.models.ReservationPlace;
import intellijP.back.models.StatutReservation;
import intellijP.back.models.Vehicule;
import intellijP.back.models.TypeVehicule;
import intellijP.back.repositories.VehiculeRepository;
import intellijP.back.repositories.PersonneRepository;
import intellijP.back.repositories.ReservationPlaceRepository;
import intellijP.back.dto.DtoMapper;
import intellijP.back.dto.VehiculeCreateDTO;
import intellijP.back.dto.VehiculeDTO;
import intellijP.back.exceptions.ConflictException;
import intellijP.back.exceptions.OperationNotAllowedException;
import intellijP.back.exceptions.ResourceNotFoundException;
import intellijP.back.exceptions.ValidationException;
import intellijP.back.models.Personne;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class VehiculeService {

    private static final Logger logger = LoggerFactory.getLogger(VehiculeService.class);

    private final VehiculeRepository vehiculeRepository;
    private final PersonneRepository personneRepository;
    private final ReservationPlaceRepository reservationPlaceRepository;
    private final DtoMapper dtoMapper;
    private final NotificationWebSocketService notificationService;

    public VehiculeService(VehiculeRepository vehiculeRepository,
                          PersonneRepository personneRepository,
                          ReservationPlaceRepository reservationPlaceRepository,
                          DtoMapper dtoMapper,
                          NotificationWebSocketService notificationService) {
        this.vehiculeRepository = vehiculeRepository;
        this.personneRepository = personneRepository;
        this.reservationPlaceRepository = reservationPlaceRepository;
        this.dtoMapper = dtoMapper;
        this.notificationService = notificationService;
        logger.info("VehiculeService initialise");
    }
    public List<VehiculeDTO> findAllDTO() {
        logger.debug("Recuperation de tous les vehicules (DTO)");
        List<VehiculeDTO> result = vehiculeRepository.findAll().stream()
                .map(dtoMapper::toVehiculeDTO)
                .collect(Collectors.toList());
        logger.info("Nombre de vehicules recuperes: {}", result.size());
        return result;
    }

    public Optional<VehiculeDTO> findByIdDTO(Long id) {
        logger.debug("Recherche du vehicule avec id: {} (DTO)", id);
        Optional<VehiculeDTO> result = vehiculeRepository.findById(id)
                .map(dtoMapper::toVehiculeDTO);
        if (result.isEmpty()) {
            logger.warn("Vehicule non trouve avec id: {}", id);
        }
        return result;
    }

    public List<VehiculeDTO> findByPersonneDTO(Long personneId) {
        logger.debug("Recherche des vehicules pour la personne id: {} (DTO)", personneId);
        return vehiculeRepository.findByPersonneId(personneId).stream()
                .map(dtoMapper::toVehiculeDTO)
                .collect(Collectors.toList());
    }

    public Optional<VehiculeDTO> findByImmatriculationDTO(String immatriculation) {
        logger.debug("Recherche du vehicule avec immatriculation: {} (DTO)", immatriculation);
        return vehiculeRepository.findByImmatriculation(immatriculation)
                .map(dtoMapper::toVehiculeDTO);
    }

    public List<VehiculeDTO> findByTypeDTO(TypeVehicule typeVehicule) {
        logger.debug("Recherche des vehicules de type: {} (DTO)", typeVehicule);
        return vehiculeRepository.findByTypeVehicule(typeVehicule).stream()
                .map(dtoMapper::toVehiculeDTO)
                .collect(Collectors.toList());
    }

    public VehiculeDTO createDTO(VehiculeCreateDTO createDTO) {
        logger.info("Tentative de creation d'un vehicule: {}", createDTO.getImmatriculation());

        Vehicule vehicule = new Vehicule();
        vehicule.setImmatriculation(createDTO.getImmatriculation());
        vehicule.setTypeVehicule(createDTO.getTypeVehicule());

        if (createDTO.getPersonneId() != null) {
            Personne personne = personneRepository.findById(createDTO.getPersonneId())
                    .orElseThrow(() -> new ResourceNotFoundException("Personne", createDTO.getPersonneId()));
            vehicule.setPersonne(personne);
        }

        Vehicule created = create(vehicule);
        VehiculeDTO result = dtoMapper.toVehiculeDTO(created);

        notificationService.notifyVehiculeCreated(result.getImmatriculation(), result.getId());

        return result;
    }

    public VehiculeDTO updateDTO(Long id, VehiculeCreateDTO updateDTO) {
        logger.info("Tentative de mise a jour du vehicule id={}", id);

        Vehicule vehiculeDetails = new Vehicule();
        vehiculeDetails.setImmatriculation(updateDTO.getImmatriculation());
        vehiculeDetails.setTypeVehicule(updateDTO.getTypeVehicule());

        if (updateDTO.getPersonneId() != null) {
            Personne personne = personneRepository.findById(updateDTO.getPersonneId())
                    .orElseThrow(() -> new ResourceNotFoundException("Personne", updateDTO.getPersonneId()));
            vehiculeDetails.setPersonne(personne);
        }

        Vehicule updated = update(id, vehiculeDetails);
        VehiculeDTO result = dtoMapper.toVehiculeDTO(updated);

        notificationService.notifyVehiculeUpdated(result.getImmatriculation(), result.getId());

        return result;
    }
    public List<Vehicule> findAll() {
        return vehiculeRepository.findAll();
    }

    public Optional<Vehicule> findById(Long id) {
        return vehiculeRepository.findById(id);
    }

    public List<Vehicule> findByPersonne(Long personneId) {
        return vehiculeRepository.findByPersonneId(personneId);
    }

    public Optional<Vehicule> findByImmatriculation(String immatriculation) {
        return vehiculeRepository.findByImmatriculation(immatriculation);
    }

    public List<Vehicule> findByType(TypeVehicule typeVehicule) {
        return vehiculeRepository.findByTypeVehicule(typeVehicule);
    }

    public Vehicule create(Vehicule vehicule) {
        logger.info("Creation d'un vehicule: immatriculation={}", vehicule.getImmatriculation());

        if (vehicule.getImmatriculation() == null || vehicule.getImmatriculation().trim().isEmpty()) {
            logger.error("Echec creation: immatriculation manquante");
            throw new ValidationException("L'immatriculation est obligatoire");
        }

        if (vehiculeRepository.findByImmatriculation(vehicule.getImmatriculation()).isPresent()) {
            logger.error("Echec creation: immatriculation '{}' deja utilisee", vehicule.getImmatriculation());
            throw new ConflictException("Un vehicule avec l'immatriculation '" +
                    vehicule.getImmatriculation() + "' existe deja");
        }

        if (vehicule.getTypeVehicule() == null) {
            logger.error("Echec creation: type de vehicule manquant");
            throw new ValidationException("Le type de vehicule est obligatoire");
        }

        Vehicule created = vehiculeRepository.save(vehicule);
        logger.info("Vehicule cree avec succes: id={}, immatriculation={}",
            created.getId(), created.getImmatriculation());
        return created;
    }

    public Vehicule update(Long id, Vehicule vehiculeDetails) {
        logger.info("Mise a jour du vehicule id={}", id);

        Vehicule vehicule = vehiculeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicule", id));

        if (vehiculeDetails.getImmatriculation() != null &&
            !vehiculeDetails.getImmatriculation().equals(vehicule.getImmatriculation())) {
            if (vehiculeRepository.findByImmatriculation(vehiculeDetails.getImmatriculation()).isPresent()) {
                logger.error("Echec mise a jour: immatriculation '{}' deja utilisee", vehiculeDetails.getImmatriculation());
                throw new ConflictException("Un autre vehicule avec l'immatriculation '" +
                        vehiculeDetails.getImmatriculation() + "' existe deja");
            }
        }

        vehicule.setImmatriculation(vehiculeDetails.getImmatriculation());
        vehicule.setTypeVehicule(vehiculeDetails.getTypeVehicule());
        vehicule.setPersonne(vehiculeDetails.getPersonne());

        return vehiculeRepository.save(vehicule);
    }

    public void deleteDTO(Long id) {
        logger.info("Tentative de suppression du vehicule id={}", id);

        Vehicule vehicule = vehiculeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicule", id));

        List<?> reservationsActives = reservationPlaceRepository.findByVehiculeId(id);
        long nbActives = reservationsActives.stream()
                .filter(r -> {
                    if (r instanceof ReservationPlace) {
                        ReservationPlace rp = (ReservationPlace) r;
                        return rp.getStatut() == StatutReservation.CONFIRMEE ||
                               rp.getStatut() == StatutReservation.EN_COURS;
                    }
                    return false;
                })
                .count();

        if (nbActives > 0) {
            logger.error("Echec suppression: vehicule {} a {} reservation(s) active(s)",
                vehicule.getImmatriculation(), nbActives);
            throw new OperationNotAllowedException("Impossible de supprimer ce vehicule car il a " +
                    nbActives + " reservation(s) active(s)");
        }

        vehiculeRepository.deleteById(id);
        logger.info("Vehicule supprime avec succes: id={}", id);

        notificationService.notifyVehiculeDeleted(id);
    }

}

package esiag.back.services;

import esiag.back.models.ServiceEntity;
import esiag.back.models.TypeService;
import esiag.back.repositories.ReservationServiceRepository;
import esiag.back.repositories.ServiceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service metier pour la gestion des services du parking.
 * Les services incluent : depannage, laverie, etc.
 * Implemente les regles metier suivantes :
 * - Le type de service est obligatoire
 * - Impossibilite de supprimer un service avec des reservations actives
 */
@Service
@Transactional
public class ServiceEntityService {

    private static final Logger logger = LoggerFactory.getLogger(ServiceEntityService.class);

    private final ServiceRepository serviceRepository;
    private final ReservationServiceRepository reservationServiceRepository;

    public ServiceEntityService(ServiceRepository serviceRepository,
                                ReservationServiceRepository reservationServiceRepository) {
        this.serviceRepository = serviceRepository;
        this.reservationServiceRepository = reservationServiceRepository;
        logger.info("ServiceEntityService initialise");
    }

    /**
     * Recupere tous les services.
     * @return Liste de tous les services
     */
    public List<ServiceEntity> findAll() {
        logger.debug("Recuperation de tous les services");
        List<ServiceEntity> services = serviceRepository.findAll();
        logger.info("Nombre de services recuperes: {}", services.size());
        return services;
    }

    /**
     * Recherche un service par son identifiant.
     * @param id Identifiant du service
     * @return Le service trouve ou Optional vide
     */
    public Optional<ServiceEntity> findById(Long id) {
        logger.debug("Recherche du service avec id: {}", id);
        Optional<ServiceEntity> service = serviceRepository.findById(id);
        if (service.isPresent()) {
            logger.debug("Service trouve: {} (id={})", service.get().getTypeService(), id);
        } else {
            logger.warn("Service non trouve avec id: {}", id);
        }
        return service;
    }

    /**
     * Recherche les services par type.
     * @param typeService Type de service (DEPANNAGE, LAVERIE)
     * @return Liste des services correspondants
     */
    public List<ServiceEntity> findByTypeService(TypeService typeService) {
        logger.debug("Recherche des services de type: {}", typeService);
        return serviceRepository.findByTypeService(typeService);
    }

    /**
     * Cree un nouveau service.
     * Regles metier :
     * - Le type de service est obligatoire
     *
     * @param service Le service a creer
     * @return Le service cree
     * @throws RuntimeException si le type est manquant
     */
    public ServiceEntity create(ServiceEntity service) {
        logger.info("Tentative de creation d'un service: type={}", service.getTypeService());

        // Validation du type de service
        if (service.getTypeService() == null) {
            logger.error("Echec creation: type de service manquant");
            throw new RuntimeException("Le type de service est obligatoire");
        }

        ServiceEntity created = serviceRepository.save(service);
        logger.info("Service cree avec succes: id={}, type={}", created.getId(), created.getTypeService());
        return created;
    }

    /**
     * Met a jour un service existant.
     * @param id Identifiant du service
     * @param serviceDetails Nouvelles donnees
     * @return Le service mis a jour
     */
    public ServiceEntity update(Long id, ServiceEntity serviceDetails) {
        logger.info("Tentative de mise a jour du service id={}", id);

        ServiceEntity service = serviceRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Echec mise a jour: service non trouve avec id={}", id);
                    return new RuntimeException("Service non trouve avec l'id: " + id);
                });

        TypeService ancienType = service.getTypeService();
        service.setTypeService(serviceDetails.getTypeService());
        service.setDescription(serviceDetails.getDescription());

        ServiceEntity updated = serviceRepository.save(service);
        logger.info("Service mis a jour avec succes: id={}, ancien_type={}, nouveau_type={}",
            id, ancienType, updated.getTypeService());
        return updated;
    }

    /**
     * Supprime un service.
     * @param id Identifiant du service
     * @throws RuntimeException si le service a des reservations actives
     */
    public void delete(Long id) {
        logger.info("Tentative de suppression du service id={}", id);

        ServiceEntity service = serviceRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Echec suppression: service non trouve avec id={}", id);
                    return new RuntimeException("Service non trouve avec l'id: " + id);
                });

        // Verification des reservations actives
        if (!reservationServiceRepository.findByServiceId(id).isEmpty()) {
            logger.error("Echec suppression: service {} a des reservations associees", service.getTypeService());
            throw new RuntimeException("Impossible de supprimer ce service car il a des reservations associees");
        }

        TypeService typeService = service.getTypeService();
        serviceRepository.deleteById(id);
        logger.info("Service supprime avec succes: id={}, type={}", id, typeService);
    }
}

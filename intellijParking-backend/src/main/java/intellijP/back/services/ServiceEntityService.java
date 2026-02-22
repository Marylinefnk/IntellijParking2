package intellijP.back.services;

import intellijP.back.models.ServiceEntity;
import intellijP.back.models.TypeService;
import intellijP.back.repositories.ReservationServiceRepository;
import intellijP.back.repositories.ServiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service métier pour la gestion des services du parking.
 * Les services incluent : dépannage, laverie, etc.
 * Implémente les règles métier suivantes :
 * - Le type de service est obligatoire
 * - Impossibilité de supprimer un service avec des réservations actives
 */
@Service
@Transactional
public class ServiceEntityService {

    private final ServiceRepository serviceRepository;
    private final ReservationServiceRepository reservationServiceRepository;

    public ServiceEntityService(ServiceRepository serviceRepository,
                                ReservationServiceRepository reservationServiceRepository) {
        this.serviceRepository = serviceRepository;
        this.reservationServiceRepository = reservationServiceRepository;
    }

    /**
     * Récupère tous les services.
     * @return Liste de tous les services
     */
    public List<ServiceEntity> findAll() {
        return serviceRepository.findAll();
    }

    /**
     * Recherche un service par son identifiant.
     * @param id Identifiant du service
     * @return Le service trouvé ou Optional vide
     */
    public Optional<ServiceEntity> findById(Long id) {
        return serviceRepository.findById(id);
    }

    /**
     * Recherche les services par type.
     * @param typeService Type de service (DEPANNAGE, LAVERIE)
     * @return Liste des services correspondants
     */
    public List<ServiceEntity> findByTypeService(TypeService typeService) {
        return serviceRepository.findByTypeService(typeService);
    }

    /**
     * Crée un nouveau service.
     * Règles métier :
     * - Le type de service est obligatoire
     *
     * @param service Le service à créer
     * @return Le service créé
     * @throws RuntimeException si le type est manquant
     */
    public ServiceEntity create(ServiceEntity service) {
        // Validation du type de service
        if (service.getTypeService() == null) {
            throw new RuntimeException("Le type de service est obligatoire");
        }

        return serviceRepository.save(service);
    }

    /**
     * Met à jour un service existant.
     * @param id Identifiant du service
     * @param serviceDetails Nouvelles données
     * @return Le service mis à jour
     */
    public ServiceEntity update(Long id, ServiceEntity serviceDetails) {
        ServiceEntity service = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service non trouvé avec l'id: " + id));

        service.setTypeService(serviceDetails.getTypeService());
        service.setDescription(serviceDetails.getDescription());

        return serviceRepository.save(service);
    }

    /**
     * Supprime un service.
     * @param id Identifiant du service
     * @throws RuntimeException si le service a des réservations actives
     */
    public void delete(Long id) {
        ServiceEntity service = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service non trouvé avec l'id: " + id));

        // Vérification des réservations actives
        if (!reservationServiceRepository.findByServiceId(id).isEmpty()) {
            throw new RuntimeException("Impossible de supprimer ce service car il a des réservations associées");
        }

        serviceRepository.deleteById(id);
    }
}

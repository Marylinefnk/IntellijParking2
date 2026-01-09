package esiag.back.services;

import esiag.back.models.Zone;
import esiag.back.repositories.PlaceRepository;
import esiag.back.repositories.ZoneRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service métier pour la gestion des zones de parking.
 * Implémente les règles métier suivantes :
 * - Unicité du nom de zone
 * - Impossibilité de supprimer une zone contenant des places
 */
@Service
@Transactional
public class ZoneService {

    private final ZoneRepository zoneRepository;
    private final PlaceRepository placeRepository;

    public ZoneService(ZoneRepository zoneRepository, PlaceRepository placeRepository) {
        this.zoneRepository = zoneRepository;
        this.placeRepository = placeRepository;
    }

    /**
     * Récupère toutes les zones.
     * @return Liste de toutes les zones
     */
    public List<Zone> findAll() {
        return zoneRepository.findAll();
    }

    /**
     * Recherche une zone par son identifiant.
     * @param id Identifiant de la zone
     * @return La zone trouvée ou Optional vide
     */
    public Optional<Zone> findById(Long id) {
        return zoneRepository.findById(id);
    }

    /**
     * Recherche une zone par son nom.
     * @param nom Nom de la zone
     * @return La zone trouvée ou Optional vide
     */
    public Optional<Zone> findByNom(String nom) {
        return zoneRepository.findByNom(nom);
    }

    /**
     * Crée une nouvelle zone.
     * Règles métier :
     * - Le nom de la zone doit être unique
     * - Le nom est obligatoire
     *
     * @param zone La zone à créer
     * @return La zone créée
     * @throws RuntimeException si le nom existe déjà
     */
    public Zone create(Zone zone) {
        // Validation du nom
        if (zone.getNom() == null || zone.getNom().trim().isEmpty()) {
            throw new RuntimeException("Le nom de la zone est obligatoire");
        }

        // Vérification de l'unicité du nom
        if (zoneRepository.findByNom(zone.getNom()).isPresent()) {
            throw new RuntimeException("Une zone avec le nom '" + zone.getNom() + "' existe déjà");
        }

        return zoneRepository.save(zone);
    }

    /**
     * Met à jour une zone existante.
     * @param id Identifiant de la zone
     * @param zoneDetails Nouvelles données
     * @return La zone mise à jour
     */
    public Zone update(Long id, Zone zoneDetails) {
        Zone zone = zoneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Zone non trouvée avec l'id: " + id));

        // Vérification de l'unicité du nom si modifié
        if (zoneDetails.getNom() != null && !zoneDetails.getNom().equals(zone.getNom())) {
            if (zoneRepository.findByNom(zoneDetails.getNom()).isPresent()) {
                throw new RuntimeException("Une autre zone avec le nom '" +
                        zoneDetails.getNom() + "' existe déjà");
            }
        }

        zone.setNom(zoneDetails.getNom());
        zone.setDescription(zoneDetails.getDescription());

        return zoneRepository.save(zone);
    }

    /**
     * Supprime une zone.
     * @param id Identifiant de la zone
     * @throws RuntimeException si la zone contient des places
     */
    public void delete(Long id) {
        Zone zone = zoneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Zone non trouvée avec l'id: " + id));

        // Vérification que la zone ne contient pas de places
        if (!placeRepository.findByZoneId(id).isEmpty()) {
            throw new RuntimeException("Impossible de supprimer cette zone car elle contient des places. " +
                    "Veuillez d'abord supprimer ou déplacer les places de cette zone.");
        }

        zoneRepository.deleteById(id);
    }
}

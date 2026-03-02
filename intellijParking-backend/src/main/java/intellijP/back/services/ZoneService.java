package intellijP.back.services;

import intellijP.back.models.Zone;
import intellijP.back.repositories.PlaceRepository;
import intellijP.back.repositories.ZoneRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service metier pour la gestion des zones de parking.
 */
@Service
@Transactional
public class ZoneService {

    private static final Logger logger = LoggerFactory.getLogger(ZoneService.class);

    private final ZoneRepository zoneRepository;
    private final PlaceRepository placeRepository;

    public ZoneService(ZoneRepository zoneRepository, PlaceRepository placeRepository) {
        this.zoneRepository = zoneRepository;
        this.placeRepository = placeRepository;
        logger.info("ZoneService initialise");
    }

    /**
     * Recupere toutes les zones.
     * @return Liste de toutes les zones
     */
    public List<Zone> findAll() {
        logger.debug("Recuperation de toutes les zones");
        List<Zone> zones = zoneRepository.findAll();
        logger.info("Nombre de zones recuperees: {}", zones.size());
        return zones;
    }

    /**
     * Recherche une zone par son identifiant.
     * @param id Identifiant de la zone
     * @return La zone trouvee ou Optional vide
     */
    public Optional<Zone> findById(Long id) {
        logger.debug("Recherche de la zone avec id: {}", id);
        Optional<Zone> zone = zoneRepository.findById(id);
        if (zone.isPresent()) {
            logger.debug("Zone trouvee: {} (id={})", zone.get().getNom(), id);
        } else {
            logger.warn("Zone non trouvee avec id: {}", id);
        }
        return zone;
    }

    /**
     * Recherche une zone par son nom.
     * @param nom Nom de la zone
     * @return La zone trouvee ou Optional vide
     */
    public Optional<Zone> findByNom(String nom) {
        logger.debug("Recherche de la zone avec nom: {}", nom);
        return zoneRepository.findByNom(nom);
    }

    /**
     * @param zone La zone a creer
     * @return La zone creee
     * @throws RuntimeException si le nom existe deja
     */
    public Zone create(Zone zone) {
        logger.info("Tentative de creation d'une zone: nom={}", zone.getNom());

        if (zone.getNom() == null || zone.getNom().trim().isEmpty()) {
            logger.error("Echec creation: nom de zone manquant");
            throw new RuntimeException("Le nom de la zone est obligatoire");
        }

        if (zoneRepository.findByNom(zone.getNom()).isPresent()) {
            logger.error("Echec creation: nom '{}' deja utilise", zone.getNom());
            throw new RuntimeException("Une zone avec le nom '" + zone.getNom() + "' existe deja");
        }

        Zone created = zoneRepository.save(zone);
        logger.info("Zone creee avec succes: id={}, nom={}", created.getId(), created.getNom());
        return created;
    }

    /**
     * Met a jour une zone existante.
     * @param id Identifiant de la zone
     * @param zoneDetails Nouvelles donnees
     * @return La zone mise a jour
     */
    public Zone update(Long id, Zone zoneDetails) {
        logger.info("Tentative de mise a jour de la zone id={}", id);

        Zone zone = zoneRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Echec mise a jour: zone non trouvee avec id={}", id);
                    return new RuntimeException("Zone non trouvee avec l'id: " + id);
                });

        if (zoneDetails.getNom() != null && !zoneDetails.getNom().equals(zone.getNom())) {
            if (zoneRepository.findByNom(zoneDetails.getNom()).isPresent()) {
                logger.error("Echec mise a jour: nom '{}' deja utilise", zoneDetails.getNom());
                throw new RuntimeException("Une autre zone avec le nom '" +
                        zoneDetails.getNom() + "' existe deja");
            }
        }

        String ancienNom = zone.getNom();
        zone.setNom(zoneDetails.getNom());
        zone.setDescription(zoneDetails.getDescription());

        Zone updated = zoneRepository.save(zone);
        logger.info("Zone mise a jour avec succes: id={}, ancien_nom={}, nouveau_nom={}",
            id, ancienNom, updated.getNom());
        return updated;
    }

    /**
     * Supprime une zone.
     * @param id Identifiant de la zone
     * @throws RuntimeException si la zone contient des places
     */
    public void delete(Long id) {
        logger.info("Tentative de suppression de la zone id={}", id);

        Zone zone = zoneRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Echec suppression: zone non trouvee avec id={}", id);
                    return new RuntimeException("Zone non trouvee avec l'id: " + id);
                });

        if (!placeRepository.findByZoneId(id).isEmpty()) {
            logger.error("Echec suppression: zone {} contient des places", zone.getNom());
            throw new RuntimeException("Impossible de supprimer cette zone car elle contient des places. " +
                    "Veuillez d'abord supprimer ou deplacer les places de cette zone.");
        }

        String nom = zone.getNom();
        zoneRepository.deleteById(id);
        logger.info("Zone supprimee avec succes: id={}, nom={}", id, nom);
    }
}

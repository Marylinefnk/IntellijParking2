package intellijP.back.services;

import intellijP.back.repositories.PlaceRepository;
import intellijP.back.repositories.StationnementRepository;
import intellijP.back.repositories.VehiculeRepository;
import intellijP.back.models.*;
import intellijP.back.repositories.PlaceRepository;
import intellijP.back.repositories.StationnementRepository;
import intellijP.back.repositories.VehiculeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

/**
 * Service metier pour la gestion des stationnements.
 */
@Service
@Transactional
public class StationnementService {

    private static final Logger logger = LoggerFactory.getLogger(StationnementService.class);

    private final StationnementRepository stationnementRepository;
    private final VehiculeRepository vehiculeRepository;
    private final PlaceRepository placeRepository;

    /** Tarif horaire par défaut en euros */
    private static final double TARIF_HORAIRE_DEFAUT = 2.0;

    public StationnementService(StationnementRepository stationnementRepository,
                                VehiculeRepository vehiculeRepository,
                                PlaceRepository placeRepository) {
        this.stationnementRepository = stationnementRepository;
        this.vehiculeRepository = vehiculeRepository;
        this.placeRepository = placeRepository;
        logger.info("StationnementService initialise");
    }

    /**
     * Recupere tous les stationnements.
     * @return Liste de tous les stationnements
     */
    public List<Stationnement> findAll() {
        logger.debug("Recuperation de tous les stationnements");
        List<Stationnement> stationnements = stationnementRepository.findAll();
        logger.info("Nombre de stationnements recuperes: {}", stationnements.size());
        return stationnements;
    }

    /**
     * Recherche un stationnement par son identifiant.
     * @param id Identifiant du stationnement
     * @return Le stationnement trouve ou Optional vide
     */
    public Optional<Stationnement> findById(Long id) {
        logger.debug("Recherche du stationnement avec id: {}", id);
        Optional<Stationnement> stationnement = stationnementRepository.findById(id);
        if (stationnement.isPresent()) {
            logger.debug("Stationnement trouve: id={}", id);
        } else {
            logger.warn("Stationnement non trouve avec id: {}", id);
        }
        return stationnement;
    }

    /**
     * Recherche les stationnements d'une place.
     * @param placeId Identifiant de la place
     * @return Liste des stationnements de la place
     */
    public List<Stationnement> findByPlace(Long placeId) {
        logger.debug("Recherche des stationnements pour la place id: {}", placeId);
        return stationnementRepository.findByPlaceId(placeId);
    }

    /**
     * Recherche les stationnements d'un vehicule.
     * @param vehiculeId Identifiant du vehicule
     * @return Liste des stationnements du vehicule
     */
    public List<Stationnement> findByVehicule(Long vehiculeId) {
        logger.debug("Recherche des stationnements pour le vehicule id: {}", vehiculeId);
        return stationnementRepository.findByVehiculeId(vehiculeId);
    }

    /**
     * Recherche le stationnement actif d'un vehicule.
     * @param vehiculeId Identifiant du vehicule
     * @return Le stationnement actif ou Optional vide
     */
    public Optional<Stationnement> findActiveByVehicule(Long vehiculeId) {
        logger.debug("Recherche du stationnement actif pour le vehicule id: {}", vehiculeId);
        return stationnementRepository.findActiveStationnementsByVehicule(vehiculeId).stream().findFirst();
    }

    /**
     * Recherche le stationnement actif d'une place.
     * @param placeId Identifiant de la place
     * @return Le stationnement actif ou Optional vide
     */
    public Optional<Stationnement> findActiveByPlace(Long placeId) {
        logger.debug("Recherche du stationnement actif pour la place id: {}", placeId);
        return stationnementRepository.findActiveStationnementsByPlace(placeId).stream().findFirst();
    }

    /**
     * Recherche les stationnements dans une periode donnee.
     * @param debut Date de debut
     * @param fin Date de fin
     * @return Liste des stationnements correspondants
     */
    public List<Stationnement> findByDateEntreeBetween(LocalDateTime debut, LocalDateTime fin) {
        logger.debug("Recherche des stationnements entre {} et {}", debut, fin);
        return stationnementRepository.findByDateEntreeBetween(debut, fin);
    }

    /**
     * Enregistre l'entree d'un vehicule sur une place.
     * @param stationnement Les donnees d'entree
     * @return Le stationnement cree
     * @throws RuntimeException si une regle metier est violee
     */
    public Stationnement entrer(Stationnement stationnement) {
        logger.info("Tentative d'entree - vehicule={}, place={}",
            stationnement.getVehicule().getId(), stationnement.getPlace().getId());

        Vehicule vehicule = vehiculeRepository.findById(stationnement.getVehicule().getId())
                .orElseThrow(() -> {
                    logger.error("Echec entree: vehicule non trouve avec id={}",
                        stationnement.getVehicule().getId());
                    return new RuntimeException("Vehicule non trouve avec l'id: " +
                            stationnement.getVehicule().getId());
                });

        Place place = placeRepository.findById(stationnement.getPlace().getId())
                .orElseThrow(() -> {
                    logger.error("Echec entree: place non trouvee avec id={}",
                        stationnement.getPlace().getId());
                    return new RuntimeException("Place non trouvee avec l'id: " +
                            stationnement.getPlace().getId());
                });

        if (place.getStatut() != StatutPlace.LIBRE) {
            logger.error("Echec entree: place {} n'est pas libre (statut: {})",
                place.getNumero(), place.getStatut());
            throw new RuntimeException("Cette place n'est pas libre. Statut actuel: " + place.getStatut());
        }

        Optional<Stationnement> stationnementActif = stationnementRepository
                .findActiveStationnementsByVehicule(vehicule.getId()).stream().findFirst();
        if (stationnementActif.isPresent()) {
            logger.error("Echec entree: vehicule {} deja stationne sur place {}",
                vehicule.getImmatriculation(), stationnementActif.get().getPlace().getNumero());
            throw new RuntimeException("Ce vehicule est deja stationne sur la place " +
                    stationnementActif.get().getPlace().getNumero());
        }

        Optional<Stationnement> placeOccupee = stationnementRepository
                .findActiveStationnementsByPlace(place.getId()).stream().findFirst();
        if (placeOccupee.isPresent()) {
            logger.error("Echec entree: place {} deja occupee par vehicule {}",
                place.getNumero(), placeOccupee.get().getVehicule().getImmatriculation());
            throw new RuntimeException("Cette place est deja occupee par le vehicule " +
                    placeOccupee.get().getVehicule().getImmatriculation());
        }

        stationnement.setVehicule(vehicule);
        stationnement.setPlace(place);
        stationnement.setDateEntree(LocalDateTime.now());
        stationnement.setDateSortie(null);
        stationnement.setTarif(null);
        stationnement.setDureeMin(null);

        place.setStatut(StatutPlace.OCCUPEE);
        placeRepository.save(place);

        Stationnement created = stationnementRepository.save(stationnement);
        logger.info("Entree enregistree avec succes: id={}, vehicule={}, place={}, dateEntree={}",
            created.getId(), vehicule.getImmatriculation(), place.getNumero(), created.getDateEntree());

        return created;
    }

    /**
     * Enregistre la sortie d'un vehicule.
     * Calcule automatiquement la duree et le tarif.
     *
     * @param id Identifiant du stationnement
     * @param tarifHoraire Tarif horaire a appliquer (utilise le tarif par defaut si null)
     * @return Le stationnement termine
     * @throws RuntimeException si le stationnement n'existe pas ou est deja termine
     */
    public Stationnement sortir(Long id, Double tarifHoraire) {
        logger.info("Tentative de sortie - stationnement id={}", id);

        Stationnement stationnement = stationnementRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Echec sortie: stationnement non trouve avec id={}", id);
                    return new RuntimeException("Stationnement non trouve avec l'id: " + id);
                });

        if (stationnement.getDateSortie() != null) {
            logger.error("Echec sortie: stationnement {} deja termine le {}",
                id, stationnement.getDateSortie());
            throw new RuntimeException("Ce stationnement est deja termine depuis le " +
                    stationnement.getDateSortie());
        }

        LocalDateTime dateSortie = LocalDateTime.now();
        stationnement.setDateSortie(dateSortie);

        long dureeMinutes = ChronoUnit.MINUTES.between(stationnement.getDateEntree(), dateSortie);
        stationnement.setDureeMin((int) dureeMinutes);

        double tarifApplique = tarifHoraire != null ? tarifHoraire : TARIF_HORAIRE_DEFAUT;
        double tarif = (dureeMinutes / 60.0) * tarifApplique;
        stationnement.setTarif(Math.round(tarif * 100.0) / 100.0);

        Place place = stationnement.getPlace();
        place.setStatut(StatutPlace.LIBRE);
        placeRepository.save(place);

        Stationnement updated = stationnementRepository.save(stationnement);
        logger.info("Sortie enregistree avec succes: id={}, vehicule={}, place={}, duree={}min, tarif={}EUR",
            id, stationnement.getVehicule().getImmatriculation(), place.getNumero(),
            dureeMinutes, updated.getTarif());

        return updated;
    }

    /**
     * Supprime un stationnement.
     * @param id Identifiant du stationnement
     * @throws RuntimeException si le stationnement est actif
     */
    public void delete(Long id) {
        logger.info("Tentative de suppression du stationnement id={}", id);

        Stationnement stationnement = stationnementRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Echec suppression: stationnement non trouve avec id={}", id);
                    return new RuntimeException("Stationnement non trouve avec l'id: " + id);
                });

        if (stationnement.getDateSortie() == null) {
            logger.warn("Suppression d'un stationnement actif - liberation de la place {}",
                stationnement.getPlace().getNumero());
            Place place = stationnement.getPlace();
            place.setStatut(StatutPlace.LIBRE);
            placeRepository.save(place);
        }

        stationnementRepository.deleteById(id);
        logger.info("Stationnement supprime avec succes: id={}", id);
    }
}

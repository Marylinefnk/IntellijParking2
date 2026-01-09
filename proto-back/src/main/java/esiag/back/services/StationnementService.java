package esiag.back.services;

import esiag.back.models.*;
import esiag.back.repositories.PlaceRepository;
import esiag.back.repositories.StationnementRepository;
import esiag.back.repositories.VehiculeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

/**
 * Service métier pour la gestion des stationnements.
 * Implémente les règles métier suivantes :
 * - Un véhicule ne peut pas stationner sur deux places en même temps
 * - Une place ne peut accueillir qu'un seul véhicule à la fois
 * - La place doit être libre pour qu'un véhicule puisse y entrer
 * - Calcul automatique du tarif et de la durée à la sortie
 */
@Service
@Transactional
public class StationnementService {

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
    }

    /**
     * Récupère tous les stationnements.
     * @return Liste de tous les stationnements
     */
    public List<Stationnement> findAll() {
        return stationnementRepository.findAll();
    }

    /**
     * Recherche un stationnement par son identifiant.
     * @param id Identifiant du stationnement
     * @return Le stationnement trouvé ou Optional vide
     */
    public Optional<Stationnement> findById(Long id) {
        return stationnementRepository.findById(id);
    }

    /**
     * Recherche les stationnements d'une place.
     * @param placeId Identifiant de la place
     * @return Liste des stationnements de la place
     */
    public List<Stationnement> findByPlace(Long placeId) {
        return stationnementRepository.findByPlaceId(placeId);
    }

    /**
     * Recherche les stationnements d'un véhicule.
     * @param vehiculeId Identifiant du véhicule
     * @return Liste des stationnements du véhicule
     */
    public List<Stationnement> findByVehicule(Long vehiculeId) {
        return stationnementRepository.findByVehiculeId(vehiculeId);
    }

    /**
     * Recherche le stationnement actif d'un véhicule.
     * @param vehiculeId Identifiant du véhicule
     * @return Le stationnement actif ou Optional vide
     */
    public Optional<Stationnement> findActiveByVehicule(Long vehiculeId) {
        return stationnementRepository.findActiveStationnementByVehicule(vehiculeId);
    }

    /**
     * Recherche le stationnement actif d'une place.
     * @param placeId Identifiant de la place
     * @return Le stationnement actif ou Optional vide
     */
    public Optional<Stationnement> findActiveByPlace(Long placeId) {
        return stationnementRepository.findActiveStationnementByPlace(placeId);
    }

    /**
     * Recherche les stationnements dans une période donnée.
     * @param debut Date de début
     * @param fin Date de fin
     * @return Liste des stationnements correspondants
     */
    public List<Stationnement> findByDateEntreeBetween(LocalDateTime debut, LocalDateTime fin) {
        return stationnementRepository.findByDateEntreeBetween(debut, fin);
    }

    /**
     * Enregistre l'entrée d'un véhicule sur une place.
     * Règles métier :
     * - Le véhicule et la place doivent exister
     * - La place doit être libre
     * - Le véhicule ne doit pas être déjà stationné ailleurs
     *
     * @param stationnement Les données d'entrée
     * @return Le stationnement créé
     * @throws RuntimeException si une règle métier est violée
     */
    public Stationnement entrer(Stationnement stationnement) {
        // Validation du véhicule
        Vehicule vehicule = vehiculeRepository.findById(stationnement.getVehicule().getId())
                .orElseThrow(() -> new RuntimeException("Véhicule non trouvé avec l'id: " +
                        stationnement.getVehicule().getId()));

        // Validation de la place
        Place place = placeRepository.findById(stationnement.getPlace().getId())
                .orElseThrow(() -> new RuntimeException("Place non trouvée avec l'id: " +
                        stationnement.getPlace().getId()));

        // Vérification que la place est libre
        if (place.getStatut() != StatutPlace.LIBRE) {
            throw new RuntimeException("Cette place n'est pas libre. Statut actuel: " + place.getStatut());
        }

        // Vérification que le véhicule n'est pas déjà stationné
        Optional<Stationnement> stationnementActif = stationnementRepository
                .findActiveStationnementByVehicule(vehicule.getId());
        if (stationnementActif.isPresent()) {
            throw new RuntimeException("Ce véhicule est déjà stationné sur la place " +
                    stationnementActif.get().getPlace().getNumero());
        }

        // Vérification que la place n'a pas déjà un véhicule
        Optional<Stationnement> placeOccupee = stationnementRepository
                .findActiveStationnementByPlace(place.getId());
        if (placeOccupee.isPresent()) {
            throw new RuntimeException("Cette place est déjà occupée par le véhicule " +
                    placeOccupee.get().getVehicule().getImmatriculation());
        }

        // Configuration du stationnement
        stationnement.setVehicule(vehicule);
        stationnement.setPlace(place);
        stationnement.setDateEntree(LocalDateTime.now());
        stationnement.setDateSortie(null);
        stationnement.setTarif(null);
        stationnement.setDureeMin(null);

        // Mise à jour du statut de la place
        place.setStatut(StatutPlace.OCCUPEE);
        placeRepository.save(place);

        return stationnementRepository.save(stationnement);
    }

    /**
     * Enregistre la sortie d'un véhicule.
     * Calcule automatiquement la durée et le tarif.
     *
     * @param id Identifiant du stationnement
     * @param tarifHoraire Tarif horaire à appliquer (utilise le tarif par défaut si null)
     * @return Le stationnement terminé
     * @throws RuntimeException si le stationnement n'existe pas ou est déjà terminé
     */
    public Stationnement sortir(Long id, Double tarifHoraire) {
        Stationnement stationnement = stationnementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stationnement non trouvé avec l'id: " + id));

        // Vérification que le stationnement n'est pas déjà terminé
        if (stationnement.getDateSortie() != null) {
            throw new RuntimeException("Ce stationnement est déjà terminé depuis le " +
                    stationnement.getDateSortie());
        }

        // Calcul de la durée et du tarif
        LocalDateTime dateSortie = LocalDateTime.now();
        stationnement.setDateSortie(dateSortie);

        long dureeMinutes = ChronoUnit.MINUTES.between(stationnement.getDateEntree(), dateSortie);
        stationnement.setDureeMin((int) dureeMinutes);

        double tarif = (dureeMinutes / 60.0) * (tarifHoraire != null ? tarifHoraire : TARIF_HORAIRE_DEFAUT);
        stationnement.setTarif(Math.round(tarif * 100.0) / 100.0);

        // Libération de la place
        Place place = stationnement.getPlace();
        place.setStatut(StatutPlace.LIBRE);
        placeRepository.save(place);

        return stationnementRepository.save(stationnement);
    }

    /**
     * Supprime un stationnement.
     * @param id Identifiant du stationnement
     * @throws RuntimeException si le stationnement est actif
     */
    public void delete(Long id) {
        Stationnement stationnement = stationnementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stationnement non trouvé avec l'id: " + id));

        // Si le stationnement est actif, libérer la place
        if (stationnement.getDateSortie() == null) {
            Place place = stationnement.getPlace();
            place.setStatut(StatutPlace.LIBRE);
            placeRepository.save(place);
        }

        stationnementRepository.deleteById(id);
    }
}

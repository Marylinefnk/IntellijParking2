package esiag.back.services;

import esiag.back.models.Place;
import esiag.back.models.StatutPlace;
import esiag.back.models.TypePlace;
import esiag.back.repositories.PlaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service métier pour la gestion des places de parking.
 * Implémente les règles métier suivantes :
 * - Unicité du numéro de place
 * - Unicité de la position (coordonnées X, Y)
 * - Statut par défaut à LIBRE lors de la création
 */
@Service
@Transactional
public class PlaceService {

    private final PlaceRepository placeRepository;

    public PlaceService(PlaceRepository placeRepository) {
        this.placeRepository = placeRepository;
    }

    /**
     * Récupère toutes les places de parking.
     * @return Liste de toutes les places
     */
    public List<Place> findAll() {
        return placeRepository.findAll();
    }

    /**
     * Recherche une place par son identifiant.
     * @param id Identifiant de la place
     * @return La place trouvée ou Optional vide
     */
    public Optional<Place> findById(Long id) {
        return placeRepository.findById(id);
    }

    /**
     * Recherche les places par statut.
     * @param statut Statut recherché (LIBRE, OCCUPEE, RESERVEE)
     * @return Liste des places correspondantes
     */
    public List<Place> findByStatut(StatutPlace statut) {
        return placeRepository.findByStatut(statut);
    }

    /**
     * Recherche les places par type.
     * @param type Type de place (STANDARD, PMR, ELECTRIQUE, MOTO, FAMILIALE)
     * @return Liste des places correspondantes
     */
    public List<Place> findByType(TypePlace type) {
        return placeRepository.findByType(type);
    }

    /**
     * Recherche les places d'une zone spécifique.
     * @param zoneId Identifiant de la zone
     * @return Liste des places de la zone
     */
    public List<Place> findByZone(Long zoneId) {
        return placeRepository.findByZoneId(zoneId);
    }

    /**
     * Récupère toutes les places disponibles (statut LIBRE).
     * @return Liste des places libres
     */
    public List<Place> findPlacesDisponibles() {
        return placeRepository.findByStatut(StatutPlace.LIBRE);
    }

    /**
     * Crée une nouvelle place de parking.
     * Règles métier appliquées :
     * - Le numéro de place doit être unique
     * - La position (X, Y) doit être unique
     * - Le statut par défaut est LIBRE
     *
     * @param place La place à créer
     * @return La place créée
     * @throws RuntimeException si le numéro existe déjà ou si la position est occupée
     */
    public Place create(Place place) {
        // Vérification de l'unicité du numéro de place
        if (place.getNumero() != null && placeRepository.existsByNumero(place.getNumero())) {
            throw new RuntimeException("Une place avec le numéro '" + place.getNumero() + "' existe déjà");
        }

        // Vérification de l'unicité de la position
        if (place.getPositionX() != null && place.getPositionY() != null) {
            if (placeRepository.existsByPosition(place.getPositionX(), place.getPositionY())) {
                throw new RuntimeException("Une place existe déjà à la position (" +
                        place.getPositionX() + ", " + place.getPositionY() + ")");
            }
        }

        // Statut par défaut à LIBRE si non spécifié
        if (place.getStatut() == null) {
            place.setStatut(StatutPlace.LIBRE);
        }

        return placeRepository.save(place);
    }

    /**
     * Met à jour une place existante.
     * Règles métier appliquées :
     * - Le nouveau numéro ne doit pas être utilisé par une autre place
     * - La nouvelle position ne doit pas être occupée par une autre place
     *
     * @param id Identifiant de la place à modifier
     * @param placeDetails Nouvelles données de la place
     * @return La place mise à jour
     * @throws RuntimeException si la place n'existe pas ou si les contraintes sont violées
     */
    public Place update(Long id, Place placeDetails) {
        Place place = placeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Place non trouvée avec l'id: " + id));

        // Vérification de l'unicité du numéro (si modifié)
        if (placeDetails.getNumero() != null && !placeDetails.getNumero().equals(place.getNumero())) {
            if (placeRepository.existsByNumeroAndIdNot(placeDetails.getNumero(), id)) {
                throw new RuntimeException("Une autre place avec le numéro '" +
                        placeDetails.getNumero() + "' existe déjà");
            }
        }

        // Vérification de l'unicité de la position (si modifiée)
        if (placeDetails.getPositionX() != null && placeDetails.getPositionY() != null) {
            boolean positionChanged = !placeDetails.getPositionX().equals(place.getPositionX()) ||
                                      !placeDetails.getPositionY().equals(place.getPositionY());
            if (positionChanged && placeRepository.existsByPositionAndIdNot(
                    placeDetails.getPositionX(), placeDetails.getPositionY(), id)) {
                throw new RuntimeException("Une autre place existe déjà à la position (" +
                        placeDetails.getPositionX() + ", " + placeDetails.getPositionY() + ")");
            }
        }

        // Mise à jour des champs
        place.setNumero(placeDetails.getNumero());
        place.setType(placeDetails.getType());
        place.setStatut(placeDetails.getStatut());
        place.setPositionX(placeDetails.getPositionX());
        place.setPositionY(placeDetails.getPositionY());
        place.setZone(placeDetails.getZone());

        return placeRepository.save(place);
    }

    /**
     * Met à jour le statut d'une place.
     * @param id Identifiant de la place
     * @param statut Nouveau statut
     * @throws RuntimeException si la place n'existe pas
     */
    public void updateStatut(Long id, StatutPlace statut) {
        Place place = placeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Place non trouvée avec l'id: " + id));
        place.setStatut(statut);
        placeRepository.save(place);
    }

    /**
     * Supprime une place de parking.
     * @param id Identifiant de la place à supprimer
     * @throws RuntimeException si la place est occupée ou réservée
     */
    public void delete(Long id) {
        Place place = placeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Place non trouvée avec l'id: " + id));

        // Vérification que la place n'est pas en cours d'utilisation
        if (place.getStatut() == StatutPlace.OCCUPEE) {
            throw new RuntimeException("Impossible de supprimer une place actuellement occupée");
        }
        if (place.getStatut() == StatutPlace.RESERVEE) {
            throw new RuntimeException("Impossible de supprimer une place actuellement réservée");
        }

        placeRepository.deleteById(id);
    }
}

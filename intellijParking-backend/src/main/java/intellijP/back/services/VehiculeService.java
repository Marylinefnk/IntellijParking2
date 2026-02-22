package intellijP.back.services;

import intellijP.back.models.ReservationPlace;
import intellijP.back.models.StatutReservation;
import intellijP.back.models.Vehicule;
import intellijP.back.models.TypeVehicule;
import intellijP.back.repositories.VehiculeRepository;
import intellijP.back.repositories.PersonneRepository;
import intellijP.back.repositories.ReservationPlaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service métier pour la gestion des véhicules.
 * Implémente les règles métier suivantes :
 * - Unicité de l'immatriculation
 * - Validation du format de l'immatriculation
 * - Le propriétaire (personne) doit exister
 * - Impossibilité de supprimer un véhicule avec des réservations actives
 */
@Service
@Transactional
public class VehiculeService {

    private final VehiculeRepository vehiculeRepository;
    private final PersonneRepository personneRepository;
    private final ReservationPlaceRepository reservationPlaceRepository;

    public VehiculeService(VehiculeRepository vehiculeRepository,
                          PersonneRepository personneRepository,
                          ReservationPlaceRepository reservationPlaceRepository) {
        this.vehiculeRepository = vehiculeRepository;
        this.personneRepository = personneRepository;
        this.reservationPlaceRepository = reservationPlaceRepository;
    }

    /**
     * Récupère tous les véhicules.
     * @return Liste de tous les véhicules
     */
    public List<Vehicule> findAll() {
        return vehiculeRepository.findAll();
    }

    /**
     * Recherche un véhicule par son identifiant.
     * @param id Identifiant du véhicule
     * @return Le véhicule trouvé ou Optional vide
     */
    public Optional<Vehicule> findById(Long id) {
        return vehiculeRepository.findById(id);
    }

    /**
     * Recherche les véhicules d'une personne.
     * @param personneId Identifiant de la personne
     * @return Liste des véhicules de la personne
     */
    public List<Vehicule> findByPersonne(Long personneId) {
        return vehiculeRepository.findByPersonneId(personneId);
    }

    /**
     * Recherche un véhicule par son immatriculation.
     * @param immatriculation Immatriculation recherchée
     * @return Le véhicule trouvé ou Optional vide
     */
    public Optional<Vehicule> findByImmatriculation(String immatriculation) {
        return vehiculeRepository.findByImmatriculation(immatriculation);
    }

    /**
     * Recherche les véhicules par type.
     * @param typeVehicule Type de véhicule (VOITURE, MOTO)
     * @return Liste des véhicules correspondants
     */
    public List<Vehicule> findByType(TypeVehicule typeVehicule) {
        return vehiculeRepository.findByTypeVehicule(typeVehicule);
    }

    /**
     * Crée un nouveau véhicule.
     * Règles métier :
     * - L'immatriculation doit être unique
     * - Le propriétaire doit exister
     * - Le type de véhicule est obligatoire
     *
     * @param vehicule Le véhicule à créer
     * @return Le véhicule créé
     * @throws RuntimeException si l'immatriculation existe déjà
     */
    public Vehicule create(Vehicule vehicule) {
        // Validation de l'immatriculation
        if (vehicule.getImmatriculation() == null || vehicule.getImmatriculation().trim().isEmpty()) {
            throw new RuntimeException("L'immatriculation est obligatoire");
        }

        // Vérification de l'unicité de l'immatriculation
        if (vehiculeRepository.findByImmatriculation(vehicule.getImmatriculation()).isPresent()) {
            throw new RuntimeException("Un véhicule avec l'immatriculation '" +
                    vehicule.getImmatriculation() + "' existe déjà");
        }

        // Validation du type de véhicule
        if (vehicule.getTypeVehicule() == null) {
            throw new RuntimeException("Le type de véhicule est obligatoire");
        }

        // Validation du propriétaire
        if (vehicule.getPersonne() != null && vehicule.getPersonne().getId() != null) {
            personneRepository.findById(vehicule.getPersonne().getId())
                    .orElseThrow(() -> new RuntimeException("Personne non trouvée avec l'id: " +
                            vehicule.getPersonne().getId()));
        }

        return vehiculeRepository.save(vehicule);
    }

    /**
     * Met à jour un véhicule existant.
     * @param id Identifiant du véhicule
     * @param vehiculeDetails Nouvelles données
     * @return Le véhicule mis à jour
     */
    public Vehicule update(Long id, Vehicule vehiculeDetails) {
        Vehicule vehicule = vehiculeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Véhicule non trouvé avec l'id: " + id));

        // Vérification de l'unicité de l'immatriculation si modifiée
        if (vehiculeDetails.getImmatriculation() != null &&
            !vehiculeDetails.getImmatriculation().equals(vehicule.getImmatriculation())) {
            if (vehiculeRepository.findByImmatriculation(vehiculeDetails.getImmatriculation()).isPresent()) {
                throw new RuntimeException("Un autre véhicule avec l'immatriculation '" +
                        vehiculeDetails.getImmatriculation() + "' existe déjà");
            }
        }

        vehicule.setImmatriculation(vehiculeDetails.getImmatriculation());
        vehicule.setTypeVehicule(vehiculeDetails.getTypeVehicule());
        vehicule.setPersonne(vehiculeDetails.getPersonne());

        return vehiculeRepository.save(vehicule);
    }

    /**
     * Supprime un véhicule.
     * @param id Identifiant du véhicule
     * @throws RuntimeException si le véhicule a des réservations actives
     */
    public void delete(Long id) {
        Vehicule vehicule = vehiculeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Véhicule non trouvé avec l'id: " + id));

        // Vérification des réservations actives
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
            throw new RuntimeException("Impossible de supprimer ce véhicule car il a " +
                    nbActives + " réservation(s) active(s)");
        }

        vehiculeRepository.deleteById(id);
    }
}

package esiag.back.services;

import esiag.back.models.Personne;
import esiag.back.repositories.PersonneRepository;
import esiag.back.repositories.ReservationPlaceRepository;
import esiag.back.repositories.VehiculeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service métier pour la gestion des personnes.
 * Implémente les règles métier suivantes :
 * - Unicité de l'adresse email
 * - Validation du format de l'email
 * - Impossibilité de supprimer une personne avec des réservations actives
 */
@Service
@Transactional
public class PersonneService {

    private final PersonneRepository personneRepository;
    private final VehiculeRepository vehiculeRepository;
    private final ReservationPlaceRepository reservationPlaceRepository;

    public PersonneService(PersonneRepository personneRepository,
                          VehiculeRepository vehiculeRepository,
                          ReservationPlaceRepository reservationPlaceRepository) {
        this.personneRepository = personneRepository;
        this.vehiculeRepository = vehiculeRepository;
        this.reservationPlaceRepository = reservationPlaceRepository;
    }

    /**
     * Récupère toutes les personnes.
     * @return Liste de toutes les personnes
     */
    public List<Personne> findAll() {
        return personneRepository.findAll();
    }

    /**
     * Recherche une personne par son identifiant.
     * @param id Identifiant de la personne
     * @return La personne trouvée ou Optional vide
     */
    public Optional<Personne> findById(Long id) {
        return personneRepository.findById(id);
    }

    /**
     * Recherche une personne par son adresse email.
     * @param mail Adresse email recherchée
     * @return La personne trouvée ou Optional vide
     */
    public Optional<Personne> findByMail(String mail) {
        return personneRepository.findByMail(mail);
    }

    /**
     * Crée une nouvelle personne.
     * Règles métier :
     * - L'email doit être unique
     * - L'email doit avoir un format valide
     * - Le nom et prénom sont obligatoires
     *
     * @param personne La personne à créer
     * @return La personne créée
     * @throws RuntimeException si l'email existe déjà ou est invalide
     */
    public Personne create(Personne personne) {
        // Validation des champs obligatoires
        if (personne.getNom() == null || personne.getNom().trim().isEmpty()) {
            throw new RuntimeException("Le nom est obligatoire");
        }
        if (personne.getPrenom() == null || personne.getPrenom().trim().isEmpty()) {
            throw new RuntimeException("Le prénom est obligatoire");
        }
        if (personne.getMail() == null || personne.getMail().trim().isEmpty()) {
            throw new RuntimeException("L'adresse email est obligatoire");
        }

        // Validation du format de l'email
        if (!isValidEmail(personne.getMail())) {
            throw new RuntimeException("Le format de l'adresse email est invalide");
        }

        // Vérification de l'unicité de l'email
        if (personneRepository.existsByMail(personne.getMail())) {
            throw new RuntimeException("Une personne avec l'email '" + personne.getMail() + "' existe déjà");
        }

        return personneRepository.save(personne);
    }

    /**
     * Met à jour une personne existante.
     * @param id Identifiant de la personne
     * @param personneDetails Nouvelles données
     * @return La personne mise à jour
     */
    public Personne update(Long id, Personne personneDetails) {
        Personne personne = personneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Personne non trouvée avec l'id: " + id));

        // Vérification de l'unicité de l'email si modifié
        if (personneDetails.getMail() != null && !personneDetails.getMail().equals(personne.getMail())) {
            if (personneRepository.existsByMail(personneDetails.getMail())) {
                throw new RuntimeException("Une autre personne avec l'email '" +
                        personneDetails.getMail() + "' existe déjà");
            }
            if (!isValidEmail(personneDetails.getMail())) {
                throw new RuntimeException("Le format de l'adresse email est invalide");
            }
        }

        personne.setNom(personneDetails.getNom());
        personne.setPrenom(personneDetails.getPrenom());
        personne.setMail(personneDetails.getMail());
        if (personneDetails.getTypePersonne() != null) {
            personne.setTypePersonne(personneDetails.getTypePersonne());
        }

        return personneRepository.save(personne);
    }

    /**
     * Supprime une personne.
     * @param id Identifiant de la personne
     * @throws RuntimeException si la personne a des réservations actives ou des véhicules
     */
    public void delete(Long id) {
        Personne personne = personneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Personne non trouvée avec l'id: " + id));

        // Vérification des réservations actives
        long nbReservationsActives = reservationPlaceRepository.countActiveReservationsByPersonne(id);
        if (nbReservationsActives > 0) {
            throw new RuntimeException("Impossible de supprimer cette personne car elle a " +
                    nbReservationsActives + " réservation(s) active(s)");
        }

        // Vérification des véhicules associés
        if (!vehiculeRepository.findByPersonneId(id).isEmpty()) {
            throw new RuntimeException("Impossible de supprimer cette personne car elle possède des véhicules. " +
                    "Veuillez d'abord supprimer ses véhicules.");
        }

        personneRepository.deleteById(id);
    }

    /**
     * Vérifie si une adresse email a un format valide.
     * @param email Adresse email à vérifier
     * @return true si le format est valide
     */
    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
}

package intellijP.back.services;

import intellijP.back.models.Personne;
import intellijP.back.repositories.PersonneRepository;
import intellijP.back.repositories.ReservationPlaceRepository;
import intellijP.back.repositories.VehiculeRepository;
import intellijP.back.dto.DtoMapper;
import intellijP.back.dto.PersonneCreateDTO;
import intellijP.back.dto.PersonneDTO;
import intellijP.back.models.Personne;
import intellijP.back.repositories.PersonneRepository;
import intellijP.back.repositories.ReservationPlaceRepository;
import intellijP.back.repositories.VehiculeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


 //Service metier pour la gestion des personnes.
 //Implemente les regles metier et le mapping DTO que les les repositories vont utiliser

@Service
@Transactional
public class PersonneService {

    private static final Logger logger = LoggerFactory.getLogger(PersonneService.class);
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";

    private final PersonneRepository personneRepository;
    private final VehiculeRepository vehiculeRepository;
    private final ReservationPlaceRepository reservationPlaceRepository;
    private final PasswordEncoder passwordEncoder;
    private final DtoMapper dtoMapper;

    public PersonneService(PersonneRepository personneRepository,
                          VehiculeRepository vehiculeRepository,
                          ReservationPlaceRepository reservationPlaceRepository,
                          PasswordEncoder passwordEncoder,
                          DtoMapper dtoMapper) {
        this.personneRepository = personneRepository;
        this.vehiculeRepository = vehiculeRepository;
        this.reservationPlaceRepository = reservationPlaceRepository;
        this.passwordEncoder = passwordEncoder;
        this.dtoMapper = dtoMapper;
        logger.info("PersonneService initialise");
    }

    // Methodes DTO

    public List<PersonneDTO> findAllDTO() {
        logger.debug("Recuperation de toutes les personnes (DTO)");
        List<PersonneDTO> result = personneRepository.findAll().stream()
                .map(dtoMapper::toPersonneDTO)
                .collect(Collectors.toList());
        logger.info("Nombre de personnes recuperees: {}", result.size());
        return result;
    }

    public Optional<PersonneDTO> findByIdDTO(Long id) {
        logger.debug("Recherche de la personne avec id: {} (DTO)", id);
        Optional<PersonneDTO> result = personneRepository.findById(id)
                .map(dtoMapper::toPersonneDTO);
        if (result.isEmpty()) {
            logger.warn("Personne non trouvee avec id: {}", id);
        }
        return result;
    }

    public Optional<PersonneDTO> findByMailDTO(String mail) {
        logger.debug("Recherche de la personne avec email: {} (DTO)", mail);
        return personneRepository.findByMail(mail)
                .map(dtoMapper::toPersonneDTO);
    }

    public PersonneDTO createDTO(PersonneCreateDTO createDTO) {
        logger.info("Tentative de creation d'une personne: {}", createDTO.getMail());

        validatePassword(createDTO.getPassword());

        Personne personne = new Personne();
        personne.setNom(createDTO.getNom());
        personne.setPrenom(createDTO.getPrenom());
        personne.setMail(createDTO.getMail());
        personne.setPassword(passwordEncoder.encode(createDTO.getPassword()));
        personne.setTypePersonne(createDTO.getTypePersonne());

        Personne created = create(personne);
        logger.info("Personne creee avec succes: id={}", created.getId());
        return dtoMapper.toPersonneDTO(created);
    }

    public PersonneDTO updateDTO(Long id, PersonneCreateDTO updateDTO) {
        logger.info("Tentative de mise a jour de la personne id={}", id);

        Personne personneDetails = new Personne();
        personneDetails.setNom(updateDTO.getNom());
        personneDetails.setPrenom(updateDTO.getPrenom());
        personneDetails.setMail(updateDTO.getMail());
        personneDetails.setTypePersonne(updateDTO.getTypePersonne());

        Personne updated = update(id, personneDetails);
        return dtoMapper.toPersonneDTO(updated);
    }

    //  Methodes internes (entites)

    public List<Personne> findAll() {
        return personneRepository.findAll();
    }

    public Optional<Personne> findById(Long id) {
        return personneRepository.findById(id);
    }

    public Optional<Personne> findByMail(String mail) {
        return personneRepository.findByMail(mail);
    }

    public Personne create(Personne personne) {
        logger.info("Creation d'une personne: email={}", personne.getMail());

        if (personne.getNom() == null || personne.getNom().trim().isEmpty()) {
            logger.error("Echec creation: nom manquant");
            throw new RuntimeException("Le nom est obligatoire");
        }
        if (personne.getPrenom() == null || personne.getPrenom().trim().isEmpty()) {
            logger.error("Echec creation: prenom manquant");
            throw new RuntimeException("Le prenom est obligatoire");
        }
        if (personne.getMail() == null || personne.getMail().trim().isEmpty()) {
            logger.error("Echec creation: email manquant");
            throw new RuntimeException("L'adresse email est obligatoire");
        }
        if (!isValidEmail(personne.getMail())) {
            logger.error("Echec creation: format email invalide - {}", personne.getMail());
            throw new RuntimeException("Le format de l'adresse email est invalide");
        }
        if (personneRepository.existsByMail(personne.getMail())) {
            logger.error("Echec creation: email deja utilise - {}", personne.getMail());
            throw new RuntimeException("Une personne avec l'email '" + personne.getMail() + "' existe deja");
        }

        return personneRepository.save(personne);
    }

    public Personne update(Long id, Personne personneDetails) {
        logger.info("Mise a jour de la personne id={}", id);

        Personne personne = personneRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Echec mise a jour: personne non trouvee avec id={}", id);
                    return new RuntimeException("Personne non trouvee avec l'id: " + id);
                });

        if (personneDetails.getMail() != null && !personneDetails.getMail().equals(personne.getMail())) {
            if (personneRepository.existsByMail(personneDetails.getMail())) {
                logger.error("Echec mise a jour: email deja utilise - {}", personneDetails.getMail());
                throw new RuntimeException("Une autre personne avec l'email '" +
                        personneDetails.getMail() + "' existe deja");
            }
            if (!isValidEmail(personneDetails.getMail())) {
                logger.error("Echec mise a jour: format email invalide - {}", personneDetails.getMail());
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

    public void delete(Long id) {
        logger.info("Tentative de suppression de la personne id={}", id);

        Personne personne = personneRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Echec suppression: personne non trouvee avec id={}", id);
                    return new RuntimeException("Personne non trouvee avec l'id: " + id);
                });

        long nbReservationsActives = reservationPlaceRepository.countActiveReservationsByPersonne(id);
        if (nbReservationsActives > 0) {
            logger.error("Echec suppression: personne {} a {} reservation(s) active(s)",
                personne.getMail(), nbReservationsActives);
            throw new RuntimeException("Impossible de supprimer cette personne car elle a " +
                    nbReservationsActives + " reservation(s) active(s)");
        }

        if (!vehiculeRepository.findByPersonneId(id).isEmpty()) {
            logger.error("Echec suppression: personne {} possede des vehicules", personne.getMail());
            throw new RuntimeException("Impossible de supprimer cette personne car elle possede des vehicules.");
        }

        personneRepository.deleteById(id);
        logger.info("Personne supprimee avec succes: id={}", id);
    }

    private void validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new RuntimeException("Le mot de passe est obligatoire");
        }
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches(EMAIL_REGEX);
    }
}

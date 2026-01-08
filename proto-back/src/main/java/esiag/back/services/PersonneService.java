package esiag.back.services;

import esiag.back.models.Personne;
import esiag.back.repositories.PersonneRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PersonneService {

    private final PersonneRepository personneRepository;

    public PersonneService(PersonneRepository personneRepository) {
        this.personneRepository = personneRepository;
    }

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
        if (personneRepository.existsByMail(personne.getMail())) {
            throw new RuntimeException("Une personne avec cet email existe déjà");
        }
        return personneRepository.save(personne);
    }

    public Personne update(Long id, Personne personneDetails) {
        Personne personne = personneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Personne non trouvée"));

        personne.setNom(personneDetails.getNom());
        personne.setPrenom(personneDetails.getPrenom());
        personne.setMail(personneDetails.getMail());

        return personneRepository.save(personne);
    }

    public void delete(Long id) {
        personneRepository.deleteById(id);
    }
}

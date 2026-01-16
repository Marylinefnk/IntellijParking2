package esiag.back.controllers;

import esiag.back.dto.DtoMapper;
import esiag.back.dto.PersonneCreateDTO;
import esiag.back.dto.PersonneDTO;
import esiag.back.models.Personne;
import esiag.back.services.PersonneService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/personnes")
public class PersonneController {

    private final PersonneService personneService;
    private final DtoMapper dtoMapper;
    private final PasswordEncoder passwordEncoder;

    public PersonneController(PersonneService personneService, DtoMapper dtoMapper, PasswordEncoder passwordEncoder) {
        this.personneService = personneService;
        this.dtoMapper = dtoMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public List<PersonneDTO> getAllPersonnes() {
        return personneService.findAll().stream()
                .map(dtoMapper::toPersonneDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonneDTO> getPersonneById(@PathVariable Long id) {
        return personneService.findById(id)
                .map(dtoMapper::toPersonneDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/mail/{mail}")
    public ResponseEntity<PersonneDTO> getPersonneByMail(@PathVariable String mail) {
        return personneService.findByMail(mail)
                .map(dtoMapper::toPersonneDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PersonneDTO> createPersonne(@RequestBody PersonneCreateDTO createDTO) {
        try {
            // Validate password is provided for new users
            if (createDTO.getPassword() == null || createDTO.getPassword().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            Personne personne = new Personne();
            personne.setNom(createDTO.getNom());
            personne.setPrenom(createDTO.getPrenom());
            personne.setMail(createDTO.getMail());
            personne.setPassword(passwordEncoder.encode(createDTO.getPassword()));
            personne.setTypePersonne(createDTO.getTypePersonne());

            Personne created = personneService.create(personne);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(dtoMapper.toPersonneDTO(created));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<PersonneDTO> updatePersonne(@PathVariable Long id, @RequestBody PersonneCreateDTO updateDTO) {
        try {
            Personne personneDetails = new Personne();
            personneDetails.setNom(updateDTO.getNom());
            personneDetails.setPrenom(updateDTO.getPrenom());
            personneDetails.setMail(updateDTO.getMail());
            personneDetails.setTypePersonne(updateDTO.getTypePersonne());

            Personne updated = personneService.update(id, personneDetails);
            return ResponseEntity.ok(dtoMapper.toPersonneDTO(updated));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePersonne(@PathVariable Long id) {
        personneService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

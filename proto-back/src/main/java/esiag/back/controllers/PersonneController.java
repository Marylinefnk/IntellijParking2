package esiag.back.controllers;

import esiag.back.models.Personne;
import esiag.back.services.PersonneService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/personnes")
public class PersonneController {

    private final PersonneService personneService;

    public PersonneController(PersonneService personneService) {
        this.personneService = personneService;
    }

    @GetMapping
    public List<Personne> getAllPersonnes() {
        return personneService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Personne> getPersonneById(@PathVariable Long id) {
        return personneService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/mail/{mail}")
    public ResponseEntity<Personne> getPersonneByMail(@PathVariable String mail) {
        return personneService.findByMail(mail)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Personne> createPersonne(@RequestBody Personne personne) {
        try {
            Personne created = personneService.create(personne);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Personne> updatePersonne(@PathVariable Long id, @RequestBody Personne personne) {
        try {
            Personne updated = personneService.update(id, personne);
            return ResponseEntity.ok(updated);
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

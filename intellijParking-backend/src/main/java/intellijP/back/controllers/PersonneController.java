package intellijP.back.controllers;

import intellijP.back.dto.DtoMapper;
import intellijP.back.dto.PersonneCreateDTO;
import intellijP.back.dto.PersonneDTO;
import intellijP.back.models.Personne;
import intellijP.back.services.PersonneService;
import intellijP.back.dto.PersonneCreateDTO;
import intellijP.back.dto.PersonneDTO;
import intellijP.back.exceptions.ResourceNotFoundException;
import intellijP.back.services.PersonneService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/personnes")
public class PersonneController {

    private final PersonneService personneService;

    public PersonneController(PersonneService personneService) {
        this.personneService = personneService;
    }

    // ENDPOINTS DE LECTURE

    @GetMapping
    public List<PersonneDTO> getAllPersonnes() {
        return personneService.findAllDTO();
    }

    @GetMapping("/{id}")
    public PersonneDTO getPersonneById(@PathVariable Long id) {
        return personneService.findByIdDTO(id)
                .orElseThrow(() -> new ResourceNotFoundException("Personne", id));
    }

    @GetMapping("/mail/{mail}")
    public PersonneDTO getPersonneByMail(@PathVariable String mail) {
        return personneService.findByMailDTO(mail)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Personne avec email '" + mail + "' non trouvee"));
    }

    // ENDPOINTS DE MODIFICATION

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PersonneDTO createPersonne(@RequestBody PersonneCreateDTO createDTO) {
        return personneService.createDTO(createDTO);
    }

    @PutMapping("/{id}")
    public PersonneDTO updatePersonne(@PathVariable Long id, @RequestBody PersonneCreateDTO updateDTO) {
        return personneService.updateDTO(id, updateDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePersonne(@PathVariable Long id) {
        personneService.delete(id);
    }
}

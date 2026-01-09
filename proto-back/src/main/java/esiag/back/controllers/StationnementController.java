package esiag.back.controllers;

import esiag.back.dto.DtoMapper;
import esiag.back.dto.StationnementCreateDTO;
import esiag.back.dto.StationnementDTO;
import esiag.back.models.Place;
import esiag.back.models.Stationnement;
import esiag.back.models.Vehicule;
import esiag.back.services.StationnementService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/stationnements")
public class StationnementController {

    private final StationnementService stationnementService;
    private final DtoMapper dtoMapper;

    public StationnementController(StationnementService stationnementService, DtoMapper dtoMapper) {
        this.stationnementService = stationnementService;
        this.dtoMapper = dtoMapper;
    }

    @GetMapping
    public List<StationnementDTO> getAllStationnements() {
        return stationnementService.findAll().stream()
                .map(dtoMapper::toStationnementDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StationnementDTO> getStationnementById(@PathVariable Long id) {
        return stationnementService.findById(id)
                .map(dtoMapper::toStationnementDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/place/{placeId}")
    public List<StationnementDTO> getStationnementsByPlace(@PathVariable Long placeId) {
        return stationnementService.findByPlace(placeId).stream()
                .map(dtoMapper::toStationnementDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/vehicule/{vehiculeId}")
    public List<StationnementDTO> getStationnementsByVehicule(@PathVariable Long vehiculeId) {
        return stationnementService.findByVehicule(vehiculeId).stream()
                .map(dtoMapper::toStationnementDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/actif/vehicule/{vehiculeId}")
    public ResponseEntity<StationnementDTO> getActiveStationnementByVehicule(@PathVariable Long vehiculeId) {
        return stationnementService.findActiveByVehicule(vehiculeId)
                .map(dtoMapper::toStationnementDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/actif/place/{placeId}")
    public ResponseEntity<StationnementDTO> getActiveStationnementByPlace(@PathVariable Long placeId) {
        return stationnementService.findActiveByPlace(placeId)
                .map(dtoMapper::toStationnementDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/periode")
    public List<StationnementDTO> getStationnementsByPeriode(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return stationnementService.findByDateEntreeBetween(debut, fin).stream()
                .map(dtoMapper::toStationnementDTO)
                .collect(Collectors.toList());
    }

    @PostMapping("/entrer")
    public ResponseEntity<StationnementDTO> entrer(@RequestBody StationnementCreateDTO createDTO) {
        try {
            Stationnement stationnement = new Stationnement();

            Vehicule vehicule = new Vehicule();
            vehicule.setId(createDTO.getVehiculeId());
            stationnement.setVehicule(vehicule);

            Place place = new Place();
            place.setId(createDTO.getPlaceId());
            stationnement.setPlace(place);

            if (createDTO.getDateEntree() != null) {
                stationnement.setDateEntree(createDTO.getDateEntree());
            }

            Stationnement created = stationnementService.entrer(stationnement);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(dtoMapper.toStationnementDTO(created));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/sortir")
    public ResponseEntity<StationnementDTO> sortir(@PathVariable Long id,
                                                    @RequestParam(defaultValue = "2.0") Double tarifHoraire) {
        try {
            Stationnement updated = stationnementService.sortir(id, tarifHoraire);
            return ResponseEntity.ok(dtoMapper.toStationnementDTO(updated));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStationnement(@PathVariable Long id) {
        stationnementService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

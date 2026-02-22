package intellijP.back.controllers;

import intellijP.back.dto.DtoMapper;
import intellijP.back.dto.StationnementCreateDTO;
import intellijP.back.dto.StationnementDTO;
import intellijP.back.models.Place;
import intellijP.back.models.Stationnement;
import intellijP.back.models.Vehicule;
import intellijP.back.services.StationnementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(StationnementController.class);

    private final StationnementService stationnementService;
    private final DtoMapper dtoMapper;

    public StationnementController(StationnementService stationnementService, DtoMapper dtoMapper) {
        this.stationnementService = stationnementService;
        this.dtoMapper = dtoMapper;
        logger.info("StationnementController initialise");
    }

    @GetMapping
    public List<StationnementDTO> getAllStationnements() {
        logger.debug("GET /api/stationnements");
        List<StationnementDTO> result = stationnementService.findAll().stream()
                .map(dtoMapper::toStationnementDTO)
                .collect(Collectors.toList());
        logger.info("GET /api/stationnements - {} stationnements retournes", result.size());
        return result;
    }

    @GetMapping("/{id}")
    public ResponseEntity<StationnementDTO> getStationnementById(@PathVariable Long id) {
        logger.debug("GET /api/stationnements/{}", id);
        return stationnementService.findById(id)
                .map(stationnement -> {
                    logger.debug("GET /api/stationnements/{} - stationnement trouve", id);
                    return ResponseEntity.ok(dtoMapper.toStationnementDTO(stationnement));
                })
                .orElseGet(() -> {
                    logger.warn("GET /api/stationnements/{} - stationnement non trouve", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @GetMapping("/place/{placeId}")
    public List<StationnementDTO> getStationnementsByPlace(@PathVariable Long placeId) {
        logger.debug("GET /api/stationnements/place/{}", placeId);
        List<StationnementDTO> result = stationnementService.findByPlace(placeId).stream()
                .map(dtoMapper::toStationnementDTO)
                .collect(Collectors.toList());
        logger.debug("GET /api/stationnements/place/{} - {} stationnements trouves", placeId, result.size());
        return result;
    }

    @GetMapping("/vehicule/{vehiculeId}")
    public List<StationnementDTO> getStationnementsByVehicule(@PathVariable Long vehiculeId) {
        logger.debug("GET /api/stationnements/vehicule/{}", vehiculeId);
        List<StationnementDTO> result = stationnementService.findByVehicule(vehiculeId).stream()
                .map(dtoMapper::toStationnementDTO)
                .collect(Collectors.toList());
        logger.debug("GET /api/stationnements/vehicule/{} - {} stationnements trouves", vehiculeId, result.size());
        return result;
    }

    @GetMapping("/actif/vehicule/{vehiculeId}")
    public ResponseEntity<StationnementDTO> getActiveStationnementByVehicule(@PathVariable Long vehiculeId) {
        logger.debug("GET /api/stationnements/actif/vehicule/{}", vehiculeId);
        return stationnementService.findActiveByVehicule(vehiculeId)
                .map(stationnement -> {
                    logger.debug("GET /api/stationnements/actif/vehicule/{} - stationnement actif trouve", vehiculeId);
                    return ResponseEntity.ok(dtoMapper.toStationnementDTO(stationnement));
                })
                .orElseGet(() -> {
                    logger.debug("GET /api/stationnements/actif/vehicule/{} - pas de stationnement actif", vehiculeId);
                    return ResponseEntity.notFound().build();
                });
    }

    @GetMapping("/actif/place/{placeId}")
    public ResponseEntity<StationnementDTO> getActiveStationnementByPlace(@PathVariable Long placeId) {
        logger.debug("GET /api/stationnements/actif/place/{}", placeId);
        return stationnementService.findActiveByPlace(placeId)
                .map(stationnement -> {
                    logger.debug("GET /api/stationnements/actif/place/{} - stationnement actif trouve", placeId);
                    return ResponseEntity.ok(dtoMapper.toStationnementDTO(stationnement));
                })
                .orElseGet(() -> {
                    logger.debug("GET /api/stationnements/actif/place/{} - pas de stationnement actif", placeId);
                    return ResponseEntity.notFound().build();
                });
    }

    @GetMapping("/periode")
    public List<StationnementDTO> getStationnementsByPeriode(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        logger.debug("GET /api/stationnements/periode - debut={}, fin={}", debut, fin);
        List<StationnementDTO> result = stationnementService.findByDateEntreeBetween(debut, fin).stream()
                .map(dtoMapper::toStationnementDTO)
                .collect(Collectors.toList());
        logger.debug("GET /api/stationnements/periode - {} stationnements trouves", result.size());
        return result;
    }

    @PostMapping("/entrer")
    public ResponseEntity<StationnementDTO> entrer(@RequestBody StationnementCreateDTO createDTO) {
        logger.debug("POST /api/stationnements/entrer - vehicule={}, place={}",
            createDTO.getVehiculeId(), createDTO.getPlaceId());
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
            logger.info("POST /api/stationnements/entrer - entree enregistree id={}", created.getId());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(dtoMapper.toStationnementDTO(created));
        } catch (RuntimeException e) {
            logger.warn("POST /api/stationnements/entrer - echec: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/sortir")
    public ResponseEntity<StationnementDTO> sortir(@PathVariable Long id,
                                                    @RequestParam(defaultValue = "2.0") Double tarifHoraire) {
        logger.debug("POST /api/stationnements/{}/sortir - tarifHoraire={}", id, tarifHoraire);
        try {
            Stationnement updated = stationnementService.sortir(id, tarifHoraire);
            logger.info("POST /api/stationnements/{}/sortir - sortie enregistree, tarif={}",
                id, updated.getTarif());
            return ResponseEntity.ok(dtoMapper.toStationnementDTO(updated));
        } catch (RuntimeException e) {
            logger.warn("POST /api/stationnements/{}/sortir - echec: {}", id, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStationnement(@PathVariable Long id) {
        logger.debug("DELETE /api/stationnements/{}", id);
        try {
            stationnementService.delete(id);
            logger.info("DELETE /api/stationnements/{} - stationnement supprime", id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            logger.warn("DELETE /api/stationnements/{} - echec: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}

package intellijP.back.controllers;

import intellijP.back.dto.DtoMapper;
import intellijP.back.dto.ZoneCreateDTO;
import intellijP.back.dto.ZoneDTO;
import intellijP.back.models.Zone;
import intellijP.back.services.ZoneService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/zones")
public class ZoneController {

    private static final Logger logger = LoggerFactory.getLogger(ZoneController.class);

    private final ZoneService zoneService;
    private final DtoMapper dtoMapper;

    public ZoneController(ZoneService zoneService, DtoMapper dtoMapper) {
        this.zoneService = zoneService;
        this.dtoMapper = dtoMapper;
        logger.info("ZoneController initialise");
    }

    @GetMapping
    public List<ZoneDTO> getAllZones() {
        logger.debug("GET /api/zones");
        List<ZoneDTO> result = zoneService.findAll().stream()
                .map(dtoMapper::toZoneDTO)
                .collect(Collectors.toList());
        logger.info("GET /api/zones - {} zones retournees", result.size());
        return result;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ZoneDTO> getZoneById(@PathVariable Long id) {
        logger.debug("GET /api/zones/{}", id);
        return zoneService.findById(id)
                .map(zone -> {
                    logger.debug("GET /api/zones/{} - zone trouvee", id);
                    return ResponseEntity.ok(dtoMapper.toZoneDTO(zone));
                })
                .orElseGet(() -> {
                    logger.warn("GET /api/zones/{} - zone non trouvee", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @GetMapping("/nom/{nom}")
    public ResponseEntity<ZoneDTO> getZoneByNom(@PathVariable String nom) {
        logger.debug("GET /api/zones/nom/{}", nom);
        return zoneService.findByNom(nom)
                .map(zone -> {
                    logger.debug("GET /api/zones/nom/{} - zone trouvee", nom);
                    return ResponseEntity.ok(dtoMapper.toZoneDTO(zone));
                })
                .orElseGet(() -> {
                    logger.warn("GET /api/zones/nom/{} - zone non trouvee", nom);
                    return ResponseEntity.notFound().build();
                });
    }

    @PostMapping
    public ResponseEntity<ZoneDTO> createZone(@RequestBody ZoneCreateDTO createDTO) {
        logger.debug("POST /api/zones - nom: {}", createDTO.getNom());
        try {
            Zone zone = new Zone();
            zone.setNom(createDTO.getNom());
            zone.setDescription(createDTO.getDescription());

            Zone created = zoneService.create(zone);
            logger.info("POST /api/zones - zone creee id={}", created.getId());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(dtoMapper.toZoneDTO(created));
        } catch (RuntimeException e) {
            logger.warn("POST /api/zones - echec: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ZoneDTO> updateZone(@PathVariable Long id,
                                               @RequestBody ZoneCreateDTO updateDTO) {
        logger.debug("PUT /api/zones/{}", id);
        try {
            Zone zoneDetails = new Zone();
            zoneDetails.setNom(updateDTO.getNom());
            zoneDetails.setDescription(updateDTO.getDescription());

            Zone updated = zoneService.update(id, zoneDetails);
            logger.info("PUT /api/zones/{} - zone mise a jour", id);
            return ResponseEntity.ok(dtoMapper.toZoneDTO(updated));
        } catch (RuntimeException e) {
            logger.warn("PUT /api/zones/{} - echec: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteZone(@PathVariable Long id) {
        logger.debug("DELETE /api/zones/{}", id);
        try {
            zoneService.delete(id);
            logger.info("DELETE /api/zones/{} - zone supprimee", id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            logger.warn("DELETE /api/zones/{} - echec: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}

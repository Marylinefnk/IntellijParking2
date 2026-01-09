package esiag.back.controllers;

import esiag.back.dto.DtoMapper;
import esiag.back.dto.ZoneCreateDTO;
import esiag.back.dto.ZoneDTO;
import esiag.back.models.Zone;
import esiag.back.services.ZoneService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/zones")
public class ZoneController {

    private final ZoneService zoneService;
    private final DtoMapper dtoMapper;

    public ZoneController(ZoneService zoneService, DtoMapper dtoMapper) {
        this.zoneService = zoneService;
        this.dtoMapper = dtoMapper;
    }

    @GetMapping
    public List<ZoneDTO> getAllZones() {
        return zoneService.findAll().stream()
                .map(dtoMapper::toZoneDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ZoneDTO> getZoneById(@PathVariable Long id) {
        return zoneService.findById(id)
                .map(dtoMapper::toZoneDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/nom/{nom}")
    public ResponseEntity<ZoneDTO> getZoneByNom(@PathVariable String nom) {
        return zoneService.findByNom(nom)
                .map(dtoMapper::toZoneDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ZoneDTO> createZone(@RequestBody ZoneCreateDTO createDTO) {
        try {
            Zone zone = new Zone();
            zone.setNom(createDTO.getNom());
            zone.setDescription(createDTO.getDescription());

            Zone created = zoneService.create(zone);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(dtoMapper.toZoneDTO(created));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ZoneDTO> updateZone(@PathVariable Long id,
                                               @RequestBody ZoneCreateDTO updateDTO) {
        try {
            Zone zoneDetails = new Zone();
            zoneDetails.setNom(updateDTO.getNom());
            zoneDetails.setDescription(updateDTO.getDescription());

            Zone updated = zoneService.update(id, zoneDetails);
            return ResponseEntity.ok(dtoMapper.toZoneDTO(updated));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteZone(@PathVariable Long id) {
        zoneService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

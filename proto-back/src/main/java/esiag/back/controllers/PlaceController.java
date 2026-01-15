package esiag.back.controllers;

import esiag.back.dto.DtoMapper;
import esiag.back.dto.PlaceAvailabilityDTO;
import esiag.back.dto.PlaceCreateDTO;
import esiag.back.dto.PlaceDTO;
import esiag.back.models.Place;
import esiag.back.models.StatutPlace;
import esiag.back.models.TypePlace;
import esiag.back.models.Zone;
import esiag.back.services.PlaceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/places")
public class PlaceController {

    private final PlaceService placeService;
    private final DtoMapper dtoMapper;

    public PlaceController(PlaceService placeService, DtoMapper dtoMapper) {
        this.placeService = placeService;
        this.dtoMapper = dtoMapper;
    }

    @GetMapping
    public List<PlaceDTO> getAllPlaces() {
        return placeService.findAll().stream()
                .map(dtoMapper::toPlaceDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlaceDTO> getPlaceById(@PathVariable Long id) {
        return placeService.findById(id)
                .map(dtoMapper::toPlaceDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/disponibles")
    public List<PlaceDTO> getPlacesDisponibles() {
        return placeService.findPlacesDisponibles().stream()
                .map(dtoMapper::toPlaceDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retourne toutes les places avec leur statut calcule dynamiquement
     * en fonction des reservations en cours.
     */
    @GetMapping("/availability")
    public List<PlaceAvailabilityDTO> getAllPlacesWithAvailability() {
        return placeService.findAllWithAvailability();
    }

    /**
     * Retourne une place avec son statut calcule dynamiquement.
     */
    @GetMapping("/{id}/availability")
    public ResponseEntity<PlaceAvailabilityDTO> getPlaceAvailability(@PathVariable Long id) {
        return placeService.findByIdWithAvailability(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/statut/{statut}")
    public List<PlaceDTO> getPlacesByStatut(@PathVariable StatutPlace statut) {
        return placeService.findByStatut(statut).stream()
                .map(dtoMapper::toPlaceDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/type/{type}")
    public List<PlaceDTO> getPlacesByType(@PathVariable TypePlace type) {
        return placeService.findByType(type).stream()
                .map(dtoMapper::toPlaceDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/zone/{zoneId}")
    public List<PlaceDTO> getPlacesByZone(@PathVariable Long zoneId) {
        return placeService.findByZone(zoneId).stream()
                .map(dtoMapper::toPlaceDTO)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<PlaceDTO> createPlace(@RequestBody PlaceCreateDTO createDTO) {
        try {
            Place place = new Place();
            place.setNumero(createDTO.getNumero());
            place.setType(createDTO.getType());
            place.setStatut(createDTO.getStatut());
            place.setPositionX(createDTO.getPositionX());
            place.setPositionY(createDTO.getPositionY());

            if (createDTO.getZoneId() != null) {
                Zone zone = new Zone();
                zone.setId(createDTO.getZoneId());
                place.setZone(zone);
            }

            Place created = placeService.create(place);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(dtoMapper.toPlaceDTO(created));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlaceDTO> updatePlace(@PathVariable Long id, @RequestBody PlaceCreateDTO updateDTO) {
        try {
            Place placeDetails = new Place();
            placeDetails.setNumero(updateDTO.getNumero());
            placeDetails.setType(updateDTO.getType());
            placeDetails.setStatut(updateDTO.getStatut());
            placeDetails.setPositionX(updateDTO.getPositionX());
            placeDetails.setPositionY(updateDTO.getPositionY());

            if (updateDTO.getZoneId() != null) {
                Zone zone = new Zone();
                zone.setId(updateDTO.getZoneId());
                placeDetails.setZone(zone);
            }

            Place updated = placeService.update(id, placeDetails);
            return ResponseEntity.ok(dtoMapper.toPlaceDTO(updated));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/statut")
    public ResponseEntity<PlaceDTO> updateStatut(@PathVariable Long id, @RequestBody StatutPlace statut) {
        try {
            placeService.updateStatut(id, statut);
            return placeService.findById(id)
                    .map(dtoMapper::toPlaceDTO)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlace(@PathVariable Long id) {
        placeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

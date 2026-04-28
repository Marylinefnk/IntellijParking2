package intellijP.back.controllers;

import intellijP.back.dto.DtoMapper;
import intellijP.back.dto.PlaceAvailabilityDTO;
import intellijP.back.dto.PlaceCreateDTO;
import intellijP.back.dto.PlaceDTO;
import intellijP.back.models.Place;
import intellijP.back.models.StatutPlace;
import intellijP.back.models.TypePlace;
import intellijP.back.models.Zone;
import intellijP.back.services.PlaceService;
import intellijP.back.exceptions.ResourceNotFoundException;
import intellijP.back.services.PlaceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api/places")
public class PlaceController {

    private final PlaceService placeService;

    public PlaceController(PlaceService placeService) {
        this.placeService = placeService;
    }

    @GetMapping
    public List<PlaceDTO> getAllPlaces() {
        return placeService.findAllDTO();
    }

    @GetMapping("/{id}")
    public PlaceDTO getPlaceById(@PathVariable Long id) {
        return placeService.findByIdDTO(id)
                .orElseThrow(() -> new ResourceNotFoundException("Place", id));
    }

    @GetMapping("/stats")
    public Map<String, Long> getPlacesStats() {
        return placeService.getStats();
    }

    @GetMapping("/disponibles")
    public List<PlaceDTO> getPlacesDisponibles() {
        return placeService.findPlacesDisponiblesDTO();
    }

    @GetMapping("/availability")
    public List<PlaceAvailabilityDTO> getAllPlacesWithAvailability() {
        return placeService.findAllWithAvailability();
    }

    @GetMapping("/availability/page")
    public Page<PlaceAvailabilityDTO> getAllPlacesWithAvailabilityPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String date) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("numero"));
        if (date != null && !date.isBlank()) {
            return placeService.findAllWithAvailabilityByDate(pageRequest, LocalDate.parse(date));
        }
        return placeService.findAllWithAvailabilityPaged(pageRequest);
    }

    @GetMapping("/{id}/availability")
    public PlaceAvailabilityDTO getPlaceAvailability(@PathVariable Long id) {
        return placeService.findByIdWithAvailability(id)
                .orElseThrow(() -> new ResourceNotFoundException("Place", id));
    }

    @GetMapping("/statut/{statut}")
    public List<PlaceDTO> getPlacesByStatut(@PathVariable StatutPlace statut) {
        return placeService.findByStatutDTO(statut);
    }

    @GetMapping("/type/{type}")
    public List<PlaceDTO> getPlacesByType(@PathVariable TypePlace type) {
        return placeService.findByTypeDTO(type);
    }

    @GetMapping("/zone/{zoneId}")
    public List<PlaceDTO> getPlacesByZone(@PathVariable Long zoneId) {
        return placeService.findByZoneDTO(zoneId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PlaceDTO createPlace(@RequestBody PlaceCreateDTO createDTO) {
        return placeService.createDTO(createDTO);
    }

    @PutMapping("/{id}")
    public PlaceDTO updatePlace(@PathVariable Long id, @RequestBody PlaceCreateDTO updateDTO) {
        return placeService.updateDTO(id, updateDTO);
    }

    @PutMapping("/{id}/statut")
    public PlaceDTO updateStatut(@PathVariable Long id, @RequestBody StatutPlace statut) {
        return placeService.updateStatutDTO(id, statut);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePlace(@PathVariable Long id) {
        placeService.deleteDTO(id);
    }
}

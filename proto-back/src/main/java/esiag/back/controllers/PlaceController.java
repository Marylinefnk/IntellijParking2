package esiag.back.controllers;

import esiag.back.models.Place;
import esiag.back.models.StatutPlace;
import esiag.back.models.TypePlace;
import esiag.back.services.PlaceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/places")
public class PlaceController {

    private final PlaceService placeService;

    public PlaceController(PlaceService placeService) {
        this.placeService = placeService;
    }

    @GetMapping
    public List<Place> getAllPlaces() {
        return placeService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Place> getPlaceById(@PathVariable Long id) {
        return placeService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/disponibles")
    public List<Place> getPlacesDisponibles() {
        return placeService.findPlacesDisponibles();
    }

    @GetMapping("/statut/{statut}")
    public List<Place> getPlacesByStatut(@PathVariable StatutPlace statut) {
        return placeService.findByStatut(statut);
    }

    @GetMapping("/type/{type}")
    public List<Place> getPlacesByType(@PathVariable TypePlace type) {
        return placeService.findByType(type);
    }

    @GetMapping("/zone/{zoneId}")
    public List<Place> getPlacesByZone(@PathVariable Long zoneId) {
        return placeService.findByZone(zoneId);
    }

    @PostMapping
    public ResponseEntity<Place> createPlace(@RequestBody Place place) {
        Place created = placeService.create(place);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Place> updatePlace(@PathVariable Long id, @RequestBody Place place) {
        try {
            Place updated = placeService.update(id, place);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/statut")
    public ResponseEntity<Void> updateStatut(@PathVariable Long id, @RequestBody StatutPlace statut) {
        try {
            placeService.updateStatut(id, statut);
            return ResponseEntity.ok().build();
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

package esiag.back.controllers;

import esiag.back.models.Place;
import esiag.back.repositories.PlaceRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PlaceController {

    private final PlaceRepository placeRepository;

    public PlaceController(PlaceRepository placeRepository) {
        this.placeRepository = placeRepository;
    }

    @GetMapping("/api/places")
    public List<Place> getPlaces() {
        return placeRepository.findAll();
    }
}

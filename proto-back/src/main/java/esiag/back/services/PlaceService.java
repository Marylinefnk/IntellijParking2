package esiag.back.services;

import esiag.back.models.Place;
import esiag.back.models.StatutPlace;
import esiag.back.models.TypePlace;
import esiag.back.repositories.PlaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PlaceService {

    private final PlaceRepository placeRepository;

    public PlaceService(PlaceRepository placeRepository) {
        this.placeRepository = placeRepository;
    }

    public List<Place> findAll() {
        return placeRepository.findAll();
    }

    public Optional<Place> findById(Long id) {
        return placeRepository.findById(id);
    }

    public List<Place> findByStatut(StatutPlace statut) {
        return placeRepository.findByStatut(statut);
    }

    public List<Place> findByType(TypePlace type) {
        return placeRepository.findByType(type);
    }

    public List<Place> findByZone(Long zoneId) {
        return placeRepository.findByZoneId(zoneId);
    }

    public List<Place> findPlacesDisponibles() {
        return placeRepository.findByStatut(StatutPlace.LIBRE);
    }

    public Place create(Place place) {
        if (place.getStatut() == null) {
            place.setStatut(StatutPlace.LIBRE);
        }
        return placeRepository.save(place);
    }

    public Place update(Long id, Place placeDetails) {
        Place place = placeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Place non trouvée"));
        
        place.setNumero(placeDetails.getNumero());
        place.setType(placeDetails.getType());
        place.setStatut(placeDetails.getStatut());
        place.setPositionX(placeDetails.getPositionX());
        place.setPositionY(placeDetails.getPositionY());
        place.setZone(placeDetails.getZone());
        
        return placeRepository.save(place);
    }

    public void updateStatut(Long id, StatutPlace statut) {
        Place place = placeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Place non trouvée"));
        place.setStatut(statut);
        placeRepository.save(place);
    }

    public void delete(Long id) {
        placeRepository.deleteById(id);
    }
}

package intellijP.back.services;

import intellijP.back.exceptions.BadRequestException;
import intellijP.back.exceptions.ConflictException;
import intellijP.back.exceptions.OperationNotAllowedException;
import intellijP.back.models.Place;
import intellijP.back.models.StatutPlace;
import intellijP.back.repositories.PlaceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PlaceServiceTest {
    @Mock
    private PlaceRepository placeRepository;
    @InjectMocks
    private PlaceService placeService;

    @Test
    void testCreatePlace() {
        Place place = new Place();
        place.setNumero("A01");
        place.setPositionX(68.0);
        place.setPositionY(95.0);

        when(placeRepository.save(place)).thenReturn(place);
        when(placeRepository.existsByNumero("A01")).thenReturn(false);
        when(placeRepository.existsByPosition(68.0, 95.0)).thenReturn(false);

        Place resultat = placeService.create(place);

        assertEquals("A01", resultat.getNumero());
        assertEquals(StatutPlace.LIBRE, resultat.getStatut());
    }

    @Test
    void testCreatePlaceWithNumPlaceNull() {
        Place place = new Place();
        place.setNumero(null);
        place.setPositionX(null);
        place.setPositionY(null);

        var resultat = assertThrows(BadRequestException.class, () -> placeService.create(place));
        assertEquals("Numero de place requis", resultat.getMessage());

    }
    @Test
    void testCreatePlaceNumExistant() {
        Place place = new Place();
        place.setNumero("A01");
        place.setPositionX(68.0);
        place.setPositionY(95.0);

        when(placeRepository.existsByNumero("A01")).thenReturn(true);

        var resultat = assertThrows(ConflictException.class, () -> placeService.create(place));
        assertEquals("Une place avec le numero '" + place.getNumero() + "' existe deja", resultat.getMessage());

    }
      @Test
void testSupprimerPlaceOccupee() {
        Place place = new Place();
        place.setId(6);
        place.setNumero("A03");
        place.setStatut(StatutPlace.OCCUPEE);
          when(placeRepository.findById(6)).thenReturn(Optional.of(place));
          assertThrows(OperationNotAllowedException.class, () -> placeService.deleteDTO(6));

      }
}
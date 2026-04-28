package intellijP.back.services;

import intellijP.back.exceptions.OperationNotAllowedException;
import intellijP.back.exceptions.ValidationException;
import intellijP.back.models.*;
import intellijP.back.repositories.PersonneRepository;
import intellijP.back.repositories.PlaceRepository;
import intellijP.back.repositories.ReservationPlaceRepository;
import intellijP.back.repositories.VehiculeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReservationPlaceServiceTest {
    @Mock
    private ReservationPlaceRepository reservationRepository;
    @Mock
    private PlaceRepository placeRepository;
    @Mock
    private PersonneRepository personneRepository;
    @Mock
    private VehiculeRepository vehiculeRepository;
    @InjectMocks
    private ReservationPlaceService reservationPlaceService;

    @Test
    void testCreerReservation() {
        Personne personne = new Personne();
        personne.setId(2L);
        Place place = new Place();
        place.setId(7L);
        place.setNumero("B12");
        place.setStatut(StatutPlace.LIBRE);
        Vehicule vehicule = new Vehicule();
        vehicule.setId(4L);
        vehicule.setPersonne(personne);

        ReservationPlace reservation = new ReservationPlace();
        reservation.setPersonne(personne);
        reservation.setPlace(place);
        reservation.setVehicule(vehicule);
        reservation.setDateDebut(LocalDateTime.of(2026, 6, 1, 10, 0));
        reservation.setDateFin(LocalDateTime.of(2026, 6, 1, 12, 0));

        when(placeRepository.findById(7L)).thenReturn(Optional.of(place));
        when(personneRepository.findById(2L)).thenReturn(Optional.of(personne));
        when(vehiculeRepository.findById(4L)).thenReturn(Optional.of(vehicule));
        when(reservationRepository.save(reservation)).thenReturn(reservation);

        ReservationPlace resultat = reservationPlaceService.create(reservation);
        assertEquals(StatutReservation.CONFIRMEE, resultat.getStatut());
    }

      @Test
    void testCreerReservationVehiculePasAuProprietaire() {
        Personne personne = new Personne();
        personne.setId(3L);
        Personne autre = new Personne();
        autre.setId(9L);

        Place place = new Place();
        place.setId(5L);
        place.setStatut(StatutPlace.LIBRE);
        Vehicule vehicule = new Vehicule();
        vehicule.setId(8L);
        vehicule.setPersonne(autre);
          ReservationPlace reservation = new ReservationPlace();
          reservation.setPersonne(personne);
          reservation.setPlace(place);
          reservation.setVehicule(vehicule);
          reservation.setDateDebut(LocalDateTime.of(2026, 7, 5, 9, 0));
          reservation.setDateFin(LocalDateTime.of(2026, 7, 5, 11, 0));

        when(placeRepository.findById(5L)).thenReturn(Optional.of(place));
        when(personneRepository.findById(3L)).thenReturn(Optional.of(personne));
        when(vehiculeRepository.findById(8L)).thenReturn(Optional.of(vehicule));

        var resultat = assertThrows(ValidationException.class, () -> reservationPlaceService.create(reservation));
        assertEquals("Le vehicule n'appartient pas a cette personne", resultat.getMessage());
    }
    @Test
    void testCreerReservationPlaceOccupee() {
        Personne personne = new Personne();
        personne.setId(1L);
        Place place = new Place();
        place.setId(2L);
        place.setStatut(StatutPlace.OCCUPEE);
        Vehicule vehicule = new Vehicule();
        vehicule.setId(3L);
        vehicule.setPersonne(personne);
        ReservationPlace reservation = new ReservationPlace();
        reservation.setPersonne(personne);
        reservation.setPlace(place);
        reservation.setVehicule(vehicule);
        reservation.setDateDebut(LocalDateTime.of(2026, 8, 2, 14, 0));
        reservation.setDateFin(LocalDateTime.of(2026, 8, 2, 16, 0));
        when(placeRepository.findById(2L)).thenReturn(Optional.of(place));
        when(personneRepository.findById(1L)).thenReturn(Optional.of(personne));
        when(vehiculeRepository.findById(3L)).thenReturn(Optional.of(vehicule));
        assertThrows(OperationNotAllowedException.class, () -> reservationPlaceService.create(reservation));
    }

       @Test
    void testAnnulerReservationEnCours(){
        ReservationPlace reservation = new ReservationPlace();
        reservation.setId(12L);
        reservation.setStatut(StatutReservation.EN_COURS);
        when(reservationRepository.findById(12L)).thenReturn(Optional.of(reservation));
        assertThrows(OperationNotAllowedException.class, () -> reservationPlaceService.annuler(12L));
    }
      @Test
    void testTerminerReservation() {
        ReservationPlace reservation = new ReservationPlace();
        reservation.setId(15L);
        reservation.setStatut(StatutReservation.EN_COURS);
        when(reservationRepository.findById(15L)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(reservation)).thenReturn(reservation);
        reservationPlaceService.terminer(15L);
        assertEquals(StatutReservation.TERMINEE, reservation.getStatut());
    }
}

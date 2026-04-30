package intellijP.back.services;

import intellijP.back.dto.InitialisationResultatDTO;
import intellijP.back.repositories.CapteurRepository;
import intellijP.back.repositories.EvenementCapteurRepository;
import intellijP.back.repositories.PersonneRepository;
import intellijP.back.repositories.PlaceRepository;
import intellijP.back.repositories.ReservationPlaceRepository;
import intellijP.back.repositories.StationnementRepository;
import intellijP.back.repositories.VehiculeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SimulationServiceTest {
    @Mock
    private PlaceRepository placeRepository;
    @Mock
    private PersonneRepository personneRepository;
    @Mock
    private VehiculeRepository vehiculeRepository;
    @Mock
    private CapteurRepository capteurRepository;
    @Mock
    private EvenementCapteurRepository evenementCapteurRepository;
    @Mock
    private ReservationPlaceRepository reservationPlaceRepository;
    @Mock
    private StationnementRepository stationnementRepository;
    @Mock
    private FluxSSEService fluxSSEService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private ApplicationContext applicationContext;
    @InjectMocks
    private SimulationService simulationService;

    @Test
    void testInitialiser() {
        when(passwordEncoder.encode("sirius2026")).thenReturn("hashe");
        when(vehiculeRepository.findByImmatriculation(anyString())).thenReturn(Optional.empty());
        when(placeRepository.findAll()).thenReturn(List.of());

        InitialisationResultatDTO resultat = simulationService.initialiser(1, 1, 42L);
        assertEquals(1, resultat.getNbPersonnesCreees());
        assertEquals(1, resultat.getNbVehiculesCreees());
        assertEquals(0, resultat.getNbCapteursCreees());
    }
      @Test
    void testArreterSimulation() {
        simulationService.arreterSimulation();
        assertFalse(simulationService.isSimulationActive());
    }
    @Test
    void testArreterReservation(){
        simulationService.arreterReservation();
        assertFalse(simulationService.isReservationActive());
    }

       @Test
    void testGenererReservationsAucunePlace() {
        when(placeRepository.findAll()).thenReturn(List.of());
        simulationService.genererReservationsJournee(LocalDate.of(2026, 6, 1), 0, null, null);
        verify(vehiculeRepository, never()).findAll();
    }
}

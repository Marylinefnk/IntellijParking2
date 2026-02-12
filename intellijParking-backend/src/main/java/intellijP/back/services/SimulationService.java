package intellijP.back.services;

import intellijP.back.dto.InitialisationResultatDTO;
import intellijP.back.models.*;
import intellijP.back.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Service
public class SimulationService {

    private static final Logger logger = LoggerFactory.getLogger(SimulationService.class);

    private final PlaceRepository placeRepository;
    private final PersonneRepository personneRepository;
    private final VehiculeRepository vehiculeRepository;
    private final CapteurRepository capteurRepository;
    private final EvenementCapteurRepository evenementCapteurRepository;
    private final ReservationPlaceRepository reservationPlaceRepository;
    private final StationnementRepository stationnementRepository;
    private final PasswordEncoder passwordEncoder;

    public SimulationService(PlaceRepository placeRepository,
                             PersonneRepository personneRepository,
                             VehiculeRepository vehiculeRepository,
                             CapteurRepository capteurRepository,
                             EvenementCapteurRepository evenementCapteurRepository,
                             ReservationPlaceRepository reservationPlaceRepository,
                             StationnementRepository stationnementRepository,
                             PasswordEncoder passwordEncoder) {
        this.placeRepository = placeRepository;
        this.personneRepository = personneRepository;
        this.vehiculeRepository = vehiculeRepository;
        this.capteurRepository = capteurRepository;
        this.evenementCapteurRepository = evenementCapteurRepository;
        this.reservationPlaceRepository = reservationPlaceRepository;
        this.stationnementRepository = stationnementRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public InitialisationResultatDTO initialiser(int nbPersonnes, int maxVehiculesParPersonne, Long seed) {
        logger.info("Initialisation simulation - {} personnes", nbPersonnes);
        return new InitialisationResultatDTO(0, 0, 0);
    }

}

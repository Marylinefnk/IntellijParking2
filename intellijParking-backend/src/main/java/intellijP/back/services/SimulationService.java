package intellijP.back.services;

import intellijP.back.dto.InitialisationResultatDTO;
import intellijP.back.models.*;
import intellijP.back.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
        logger.info("Initialisation simulation - {} personnes, max {} vehicules/personne", nbPersonnes, maxVehiculesParPersonne);

        Random rng = seed != null ? new Random(seed) : new Random();
        int compteurPersonnes = 0;
        int compteurVehicules = 0;

        String[] prenoms = {"Alice", "Bob", "Charlie", "Diana", "Eric", "Fatou", "Georges", "Hana",
                "Ibrahim", "Julie", "Kevin", "Laura", "Marc", "Nina", "Oscar", "Paula"};
        String[] noms = {"Martin", "Bernard", "Thomas", "Petit", "Robert", "Durand", "Leroy", "Moreau",
                "Simon", "Laurent", "Lefebvre", "Michel", "Garcia", "David", "Bertrand", "Roux"};

        for (int i = 0; i < nbPersonnes; i++) {
            String prenom = prenoms[rng.nextInt(prenoms.length)];
            String nom = noms[rng.nextInt(noms.length)];
            String mail = prenom.toLowerCase() + "." + nom.toLowerCase() + i + "@parking-sirius.fr";

            Personne p = Personne.builder()
                    .prenom(prenom)
                    .nom(nom)
                    .mail(mail)
                    .password(passwordEncoder.encode("sirius2026"))
                    .typePersonne(TypePersonne.ABONNE)
                    .build();
            personneRepository.save(p);
            compteurPersonnes++;

            int nbVehicules = 1 + rng.nextInt(maxVehiculesParPersonne);
            TypeVehicule[] types = TypeVehicule.values();

            for (int j = 0; j < nbVehicules; j++) {
                String immat = genererImmatriculation(rng);
                if (vehiculeRepository.findByImmatriculation(immat).isPresent()) {
                    immat = immat + "-" + rng.nextInt(99);
                }
                Vehicule v = Vehicule.builder()
                        .immatriculation(immat)
                        .typeVehicule(types[rng.nextInt(types.length)])
                        .personne(p)
                        .build();
                vehiculeRepository.save(v);
                compteurVehicules++;
            }
        }

        List<Place> toutesLesPlaces = placeRepository.findAll();
        int compteurCapteurs = 0;

        for (Place place : toutesLesPlaces) {
            if (!capteurRepository.existsByPlaceId(place.getId())) {
                Capteur c = Capteur.builder()
                        .place(place)
                        .etatCapteur(EtatCapteur.ACTIF)
                        .presenceDetectee(false)
                        .build();
                capteurRepository.save(c);
                compteurCapteurs++;
            }
        }

        logger.info("Init terminée: {} personnes, {} véhicules, {} capteurs créés",
                compteurPersonnes, compteurVehicules, compteurCapteurs);

        return new InitialisationResultatDTO(compteurPersonnes, compteurVehicules, compteurCapteurs);
    }

    private String genererImmatriculation(Random rng) {
        String lettres1 = String.valueOf((char) ('A' + rng.nextInt(26))) + (char) ('A' + rng.nextInt(26));
        int chiffres = 100 + rng.nextInt(900);
        String lettres2 = String.valueOf((char) ('A' + rng.nextInt(26))) + (char) ('A' + rng.nextInt(26));
        return lettres1 + "-" + chiffres + "-" + lettres2;
    }

}

package intellijP.back.services;

import intellijP.back.dto.InitialisationResultatDTO;
import intellijP.back.models.*;
import intellijP.back.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

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
            Personne p = Personne.builder().prenom(prenom).nom(nom).mail(mail)
                    .password(passwordEncoder.encode("sirius2026")).typePersonne(TypePersonne.ABONNE).build();
            personneRepository.save(p);
            compteurPersonnes++;
            int nbVehicules = 1 + rng.nextInt(maxVehiculesParPersonne);
            TypeVehicule[] types = TypeVehicule.values();
            for (int j = 0; j < nbVehicules; j++) {
                String immat = genererImmatriculation(rng);
                if (vehiculeRepository.findByImmatriculation(immat).isPresent()) immat = immat + "-" + rng.nextInt(99);
                vehiculeRepository.save(Vehicule.builder().immatriculation(immat)
                        .typeVehicule(types[rng.nextInt(types.length)]).personne(p).build());
                compteurVehicules++;
            }
        }
        List<Place> toutesLesPlaces = placeRepository.findAll();
        int compteurCapteurs = 0;
        for (Place place : toutesLesPlaces) {
            if (!capteurRepository.existsByPlaceId(place.getId())) {
                capteurRepository.save(Capteur.builder().place(place).etatCapteur(EtatCapteur.ACTIF).presenceDetectee(false).build());
                compteurCapteurs++;
            }
        }
        logger.info("Init terminée: {} personnes, {} véhicules, {} capteurs créés", compteurPersonnes, compteurVehicules, compteurCapteurs);
        return new InitialisationResultatDTO(compteurPersonnes, compteurVehicules, compteurCapteurs);
    }

    @Async
    @Transactional
    public void genererReservationsJournee(LocalDate date, int pasSecondes, String niveau, Double tauxCible) {
        logger.info("Génération réservations journée {} - niveau={}, pas={}s", date, niveau, pasSecondes);

        List<Place> places;
        if (niveau != null && !niveau.isBlank()) {
            places = placeRepository.findByZoneNom(niveau);
        } else {
            places = placeRepository.findAll();
        }

        if (places.isEmpty()) {
            logger.warn("Aucune place trouvée pour la génération - niveau={}", niveau);
            return;
        }

        List<Vehicule> tousVehicules = vehiculeRepository.findAll();
        if (tousVehicules.isEmpty()) {
            logger.warn("Aucun vehicule en base - lancer d'abord /simulation/initialiser");
            return;
        }

        Random rng = new Random();
        int compteur = 0;

        // on génère des créneaux entre 6h et 22h
        for (Place place : places) {
            LocalDateTime curseur = LocalDateTime.of(date, LocalTime.of(6, 0));
            LocalDateTime finJournee = LocalDateTime.of(date, LocalTime.of(22, 0));

            while (curseur.isBefore(finJournee)) {
                // durée aléatoire entre 30min et 3h
                int dureeMin = 30 + rng.nextInt(150);
                LocalDateTime debut = curseur.plusMinutes(rng.nextInt(30));
                LocalDateTime fin = debut.plusMinutes(dureeMin);

                if (fin.isAfter(finJournee)) break;

                // on vérifie pas de conflit
                List<ReservationPlace> conflits = reservationPlaceRepository.findConflictingReservations(
                        place.getId(), debut, fin,
                        Arrays.asList(StatutReservation.CONFIRMEE, StatutReservation.EN_COURS));

                if (conflits.isEmpty()) {
                    Vehicule v = tousVehicules.get(rng.nextInt(tousVehicules.size()));

                    ReservationPlace resa = ReservationPlace.builder()
                            .place(place)
                            .vehicule(v)
                            .personne(v.getPersonne())
                            .dateDebut(debut)
                            .dateFin(fin)
                            .statut(StatutReservation.CONFIRMEE)
                            .build();
                    reservationPlaceRepository.save(resa);
                    compteur++;
                }

                curseur = fin.plusMinutes(5 + rng.nextInt(20));

                // pause entre chaque batch pour l'effet progressif en démo
                try {
                    Thread.sleep(pasSecondes * 1000L);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.warn("Génération interrompue");
                    return;
                }
            }
        }

        logger.info("Génération terminée: {} réservations créées pour le {}", compteur, date);
    }

    private String genererImmatriculation(Random rng) {
        String lettres1 = String.valueOf((char) ('A' + rng.nextInt(26))) + (char) ('A' + rng.nextInt(26));
        int chiffres = 100 + rng.nextInt(900);
        String lettres2 = String.valueOf((char) ('A' + rng.nextInt(26))) + (char) ('A' + rng.nextInt(26));
        return lettres1 + "-" + chiffres + "-" + lettres2;
    }

}

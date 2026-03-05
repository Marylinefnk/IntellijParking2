package intellijP.back.services;

import intellijP.back.dto.ChangementPlaceSSEDTO;
import intellijP.back.dto.InitialisationResultatDTO;
import intellijP.back.dto.ReservationSSEDTO;
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
    private final FluxSSEService fluxSSEService;
    private final PasswordEncoder passwordEncoder;

    private volatile boolean simulationActive = false;
    private volatile boolean reservationActive = false;

    public SimulationService(PlaceRepository placeRepository,
                             PersonneRepository personneRepository,
                             VehiculeRepository vehiculeRepository,
                             CapteurRepository capteurRepository,
                             EvenementCapteurRepository evenementCapteurRepository,
                             ReservationPlaceRepository reservationPlaceRepository,
                             StationnementRepository stationnementRepository,
                             FluxSSEService fluxSSEService,
                             PasswordEncoder passwordEncoder) {
        this.placeRepository = placeRepository;
        this.personneRepository = personneRepository;
        this.vehiculeRepository = vehiculeRepository;
        this.capteurRepository = capteurRepository;
        this.evenementCapteurRepository = evenementCapteurRepository;
        this.reservationPlaceRepository = reservationPlaceRepository;
        this.stationnementRepository = stationnementRepository;
        this.fluxSSEService = fluxSSEService;
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
                        .dateDernierSignal(null)
                        .vehiculeDetecte(null)
                        .build();
                capteurRepository.save(c);
                compteurCapteurs++;
            }
        }

        logger.info("Init terminée: {} personnes, {} véhicules, {} capteurs créés",
                compteurPersonnes, compteurVehicules, compteurCapteurs);

        return new InitialisationResultatDTO(compteurPersonnes, compteurVehicules, compteurCapteurs);
    }

    @Async
    @Transactional
    public void genererReservationsJournee(LocalDate date, int pasSecondes, String niveau, Double tauxCible) {
        reservationActive = true;
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

        for (Place place : places) {
            if (!reservationActive) {
                logger.info("Génération réservations interrompue après {} réservations", compteur);
                return;
            }
            LocalDateTime curseur = LocalDateTime.of(date, LocalTime.of(0, 0));
            LocalDateTime finJournee = LocalDateTime.of(date, LocalTime.of(23, 59));

            while (curseur.isBefore(finJournee) && reservationActive) {
                int dureeMin = 30 + rng.nextInt(150);
                LocalDateTime debut = curseur.plusMinutes(rng.nextInt(30));
                LocalDateTime fin = debut.plusMinutes(dureeMin);

                if (fin.isAfter(finJournee)) break;

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
                    logger.info("  [Resa #{}] Place={} | Vehicule={} ({}) | {} -> {} | {}",
                            compteur, place.getNumero(), v.getImmatriculation(),
                            v.getPersonne().getNom(), debut, fin,
                            v.getTypeVehicule());

                    fluxSSEService.diffuserReservation(ReservationSSEDTO.builder()
                            .type("nouvelle_reservation")
                            .dateEvenement(LocalDateTime.now())
                            .idReservation(resa.getId())
                            .numeroPlace(place.getNumero())
                            .niveauPlace(place.getZone() != null ? place.getZone().getNom() : null)
                            .immatriculation(v.getImmatriculation())
                            .typeVehicule(v.getTypeVehicule() != null ? v.getTypeVehicule().name() : null)
                            .nomPersonne(v.getPersonne().getNom())
                            .prenomPersonne(v.getPersonne().getPrenom())
                            .dateDebut(debut)
                            .dateFin(fin)
                            .statut(StatutReservation.CONFIRMEE.name())
                            .compteur(compteur)
                            .build());
                }

                curseur = fin.plusMinutes(5 + rng.nextInt(20));

                try {
                    Thread.sleep(pasSecondes * 1000L);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.warn("Génération interrompue");
                    return;
                }
            }
        }

        reservationActive = false;
        logger.info("Génération terminée: {} réservations créées pour le {}", compteur, date);
    }

    public void arreterReservation() {
        reservationActive = false;
        logger.info("Arrêt génération réservations demandé");
    }

    public boolean isReservationActive() {
        return reservationActive;
    }

    @Async
    public void demarrerSimulation(int intervalleSecondes, String niveau, double probaPresence, int nbCapteursParCycle) {
        if (simulationActive) {
            logger.warn("Simulation déjà active, on ignore la demande");
            return;
        }

        simulationActive = true;
        logger.info("========================================");
        logger.info("SIMULATION DEMARREE - intervalle={}s, niveau={}, proba={}, nbCapteurs={}", intervalleSecondes, niveau, probaPresence, nbCapteursParCycle);
        logger.info("========================================");

        Random rng = new Random();
        int cycle = 0;

        while (simulationActive) {
            cycle++;
            try {
                List<Capteur> capteurs;
                if (niveau != null && !niveau.isBlank()) {
                    capteurs = capteurRepository.findActifsByZoneNom(niveau);
                } else {
                    capteurs = capteurRepository.findAllActifs();
                }

                if (capteurs.isEmpty()) {
                    logger.warn("[Cycle {}] Aucun capteur actif trouve - simulation en attente", cycle);
                } else {
                    int nb = Math.min(nbCapteursParCycle, capteurs.size());
                    Collections.shuffle(capteurs, rng);
                    for (int i = 0; i < nb; i++) {
                        Capteur capteur = capteurs.get(i);
                        boolean nouvellePresence = rng.nextDouble() < probaPresence;

                        if (nouvellePresence != capteur.isPresenceDetectee()) {
                            logger.info("[Cycle {}] Changement detecte place={} : {} -> {}",
                                    cycle, capteur.getPlace().getNumero(),
                                    capteur.isPresenceDetectee() ? "PRESENT" : "ABSENT",
                                    nouvellePresence ? "PRESENT" : "ABSENT");
                            traiterChangementPresence(capteur, nouvellePresence);
                        } else {
                            logger.debug("[Cycle {}] Pas de changement pour place={}", cycle, capteur.getPlace().getNumero());
                        }
                    }
                }

                Thread.sleep(intervalleSecondes * 1000L);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                logger.error("Erreur dans la boucle simulation: {}", e.getMessage());
                e.printStackTrace();
            }
        }

        logger.info("========================================");
        logger.info("SIMULATION ARRETEE apres {} cycles", cycle);
        logger.info("========================================");
    }

    public void arreterSimulation() {
        simulationActive = false;
        logger.info("Arrêt simulation demandé");
    }

    public boolean isSimulationActive() {
        return simulationActive;
    }

    @Transactional
    public void traiterChangementPresence(Capteur capteur, boolean nouvellePresence) {
        Place place = capteur.getPlace();
        logger.info("=== Changement presence : place={}, nouvelle_presence={} ===", place.getNumero(), nouvellePresence);

        if (nouvellePresence) {

            Vehicule vehiculeTrouve = trouverOuChoisirVehicule(place);
            if (vehiculeTrouve == null) {
                logger.warn("Pas de vehicule dispo pour place {}, on skip", place.getNumero());
                return;
            }
            logger.info(">>> ARRIVEE place={} | Vehicule {} ({})",
                    place.getNumero(), vehiculeTrouve.getImmatriculation(),
                    vehiculeTrouve.getTypeVehicule());

            List<ReservationPlace> resasActives = reservationPlaceRepository.findActiveReservationsAtMoment(
                    place.getId(), LocalDateTime.now());

            ReservationPlace resa = null;
            String typeResa = "CREE_A_LA_VOLEE";
            String statutResaAvant = null;

            for (ReservationPlace r : resasActives) {
                if (r.getVehicule().getId().equals(vehiculeTrouve.getId())) {
                    resa = r;
                    typeResa = "PREEXISTANTE";
                    break;
                }
            }

            if (resa != null) {
                statutResaAvant = resa.getStatut().name();
                resa.setStatut(StatutReservation.EN_COURS);
                reservationPlaceRepository.save(resa);
                logger.info("    Reservation id={} PREEXISTANTE ({} -> EN_COURS) pour vehicule {}",
                        resa.getId(), statutResaAvant, vehiculeTrouve.getImmatriculation());
            } else {
                statutResaAvant = null;
                resa = ReservationPlace.builder()
                        .place(place)
                        .vehicule(vehiculeTrouve)
                        .personne(vehiculeTrouve.getPersonne())
                        .dateDebut(LocalDateTime.now())
                        .dateFin(LocalDateTime.now().plusHours(2))
                        .statut(StatutReservation.EN_COURS)
                        .build();
                reservationPlaceRepository.save(resa);
                logger.info("    Reservation id={} CREEE A LA VOLEE pour vehicule {} sur place {}",
                        resa.getId(), vehiculeTrouve.getImmatriculation(), place.getNumero());
            }

            Stationnement stationn = Stationnement.builder()
                    .place(place)
                    .vehicule(vehiculeTrouve)
                    .dateEntree(LocalDateTime.now())
                    .build();
            stationnementRepository.save(stationn);
            logger.info("    Stationnement cree id={} | entree={}",
                    stationn.getId(), stationn.getDateEntree());

            capteur.setPresenceDetectee(true);
            capteur.setVehiculeDetecte(vehiculeTrouve);
            capteur.setDateDernierSignal(LocalDateTime.now());
            capteurRepository.save(capteur);

            EvenementCapteur evt = EvenementCapteur.builder()
                    .capteur(capteur)
                    .place(place)
                    .vehicule(vehiculeTrouve)
                    .reservation(resa)
                    .presenceDetectee(true)
                    .dateEvenement(LocalDateTime.now())
                    .source(SourceEvenement.SIMULATION)
                    .build();
            evenementCapteurRepository.save(evt);

            StatutPlace ancienStatut = place.getStatut();
            place.setStatut(StatutPlace.OCCUPEE);
            placeRepository.save(place);
            logger.info("    Place {} : {} -> OCCUPEE", place.getNumero(), ancienStatut);

            ChangementPlaceSSEDTO dto = construireDTO(place, ancienStatut, StatutPlace.OCCUPEE,
                    "CAPTEUR", vehiculeTrouve, resa, typeResa, statutResaAvant, "EN_COURS");
            fluxSSEService.diffuserChangementPlace(dto);
            logger.info("    SSE diffuse : place={} {} -> OCCUPEE | vehicule={}",
                    place.getNumero(), ancienStatut, vehiculeTrouve.getImmatriculation());

        } else {

            Vehicule vehiculePartant = capteur.getVehiculeDetecte();
            if (vehiculePartant == null) {
                logger.warn("Départ détecté mais pas de vehicule connu sur place {}", place.getNumero());
                return;
            }
            logger.info("<<< DEPART place={} | Vehicule {}",
                    place.getNumero(), vehiculePartant.getImmatriculation());

            Optional<Stationnement> statActif = stationnementRepository.findActiveStationnementsByPlace(place.getId()).stream().findFirst();
            if (statActif.isPresent()) {
                Stationnement s = statActif.get();
                s.setDateSortie(LocalDateTime.now());
                long dureeMin = java.time.temporal.ChronoUnit.MINUTES.between(s.getDateEntree(), s.getDateSortie());
                s.setDureeMin((int) dureeMin);
                s.setTarif(Math.round((dureeMin / 60.0) * 2.0 * 100.0) / 100.0);
                stationnementRepository.save(s);
                logger.info("    Stationnement clos id={} | duree={}min | tarif={}EUR",
                        s.getId(), s.getDureeMin(), s.getTarif());
            } else {
                logger.warn("    Aucun stationnement actif trouve pour place {}", place.getNumero());
            }

            ReservationPlace resaActive = null;
            List<ReservationPlace> resasEnCours = reservationPlaceRepository.findActiveReservationsAtMoment(
                    place.getId(), LocalDateTime.now());
            for (ReservationPlace r : resasEnCours) {
                if (r.getStatut() == StatutReservation.EN_COURS) {
                    resaActive = r;
                    break;
                }
            }

            if (resaActive != null) {
                resaActive.setStatut(StatutReservation.TERMINEE);
                reservationPlaceRepository.save(resaActive);
                logger.info("    Reservation id={} terminee (EN_COURS -> TERMINEE)", resaActive.getId());
            } else {
                logger.info("    Aucune reservation EN_COURS a terminer pour place {}", place.getNumero());
            }

            capteur.setPresenceDetectee(false);
            capteur.setVehiculeDetecte(null);
            capteur.setDateDernierSignal(LocalDateTime.now());
            capteurRepository.save(capteur);

            EvenementCapteur evt = EvenementCapteur.builder()
                    .capteur(capteur)
                    .place(place)
                    .vehicule(vehiculePartant)
                    .reservation(resaActive)
                    .presenceDetectee(false)
                    .dateEvenement(LocalDateTime.now())
                    .source(SourceEvenement.SIMULATION)
                    .build();
            evenementCapteurRepository.save(evt);

            StatutPlace ancienStatut = place.getStatut();
            StatutPlace nouveauStatut = StatutPlace.LIBRE;

            List<ReservationPlace> resasSuivantes = reservationPlaceRepository.findActiveReservationsAtMoment(
                    place.getId(), LocalDateTime.now().plusSeconds(1));
            for (ReservationPlace r : resasSuivantes) {
                if (r.getStatut() == StatutReservation.CONFIRMEE) {
                    nouveauStatut = StatutPlace.RESERVEE;
                    break;
                }
            }

            place.setStatut(nouveauStatut);
            placeRepository.save(place);
            logger.info("    Place {} : {} -> {}", place.getNumero(), ancienStatut, nouveauStatut);

            ChangementPlaceSSEDTO dto = construireDTO(place, ancienStatut, nouveauStatut,
                    "CAPTEUR", vehiculePartant, resaActive,
                    resaActive != null ? "PREEXISTANTE" : null,
                    "EN_COURS", resaActive != null ? "TERMINEE" : null);
            fluxSSEService.diffuserChangementPlace(dto);
            logger.info("    SSE diffuse : place={} {} -> {} | vehicule={}",
                    place.getNumero(), ancienStatut, nouveauStatut, vehiculePartant.getImmatriculation());
        }
    }

    private Vehicule trouverOuChoisirVehicule(Place place) {
        List<ReservationPlace> resasActives = reservationPlaceRepository.findActiveReservationsAtMoment(
                place.getId(), LocalDateTime.now());

        for (ReservationPlace r : resasActives) {
            if (r.getStatut() == StatutReservation.CONFIRMEE) {
                return r.getVehicule();
            }
        }

        List<Vehicule> tousVehicules = vehiculeRepository.findAll();
        Collections.shuffle(tousVehicules);
        for (Vehicule v : tousVehicules) {
            Optional<Stationnement> statActif = stationnementRepository.findActiveStationnementsByVehicule(v.getId()).stream().findFirst();
            if (statActif.isEmpty()) {
                return v;
            }
        }

        return null;
    }

    private String genererImmatriculation(Random rng) {
        String lettres1 = String.valueOf((char) ('A' + rng.nextInt(26))) + (char) ('A' + rng.nextInt(26));
        int chiffres = 100 + rng.nextInt(900);
        String lettres2 = String.valueOf((char) ('A' + rng.nextInt(26))) + (char) ('A' + rng.nextInt(26));
        return lettres1 + "-" + chiffres + "-" + lettres2;
    }

    private ChangementPlaceSSEDTO construireDTO(Place place, StatutPlace ancienStatut, StatutPlace nouveauStatut,
                                                 String cause, Vehicule vehicule, ReservationPlace resa,
                                                 String typeResa, String statutResaAvant, String statutResaApres) {
        ChangementPlaceSSEDTO dto = new ChangementPlaceSSEDTO();
        dto.setDateEvenement(LocalDateTime.now());
        dto.setIdPlace(place.getId());
        dto.setNumero(place.getNumero());
        dto.setNiveau(place.getZone() != null ? place.getZone().getNom() : null);
        dto.setAncienStatut(ancienStatut != null ? ancienStatut.name() : null);
        dto.setNouveauStatut(nouveauStatut.name());
        dto.setCause(cause);

        if (vehicule != null) {
            dto.setIdVehicule(vehicule.getId());
            dto.setImmatriculation(vehicule.getImmatriculation());
            dto.setTypeVehicule(vehicule.getTypeVehicule() != null ? vehicule.getTypeVehicule().name() : null);
        }

        if (resa != null) {
            dto.setIdReservation(resa.getId());
            dto.setTypeReservation(typeResa);
            dto.setStatutResaAvant(statutResaAvant);
            dto.setStatutResaApres(statutResaApres);
        }

        return dto;
    }

}

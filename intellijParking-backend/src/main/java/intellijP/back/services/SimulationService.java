package intellijP.back.services;

import intellijP.back.dto.ChangementPlaceSSEDTO;
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
    private final FluxSSEService fluxSSEService;
    private final PasswordEncoder passwordEncoder;

    // flag pour contrôler la boucle de simulation - volatile pour la visibilité inter-threads
    private volatile boolean simulationActive = false;

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

        // on génère les personnes avec des données aléatoires
        String[] prenoms = {"Alice", "Bob", "Charlie", "Diana", "Eric", "Fatou", "Georges", "Hana",
                "Ibrahim", "Julie", "Kevin", "Laura", "Marc", "Nina", "Oscar", "Paula"};
        String[] noms = {"Martin", "Bernard", "Thomas", "Petit", "Robert", "Durand", "Leroy", "Moreau",
                "Simon", "Laurent", "Lefebvre", "Michel", "Garcia", "David", "Bertrand", "Roux"};

        for (int i = 0; i < nbPersonnes; i++) {
            String prenom = prenoms[rng.nextInt(prenoms.length)];
            String nom = noms[rng.nextInt(noms.length)];
            // on génère un mail unique avec timestamp pour éviter les doublons
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

            // entre 1 et maxVehiculesParPersonne véhicules par personne
            int nbVehicules = 1 + rng.nextInt(maxVehiculesParPersonne);
            TypeVehicule[] types = TypeVehicule.values();

            for (int j = 0; j < nbVehicules; j++) {
                String immat = genererImmatriculation(rng);
                // on vérifie que l'immat n'existe pas déjà
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

        // on crée les capteurs pour les places qui n'en ont pas encore
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
            // quelques créneaux par place dans la journée
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

    @Async
    public void demarrerSimulation(int intervalleSecondes, String niveau, double probaPresence) {
        if (simulationActive) {
            logger.warn("Simulation déjà active, on ignore la demande");
            return;
        }

        simulationActive = true;
        logger.info("Simulation démarrée - intervalle={}s, niveau={}, proba={}", intervalleSecondes, niveau, probaPresence);

        Random rng = new Random();

        while (simulationActive) {
            try {
                // on récupère les capteurs actifs selon le filtre
                List<Capteur> capteurs;
                if (niveau != null && !niveau.isBlank()) {
                    capteurs = capteurRepository.findActifsByZoneNom(niveau);
                } else {
                    capteurs = capteurRepository.findAllActifs();
                }

                if (capteurs.isEmpty()) {
                    logger.warn("Aucun capteur actif trouvé - simulation en attente");
                } else {
                    // on tire un capteur au hasard et on change son état
                    Capteur capteur = capteurs.get(rng.nextInt(capteurs.size()));
                    boolean nouvellePresence = rng.nextDouble() < probaPresence;

                    // on traite le changement seulement si l'état change vraiment
                    if (nouvellePresence != capteur.isPresenceDetectee()) {
                        traiterChangementPresence(capteur, nouvellePresence);
                    }
                }

                Thread.sleep(intervalleSecondes * 1000L);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                // FIXME: à améliorer - ça évite que toute la simulation tombe sur une erreur
                logger.error("Erreur dans la boucle simulation: {}", e.getMessage());
                e.printStackTrace();
            }
        }

        logger.info("Simulation arrêtée");
    }

    public void arreterSimulation() {
        simulationActive = false;
        logger.info("Arrêt simulation demandé");
    }

    public boolean isSimulationActive() {
        return simulationActive;
    }

    // méthode principale qui gère toute la cascade capteur -> reservation -> stationnement -> sse
    @Transactional
    public void traiterChangementPresence(Capteur capteur, boolean nouvellePresence) {
        Place place = capteur.getPlace();
        logger.debug("Traitement changement présence: place={}, presence={}", place.getNumero(), nouvellePresence);

        if (nouvellePresence) {
            // === ARRIVÉE DU VÉHICULE ===

            // on lit la plaque -> on trouve le vehicule
            Vehicule vehiculeTrouve = trouverOuChoisirVehicule(place);
            if (vehiculeTrouve == null) {
                logger.warn("Pas de vehicule dispo pour place {}, on skip", place.getNumero());
                return;
            }

            // on cherche une reservation active pour ce vehicule sur cette place
            List<ReservationPlace> resasActives = reservationPlaceRepository.findActiveReservationsAtMoment(
                    place.getId(), LocalDateTime.now());

            ReservationPlace resa = null;
            String typeResa = "CREE_A_LA_VOLEE";
            String statutResaAvant = null;

            // on regarde si une des resas correspond à ce vehicule
            for (ReservationPlace r : resasActives) {
                if (r.getVehicule().getId().equals(vehiculeTrouve.getId())) {
                    resa = r;
                    typeResa = "PREEXISTANTE";
                    break;
                }
            }

            if (resa != null) {
                // cas 1: réservation trouvée -> on la passe EN_COURS
                statutResaAvant = resa.getStatut().name();
                resa.setStatut(StatutReservation.EN_COURS);
                reservationPlaceRepository.save(resa);
                logger.debug("Resa {} passée EN_COURS pour vehicule {}", resa.getId(), vehiculeTrouve.getImmatriculation());
            } else {
                // cas 2: pas de resa -> on en crée une à la volée
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
                logger.debug("Resa créée à la volée pour vehicule {} sur place {}", vehiculeTrouve.getImmatriculation(), place.getNumero());
            }

            // on ouvre le stationnement
            Stationnement stationn = Stationnement.builder()
                    .place(place)
                    .vehicule(vehiculeTrouve)
                    .dateEntree(LocalDateTime.now())
                    .build();
            stationnementRepository.save(stationn);

            // on met à jour le capteur
            capteur.setPresenceDetectee(true);
            capteur.setVehiculeDetecte(vehiculeTrouve);
            capteur.setDateDernierSignal(LocalDateTime.now());
            capteurRepository.save(capteur);

            // historisation de l'événement
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

            // on change le statut de la place
            StatutPlace ancienStatut = place.getStatut();
            place.setStatut(StatutPlace.OCCUPEE);
            placeRepository.save(place);

            // diffusion SSE
            ChangementPlaceSSEDTO dto = construireDTO(place, ancienStatut, StatutPlace.OCCUPEE,
                    "CAPTEUR", vehiculeTrouve, resa, typeResa, statutResaAvant, "EN_COURS");
            fluxSSEService.diffuserChangementPlace(dto);

        } else {
            // === DÉPART DU VÉHICULE ===

            // on récupère le vehicule qui était détecté
            Vehicule vehiculePartant = capteur.getVehiculeDetecte();
            if (vehiculePartant == null) {
                // FIXME: ça arrive parfois en démo si le capteur est dans un état incohérent
                logger.warn("Départ détecté mais pas de vehicule connu sur place {}", place.getNumero());
                return;
            }

            // on clôture le stationnement actif
            Optional<Stationnement> statActif = stationnementRepository.findActiveStationnementsByPlace(place.getId()).stream().findFirst();
            if (statActif.isPresent()) {
                Stationnement s = statActif.get();
                s.setDateSortie(LocalDateTime.now());
                long dureeMin = java.time.temporal.ChronoUnit.MINUTES.between(s.getDateEntree(), s.getDateSortie());
                s.setDureeMin((int) dureeMin);
                s.setTarif(Math.round((dureeMin / 60.0) * 2.0 * 100.0) / 100.0);
                stationnementRepository.save(s);
            }

            // on termine la reservation active
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
            }

            // on réinitialise le capteur
            capteur.setPresenceDetectee(false);
            capteur.setVehiculeDetecte(null);
            capteur.setDateDernierSignal(LocalDateTime.now());
            capteurRepository.save(capteur);

            // historisation
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

            // on détermine le nouveau statut de la place
            // si y'a une resa confirmée qui suit, on met RESERVEE sinon LIBRE
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

            // diffusion SSE
            ChangementPlaceSSEDTO dto = construireDTO(place, ancienStatut, nouveauStatut,
                    "CAPTEUR", vehiculePartant, resaActive,
                    resaActive != null ? "PREEXISTANTE" : null,
                    "EN_COURS", resaActive != null ? "TERMINEE" : null);
            fluxSSEService.diffuserChangementPlace(dto);
        }
    }

    // on essaie de trouver le vehicule via la reservation active, sinon on en choisit un dispo
    private Vehicule trouverOuChoisirVehicule(Place place) {
        // d'abord on regarde s'il y a une resa confirmee sur cette place
        List<ReservationPlace> resasActives = reservationPlaceRepository.findActiveReservationsAtMoment(
                place.getId(), LocalDateTime.now());

        for (ReservationPlace r : resasActives) {
            if (r.getStatut() == StatutReservation.CONFIRMEE) {
                // on a trouvé le vehicule via la plaque / reservation
                return r.getVehicule();
            }
        }

        // sinon on prend un vehicule aléatoire pas déjà en stationnement actif
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
        // format français: AB-123-CD
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

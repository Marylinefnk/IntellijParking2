package esiag.back.config;

import esiag.back.models.*;
import esiag.back.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
public class DataLoader {

    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);

    @Bean
    CommandLineRunner initDatabase(
            ZoneRepository zoneRepository,
            PersonneRepository personneRepository,
            VehiculeRepository vehiculeRepository,
            PlaceRepository placeRepository,
            StationnementRepository stationnementRepository,
            ServiceRepository serviceRepository,
            ReservationPlaceRepository reservationPlaceRepository,
            ReservationServiceRepository reservationServiceRepository) {

        return args -> {
            // Check if data already exists
            if (zoneRepository.count() > 0) {
                logger.info("Database already contains data. Skipping initialization.");
                return;
            }

            logger.info("Starting database initialization with sample data...");

            // ==================== INSERT ZONES ====================
            logger.info("Inserting zones...");
            Zone zoneA = zoneRepository.save(new Zone("Zone A", "Parking principal - niveau 1"));
            Zone zoneB = zoneRepository.save(new Zone("Zone B", "Parking visiteurs - niveau 1"));
            Zone zoneC = zoneRepository.save(new Zone("Zone C", "Parking réservé - niveau 2"));
            logger.info("Zones created: {}", zoneRepository.count());

            // ==================== INSERT PERSONNES ====================
            logger.info("Inserting personnes...");
            Personne p1 = personneRepository.save(new Personne("Dupont", "Jean", "jean.dupont@example.com"));
            Personne p2 = personneRepository.save(new Personne("Martin", "Marie", "marie.martin@example.com"));
            Personne p3 = personneRepository.save(new Personne("Durand", "Pierre", "pierre.durand@example.com"));
            Personne p4 = personneRepository.save(new Personne("Leroy", "Sophie", "sophie.leroy@example.com"));
            logger.info("Personnes created: {}", personneRepository.count());

            // ==================== INSERT VEHICULES ====================
            logger.info("Inserting vehicules...");
            Vehicule v1 = new Vehicule("AB-123-CD", TypeVehicule.VOITURE);
            v1.setPersonne(p1);
            vehiculeRepository.save(v1);

            Vehicule v2 = new Vehicule("EF-456-GH", TypeVehicule.VOITURE);
            v2.setPersonne(p2);
            vehiculeRepository.save(v2);

            Vehicule v3 = new Vehicule("IJ-789-KL", TypeVehicule.MOTO);
            v3.setPersonne(p3);
            vehiculeRepository.save(v3);

            Vehicule v4 = new Vehicule("MN-012-OP", TypeVehicule.VOITURE);
            v4.setPersonne(p4);
            vehiculeRepository.save(v4);
            logger.info("Vehicules created: {}", vehiculeRepository.count());

            // ==================== INSERT PLACES ====================
            logger.info("Inserting places...");
            Place place1 = new Place("A001", TypePlace.STANDARD, StatutPlace.LIBRE, 10.5, 20.3);
            place1.setZone(zoneA);
            placeRepository.save(place1);

            Place place2 = new Place("A002", TypePlace.STANDARD, StatutPlace.OCCUPEE, 11.5, 20.3);
            place2.setZone(zoneA);
            placeRepository.save(place2);

            Place place3 = new Place("A003", TypePlace.PMR, StatutPlace.LIBRE, 12.5, 20.3);
            place3.setZone(zoneA);
            placeRepository.save(place3);

            Place place4 = new Place("A004", TypePlace.ELECTRIQUE, StatutPlace.RESERVEE, 13.5, 20.3);
            place4.setZone(zoneA);
            placeRepository.save(place4);

            Place place5 = new Place("B001", TypePlace.STANDARD, StatutPlace.LIBRE, 10.5, 30.3);
            place5.setZone(zoneB);
            placeRepository.save(place5);

            Place place6 = new Place("B002", TypePlace.MOTO, StatutPlace.LIBRE, 11.5, 30.3);
            place6.setZone(zoneB);
            placeRepository.save(place6);

            Place place7 = new Place("B003", TypePlace.FAMILIALE, StatutPlace.LIBRE, 12.5, 30.3);
            place7.setZone(zoneB);
            placeRepository.save(place7);

            Place place8 = new Place("C001", TypePlace.STANDARD, StatutPlace.LIBRE, 10.5, 40.3);
            place8.setZone(zoneC);
            placeRepository.save(place8);

            Place place9 = new Place("C002", TypePlace.ELECTRIQUE, StatutPlace.LIBRE, 11.5, 40.3);
            place9.setZone(zoneC);
            placeRepository.save(place9);

            Place place10 = new Place("C003", TypePlace.STANDARD, StatutPlace.OCCUPEE, 12.5, 40.3);
            place10.setZone(zoneC);
            placeRepository.save(place10);
            logger.info("Places created: {}", placeRepository.count());

            // ==================== INSERT STATIONNEMENTS ====================
            logger.info("Inserting stationnements...");
            Stationnement stat1 = new Stationnement(
                    LocalDateTime.of(2026, 1, 8, 8, 0), v1, place2);
            stat1.setDateSortie(LocalDateTime.of(2026, 1, 8, 18, 0));
            stat1.setTarif(15.50);
            stat1.setDureeMin(600);
            stationnementRepository.save(stat1);

            Stationnement stat2 = new Stationnement(
                    LocalDateTime.of(2026, 1, 8, 9, 30), v2, place10);
            stationnementRepository.save(stat2);
            logger.info("Stationnements created: {}", stationnementRepository.count());

            // ==================== INSERT SERVICES ====================
            logger.info("Inserting services...");
            ServiceEntity service1 = new ServiceEntity(TypeService.DEPANNAGE,
                    "Service de dépannage automobile 24h/24");
            serviceRepository.save(service1);

            ServiceEntity service2 = new ServiceEntity(TypeService.LAVERIE,
                    "Service de lavage et nettoyage de véhicules");
            serviceRepository.save(service2);
            logger.info("Services created: {}", serviceRepository.count());

            // ==================== INSERT RESERVATION PLACES ====================
            logger.info("Inserting reservation places...");
            ReservationPlace res1 = new ReservationPlace(
                    p1, place4, v1,
                    LocalDateTime.of(2026, 1, 9, 8, 0),
                    LocalDateTime.of(2026, 1, 9, 18, 0),
                    StatutReservation.CONFIRMEE
            );
            reservationPlaceRepository.save(res1);

            ReservationPlace res2 = new ReservationPlace(
                    p3, place6, v3,
                    LocalDateTime.of(2026, 1, 9, 10, 0),
                    LocalDateTime.of(2026, 1, 9, 12, 0),
                    StatutReservation.CONFIRMEE
            );
            reservationPlaceRepository.save(res2);

            ReservationPlace res3 = new ReservationPlace(
                    p4, place1, v4,
                    LocalDateTime.of(2026, 1, 10, 8, 0),
                    LocalDateTime.of(2026, 1, 10, 18, 0),
                    StatutReservation.EN_COURS
            );
            reservationPlaceRepository.save(res3);
            logger.info("Reservation places created: {}", reservationPlaceRepository.count());

            // ==================== INSERT RESERVATION SERVICES ====================
            logger.info("Inserting reservation services...");
            ReservationService resSrv1 = new ReservationService(
                    p1, service1,
                    LocalDateTime.of(2026, 1, 9, 12, 0),
                    LocalDateTime.of(2026, 1, 9, 13, 0),
                    StatutReservation.CONFIRMEE
            );
            reservationServiceRepository.save(resSrv1);

            ReservationService resSrv2 = new ReservationService(
                    p2, service2,
                    LocalDateTime.of(2026, 1, 9, 8, 0),
                    LocalDateTime.of(2026, 1, 9, 18, 0),
                    StatutReservation.EN_COURS
            );
            reservationServiceRepository.save(resSrv2);

            ReservationService resSrv3 = new ReservationService(
                    p3, service2,
                    LocalDateTime.of(2026, 1, 10, 14, 0),
                    LocalDateTime.of(2026, 1, 10, 15, 0),
                    StatutReservation.CONFIRMEE
            );
            reservationServiceRepository.save(resSrv3);
            logger.info("Reservation services created: {}", reservationServiceRepository.count());

            logger.info("========================================");
            logger.info("Database initialization completed successfully!");
            logger.info("Total records:");
            logger.info("  - Zones: {}", zoneRepository.count());
            logger.info("  - Personnes: {}", personneRepository.count());
            logger.info("  - Vehicules: {}", vehiculeRepository.count());
            logger.info("  - Places: {}", placeRepository.count());
            logger.info("  - Stationnements: {}", stationnementRepository.count());
            logger.info("  - Services: {}", serviceRepository.count());
            logger.info("  - Reservation Places: {}", reservationPlaceRepository.count());
            logger.info("  - Reservation Services: {}", reservationServiceRepository.count());
            logger.info("========================================");
        };
    }
}

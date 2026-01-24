package esiag.back.config;

import esiag.back.models.*;
import esiag.back.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Configuration
public class DataLoader {

    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);

    @Bean
    CommandLineRunner initDatabase(
            PasswordEncoder passwordEncoder,
            ZoneRepository zoneRepository,
            PersonneRepository personneRepository,
            VehiculeRepository vehiculeRepository,
            PlaceRepository placeRepository,
            StationnementRepository stationnementRepository,
            ServiceRepository serviceRepository,
            ReservationPlaceRepository reservationPlaceRepository,
            ReservationServiceRepository reservationServiceRepository) {

        return args -> {

            ensureAdminExists(passwordEncoder, personneRepository);

            if (zoneRepository.count() > 0) {
                logger.info("Database already initialized. Skipping sample data.");
                return;
            }

            logger.info("Initializing database with sample data...");

            // ZONES
            Zone zoneA = zoneRepository.save(
                    Zone.builder().nom("Zone A").description("Parking principal - niveau 1").build()
            );
            Zone zoneB = zoneRepository.save(
                    Zone.builder().nom("Zone B").description("Parking visiteurs - niveau 1").build()
            );
            Zone zoneC = zoneRepository.save(
                    Zone.builder().nom("Zone C").description("Parking réservé - niveau 2").build()
            );

            // superviseur/admin
            Personne p1 = personneRepository.save(
                    Personne.builder()
                            .nom("Admin")
                            .prenom("saurelle")
                            .mail("adminTNT@parking.com")
                            .password(passwordEncoder.encode("admin123@"))
                            .typePersonne(TypePersonne.SUPERVISEUR)
                            .build()
            );
            //abonné
            Personne p2 = personneRepository.save(
                    Personne.builder()
                            .nom("bouga")
                            .prenom("milca")
                            .mail("milca@gmail.com")
                            .password(passwordEncoder.encode("Milca123"))
                            .typePersonne(TypePersonne.ABONNE)
                            .build()
            );
            // visiteur123
            Personne p3 = personneRepository.save(
                    Personne.builder()
                            .nom("Durand")
                            .prenom("Pierre")
                            .mail("pierre@gmail.com")
                            .password(passwordEncoder.encode("pierre123"))
                            .typePersonne(TypePersonne.VISITEUR)
                            .build()
            );
            Personne p4 = personneRepository.save(
                    Personne.builder()
                            .nom("Leroy")
                            .prenom("Sophie")
                            .mail("sophie@gmail.com")
                            .password(passwordEncoder.encode("sophie123"))
                            .typePersonne(TypePersonne.VISITEUR)
                            .build()
            );

            // VEHICULES
            Vehicule v1 = vehiculeRepository.save(
                    Vehicule.builder().immatriculation("AB-123-CD").typeVehicule(TypeVehicule.VOITURE).personne(p1).build()
            );
            Vehicule v2 = vehiculeRepository.save(
                    Vehicule.builder().immatriculation("EF-456-GH").typeVehicule(TypeVehicule.VOITURE).personne(p2).build()
            );
            Vehicule v3 = vehiculeRepository.save(
                    Vehicule.builder().immatriculation("IJ-789-KL").typeVehicule(TypeVehicule.MOTO).personne(p3).build()
            );
            Vehicule v4 = vehiculeRepository.save(
                    Vehicule.builder().immatriculation("MN-012-OP").typeVehicule(TypeVehicule.VOITURE).personne(p4).build()
            );

            // PLACES
            Place place1 = placeRepository.save(
                    Place.builder().numero("A001").type(TypePlace.STANDARD).statut(StatutPlace.LIBRE)
                            .positionX(10.5).positionY(20.3).zone(zoneA).build()
            );
            Place place2 = placeRepository.save(
                    Place.builder().numero("A002").type(TypePlace.STANDARD).statut(StatutPlace.OCCUPEE)
                            .positionX(11.5).positionY( 20.3).zone(zoneA).build()
            );
            Place place3 = placeRepository.save(
                    Place.builder().numero("A003").type(TypePlace.PMR).statut(StatutPlace.LIBRE)
                            .positionX(12.5).positionY( 20.3).zone(zoneA).build()
            );
            Place place4 = placeRepository.save(
                    Place.builder().numero("A004").type(TypePlace.ELECTRIQUE).statut(StatutPlace.RESERVEE)
                            .positionX(13.5).positionY(20.3).zone(zoneA).build()
            );

            //  STATIONNEMENTS
            stationnementRepository.save(
                    Stationnement.builder()
                            .dateEntree(LocalDateTime.of(2026, 1, 8, 8, 0))
                            .dateSortie(LocalDateTime.of(2026, 1, 8, 18, 0))
                            .tarif(15.50)
                            .dureeMin(600)
                            .vehicule(v1)
                            .place(place2)
                            .build()
            );

            stationnementRepository.save(
                    Stationnement.builder()
                            .dateEntree(LocalDateTime.of(2026, 1, 8, 9, 30))
                            .dateSortie(LocalDateTime.of(2026, 1, 8, 18, 0))
                            .tarif(15.50)
                            .vehicule(v2)
                            .place(place1)
                            .build()
            );

            // SERVICES
            ServiceEntity service1 = serviceRepository.save(
                    ServiceEntity.builder()
                            .typeService(TypeService.DEPANNAGE)
                            .description("Service de dépannage automobile 24h/24")
                            .build()
            );

            ServiceEntity service2 = serviceRepository.save(
                    ServiceEntity.builder()
                            .typeService(TypeService.LAVERIE)
                            .description("Service de lavage et nettoyage de véhicules")
                            .build()
            );

            // RESERVATION PLACES
            reservationPlaceRepository.save(
                    ReservationPlace.builder()
                            .personne(p1)
                            .place(place4)
                            .vehicule(v1)
                            .dateDebut(LocalDateTime.of(2026, 1, 9, 8, 0))
                            .dateFin(LocalDateTime.of(2026, 1, 9, 18, 0))
                            .statut(StatutReservation.CONFIRMEE)
                            .build()
            );

            // RESERVATION SERVICES
            reservationServiceRepository.save(
                    ReservationService.builder()
                            .personne(p1)
                            .service(service1)
                            .dateDebut(LocalDateTime.of(2026, 1, 9, 12, 0))
                            .dateFin(LocalDateTime.of(2026, 1, 9, 13, 0))
                            .statut(StatutReservation.CONFIRMEE)
                            .build()
            );

            logger.info("Database initialization completed successfully.");
        };
    }

    private void ensureAdminExists(PasswordEncoder passwordEncoder, PersonneRepository personneRepository) {
        String adminEmail = "adminTN@parking.com";

        if (personneRepository.findByMail(adminEmail).isEmpty()) {
            logger.info("Creating default admin user...");
            personneRepository.save(
                    Personne.builder()
                            .nom("Admin2")
                            .prenom("saurelle2")
                            .mail(adminEmail)
                            .password(passwordEncoder.encode("admin123@"))
                            .typePersonne(TypePersonne.SUPERVISEUR)
                            .build()
            );
            logger.info("Default admin user created: {} / admin123@", adminEmail);
        } else {
            logger.info("Admin saurelle already exists.");
        }
    }
}

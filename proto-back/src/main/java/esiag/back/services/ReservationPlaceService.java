package esiag.back.services;

import esiag.back.models.*;
import esiag.back.repositories.ReservationPlaceRepository;
import esiag.back.repositories.PlaceRepository;
import esiag.back.repositories.PersonneRepository;
import esiag.back.repositories.VehiculeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ReservationPlaceService {

    private final ReservationPlaceRepository reservationRepository;
    private final PlaceRepository placeRepository;
    private final PersonneRepository personneRepository;
    private final VehiculeRepository vehiculeRepository;

    public ReservationPlaceService(ReservationPlaceRepository reservationRepository,
                                  PlaceRepository placeRepository,
                                  PersonneRepository personneRepository,
                                  VehiculeRepository vehiculeRepository) {
        this.reservationRepository = reservationRepository;
        this.placeRepository = placeRepository;
        this.personneRepository = personneRepository;
        this.vehiculeRepository = vehiculeRepository;
    }

    public List<ReservationPlace> findAll() {
        return reservationRepository.findAll();
    }

    public Optional<ReservationPlace> findById(Long id) {
        return reservationRepository.findById(id);
    }

    public List<ReservationPlace> findByPersonne(Long personneId) {
        return reservationRepository.findByPersonneId(personneId);
    }

    public List<ReservationPlace> findByPlace(Long placeId) {
        return reservationRepository.findByPlaceId(placeId);
    }

    public List<ReservationPlace> findByStatut(StatutReservation statut) {
        return reservationRepository.findByStatut(statut);
    }

    public ReservationPlace create(ReservationPlace reservation) {
        Place place = placeRepository.findById(reservation.getPlace().getId())
                .orElseThrow(() -> new RuntimeException("Place non trouvée"));
        
        Personne personne = personneRepository.findById(reservation.getPersonne().getId())
                .orElseThrow(() -> new RuntimeException("Personne non trouvée"));
        
        Vehicule vehicule = vehiculeRepository.findById(reservation.getVehicule().getId())
                .orElseThrow(() -> new RuntimeException("Véhicule non trouvé"));

        List<ReservationPlace> conflits = reservationRepository.findConflictingReservations(
                place.getId(),
                reservation.getDateDebut(),
                reservation.getDateFin(),
                StatutReservation.CONFIRMEE
        );

        if (!conflits.isEmpty()) {
            throw new RuntimeException("Cette place est déjà réservée pour cette période");
        }

        if (place.getStatut() == StatutPlace.OCCUPEE) {
            throw new RuntimeException("Cette place est actuellement occupée");
        }

        reservation.setStatut(StatutReservation.CONFIRMEE);
        place.setStatut(StatutPlace.RESERVEE);
        placeRepository.save(place);

        return reservationRepository.save(reservation);
    }

    public ReservationPlace update(Long id, ReservationPlace reservationDetails) {
        ReservationPlace reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));

        reservation.setDateDebut(reservationDetails.getDateDebut());
        reservation.setDateFin(reservationDetails.getDateFin());
        reservation.setStatut(reservationDetails.getStatut());

        return reservationRepository.save(reservation);
    }

    public void annuler(Long id) {
        ReservationPlace reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));

        reservation.setStatut(StatutReservation.ANNULEE);
        reservationRepository.save(reservation);

        Place place = reservation.getPlace();
        place.setStatut(StatutPlace.LIBRE);
        placeRepository.save(place);
    }

    public void commencer(Long id) {
        ReservationPlace reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));

        if (reservation.getStatut() != StatutReservation.CONFIRMEE) {
            throw new RuntimeException("Seule une réservation confirmée peut être commencée");
        }

        reservation.setStatut(StatutReservation.EN_COURS);
        reservationRepository.save(reservation);

        Place place = reservation.getPlace();
        place.setStatut(StatutPlace.OCCUPEE);
        placeRepository.save(place);
    }

    public void terminer(Long id) {
        ReservationPlace reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));

        reservation.setStatut(StatutReservation.TERMINEE);
        reservationRepository.save(reservation);

        Place place = reservation.getPlace();
        place.setStatut(StatutPlace.LIBRE);
        placeRepository.save(place);
    }

    public void delete(Long id) {
        reservationRepository.deleteById(id);
    }
}

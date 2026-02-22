package intellijP.back.dto;

import intellijP.back.models.*;
import org.springframework.stereotype.Component;

@Component
public class DtoMapper {

    //  Personne
    public PersonneDTO toPersonneDTO(Personne personne) {
        if (personne == null) return null;
        return PersonneDTO.builder()
                .id(personne.getId())
                .nom(personne.getNom())
                .prenom(personne.getPrenom())
                .mail(personne.getMail())
                .typePersonne(personne.getTypePersonne())
                .build();
    }

    //  Zone
    public ZoneDTO toZoneDTO(Zone zone) {
        if (zone == null) return null;
        return ZoneDTO.builder()
                .id(zone.getId())
                .nom(zone.getNom())
                .description(zone.getDescription())
                .build();
    }

    // Vehicule
    public VehiculeDTO toVehiculeDTO(Vehicule vehicule) {
        if (vehicule == null) return null;
        return VehiculeDTO.builder()
                .id(vehicule.getId())
                .immatriculation(vehicule.getImmatriculation())
                .typeVehicule(vehicule.getTypeVehicule())
                .personneId(vehicule.getPersonne() != null ? vehicule.getPersonne().getId() : null)
                .build();
    }

    // Place
    public PlaceDTO toPlaceDTO(Place place) {
        if (place == null) return null;
        return PlaceDTO.builder()
                .id(place.getId())
                .numero(place.getNumero())
                .type(place.getType())
                .statut(place.getStatut())
                .positionX(place.getPositionX())
                .positionY(place.getPositionY())
                .zone(toZoneDTO(place.getZone()))
                .build();
    }

    // Service
    public ServiceDTO toServiceDTO(ServiceEntity service) {
        if (service == null) return null;
        return ServiceDTO.builder()
                .id(service.getId())
                .typeService(service.getTypeService())
                .description(service.getDescription())
                .build();
    }

    //  ReservationPlace
    public ReservationPlaceResponseDTO toReservationPlaceResponseDTO(ReservationPlace reservation) {
        if (reservation == null) return null;
        return ReservationPlaceResponseDTO.builder()
                .id(reservation.getId())
                .personne(toPersonneDTO(reservation.getPersonne()))
                .place(toPlaceDTO(reservation.getPlace()))
                .vehicule(toVehiculeDTO(reservation.getVehicule()))
                .dateDebut(reservation.getDateDebut())
                .dateFin(reservation.getDateFin())
                .statut(reservation.getStatut())
                .build();
    }

    //  ReservationService
    public ReservationServiceResponseDTO toReservationServiceResponseDTO(ReservationService reservation) {
        if (reservation == null) return null;
        return ReservationServiceResponseDTO.builder()
                .id(reservation.getId())
                .personne(toPersonneDTO(reservation.getPersonne()))
                .service(toServiceDTO(reservation.getService()))
                .dateDebut(reservation.getDateDebut())
                .dateFin(reservation.getDateFin())
                .statut(reservation.getStatut())
                .build();
    }

    //  Stationnement
    public StationnementDTO toStationnementDTO(Stationnement stationnement) {
        if (stationnement == null) return null;
        return StationnementDTO.builder()
                .id(stationnement.getId())
                .dateEntree(stationnement.getDateEntree())
                .dateSortie(stationnement.getDateSortie())
                .tarif(stationnement.getTarif())
                .dureeMin(stationnement.getDureeMin())
                .vehicule(toVehiculeDTO(stationnement.getVehicule()))
                .place(toPlaceDTO(stationnement.getPlace()))
                .build();
    }
}

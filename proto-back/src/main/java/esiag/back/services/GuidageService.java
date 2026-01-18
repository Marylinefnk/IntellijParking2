package esiag.back.services;

import esiag.back.services.ReservationPlaceService;
import esiag.back.models.Noeud;
import esiag.back.services.NoeudService;
import esiag.back.models.Arete;
import esiag.back.services.AreteService;
import esiag.back.models.Place;
import esiag.back.services.PLaceService;
import esiag.back.models.ReservationPlace;

//Créer les services NeoudService, AreteService

public class GuidageService {

    public void afficherCheminVersPlace(Long idReservation) {
        //ReservationPlace reservation = findReservationById(idReservation);
        ReservationPlace reservation = findById(idReservation).orElse(null);

        if (reservation == null) {
            System.out.println("Réservation introuvable avec l'ID : " + idReservation);
            return;
        }

        System.out.println("-------CHEMIN VERS LA PLACE RÉSERVÉE------- ");
        System.out.println();
        System.out.println("Conducteur : " + reservation.getPersonne().getPrenom() + " "
                + reservation.getPersonne().getNom());
        System.out.println("Véhicule  : " + reservation.getVehicule().getTypeVehicule()
                + " (" + reservation.getVehicule().getImmatriculation() + ")");
        System.out.println("N° de la place réservée : " + reservation.getPlace().getNumero());
        System.out.println("Type de place : " + reservation.getPlace().getType());
        System.out.println("État de la place : " + reservation.getPlace().getStatut());
        System.out.println("Statut de la réservation : " + reservation.getStatut());
        System.out.println();
        System.out.println("Coordonnées de la place:");
        System.out.println("   Position X   : " + reservation.getPlace().getPositionX());
        System.out.println("   Position Y   : " + reservation.getPlace().getPositionY());
        System.out.println();
        System.out.println("CHEMIN CALCULÉ:");
        System.out.println("   Entrée (0, 0) ---> Place (" + reservation.getPlace().getPositionX()
                + ", " + reservation.getPlace().getPositionY() + ")");
        System.out.println();

        double distance = Math.sqrt(
                Math.pow(reservation.getPlace().getPositionX(), 2) +
                        Math.pow(reservation.getPlace().getPositionY(), 2)
        );
        System.out.println("Distance estimée : " + String.format("%.2f", distance) + " mètres");
    }

    public void afficherToutesLesReservations() {
        List<ReservationPlace> reservations = findAll();

        if (reservations.isEmpty()) {
            System.out.println("Aucune réservation trouvée dans la base de données.");
            return;
        }

        System.out.println("----- LISTE DES RESERVATIONS ------");

        for (ReservationPlace r : reservations) {
            System.out.println("ID: " + r.getId() + " | "
                    + r.getPersonne().getPrenom() + " " + r.getPersonne().getNom()
                    + " Place réservée " + r.getPlace().getNumero()
                    + " | Statut : " + r.getStatut());
        }
        System.out.println("------------------------------------");
    }

}
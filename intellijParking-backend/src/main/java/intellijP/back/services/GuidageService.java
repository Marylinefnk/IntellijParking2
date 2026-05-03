package intellijP.back.services;

import intellijP.back.models.Noeud;
import intellijP.back.models.NoeudType;
import intellijP.back.models.Arete;
import intellijP.back.models.Place;
import intellijP.back.models.Personne;
import intellijP.back.models.ReservationPlace;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import intellijP.back.services.NoeudService;
import intellijP.back.services.AreteService;
import intellijP.back.services.ReservationPlaceService;

@Service
@Transactional
public class GuidageService {

    private final ReservationPlaceService reservationPlaceService;
    private final NoeudService noeudService;
    private final AreteService areteService;

    public GuidageService(ReservationPlaceService reservationPlaceService,
                          NoeudService noeudService,
                          AreteService areteService) {
        this.reservationPlaceService = reservationPlaceService;
        this.noeudService = noeudService;
        this.areteService = areteService;
    }

    private Noeud trouverNoeudLePlusProche(Place placeReservee, List<Noeud> noeuds) {
        Noeud noeudPlusProche = null;
        double distanceMin = Double.MAX_VALUE;

        for (Noeud n : noeuds) {
            if (n.getNoeudType() != NoeudType.acces_zone){
                continue;
            }

            double distanceDesX = n.getPositionX() - placeReservee.getPositionX();
            double distanceDesY = n.getPositionY() - placeReservee.getPositionY();
            double distanceNoeudPlace = Math.sqrt(distanceDesX * distanceDesX + distanceDesY * distanceDesY);
            if (distanceNoeudPlace < distanceMin) {
                distanceMin = distanceNoeudPlace;
                noeudPlusProche = n;
            }
        }

        if (noeudPlusProche == null) {
            for (Noeud n : noeuds) {
                if (n.getNoeudType() == NoeudType.couloir) {
                    double distanceDesX = n.getPositionX() - placeReservee.getPositionX();
                    double distanceDesY = n.getPositionY() - placeReservee.getPositionY();
                    double distanceNoeudPlace = Math.sqrt(distanceDesX * distanceDesX + distanceDesY * distanceDesY);

                    if (distanceNoeudPlace < distanceMin) {
                        distanceMin = distanceNoeudPlace;
                        noeudPlusProche = n;
                    }
                }
            }
        }

        return noeudPlusProche;
    }

    public List<Noeud> calculerChemin(Place placeReservee, Map<Long, Double> distances) {
        List<Noeud> noeuds = noeudService.findAll();
        List<Arete> aretes = areteService.findAll();

        Map<Long, List<Arete>> liste = new HashMap<>();
        for (Noeud n : noeuds) liste.put(n.getId(), new ArrayList<>());
        for (Arete a : aretes) liste.get(a.getNoeudSource().getId()).add(a);

        Map<Long, Long> precedents = new HashMap<>();
        Set<Long> noeudsNonVisites = new HashSet<>();

        for (Noeud n : noeuds) {
            distances.put(n.getId(), Double.MAX_VALUE);
            noeudsNonVisites.add(n.getId());
        }

        Long pointDeDepart = 1L;
        distances.put(pointDeDepart, 0.0);

        Noeud noeudCible = trouverNoeudLePlusProche(placeReservee, noeuds);
        Long IdNoeudCible = noeudCible.getId();

        while (!noeudsNonVisites.isEmpty()) {
            Long noeudLePlusProche = null;
            double distanceMin = Double.MAX_VALUE;
            for (Long id : noeudsNonVisites) {
                if (distances.get(id) < distanceMin) {
                    distanceMin = distances.get(id);
                    noeudLePlusProche = id;
                }
            }
            if (noeudLePlusProche == null) break;

            noeudsNonVisites.remove(noeudLePlusProche);

            for (Arete a : liste.get(noeudLePlusProche)) {
                Long voisin = a.getNoeudDestination().getId();
                if (noeudsNonVisites.contains(voisin)) {
                    double distanceTotaleJusquAuVoisin = distances.get(noeudLePlusProche) + a.getPoids();

                    System.out.println("\nNoeud courant : " + noeudService.findById(noeudLePlusProche).get().getNom() + " (distance = " + distances.get(noeudLePlusProche) + ")");
                    System.out.println(" Test : " + noeudService.findById(noeudLePlusProche).get().getNom() + " vers " + a.getNoeudDestination().getNom() + " = " + distances.get(noeudLePlusProche) + " + " + a.getPoids()
                            + " = " + distanceTotaleJusquAuVoisin);
                    if (distanceTotaleJusquAuVoisin < distances.get(voisin)) {
                        distances.put(voisin, distanceTotaleJusquAuVoisin);
                        precedents.put(voisin, noeudLePlusProche);
                    }
                }
            }
        }

        List<Noeud> chemin = new ArrayList<>();
        Long noeudCourrant = IdNoeudCible;
        while (noeudCourrant != null) {
            Noeud n = noeudService.findById(noeudCourrant).orElse(null);
            if (n != null) chemin.add(0, n);
            noeudCourrant = precedents.get(noeudCourrant);
        }

        return chemin;
    }
    public void afficherCheminVersPlace(Long idReservation) {
        ReservationPlace reservation = reservationPlaceService.findById(idReservation).orElse(null);
        if (reservation == null) {
            System.out.println("Réservation introuvable avec l'ID : " + idReservation);
            return;
        }

        System.out.println("        RECAPITULATIF DE LA RESERVATION DE LA PLACE "+reservation.getPlace().getNumero());
        System.out.println("Conducteur : " + reservation.getPersonne().getPrenom() + " " + reservation.getPersonne().getNom());
        System.out.println("Véhicule : " + reservation.getVehicule().getTypeVehicule() + " (" + reservation.getVehicule().getImmatriculation() + ")");
        System.out.println("N° de la place réservée : " + reservation.getPlace().getNumero());
        System.out.println("Type de place : " + reservation.getPlace().getType());
        System.out.println("État de la place : " + reservation.getPlace().getStatut());
        System.out.println("Coordonnées : X=" + reservation.getPlace().getPositionX() + ", Y=" + reservation.getPlace().getPositionY());
        System.out.println();

        Map<Long, Double> distances = new HashMap<>();
        List<Noeud> cheminCalcule = calculerChemin(reservation.getPlace(), distances);
        System.out.println();
        System.out.println("        ETAPES POUR ALLER A LA PLACE DE PARKING " +reservation.getPlace().getNumero());
        System.out.println();

        Noeud noeuddepart = cheminCalcule.get(0);
        System.out.println("Départ = " + noeuddepart.getNom());

        for (int i = 1; i < cheminCalcule.size(); i++) {
            Noeud noeudPrecedent = cheminCalcule.get(i - 1);
            Noeud noeudActuel = cheminCalcule.get(i);

            System.out.println();
            System.out.println("ÉTAPE " + i);
            System.out.println( noeudActuel.getNom());

            double distance = calculerDistance(noeudPrecedent, noeudActuel);
            System.out.println("Distance entre " + noeudPrecedent.getNom() + " et " +noeudActuel.getNom() +" : " + distance + " mètres");

        }

        Noeud dernierNoeud = cheminCalcule.get(cheminCalcule.size() - 1);
        double distanceFinale = calculerDistance(dernierNoeud, reservation.getPlace());
        System.out.println();
        System.out.println("Arrivée = place " + reservation.getPlace().getNumero());
        double distanceTotale = calculerDistanceTotale(cheminCalcule, reservation.getPlace(), distances);
        System.out.println();
        System.out.println("Distance totale de l'itinéraire : " +  distanceTotale + " mètres");
        System.out.println();

    }

    public void afficherToutesLesReservations() {
        List<ReservationPlace> reservations = reservationPlaceService.findAll();
        if (reservations.isEmpty()) {
            System.out.println("Aucune réservation trouvée.");
            return;
        }
        System.out.println("     LISTE DES RESERVATIONS    ");
        for (ReservationPlace r : reservations) {
            System.out.println("ID: " + r.getId() + " | "
                    + r.getPersonne().getPrenom() + " " + r.getPersonne().getNom()
                    + " | Place : " + r.getPlace().getNumero()
                    + " | Statut : " + r.getStatut());
        }
        System.out.println("----------------------------------");
        System.out.println();
    }

    public static double calculerDistance(Noeud noeud1, Noeud noeud2) {
        double distanceDesX = noeud2.getPositionX() - noeud1.getPositionX();
        double distanceDesY = noeud2.getPositionY() - noeud1.getPositionY();
        return Math.sqrt(distanceDesX * distanceDesX + distanceDesY * distanceDesY);
    }
    private double calculerDistance(Noeud n, Place p) {
        double distanceDesX = p.getPositionX() - n.getPositionX();
        double distanceDesY = p.getPositionY() - n.getPositionY();
        return Math.sqrt(distanceDesX * distanceDesX + distanceDesY * distanceDesY);
    }
    private double calculerDistanceTotale(List<Noeud> chemin, Place destination, Map<Long, Double> distances) {
        if (chemin.isEmpty()) return 0.0;

        Long dernierNoeudId = chemin.get(chemin.size() - 1).getId();
        double distanceauDernierNoeud = distances.getOrDefault(dernierNoeudId, 0.0);

        Noeud dernierNoeud = chemin.get(chemin.size() - 1);
        double distanceNoeudPlace = Math.sqrt( //distance entre le dernier noeud du chemine et la place
                Math.pow(destination.getPositionX() - dernierNoeud.getPositionX(), 2) + Math.pow(destination.getPositionY() - dernierNoeud.getPositionY(), 2)
        );
        return distanceauDernierNoeud + distanceNoeudPlace;
    }

}
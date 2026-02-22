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

        // Si aucune zone trouvée, chercher le couloir le plus proche
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

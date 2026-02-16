package intellijP.back.controllers;

import intellijP.back.dto.InitialisationResultatDTO;
import intellijP.back.services.SimulationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/simulation")
public class SimulationController {

    private final SimulationService simulationService;

    public SimulationController(SimulationService simulationService) {
        this.simulationService = simulationService;
    }

    @PostMapping("/initialiser")
    @PreAuthorize("hasRole('SUPERVISEUR')")
    public ResponseEntity<InitialisationResultatDTO> initialiser(
            @RequestParam(defaultValue = "20") int nbPersonnes,
            @RequestParam(defaultValue = "2") int vehiculesParPersonneMax,
            @RequestParam(required = false) Long seed) {

        if (nbPersonnes < 1) return ResponseEntity.badRequest().build();
        if (vehiculesParPersonneMax < 1 || vehiculesParPersonneMax > 3) return ResponseEntity.badRequest().build();

        InitialisationResultatDTO resultat = simulationService.initialiser(nbPersonnes, vehiculesParPersonneMax, seed);
        return ResponseEntity.ok(resultat);
    }

    @PostMapping("/reservations/journee")
    @PreAuthorize("hasRole('SUPERVISEUR')")
    public ResponseEntity<Map<String, Object>> genererReservations(
            @RequestParam String date,
            @RequestParam(defaultValue = "5") int pasGenerationSecondes,
            @RequestParam(required = false) String niveau,
            @RequestParam(required = false) Double tauxCible) {

        LocalDate dateJournee;
        try {
            dateJournee = LocalDate.parse(date);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

        if (pasGenerationSecondes < 1 || pasGenerationSecondes > 10) {
            return ResponseEntity.badRequest().build();
        }

        // on lance en async et on répond direct
        simulationService.genererReservationsJournee(dateJournee, pasGenerationSecondes, niveau, tauxCible);

        Map<String, Object> reponse = new HashMap<>();
        reponse.put("message", "Génération des réservations lancée en arrière-plan");
        reponse.put("date", date);
        reponse.put("niveau", niveau != null ? niveau : "tous");
        reponse.put("pasGenerationSecondes", pasGenerationSecondes);

        return ResponseEntity.accepted().body(reponse);
    }

}

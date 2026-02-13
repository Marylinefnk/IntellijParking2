package intellijP.back.controllers;

import intellijP.back.dto.InitialisationResultatDTO;
import intellijP.back.services.SimulationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

}

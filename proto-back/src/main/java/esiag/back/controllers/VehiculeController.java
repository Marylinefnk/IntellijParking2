package esiag.back.controllers;

import esiag.back.dto.VehiculeCreateDTO;
import esiag.back.dto.VehiculeDTO;
import esiag.back.exceptions.ResourceNotFoundException;
import esiag.back.models.TypeVehicule;
import esiag.back.services.VehiculeService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicules")
public class VehiculeController {

    private final VehiculeService vehiculeService;

    public VehiculeController(VehiculeService vehiculeService) {
        this.vehiculeService = vehiculeService;
    }

    //  ENDPOINTS DE LECTURE

    @GetMapping
    public List<VehiculeDTO> getAllVehicules() {
        return vehiculeService.findAllDTO();
    }

    @GetMapping("/{id}")
    public VehiculeDTO getVehiculeById(@PathVariable Long id) {
        return vehiculeService.findByIdDTO(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicule", id));
    }

    @GetMapping("/personne/{personneId}")
    public List<VehiculeDTO> getVehiculesByPersonne(@PathVariable Long personneId) {
        return vehiculeService.findByPersonneDTO(personneId);
    }

    @GetMapping("/immatriculation/{immatriculation}")
    public VehiculeDTO getVehiculeByImmatriculation(@PathVariable String immatriculation) {
        return vehiculeService.findByImmatriculationDTO(immatriculation)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Vehicule avec immatriculation '" + immatriculation + "' non trouve"));
    }

    @GetMapping("/type/{type}")
    public List<VehiculeDTO> getVehiculesByType(@PathVariable TypeVehicule type) {
        return vehiculeService.findByTypeDTO(type);
    }

    //  ENDPOINTS DE MODIFICATION

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VehiculeDTO createVehicule(@RequestBody VehiculeCreateDTO createDTO) {
        return vehiculeService.createDTO(createDTO);
    }

    @PutMapping("/{id}")
    public VehiculeDTO updateVehicule(@PathVariable Long id, @RequestBody VehiculeCreateDTO updateDTO) {
        return vehiculeService.updateDTO(id, updateDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVehicule(@PathVariable Long id) {
        vehiculeService.deleteDTO(id);
    }
}

package esiag.back.controllers;

import esiag.back.dto.DtoMapper;
import esiag.back.dto.VehiculeCreateDTO;
import esiag.back.dto.VehiculeDTO;
import esiag.back.models.Personne;
import esiag.back.models.TypeVehicule;
import esiag.back.models.Vehicule;
import esiag.back.services.VehiculeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/vehicules")
public class VehiculeController {

    private final VehiculeService vehiculeService;
    private final DtoMapper dtoMapper;

    public VehiculeController(VehiculeService vehiculeService, DtoMapper dtoMapper) {
        this.vehiculeService = vehiculeService;
        this.dtoMapper = dtoMapper;
    }

    @GetMapping
    public List<VehiculeDTO> getAllVehicules() {
        return vehiculeService.findAll().stream()
                .map(dtoMapper::toVehiculeDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehiculeDTO> getVehiculeById(@PathVariable Long id) {
        return vehiculeService.findById(id)
                .map(dtoMapper::toVehiculeDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/personne/{personneId}")
    public List<VehiculeDTO> getVehiculesByPersonne(@PathVariable Long personneId) {
        return vehiculeService.findByPersonne(personneId).stream()
                .map(dtoMapper::toVehiculeDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/immatriculation/{immatriculation}")
    public ResponseEntity<VehiculeDTO> getVehiculeByImmatriculation(@PathVariable String immatriculation) {
        return vehiculeService.findByImmatriculation(immatriculation)
                .map(dtoMapper::toVehiculeDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/type/{type}")
    public List<VehiculeDTO> getVehiculesByType(@PathVariable TypeVehicule type) {
        return vehiculeService.findByType(type).stream()
                .map(dtoMapper::toVehiculeDTO)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<VehiculeDTO> createVehicule(@RequestBody VehiculeCreateDTO createDTO) {
        try {
            Vehicule vehicule = new Vehicule();
            vehicule.setImmatriculation(createDTO.getImmatriculation());
            vehicule.setTypeVehicule(createDTO.getTypeVehicule());

            if (createDTO.getPersonneId() != null) {
                Personne personne = new Personne();
                personne.setId(createDTO.getPersonneId());
                vehicule.setPersonne(personne);
            }

            Vehicule created = vehiculeService.create(vehicule);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(dtoMapper.toVehiculeDTO(created));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehiculeDTO> updateVehicule(@PathVariable Long id,
                                                       @RequestBody VehiculeCreateDTO updateDTO) {
        try {
            Vehicule vehiculeDetails = new Vehicule();
            vehiculeDetails.setImmatriculation(updateDTO.getImmatriculation());
            vehiculeDetails.setTypeVehicule(updateDTO.getTypeVehicule());

            if (updateDTO.getPersonneId() != null) {
                Personne personne = new Personne();
                personne.setId(updateDTO.getPersonneId());
                vehiculeDetails.setPersonne(personne);
            }

            Vehicule updated = vehiculeService.update(id, vehiculeDetails);
            return ResponseEntity.ok(dtoMapper.toVehiculeDTO(updated));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicule(@PathVariable Long id) {
        vehiculeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

package intellijP.back.controllers;

import intellijP.back.dto.DtoMapper;
import intellijP.back.dto.ServiceCreateDTO;
import intellijP.back.dto.ServiceDTO;
import intellijP.back.models.ServiceEntity;
import intellijP.back.models.TypeService;
import intellijP.back.services.ServiceEntityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/services")
public class ServiceController {

    private final ServiceEntityService serviceEntityService;
    private final DtoMapper dtoMapper;

    public ServiceController(ServiceEntityService serviceEntityService, DtoMapper dtoMapper) {
        this.serviceEntityService = serviceEntityService;
        this.dtoMapper = dtoMapper;
    }

    @GetMapping
    public List<ServiceDTO> getAllServices() {
        return serviceEntityService.findAll().stream()
                .map(dtoMapper::toServiceDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceDTO> getServiceById(@PathVariable Long id) {
        return serviceEntityService.findById(id)
                .map(dtoMapper::toServiceDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/type/{type}")
    public List<ServiceDTO> getServicesByType(@PathVariable TypeService type) {
        return serviceEntityService.findByTypeService(type).stream()
                .map(dtoMapper::toServiceDTO)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<ServiceDTO> createService(@RequestBody ServiceCreateDTO createDTO) {
        try {
            ServiceEntity service = new ServiceEntity();
            service.setTypeService(createDTO.getTypeService());
            service.setDescription(createDTO.getDescription());

            ServiceEntity created = serviceEntityService.create(service);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(dtoMapper.toServiceDTO(created));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceDTO> updateService(@PathVariable Long id,
                                                     @RequestBody ServiceCreateDTO updateDTO) {
        try {
            ServiceEntity serviceDetails = new ServiceEntity();
            serviceDetails.setTypeService(updateDTO.getTypeService());
            serviceDetails.setDescription(updateDTO.getDescription());

            ServiceEntity updated = serviceEntityService.update(id, serviceDetails);
            return ResponseEntity.ok(dtoMapper.toServiceDTO(updated));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        serviceEntityService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

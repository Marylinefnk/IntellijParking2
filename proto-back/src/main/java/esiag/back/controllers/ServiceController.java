package esiag.back.controllers;

import esiag.back.dto.DtoMapper;
import esiag.back.dto.ServiceCreateDTO;
import esiag.back.dto.ServiceDTO;
import esiag.back.models.ServiceEntity;
import esiag.back.models.TypeService;
import esiag.back.services.ServiceEntityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/services")
public class ServiceController {

    private static final Logger logger = LoggerFactory.getLogger(ServiceController.class);

    private final ServiceEntityService serviceEntityService;
    private final DtoMapper dtoMapper;

    public ServiceController(ServiceEntityService serviceEntityService, DtoMapper dtoMapper) {
        this.serviceEntityService = serviceEntityService;
        this.dtoMapper = dtoMapper;
        logger.info("ServiceController initialise");
    }

    @GetMapping
    public List<ServiceDTO> getAllServices() {
        logger.debug("GET /api/services");
        List<ServiceDTO> result = serviceEntityService.findAll().stream()
                .map(dtoMapper::toServiceDTO)
                .collect(Collectors.toList());
        logger.info("GET /api/services - {} services retournes", result.size());
        return result;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceDTO> getServiceById(@PathVariable Long id) {
        logger.debug("GET /api/services/{}", id);
        return serviceEntityService.findById(id)
                .map(service -> {
                    logger.debug("GET /api/services/{} - service trouve", id);
                    return ResponseEntity.ok(dtoMapper.toServiceDTO(service));
                })
                .orElseGet(() -> {
                    logger.warn("GET /api/services/{} - service non trouve", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @GetMapping("/type/{type}")
    public List<ServiceDTO> getServicesByType(@PathVariable TypeService type) {
        logger.debug("GET /api/services/type/{}", type);
        List<ServiceDTO> result = serviceEntityService.findByTypeService(type).stream()
                .map(dtoMapper::toServiceDTO)
                .collect(Collectors.toList());
        logger.debug("GET /api/services/type/{} - {} services trouves", type, result.size());
        return result;
    }

    @PostMapping
    public ResponseEntity<ServiceDTO> createService(@RequestBody ServiceCreateDTO createDTO) {
        logger.debug("POST /api/services - type: {}", createDTO.getTypeService());
        try {
            ServiceEntity service = new ServiceEntity();
            service.setTypeService(createDTO.getTypeService());
            service.setDescription(createDTO.getDescription());

            ServiceEntity created = serviceEntityService.create(service);
            logger.info("POST /api/services - service cree id={}", created.getId());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(dtoMapper.toServiceDTO(created));
        } catch (RuntimeException e) {
            logger.warn("POST /api/services - echec: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceDTO> updateService(@PathVariable Long id,
                                                     @RequestBody ServiceCreateDTO updateDTO) {
        logger.debug("PUT /api/services/{}", id);
        try {
            ServiceEntity serviceDetails = new ServiceEntity();
            serviceDetails.setTypeService(updateDTO.getTypeService());
            serviceDetails.setDescription(updateDTO.getDescription());

            ServiceEntity updated = serviceEntityService.update(id, serviceDetails);
            logger.info("PUT /api/services/{} - service mis a jour", id);
            return ResponseEntity.ok(dtoMapper.toServiceDTO(updated));
        } catch (RuntimeException e) {
            logger.warn("PUT /api/services/{} - echec: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        logger.debug("DELETE /api/services/{}", id);
        try {
            serviceEntityService.delete(id);
            logger.info("DELETE /api/services/{} - service supprime", id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            logger.warn("DELETE /api/services/{} - echec: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}

package esiag.back.repositories;

import esiag.back.models.ServiceEntity;
import esiag.back.models.TypeService;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> {
    List<ServiceEntity> findByTypeService(TypeService typeService);
}

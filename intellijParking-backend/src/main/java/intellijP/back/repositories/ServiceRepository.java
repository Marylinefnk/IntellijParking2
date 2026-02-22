package intellijP.back.repositories;

import intellijP.back.models.ServiceEntity;
import intellijP.back.models.TypeService;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> {
    List<ServiceEntity> findByTypeService(TypeService typeService);
}

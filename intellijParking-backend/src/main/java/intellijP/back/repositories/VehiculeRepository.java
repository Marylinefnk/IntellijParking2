package intellijP.back.repositories;

import intellijP.back.models.Vehicule;
import intellijP.back.models.TypeVehicule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface VehiculeRepository extends JpaRepository<Vehicule, Long> {
    List<Vehicule> findByPersonneId(Long personneId);
    Optional<Vehicule> findByImmatriculation(String immatriculation);
    List<Vehicule> findByTypeVehicule(TypeVehicule typeVehicule);
}

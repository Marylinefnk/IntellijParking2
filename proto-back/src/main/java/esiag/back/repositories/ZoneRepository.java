package esiag.back.repositories;

import esiag.back.models.Zone;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ZoneRepository extends JpaRepository<Zone, Long> {
    Optional<Zone> findByNom(String nom);
}

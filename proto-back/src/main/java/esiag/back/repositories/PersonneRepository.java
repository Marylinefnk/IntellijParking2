package esiag.back.repositories;

import esiag.back.models.Personne;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PersonneRepository extends JpaRepository<Personne, Long> {
    Optional<Personne> findByMail(String mail);
    boolean existsByMail(String mail);
}

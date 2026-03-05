package intellijP.back.repositories;

import intellijP.back.models.Arete;
import intellijP.back.models.Noeud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface AreteRepository extends JpaRepository<Arete, Long> {
    List<Arete> findByNoeudSource(Noeud noeudSource);

}
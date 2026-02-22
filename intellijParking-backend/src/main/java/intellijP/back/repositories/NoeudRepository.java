package intellijP.back.repositories;

import intellijP.back.models.Noeud;
import intellijP.back.models.NoeudType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.*;

@Repository
public interface NoeudRepository extends JpaRepository<Noeud, Long> {

    List<Noeud> findByNoeudType(NoeudType noeudType);
}
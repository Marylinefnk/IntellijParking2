package esiag.back.repositories;

import esiag.back.models.Noeud;
import esiag.back.models.NoeudType;
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
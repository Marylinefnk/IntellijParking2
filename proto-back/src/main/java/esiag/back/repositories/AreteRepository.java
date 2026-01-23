package esiag.back.repositories;

import esiag.back.models.Arete;
import esiag.back.models.Noeud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface AreteRepository extends JpaRepository<Arete, Long> {
    List<Arete> findByNoeudSource(Noeud noeudSource);
    // SELECT * FROM arete WHERE id_noeud_source = ?;
    //Afficher toutes les aretes partant de ce noeud

}
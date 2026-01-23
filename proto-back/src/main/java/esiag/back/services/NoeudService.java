package esiag.back.services;

import esiag.back.models.Noeud;
import esiag.back.repositories.NoeudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class NoeudService {

    private final NoeudRepository noeudRepository;

    public NoeudService(NoeudRepository noeudRepository) {
        this.noeudRepository = noeudRepository;
    }

    public List<Noeud> findAll() {
        return noeudRepository.findAll();
    }

    public Optional<Noeud> findById(Long id) {
        return noeudRepository.findById(id);
    }

    public Noeud findNoeudbyId(Long id) {
        return noeudRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Noeud non trouvé avec l'id: " + id));
    }

}
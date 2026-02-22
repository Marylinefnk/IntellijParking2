package intellijP.back.services;

import intellijP.back.models.Arete;
import intellijP.back.models.Noeud;
import intellijP.back.repositories.AreteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class AreteService {
    private final AreteRepository areteRepository;

    public AreteService(AreteRepository areteRepository) {
        this.areteRepository = areteRepository;
    }

    public List<Arete> findAll() {
        return areteRepository.findAll();
    }

    public List<Arete> findAreteSortantes(Noeud noeudSource) {
        return areteRepository.findByNoeudSource(noeudSource);
    }

}
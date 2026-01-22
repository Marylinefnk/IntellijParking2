package esiag.back.services;

import esiag.back.models.Arete;
import esiag.back.models.Noeud;
import esiag.back.repositories.AreteRepository;
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
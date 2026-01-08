package esiag.back.services;

import esiag.back.models.Vehicule;
import esiag.back.models.TypeVehicule;
import esiag.back.repositories.VehiculeRepository;
import esiag.back.repositories.PersonneRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class VehiculeService {

    private final VehiculeRepository vehiculeRepository;
    private final PersonneRepository personneRepository;

    public VehiculeService(VehiculeRepository vehiculeRepository,
                          PersonneRepository personneRepository) {
        this.vehiculeRepository = vehiculeRepository;
        this.personneRepository = personneRepository;
    }

    public List<Vehicule> findAll() {
        return vehiculeRepository.findAll();
    }

    public Optional<Vehicule> findById(Long id) {
        return vehiculeRepository.findById(id);
    }

    public List<Vehicule> findByPersonne(Long personneId) {
        return vehiculeRepository.findByPersonneId(personneId);
    }

    public Optional<Vehicule> findByImmatriculation(String immatriculation) {
        return vehiculeRepository.findByImmatriculation(immatriculation);
    }

    public List<Vehicule> findByType(TypeVehicule typeVehicule) {
        return vehiculeRepository.findByTypeVehicule(typeVehicule);
    }

    public Vehicule create(Vehicule vehicule) {
        if (vehicule.getPersonne() != null) {
            personneRepository.findById(vehicule.getPersonne().getId())
                    .orElseThrow(() -> new RuntimeException("Personne non trouvée"));
        }
        return vehiculeRepository.save(vehicule);
    }

    public Vehicule update(Long id, Vehicule vehiculeDetails) {
        Vehicule vehicule = vehiculeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Véhicule non trouvé"));

        vehicule.setImmatriculation(vehiculeDetails.getImmatriculation());
        vehicule.setTypeVehicule(vehiculeDetails.getTypeVehicule());
        vehicule.setPersonne(vehiculeDetails.getPersonne());

        return vehiculeRepository.save(vehicule);
    }

    public void delete(Long id) {
        vehiculeRepository.deleteById(id);
    }
}

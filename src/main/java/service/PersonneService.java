package service;

import model.Personne;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.PersonneRepository;

import java.util.List;

@Service
public class PersonneService {

    @Autowired
    private PersonneRepository personneRepository;

    public void save(Personne personne) {
        personneRepository.save(personne);
    }

    public Personne findById(int id) {
        return personneRepository.findById(id).orElse(null);
    }

    public List<Personne> findAll() {
        return personneRepository.findAll();
    }

    public void deleteById(int id) {
        personneRepository.deleteById(id);
    }
}

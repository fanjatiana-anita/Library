package service;

import model.Exemplaire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.ExemplaireRepository;

@Service
public class ExemplaireService {

    @Autowired
    private ExemplaireRepository exemplaireRepository;

    public Exemplaire findById(Integer id) {
        return exemplaireRepository.findById(id).orElse(null);
    }
}
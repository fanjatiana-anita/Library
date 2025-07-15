package service;

import model.Exemplaire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.ExemplaireRepository;

import java.util.List;

@Service
public class ExemplaireService {

    @Autowired
    private ExemplaireRepository exemplaireRepository;

    public Exemplaire findById(Integer id) {
        return exemplaireRepository.findById(id).orElse(null);
    }

    public boolean isExemplaireDisponible(Integer idExemplaire) {
        Exemplaire exemplaire = exemplaireRepository.findById(idExemplaire).orElse(null);
        return exemplaire != null && Exemplaire.StatutExemplaireEnum.DISPONIBLE.equals(exemplaire.getStatutExemplaire());
    }

    public List<Exemplaire> findExemplairesByLivreId(Integer idLivre) {
        return exemplaireRepository.findByLivre_IdLivre(idLivre);
    }
}
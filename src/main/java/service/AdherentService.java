package service;

import model.Adherent;
import model.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.AdherentRepository;

@Service
public class AdherentService {

    @Autowired
    private AdherentRepository adherentRepository;

    public Adherent save(Adherent adherent) {
        return adherentRepository.save(adherent);
    }

    public Adherent findById(Integer id) {
        return adherentRepository.findById(id).orElse(null);
    }

    public Adherent findByUserAccount(UserAccount userAccount) {
        return adherentRepository.findByUserAccount(userAccount);
    }

    public boolean isAdherentActif(Adherent adherent) {
        return Adherent.StatutAdherentEnum.ACTIF.equals(adherent.getStatutAdherent());
    }
}

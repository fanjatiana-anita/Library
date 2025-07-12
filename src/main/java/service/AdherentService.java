package service;

import model.Adherent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.AdherentRepository;

@Service
public class AdherentService {

    @Autowired
    private AdherentRepository adherentRepository;

    public void save(Adherent adherent) {
        adherentRepository.save(adherent);
    }

    public Adherent findByUserAccountId(Integer idUserAccount) {
        return adherentRepository.findByUserAccountIdUserAccount(idUserAccount);
    }

    public int getQuotaPret(int idAdherent) {
        Adherent adherent = adherentRepository.findById(idAdherent).orElse(null);
        if (adherent != null && adherent.getProfil() != null) {
            return adherent.getProfil().getQuotaMax();
        }
        return 0; // valeur par défaut si pas trouvé
    }

    public int getQuotaReservation(int idAdherent) {
        Adherent adherent = adherentRepository.findById(idAdherent).orElse(null);
        if (adherent != null && adherent.getProfil() != null) {
            return adherent.getProfil().getQuotaReservation();
        }
        return 0;
    }

    public int getQuotaProlongement(int idAdherent) {
        Adherent adherent = adherentRepository.findById(idAdherent).orElse(null);
        if (adherent != null && adherent.getProfil() != null) {
            return adherent.getProfil().getQuotaProlongement();
        }
        return 0;
    }
}
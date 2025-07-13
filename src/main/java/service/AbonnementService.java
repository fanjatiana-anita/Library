package service;

import model.Abonnement;
import model.Adherent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.AbonnementRepository;

import java.util.List;

@Service
public class AbonnementService {

    @Autowired
    private AbonnementRepository abonnementRepository;

    public Abonnement save(Abonnement abonnement) {
        return abonnementRepository.save(abonnement);
    }

    public Abonnement findLastByAdherent(Adherent adherent) {
        List<Abonnement> abonnements = abonnementRepository.findByAdherentOrderByDateDebutDesc(adherent);
        return abonnements.isEmpty() ? null : abonnements.get(0);
    }
}
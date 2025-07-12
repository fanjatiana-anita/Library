package service;

import model.Abonnement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.AbonnementRepository;

@Service
public class AbonnementService {

    @Autowired
    private AbonnementRepository abonnementRepository;

    public Abonnement save(Abonnement abonnement) {
        return abonnementRepository.save(abonnement);
    }
}
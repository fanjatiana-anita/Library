package service;

import model.HistoriquePaiement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.HistoriquePaiementRepository;

@Service
public class HistoriquePaiementService {

    @Autowired
    private HistoriquePaiementRepository historiquePaiementRepository;

    public HistoriquePaiement save(HistoriquePaiement paiement) {
        return historiquePaiementRepository.save(paiement);
    }
}
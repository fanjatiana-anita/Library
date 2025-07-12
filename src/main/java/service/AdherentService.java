package service;

import model.Adherent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.AdherentRepository;

@Service
public class AdherentService {

    @Autowired
    private AdherentRepository adherentRepository;

    // Récupère un adhérent par son ID
    public Adherent findById(Integer idAdherent) {
        return adherentRepository.findById(idAdherent).orElse(null);
    }

    // Vérifie le quota maximum de prêts pour un adhérent
    public Integer getQuotaMaxPret(Adherent adherent) {
        if (adherent == null || adherent.getProfil() == null) {
            return 0;
        }
        return adherent.getProfil().getQuotaMaxPret();
    }

    // Vérifie le quota maximum de réservations pour un adhérent
    public Integer getQuotaMaxReservation(Adherent adherent) {
        if (adherent == null || adherent.getProfil() == null) {
            return 0;
        }
        return adherent.getProfil().getQuotaMaxReservation();
    }

    // Vérifie le quota maximum de prolongements pour un adhérent
    public Integer getQuotaMaxProlongement(Adherent adherent) {
        if (adherent == null || adherent.getProfil() == null) {
            return 0;
        }
        return adherent.getProfil().getQuotaMaxProlongement();
    }
}
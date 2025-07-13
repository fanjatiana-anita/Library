package service;

import model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.AdherentRepository;
import repository.ExemplaireRepository;
import repository.JourNonOuvrableRepository;
import repository.PenalisationRepository;
import repository.PretRepository;
import repository.RestrictionProfilLivreRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class PretService {

    @Autowired
    private PretRepository pretRepository;

    @Autowired
    private AdherentRepository adherentRepository;

    @Autowired
    private ExemplaireRepository exemplaireRepository;

    @Autowired
    private PenalisationRepository penalisationRepository;

    @Autowired
    private RestrictionProfilLivreRepository restrictionProfilLivreRepository;

    @Autowired
    private JourNonOuvrableRepository jourNonOuvrableRepository;

    public static class PretResult {
        private Pret pret;
        private String message;
        private LocalDate originalReturnDate;
        private LocalDate adjustedReturnDate;

        public PretResult(Pret pret, String message, LocalDate originalReturnDate, LocalDate adjustedReturnDate) {
            this.pret = pret;
            this.message = message;
            this.originalReturnDate = originalReturnDate;
            this.adjustedReturnDate = adjustedReturnDate;
        }

        public Pret getPret() {
            return pret;
        }

        public String getMessage() {
            return message;
        }

        public LocalDate getOriginalReturnDate() {
            return originalReturnDate;
        }

        public LocalDate getAdjustedReturnDate() {
            return adjustedReturnDate;
        }
    }

    public enum AdjustDirection {
        FORWARD, BACKWARD
    }

    public PretResult createPret(String login, Integer idExemplaire, String adjustDirection) {
        Adherent adherent = adherentRepository.findByUserAccountLogin(login);
        if (adherent == null) {
            return new PretResult(null, "Aucun adhérent trouvé avec ce login.", null, null);
        }
        if (!Adherent.StatutAdherentEnum.ACTIF.equals(adherent.getStatutAdherent())) {
            return new PretResult(null, "L'adhérent n'est pas actif.", null, null);
        }

        List<Penalisation> penalites = penalisationRepository.findByAdherentAndDateFinPenalisationAfter(adherent, LocalDate.now());
        if (!penalites.isEmpty()) {
            return new PretResult(null, "L'adhérent est sous sanction.", null, null);
        }

        List<Pret> pretsEnRetard = pretRepository.findByAdherentAndDateDeRetourReelleIsNullAndDateDeRetourPrevueBefore(adherent, LocalDate.now());
        if (!pretsEnRetard.isEmpty()) {
            return new PretResult(null, "L'adhérent a un prêt en retard.", null, null);
        }

        List<Pret> pretsActifs = pretRepository.findByAdherentAndDateDeRetourReelleIsNull(adherent);
        if (pretsActifs.size() >= adherent.getProfil().getQuotaMaxPret()) {
            return new PretResult(null, "Quota maximum de prêts atteint.", null, null);
        }

        Exemplaire exemplaire = exemplaireRepository.findById(idExemplaire).orElse(null);
        if (exemplaire == null) {
            return new PretResult(null, "L'exemplaire n'existe pas.", null, null);
        }
        if (!Exemplaire.StatutExemplaireEnum.DISPONIBLE.equals(exemplaire.getStatutExemplaire())) {
            return new PretResult(null, "L'exemplaire n'est pas disponible.", null, null);
        }

        List<RestrictionProfilLivre> restrictions = restrictionProfilLivreRepository.findByLivre(exemplaire.getLivre());
        for (RestrictionProfilLivre restriction : restrictions) {
            if (restriction.getProfil() != null && !restriction.getIdProfil().equals(adherent.getProfil().getIdProfil())) {
                return new PretResult(null, "L'adhérent n'a pas le droit d'accéder à cet exemplaire.", null, null);
            }
            if (restriction.getAgeMinRequis() != null) {
                LocalDate dateNaissance = adherent.getUserAccount().getPersonne().getDateDeNaissance();
                int age = LocalDate.now().getYear() - dateNaissance.getYear();
                if (age < restriction.getAgeMinRequis()) {
                    return new PretResult(null, "L'adhérent est trop jeune pour accéder à cet exemplaire.", null, null);
                }
            }
        }

        LocalDate dateDuPret = LocalDate.now();
        LocalDate dateDeRetourPrevue = dateDuPret.plusDays(adherent.getProfil().getDureeMaxPret());
        LocalDate originalDateDeRetourPrevue = dateDeRetourPrevue;

        List<JourNonOuvrable> joursNonOuvrables = jourNonOuvrableRepository.findAll();
        String message = null;
        if (isNonWorkingDay(dateDeRetourPrevue, joursNonOuvrables)) {
            AdjustDirection direction = adjustDirection != null ? AdjustDirection.valueOf(adjustDirection.toUpperCase()) : AdjustDirection.FORWARD;
            dateDeRetourPrevue = adjustDate(dateDeRetourPrevue, joursNonOuvrables, direction);
            message = String.format("La date de retour prévue initiale (%s) était un jour non ouvrable. Elle a été ajustée à %s.", 
                                   originalDateDeRetourPrevue, dateDeRetourPrevue);
        }

        Pret pret = new Pret();
        pret.setAdherent(adherent);
        pret.setExemplaire(exemplaire);
        pret.setDateDuPret(dateDuPret);
        pret.setDateDeRetourPrevue(dateDeRetourPrevue);
        pretRepository.save(pret);

        exemplaire.setStatutExemplaire(Exemplaire.StatutExemplaireEnum.EN_PRET);
        exemplaireRepository.save(exemplaire);

        return new PretResult(pret, message, originalDateDeRetourPrevue, dateDeRetourPrevue);
    }

    public Integer findAvailableExemplaire(Integer idLivre) {
        List<Exemplaire> exemplaires = exemplaireRepository.findAll().stream()
                .filter(e -> e.getLivre().getIdLivre().equals(idLivre) && e.getStatutExemplaire() == Exemplaire.StatutExemplaireEnum.DISPONIBLE)
                .toList();
        return exemplaires.isEmpty() ? null : exemplaires.get(0).getIdExemplaire();
    }

    private LocalDate adjustDate(LocalDate date, List<JourNonOuvrable> joursNonOuvrables, AdjustDirection direction) {
        if (direction == AdjustDirection.FORWARD) {
            while (isNonWorkingDay(date, joursNonOuvrables)) {
                date = date.plusDays(1);
            }
        } else {
            while (isNonWorkingDay(date, joursNonOuvrables)) {
                date = date.minusDays(1);
            }
        }
        return date;
    }

    private boolean isNonWorkingDay(LocalDate date, List<JourNonOuvrable> joursNonOuvrables) {
        int dayOfWeek = date.getDayOfWeek().getValue();
        for (JourNonOuvrable jour : joursNonOuvrables) {
            if (jour.getType() == JourNonOuvrable.TypeJourEnum.HEBDOMADAIRE && jour.getJourSemaine() == dayOfWeek) {
                return true;
            }
            if ((jour.getType() == JourNonOuvrable.TypeJourEnum.FERIE || jour.getType() == JourNonOuvrable.TypeJourEnum.EXCEPTIONNEL)
                    && jour.getDateFerie() != null && jour.getDateFerie().equals(date)) {
                return true;
            }
        }
        return false;
    }
}
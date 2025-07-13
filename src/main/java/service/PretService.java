package service;

import model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.AdherentRepository;
import repository.ExemplaireRepository;
import repository.JourNonOuvrableRepository;
import repository.PenalisationRepository;
import repository.PretRepository;
import repository.RestrictionProfilLivreRepository;
import repository.HistoriqueEtatRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
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

    @Autowired
    private HistoriqueEtatRepository historiqueEtatRepository;

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

    public static class ReturnResult {
        private Pret pret;
        private String message;

        public ReturnResult(Pret pret, String message) {
            this.pret = pret;
            this.message = message;
        }

        public Pret getPret() {
            return pret;
        }

        public String getMessage() {
            return message;
        }
    }

    public enum AdjustDirection {
        FORWARD, BACKWARD
    }

    @Transactional
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

        // Vérification des restrictions
        List<RestrictionProfilLivre> restrictions = restrictionProfilLivreRepository.findByLivre(exemplaire.getLivre());
        boolean isAllowed = false;
        LocalDate dateNaissance = adherent.getUserAccount().getPersonne().getDateDeNaissance();
        int age = Period.between(dateNaissance, LocalDate.now()).getYears();

        if (restrictions.isEmpty()) {
            isAllowed = true; // Pas de restrictions : le prêt est autorisé
        } else {
            for (RestrictionProfilLivre restriction : restrictions) {
                if (restriction.getIdProfil() != null && restriction.getIdProfil().equals(adherent.getProfil().getIdProfil())) {
                    // Restriction trouvée pour le profil de l'adhérent
                    if (restriction.getAgeMinRequis() == null || age >= restriction.getAgeMinRequis()) {
                        isAllowed = true; // Âge suffisant ou pas de restriction d'âge
                        break;
                    } else {
                        return new PretResult(null, "L'adhérent est trop jeune pour accéder à cet exemplaire (âge minimum requis : " + 
                            restriction.getAgeMinRequis() + " ans).", null, null);
                    }
                }
            }
        }

        if (!isAllowed) {
            return new PretResult(null, "L'adhérent n'a pas le droit d'accéder à cet exemplaire.", null, null);
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

        HistoriqueEtat historiqueEtat = new HistoriqueEtat();
        historiqueEtat.setEntite("PRET");
        historiqueEtat.setIdEntite(pret.getIdPret());
        historiqueEtat.setEtatAvant(Exemplaire.StatutExemplaireEnum.DISPONIBLE.toString());
        historiqueEtat.setEtatApres(Exemplaire.StatutExemplaireEnum.EN_PRET.toString());
        historiqueEtat.setDateChangement(LocalDateTime.now());
        historiqueEtatRepository.save(historiqueEtat);

        return new PretResult(pret, message, originalDateDeRetourPrevue, dateDeRetourPrevue);
    }

    @Transactional
    public ReturnResult returnPret(Integer idPret, LocalDate dateDeRetourReelle) {
        Pret pret = pretRepository.findById(idPret).orElse(null);
        if (pret == null) {
            return new ReturnResult(null, "Prêt non trouvé.");
        }
        if (pret.getDateDeRetourReelle() != null) {
            return new ReturnResult(null, "Ce prêt a déjà été retourné.");
        }

        Exemplaire exemplaire = pret.getExemplaire();
        Adherent adherent = pret.getAdherent();
        String message = null;

        pret.setDateDeRetourReelle(dateDeRetourReelle);
        pretRepository.save(pret);

        exemplaire.setStatutExemplaire(Exemplaire.StatutExemplaireEnum.DISPONIBLE);
        exemplaireRepository.save(exemplaire);

        HistoriqueEtat historiqueEtat = new HistoriqueEtat();
        historiqueEtat.setEntite("PRET");
        historiqueEtat.setIdEntite(pret.getIdPret());
        historiqueEtat.setEtatAvant(Exemplaire.StatutExemplaireEnum.EN_PRET.toString());
        historiqueEtat.setEtatApres(Exemplaire.StatutExemplaireEnum.DISPONIBLE.toString());
        historiqueEtat.setDateChangement(dateDeRetourReelle.atStartOfDay());
        historiqueEtatRepository.save(historiqueEtat);

        if (dateDeRetourReelle.isAfter(pret.getDateDeRetourPrevue())) {
            LocalDate dateDebutPenalisation = dateDeRetourReelle;
            LocalDate dateFinPenalisation = dateDebutPenalisation.plusDays(adherent.getProfil().getDureePenalite());

            List<Penalisation> penalitesActives = penalisationRepository.findByAdherentAndDateFinPenalisationAfter(adherent, LocalDate.now());
            if (!penalitesActives.isEmpty()) {
                Penalisation dernierePenalisation = penalitesActives.get(0);
                if (dernierePenalisation.getDateFinPenalisation().isAfter(dateDebutPenalisation)) {
                    dateFinPenalisation = dernierePenalisation.getDateFinPenalisation().plusDays(adherent.getProfil().getDureePenalite());
                }
            }

            Penalisation penalisation = new Penalisation();
            penalisation.setAdherent(adherent);
            penalisation.setPret(pret);
            penalisation.setDateDebutPenalisation(dateDebutPenalisation);
            penalisation.setDateFinPenalisation(dateFinPenalisation);
            penalisationRepository.save(penalisation);

            message = String.format("L'adhérent a été pénalisé jusqu'au %s pour retard de retour.", dateFinPenalisation);
        }

        return new ReturnResult(pret, message);
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
            if (jour.getType().equals("HEBDOMADAIRE") && jour.getJourSemaine() == dayOfWeek) {
                return true;
            }
            if ((jour.getType().equals("FERIE") || jour.getType().equals("EXCEPTIONNEL"))
                    && jour.getDateFerie() != null && jour.getDateFerie().equals(date)) {
                return true;
            }
        }
        return false;
    }
}

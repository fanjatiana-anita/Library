package service;

import model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.*;

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

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ProlongementRepository prolongementRepository;

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

    public static class ProlongementResult {
        private Prolongement prolongement;
        private String message;

        public ProlongementResult(Prolongement prolongement, String message) {
            this.prolongement = prolongement;
            this.message = message;
        }

        public Prolongement getProlongement() {
            return prolongement;
        }

        public String getMessage() {
            return message;
        }
    }

    public enum AdjustDirection {
        FORWARD, BACKWARD
    }

    @Transactional
    public PretResult createPret(String login, Integer idExemplaire, LocalDate datePret, LocalDate dateDeRetourPrevue, String adjustDirection) {
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
        if (!(Exemplaire.StatutExemplaireEnum.DISPONIBLE.equals(exemplaire.getStatutExemplaire()) ||
              Exemplaire.StatutExemplaireEnum.RESERVE.equals(exemplaire.getStatutExemplaire()))) {
            return new PretResult(null, "L'exemplaire n'est ni disponible ni réservé.", null, null);
        }

        // Vérification des restrictions
        List<RestrictionProfilLivre> restrictions = restrictionProfilLivreRepository.findByLivre(exemplaire.getLivre());
        boolean isAllowed = false;
        LocalDate dateNaissance = adherent.getUserAccount().getPersonne().getDateDeNaissance();
        int age = Period.between(dateNaissance, LocalDate.now()).getYears();

        if (restrictions.isEmpty()) {
            isAllowed = true;
        } else {
            for (RestrictionProfilLivre restriction : restrictions) {
                if (restriction.getIdProfil() != null && restriction.getIdProfil().equals(adherent.getProfil().getIdProfil())) {
                    if (restriction.getAgeMinRequis() == null || age >= restriction.getAgeMinRequis()) {
                        isAllowed = true;
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

        // Validation de datePret
        if (datePret.isBefore(adherent.getDateAdhesion())) {
            return new PretResult(null, "La date du prêt ne peut pas être antérieure à la date d'adhésion (" + adherent.getDateAdhesion() + ").", null, null);
        }

        // Définir dateDeRetourPrevue si non fournie
        LocalDate calculatedDateDeRetourPrevue = dateDeRetourPrevue != null ? dateDeRetourPrevue : datePret.plusDays(adherent.getProfil().getDureeMaxPret());
        LocalDate originalDateDeRetourPrevue = calculatedDateDeRetourPrevue;

        // Vérification de la contrainte dateDeRetourPrevue > datePret
        if (!calculatedDateDeRetourPrevue.isAfter(datePret)) {
            return new PretResult(null, "La date de retour prévue doit être postérieure à la date du prêt.", null, null);
        }

        List<JourNonOuvrable> joursNonOuvrables = jourNonOuvrableRepository.findAll();
        String message = null;
        if (isNonWorkingDay(calculatedDateDeRetourPrevue, joursNonOuvrables)) {
            AdjustDirection direction = adjustDirection != null ? AdjustDirection.valueOf(adjustDirection.toUpperCase()) : AdjustDirection.FORWARD;
            calculatedDateDeRetourPrevue = adjustDate(calculatedDateDeRetourPrevue, joursNonOuvrables, direction);
            message = String.format("La date de retour prévue initiale (%s) était un jour non ouvrable. Elle a été ajustée à %s.",
                                   originalDateDeRetourPrevue, calculatedDateDeRetourPrevue);
        }

        Pret pret = new Pret();
        pret.setAdherent(adherent);
        pret.setExemplaire(exemplaire);
        pret.setDateDuPret(datePret);
        pret.setDateDeRetourPrevue(calculatedDateDeRetourPrevue);
        pret.setNombreProlongement(0);
        pretRepository.save(pret);

        exemplaire.setStatutExemplaire(Exemplaire.StatutExemplaireEnum.EN_PRET);
        exemplaireRepository.save(exemplaire);

        HistoriqueEtat historiqueEtat = new HistoriqueEtat();
        historiqueEtat.setEntite("PRET");
        historiqueEtat.setIdEntite(pret.getIdPret());
        historiqueEtat.setEtatAvant(exemplaire.getStatutExemplaire().toString());
        historiqueEtat.setEtatApres(Exemplaire.StatutExemplaireEnum.EN_PRET.toString());
        historiqueEtat.setDateChangement(LocalDateTime.now());
        historiqueEtatRepository.save(historiqueEtat);

        return new PretResult(pret, message != null ? message : "Prêt créé avec succès.", originalDateDeRetourPrevue, calculatedDateDeRetourPrevue);
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

        return new ReturnResult(pret, message != null ? message : "Prêt retourné avec succès.");
    }

    @Transactional
    public ProlongementResult requestProlongement(String login, Integer idPret, LocalDate dateDemandeProlongement, LocalDate dateRetourPrevueApresProlongement) {
        Adherent adherent = adherentRepository.findByUserAccountLogin(login);
        if (adherent == null) {
            return new ProlongementResult(null, "Aucun adhérent trouvé avec ce login.");
        }
        if (!Adherent.StatutAdherentEnum.ACTIF.equals(adherent.getStatutAdherent())) {
            return new ProlongementResult(null, "L'adhérent n'est pas actif.");
        }

        Pret pret = pretRepository.findById(idPret).orElse(null);
        if (pret == null) {
            return new ProlongementResult(null, "Prêt non trouvé.");
        }
        if (pret.getDateDeRetourReelle() != null) {
            return new ProlongementResult(null, "Ce prêt a déjà été retourné.");
        }
        if (!pret.getAdherent().equals(adherent)) {
            return new ProlongementResult(null, "Ce prêt n'appartient pas à cet adhérent.");
        }

        // Vérifier si l'adhérent est pénalisé
        if (!penalisationRepository.findByAdherentAndDateFinPenalisationAfter(adherent, LocalDate.now()).isEmpty()) {
            return new ProlongementResult(null, "L'adhérent est actuellement pénalisé.");
        }

        // Vérifier le quota de prolongements
        if (pret.getNombreProlongement() >= adherent.getProfil().getQuotaMaxProlongement()) {
            return new ProlongementResult(null, "Quota maximum de prolongements atteint.");
        }

        // Vérifier si une demande de prolongement est déjà en attente
        List<Prolongement> prolongementsEnAttente = prolongementRepository.findByPretAndStatutProlongement(
                pret, Prolongement.StatutProlongementEnum.EN_ATTENTE);
        if (!prolongementsEnAttente.isEmpty()) {
            return new ProlongementResult(null, "Une demande de prolongement est déjà en attente pour ce prêt.");
        }

        // Vérifier la disponibilité de l'exemplaire
        List<Reservation> reservations = reservationRepository.findByExemplaireAndStatutReservation(
                pret.getExemplaire(), Reservation.StatutReservationEnum.EN_ATTENTE);
        for (Reservation reservation : reservations) {
            if (reservation.getDateDuPretPrevue().isBefore(pret.getDateDeRetourPrevue().plusDays(1))) {
                return new ProlongementResult(null, "Une réservation est prévue pour cet exemplaire avant la fin du prêt.");
            }
        }

        // Vérifier la date de demande de prolongement
        if (dateDemandeProlongement.isAfter(pret.getDateDeRetourPrevue())) {
            return new ProlongementResult(null, "La date de demande de prolongement doit être antérieure ou égale à la date de retour prévue.");
        }

        // Calculer la nouvelle date de retour si non fournie
        LocalDate nouvelleDateRetour = dateRetourPrevueApresProlongement != null
                ? dateRetourPrevueApresProlongement
                : pret.getDateDeRetourPrevue().plusDays(adherent.getProfil().getDureeMaxPret());

        // Vérifier que la nouvelle date de retour est postérieure à l'ancienne
        if (nouvelleDateRetour.isBefore(pret.getDateDeRetourPrevue()) || nouvelleDateRetour.equals(pret.getDateDeRetourPrevue())) {
            return new ProlongementResult(null, "La nouvelle date de retour doit être postérieure à la date de retour actuelle.");
        }

        // Ajuster la date pour éviter les jours non ouvrables
        List<JourNonOuvrable> joursNonOuvrables = jourNonOuvrableRepository.findAll();
        if (isNonWorkingDay(nouvelleDateRetour, joursNonOuvrables)) {
            nouvelleDateRetour = adjustDate(nouvelleDateRetour, joursNonOuvrables, AdjustDirection.FORWARD);
        }

        Prolongement prolongement = new Prolongement();
        prolongement.setPret(pret);
        prolongement.setAdherent(adherent);
        prolongement.setDateDemandeProlongement(dateDemandeProlongement);
        prolongement.setDateRetourPrevueApresProlongement(nouvelleDateRetour);
        prolongement.setStatutProlongement(Prolongement.StatutProlongementEnum.EN_ATTENTE);
        prolongementRepository.save(prolongement);

        HistoriqueEtat historiqueEtat = new HistoriqueEtat();
        historiqueEtat.setEntite("PROLONGEMENT");
        historiqueEtat.setIdEntite(prolongement.getIdProlongement());
        historiqueEtat.setEtatAvant("AUCUN");
        historiqueEtat.setEtatApres(Prolongement.StatutProlongementEnum.EN_ATTENTE.toString());
        historiqueEtat.setDateChangement(LocalDateTime.now());
        historiqueEtatRepository.save(historiqueEtat);

        return new ProlongementResult(prolongement, "Demande de prolongement créée avec succès.");
    }

    public Integer findAvailableExemplaire(Integer idLivre) {
        List<Exemplaire> exemplaires = exemplaireRepository.findAll().stream()
                .filter(e -> e.getLivre().getIdLivre().equals(idLivre) && e.getStatutExemplaire() == Exemplaire.StatutExemplaireEnum.DISPONIBLE)
                .toList();
        return exemplaires.isEmpty() ? null : exemplaires.get(0).getIdExemplaire();
    }

    public boolean isQuotaPretAvailable(Adherent adherent) {
        List<Pret> pretsActifs = pretRepository.findByAdherentAndDateDeRetourReelleIsNull(adherent);
        return pretsActifs.size() < adherent.getProfil().getQuotaMaxPret();
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
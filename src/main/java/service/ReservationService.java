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
public class ReservationService {

    @Autowired
    private AdherentRepository adherentRepository;

    @Autowired
    private ExemplaireRepository exemplaireRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private PenalisationRepository penalisationRepository;

    @Autowired
    private RestrictionProfilLivreRepository restrictionProfilLivreRepository;

    @Autowired
    private HistoriqueEtatRepository historiqueEtatRepository;

    public static class ReservationResult {
        private Reservation reservation;
        private String message;

        public ReservationResult(Reservation reservation, String message) {
            this.reservation = reservation;
            this.message = message;
        }

        public Reservation getReservation() {
            return reservation;
        }

        public String getMessage() {
            return message;
        }
    }

    @Transactional
    public ReservationResult createReservation(String login, Integer idExemplaire, LocalDate dateReservation, LocalDate datePretPrevue) {
        // Vérifier l'adhérent
        Adherent adherent = adherentRepository.findByUserAccountLogin(login);
        if (adherent == null) {
            return new ReservationResult(null, "Aucun adhérent trouvé avec ce login.");
        }
        if (!Adherent.StatutAdherentEnum.ACTIF.equals(adherent.getStatutAdherent())) {
            return new ReservationResult(null, "L'adhérent n'est pas actif.");
        }

        // Vérifier les pénalisations
        List<Penalisation> penalites = penalisationRepository.findByAdherentAndDateFinPenalisationAfter(adherent, LocalDate.now());
        if (!penalites.isEmpty()) {
            return new ReservationResult(null, "L'adhérent est sous sanction.");
        }

        // Vérifier le quota de réservations
        List<Reservation> reservationsActives = reservationRepository.findByAdherentAndStatutReservationIn(
            adherent, List.of(Reservation.StatutReservationEnum.EN_ATTENTE, Reservation.StatutReservationEnum.VALIDE));
        if (reservationsActives.size() >= adherent.getProfil().getQuotaMaxReservation()) {
            return new ReservationResult(null, "Quota maximum de réservations atteint.");
        }

        // Vérifier l'exemplaire
        Exemplaire exemplaire = exemplaireRepository.findById(idExemplaire).orElse(null);
        if (exemplaire == null) {
            return new ReservationResult(null, "L'exemplaire n'existe pas.");
        }
        if (!Exemplaire.StatutExemplaireEnum.DISPONIBLE.equals(exemplaire.getStatutExemplaire())) {
            return new ReservationResult(null, "L'exemplaire n'est pas disponible.");
        }

        // Vérifier si l'exemplaire est déjà réservé avec statut VALIDE
        List<Reservation> reservationsValides = reservationRepository.findByExemplaireAndStatutReservation(
            exemplaire, Reservation.StatutReservationEnum.VALIDE);
        if (!reservationsValides.isEmpty()) {
            return new ReservationResult(null, "L'exemplaire est déjà réservé.");
        }

        // Vérifier les restrictions (profil et âge)
        List<RestrictionProfilLivre> restrictions = restrictionProfilLivreRepository.findByLivre(exemplaire.getLivre());
        boolean isAllowed = false;
        int age = Period.between(adherent.getUserAccount().getPersonne().getDateDeNaissance(), LocalDate.now()).getYears();
        if (restrictions.isEmpty()) {
            isAllowed = true;
        } else {
            for (RestrictionProfilLivre restriction : restrictions) {
                if (restriction.getIdProfil() != null && restriction.getIdProfil().equals(adherent.getProfil().getIdProfil())) {
                    if (restriction.getAgeMinRequis() == null || age >= restriction.getAgeMinRequis()) {
                        isAllowed = true;
                        break;
                    } else {
                        return new ReservationResult(null, "L'adhérent est trop jeune pour réserver cet exemplaire (âge minimum requis : " + 
                            restriction.getAgeMinRequis() + " ans).");
                    }
                }
            }
        }
        if (!isAllowed) {
            return new ReservationResult(null, "L'adhérent n'a pas le droit de réserver cet exemplaire.");
        }

        // Vérifier les dates
        if (datePretPrevue.isBefore(dateReservation)) {
            return new ReservationResult(null, "La date du prêt prévu doit être postérieure ou égale à la date de réservation.");
        }

        // Créer la réservation
        Reservation reservation = new Reservation();
        reservation.setAdherent(adherent);
        reservation.setExemplaire(exemplaire);
        reservation.setDateDeReservation(dateReservation);
        reservation.setDateDuPretPrevue(datePretPrevue);
        reservation.setStatutReservation(Reservation.StatutReservationEnum.EN_ATTENTE);
        reservationRepository.save(reservation);

        // Enregistrer dans HistoriqueEtat
        HistoriqueEtat historique = new HistoriqueEtat();
        historique.setEntite("RESERVATION");
        historique.setIdEntite(reservation.getIdReservation());
        historique.setEtatAvant(null);
        historique.setEtatApres(Reservation.StatutReservationEnum.EN_ATTENTE.toString());
        historique.setDateChangement(LocalDateTime.now());
        historiqueEtatRepository.save(historique);

        return new ReservationResult(reservation, "Réservation enregistrée avec succès.");
    }

    public Integer findAvailableExemplaire(Integer idLivre) {
        List<Exemplaire> exemplaires = exemplaireRepository.findAll().stream()
                .filter(e -> e.getLivre().getIdLivre().equals(idLivre) 
                    && e.getStatutExemplaire() == Exemplaire.StatutExemplaireEnum.DISPONIBLE
                    && reservationRepository.findByExemplaireAndStatutReservation(
                        e, Reservation.StatutReservationEnum.VALIDE).isEmpty())
                .toList();
        return exemplaires.isEmpty() ? null : exemplaires.get(0).getIdExemplaire();
    }

    public List<Reservation> findReservationsByLogin(String login) {
        Adherent adherent = adherentRepository.findByUserAccountLogin(login);
        if (adherent == null) {
            return List.of();
        }
        return reservationRepository.findByAdherent(adherent);
    }

    public Reservation findById(Integer idReservation) {
        return reservationRepository.findById(idReservation).orElse(null);
    }
}

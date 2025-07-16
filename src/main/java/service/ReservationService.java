package service;

import model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private AdherentRepository adherentRepository;

    @Autowired
    private ExemplaireRepository exemplaireRepository;

    @Autowired
    private PretService pretService;

    @Autowired
    private HistoriqueEtatRepository historiqueEtatRepository;

    @Autowired
    private AbonnementService abonnementService;

    public static class ReservationResult {
        private Reservation reservation;
        private String message;
        private boolean isLateValidation;
        private LocalDate dateLimiteRecuperation;

        public ReservationResult(Reservation reservation, String message, boolean isLateValidation, LocalDate dateLimiteRecuperation) {
            this.reservation = reservation;
            this.message = message;
            this.isLateValidation = isLateValidation;
            this.dateLimiteRecuperation = dateLimiteRecuperation;
        }

        public Reservation getReservation() {
            return reservation;
        }

        public String getMessage() {
            return message;
        }

        public boolean isLateValidation() {
            return isLateValidation;
        }

        public LocalDate getDateLimiteRecuperation() {
            return dateLimiteRecuperation;
        }
    }

    @Transactional
    public ReservationResult createReservation(String login, Integer idLivre, LocalDate dateDeReservation, LocalDate dateDuPretPrevue) {
        Adherent adherent = adherentRepository.findByUserAccountLogin(login);
        if (adherent == null) {
            return new ReservationResult(null, "Aucun adhérent trouvé avec ce login.", false, null);
        }
        // Vérification de l'activité réelle par la date de fin d'abonnement
        Abonnement dernierAbonnement = abonnementService.findLastByAdherent(adherent);
        if (dernierAbonnement == null || dernierAbonnement.getDateFin().isBefore(dateDeReservation)) {
            return new ReservationResult(null, "Impossible d'effectuer cette réservation : l'adhérent n'est pas actif à la date de réservation (abonnement expiré).", false, null);
        }

        if (!Adherent.StatutAdherentEnum.ACTIF.equals(adherent.getStatutAdherent())) {
            return new ReservationResult(null, "L'adhérent n'est pas actif.", false, null);
        }

        if (!pretService.isQuotaPretAvailable(adherent)) {
            return new ReservationResult(null, "Quota maximum de prêts atteint.", false, null);
        }

        Integer idExemplaire = pretService.findAvailableExemplaire(idLivre);
        if (idExemplaire == null) {
            return new ReservationResult(null, "Aucun exemplaire disponible pour ce livre.", false, null);
        }

        Exemplaire exemplaire = exemplaireRepository.findById(idExemplaire).orElse(null);
        if (exemplaire == null) {
            return new ReservationResult(null, "L'exemplaire n'existe pas.", false, null);
        }


        // Vérifier le quota de réservations (en attente ou validées)
        List<Reservation> reservationsActives = reservationRepository.findByAdherentAndStatutReservationIn(
            adherent,
            List.of(Reservation.StatutReservationEnum.EN_ATTENTE, Reservation.StatutReservationEnum.VALIDE)
        );
        int quotaMaxReservation = adherent.getProfil().getQuotaMaxReservation();
        if (reservationsActives.size() >= quotaMaxReservation) {
            return new ReservationResult(null, "Quota maximum de réservations atteint.", false, null);
        }

        if (dateDuPretPrevue.isBefore(dateDeReservation)) {
            return new ReservationResult(null, "La date de prêt prévue doit être postérieure ou égale à la date de réservation.", false, null);
        }

        Reservation reservation = new Reservation();
        reservation.setAdherent(adherent);
        reservation.setExemplaire(exemplaire);
        reservation.setDateDeReservation(dateDeReservation);
        reservation.setDateDuPretPrevue(dateDuPretPrevue);
        reservation.setStatutReservation(Reservation.StatutReservationEnum.EN_ATTENTE);
        reservationRepository.save(reservation);

        // exemplaire.setStatutExemplaire(Exemplaire.StatutExemplaireEnum.RESERVE);
        exemplaireRepository.save(exemplaire);

        HistoriqueEtat historiqueEtat = new HistoriqueEtat();
        historiqueEtat.setEntite("RESERVATION");
        historiqueEtat.setIdEntite(reservation.getIdReservation());
        historiqueEtat.setEtatAvant("DISPONIBLE");
        historiqueEtat.setEtatApres(Exemplaire.StatutExemplaireEnum.RESERVE.toString());
        historiqueEtat.setDateChangement(LocalDateTime.now());
        historiqueEtatRepository.save(historiqueEtat);

        return new ReservationResult(reservation, "Réservation créée avec succès.", false, null);
    }

    @Transactional
    public ReservationResult validerReservation(Integer idReservation, LocalDate dateValidation) {
        Reservation reservation = reservationRepository.findById(idReservation).orElse(null);
        if (reservation == null) {
            return new ReservationResult(null, "Réservation non trouvée.", false, null);
        }
        if (reservation.getStatutReservation() != Reservation.StatutReservationEnum.EN_ATTENTE) {
            return new ReservationResult(null, "La réservation n'est pas en attente.", false, null);
        }

        Adherent adherent = reservation.getAdherent();
        if (!Adherent.StatutAdherentEnum.ACTIF.equals(adherent.getStatutAdherent())) {
            return new ReservationResult(null, "L'adhérent n'est pas actif.", false, null);
        }

        LocalDate dateLimiteRecuperation = dateValidation.plusDays(adherent.getProfil().getDelaiSupplementaireReservation());
        boolean isLateValidation = dateValidation.isAfter(reservation.getDateDuPretPrevue());

        reservation.setStatutReservation(Reservation.StatutReservationEnum.VALIDE);
        reservation.getExemplaire().setStatutExemplaire(Exemplaire.StatutExemplaireEnum.RESERVE);
        reservation.setDateValidation(dateValidation);
        reservation.setDateLimiteRecuperation(dateLimiteRecuperation);
        reservationRepository.save(reservation);

        String message = isLateValidation
                ? String.format("Réservation validée avec succès, mais en retard. Nouvelle date limite de récupération : %s.", dateLimiteRecuperation)
                : "Réservation validée avec succès.";

        return new ReservationResult(reservation, message, isLateValidation, dateLimiteRecuperation);
    }

    @Transactional
    public ReservationResult refuserReservation(Integer idReservation) {
        Reservation reservation = reservationRepository.findById(idReservation).orElse(null);
        if (reservation == null) {
            return new ReservationResult(null, "Réservation non trouvée.", false, null);
        }
        if (reservation.getStatutReservation() != Reservation.StatutReservationEnum.EN_ATTENTE) {
            return new ReservationResult(null, "La réservation n'est pas en attente.", false, null);
        }

        Exemplaire exemplaire = reservation.getExemplaire();
        reservation.setStatutReservation(Reservation.StatutReservationEnum.REFUSEE);
        reservationRepository.save(reservation);

        exemplaire.setStatutExemplaire(Exemplaire.StatutExemplaireEnum.DISPONIBLE);
        exemplaireRepository.save(exemplaire);

        HistoriqueEtat historiqueEtat = new HistoriqueEtat();
        historiqueEtat.setEntite("RESERVATION");
        historiqueEtat.setIdEntite(reservation.getIdReservation());
        historiqueEtat.setEtatAvant(Exemplaire.StatutExemplaireEnum.RESERVE.toString());
        historiqueEtat.setEtatApres(Exemplaire.StatutExemplaireEnum.DISPONIBLE.toString());
        historiqueEtat.setDateChangement(LocalDateTime.now());
        historiqueEtatRepository.save(historiqueEtat);

        return new ReservationResult(reservation, "Réservation refusée avec succès.", false, null);
    }

    @Transactional
    public ReservationResult transformerEnPret(Integer idReservation, String login, LocalDate datePret, LocalDate dateDeRetourPrevue, String adjustDirection) {
        Reservation reservation = reservationRepository.findById(idReservation).orElse(null);
        if (reservation == null) {
            return new ReservationResult(null, "Réservation non trouvée.", false, null);
        }
        if (reservation.getStatutReservation() != Reservation.StatutReservationEnum.VALIDE) {
            return new ReservationResult(null, "La réservation n'est pas validée.", false, null);
        }

        Adherent adherent = adherentRepository.findByUserAccountLogin(login);
        if (adherent == null) {
            return new ReservationResult(null, "Aucun adhérent trouvé avec ce login.", false, null);
        }
        if (adherent.getIdAdherent() != reservation.getAdherent().getIdAdherent()) {
            return new ReservationResult(null, "L'adhérent ne correspond pas à la réservation.", false, null);
        }

        if (datePret.isAfter(reservation.getDateLimiteRecuperation())) {
            return new ReservationResult(null, "La date du prêt est postérieure à la date limite de récupération.", false, null);
        }

        PretService.PretResult pretResult = pretService.createPret(login, reservation.getExemplaire().getIdExemplaire(), datePret, dateDeRetourPrevue, adjustDirection);
        if (pretResult.getPret() == null) {
            return new ReservationResult(null, pretResult.getMessage(), false, null);
        }

        reservation.setStatutReservation(Reservation.StatutReservationEnum.COMPLETEE);
        reservationRepository.save(reservation);

        HistoriqueEtat historiqueEtat = new HistoriqueEtat();
        historiqueEtat.setEntite("RESERVATION");
        historiqueEtat.setIdEntite(reservation.getIdReservation());
        historiqueEtat.setEtatAvant(Reservation.StatutReservationEnum.VALIDE.toString());
        historiqueEtat.setEtatApres(Reservation.StatutReservationEnum.COMPLETEE.toString());
        historiqueEtat.setDateChangement(LocalDateTime.now());
        historiqueEtatRepository.save(historiqueEtat);

        return new ReservationResult(reservation, pretResult.getMessage() != null ? pretResult.getMessage() : "Réservation transformée en prêt avec succès.", false, null);
    }

    public List<Reservation> findReservationsByStatut(Reservation.StatutReservationEnum statut) {
        return reservationRepository.findByStatutReservation(statut);
    }

    public Reservation findById(Integer idReservation) {
        return reservationRepository.findById(idReservation).orElse(null);
    }

    public List<Reservation> findReservationsByLogin(String login) {
        Adherent adherent = adherentRepository.findByUserAccountLogin(login);
        if (adherent == null) {
            return Collections.emptyList();
        }
        return reservationRepository.findByAdherent(adherent);
    }
}
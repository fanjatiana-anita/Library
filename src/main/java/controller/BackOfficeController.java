package controller;

import jakarta.servlet.http.HttpSession;
import model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import service.*;
import repository.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/backoffice")
public class BackOfficeController {

    @Autowired
    private ProfilService profilService;

    @Autowired
    private PersonneService personneService;

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private AdherentService adherentService;

    @Autowired
    private AbonnementService abonnementService;

    @Autowired
    private HistoriquePaiementService historiquePaiementService;

    @Autowired
    private PretService pretService;

    @Autowired
    private LivreService livreService;

    @Autowired
    private ExemplaireService exemplaireService;

    @Autowired
    private PretRepository pretRepository;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private PenalisationRepository penalisationRepository;

    @Autowired
    private HistoriqueEtatRepository historiqueEtatRepository;

    @Autowired
    private ProfilRepository profilRepository;

    @GetMapping({"", "/", "/accueil"})
    public String redirectToLogin() {
        return "redirect:/login";
    }

    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        UserAccount user = (UserAccount) session.getAttribute("user");
        if (user == null || !"BIBLIOTHECAIRE".equals(user.getRole())) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        model.addAttribute("today", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        return "backOffice/dashboard";
    }

    @GetMapping("/inscription")
    public String showInscriptionForm(HttpSession session, Model model) {
        UserAccount user = (UserAccount) session.getAttribute("user");
        if (user == null || !"BIBLIOTHECAIRE".equals(user.getRole())) {
            return "redirect:/login";
        }
        List<Profil> profils = profilService.findAll();
        model.addAttribute("profils", profils);
        model.addAttribute("user", user);
        model.addAttribute("today", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        return "backOffice/inscriptionAdherent";
    }

    @PostMapping("/inscription")
    public String processInscription(
            @RequestParam String nomPersonne,
            @RequestParam String dateDeNaissance,
            @RequestParam String sexe,
            @RequestParam String adresse,
            @RequestParam String login,
            @RequestParam String motDePasse,
            @RequestParam Integer idProfil,
            @RequestParam String statutAdherent,
            @RequestParam String dateAdhesion,
            @RequestParam(required = false) String dateFinAbonnement,
            HttpSession session,
            Model model) {
        UserAccount user = (UserAccount) session.getAttribute("user");
        if (user == null || !"BIBLIOTHECAIRE".equals(user.getRole())) {
            return "redirect:/login";
        }

        if (userAccountService.findByLogin(login) != null) {
            model.addAttribute("error", "Ce login est déjà utilisé.");
            model.addAttribute("profils", profilService.findAll());
            model.addAttribute("today", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
            return "backOffice/inscriptionAdherent";
        }

        try {
            Personne personne = new Personne();
            personne.setNomPersonne(nomPersonne);
            personne.setDateDeNaissance(LocalDate.parse(dateDeNaissance));
            personne.setSexe(sexe);
            personne.setAdresse(adresse);
            personne = personneService.save(personne);

            UserAccount userAccount = new UserAccount();
            userAccount.setPersonne(personne);
            userAccount.setLogin(login);
            userAccount.setMotDePasse(motDePasse);
            userAccount.setRole("MEMBRE");
            userAccount = userAccountService.save(userAccount);

            Adherent adherent = new Adherent();
            adherent.setUserAccount(userAccount);
            Profil profil = profilService.findById(idProfil);
            adherent.setProfil(profil);
            adherent.setStatutAdherent(Adherent.StatutAdherentEnum.valueOf(statutAdherent.toUpperCase()));
            LocalDate adhesionDate = LocalDate.parse(dateAdhesion);
            adherent.setDateAdhesion(adhesionDate);
            adherent = adherentService.save(adherent);

            Abonnement abonnement = new Abonnement();
            abonnement.setAdherent(adherent);
            abonnement.setDateDebut(adhesionDate);
            LocalDate dateFin;
            if (dateFinAbonnement != null && !dateFinAbonnement.isEmpty()) {
                dateFin = LocalDate.parse(dateFinAbonnement);
                if (dateFin.isBefore(adhesionDate)) {
                    model.addAttribute("error", "La date de fin d'abonnement doit être postérieure à la date d'adhésion.");
                    model.addAttribute("profils", profilService.findAll());
                    model.addAttribute("today", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
                    return "backOffice/inscriptionAdherent";
                }
            } else {
                dateFin = adhesionDate.plusDays(profil.getDureeAbonnement());
            }
            abonnement.setDateFin(dateFin);
            abonnementService.save(abonnement);

            HistoriquePaiement paiement = new HistoriquePaiement();
            paiement.setAdherent(adherent);
            paiement.setDatePaiement(adhesionDate);
            paiement.setMontantCotisation(profil.getMontantCotisation());
            historiquePaiementService.save(paiement);

            model.addAttribute("success", "Adhérent inscrit et abonné avec succès.");
            model.addAttribute("profils", profilService.findAll());
            model.addAttribute("today", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
            return "backOffice/inscriptionAdherent";
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de l'inscription : " + e.getMessage());
            model.addAttribute("profils", profilService.findAll());
            model.addAttribute("today", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
            return "backOffice/inscriptionAdherent";
        }
    }

    @GetMapping("/reabonnement")
    public String showReabonnementForm(HttpSession session, Model model) {
        UserAccount user = (UserAccount) session.getAttribute("user");
        if (user == null || !"BIBLIOTHECAIRE".equals(user.getRole())) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        model.addAttribute("today", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        return "backOffice/reabonnement";
    }

    @PostMapping("/reabonnement")
    public String processReabonnement(
            @RequestParam String login,
            @RequestParam String datePaiement,
            @RequestParam(required = false) String dateFinAbonnement,
            HttpSession session,
            Model model) {
        UserAccount user = (UserAccount) session.getAttribute("user");
        if (user == null || !"BIBLIOTHECAIRE".equals(user.getRole())) {
            return "redirect:/login";
        }

        try {
            UserAccount userAccount = userAccountService.findByLogin(login);
            if (userAccount == null || !"MEMBRE".equals(userAccount.getRole())) {
                model.addAttribute("error", "Aucun adhérent trouvé avec ce login.");
                model.addAttribute("today", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
                return "backOffice/reabonnement";
            }

            Adherent adherent = adherentService.findByUserAccount(userAccount);
            if (adherent == null) {
                model.addAttribute("error", "Aucun adhérent associé à ce compte.");
                model.addAttribute("today", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
                return "backOffice/reabonnement";
            }

            Abonnement dernierAbonnement = abonnementService.findLastByAdherent(adherent);
            if (dernierAbonnement == null) {
                model.addAttribute("error", "Aucun abonnement existant pour cet adhérent.");
                model.addAttribute("today", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
                return "backOffice/reabonnement";
            }

            LocalDate datePaiementParsed = LocalDate.parse(datePaiement);
            if (datePaiementParsed.isBefore(dernierAbonnement.getDateDebut())) {
                model.addAttribute("error", "La date de paiement doit être postérieure ou égale à la date de début du dernier abonnement.");
                model.addAttribute("today", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
                return "backOffice/reabonnement";
            }

            LocalDate dateDebut = dernierAbonnement.getDateFin();
            LocalDate dateFin;
            Profil profil = adherent.getProfil();
            if (dateFinAbonnement != null && !dateFinAbonnement.isEmpty()) {
                dateFin = LocalDate.parse(dateFinAbonnement);
                if (dateFin.isBefore(dernierAbonnement.getDateFin()) || dateFin.equals(dernierAbonnement.getDateFin())) {
                    model.addAttribute("error", "La date de fin d'abonnement doit être postérieure à la date de fin du dernier abonnement.");
                    model.addAttribute("today", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
                    return "backOffice/reabonnement";
                }
            } else {
                dateFin = dateDebut.plusDays(profil.getDureeAbonnement());
            }

            adherent.setStatutAdherent(Adherent.StatutAdherentEnum.ACTIF);
            adherentService.save(adherent);

            Abonnement nouvelAbonnement = new Abonnement();
            nouvelAbonnement.setAdherent(adherent);
            nouvelAbonnement.setDateDebut(dateDebut);
            nouvelAbonnement.setDateFin(dateFin);
            abonnementService.save(nouvelAbonnement);

            HistoriquePaiement paiement = new HistoriquePaiement();
            paiement.setAdherent(adherent);
            paiement.setDatePaiement(datePaiementParsed);
            paiement.setMontantCotisation(profil.getMontantCotisation());
            historiquePaiementService.save(paiement);

            model.addAttribute("adherent", adherent);
            model.addAttribute("userAccount", userAccount);
            model.addAttribute("abonnement", nouvelAbonnement);
            model.addAttribute("personne", userAccount.getPersonne());
            model.addAttribute("today", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
            return "backOffice/detailsAdherent";
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors du réabonnement : " + e.getMessage());
            model.addAttribute("today", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
            return "backOffice/reabonnement";
        }
    }

    @GetMapping("/detailsAdherent")
    public String showDetailsAdherent(@RequestParam Integer idAdherent, HttpSession session, Model model) {
        UserAccount user = (UserAccount) session.getAttribute("user");
        if (user == null || !"BIBLIOTHECAIRE".equals(user.getRole())) {
            return "redirect:/login";
        }

        Adherent adherent = adherentService.findById(idAdherent);
        if (adherent == null) {
            model.addAttribute("error", "Adhérent non trouvé.");
            model.addAttribute("today", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
            return "backOffice/reabonnement";
        }

        UserAccount userAccount = adherent.getUserAccount();
        Abonnement dernierAbonnement = abonnementService.findLastByAdherent(adherent);
        model.addAttribute("adherent", adherent);
        model.addAttribute("userAccount", userAccount);
        model.addAttribute("abonnement", dernierAbonnement);
        model.addAttribute("personne", userAccount.getPersonne());
        model.addAttribute("today", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        return "backOffice/detailsAdherent";
    }

    @GetMapping("/pret")
    public String showPretForm(HttpSession session, Model model) {
        UserAccount user = (UserAccount) session.getAttribute("user");
        if (user == null || !"BIBLIOTHECAIRE".equals(user.getRole())) {
            return "redirect:/login";
        }
        List<Livre> livres = livreService.findAll();
        model.addAttribute("livres", livres);
        model.addAttribute("user", user);
        model.addAttribute("today", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        return "backOffice/pret";
    }

    @GetMapping("/formPret")
    public String showFormPret(@RequestParam Integer idLivre, @RequestParam(required = false) Integer idReservation, HttpSession session, Model model) {
        UserAccount user = (UserAccount) session.getAttribute("user");
        if (user == null || !"BIBLIOTHECAIRE".equals(user.getRole())) {
            return "redirect:/login";
        }
        Integer idExemplaire;
        String login = null;
        if (idReservation != null) {
            Reservation reservation = reservationService.findById(idReservation);
            if (reservation == null || reservation.getStatutReservation() != Reservation.StatutReservationEnum.VALIDE) {
                model.addAttribute("error", "Réservation non trouvée ou non validée.");
                model.addAttribute("livres", livreService.findAll());
                model.addAttribute("today", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
                return "backOffice/pret";
            }
            idExemplaire = reservation.getExemplaire().getIdExemplaire();
            login = reservation.getAdherent().getUserAccount().getLogin();
        } else {
            idExemplaire = pretService.findAvailableExemplaire(idLivre);
            if (idExemplaire == null) {
                model.addAttribute("error", "Aucun exemplaire disponible pour ce livre.");
                model.addAttribute("livres", livreService.findAll());
                model.addAttribute("today", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
                return "backOffice/pret";
            }
        }
        Livre livre = livreService.findAll().stream()
                .filter(l -> l.getIdLivre().equals(idLivre))
                .findFirst()
                .orElse(null);
        model.addAttribute("idLivre", idLivre);
        model.addAttribute("idExemplaire", idExemplaire);
        model.addAttribute("idReservation", idReservation);
        model.addAttribute("login", login);
        model.addAttribute("livre", livre);
        model.addAttribute("today", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        return "backOffice/formPret";
    }

    @PostMapping("/pret")
    public String processPret(
            @RequestParam String login,
            @RequestParam Integer idExemplaire,
            @RequestParam(required = false) Integer idReservation,
            @RequestParam String datePret,
            @RequestParam(required = false) String dateDeRetourPrevue,
            @RequestParam(required = false, defaultValue = "FORWARD") String adjustDirection,
            HttpSession session,
            Model model) {
        UserAccount user = (UserAccount) session.getAttribute("user");
        if (user == null || !"BIBLIOTHECAIRE".equals(user.getRole())) {
            return "redirect:/login";
        }

        try {
            LocalDate datePretParsed = LocalDate.parse(datePret);
            LocalDate dateRetourParsed = (dateDeRetourPrevue != null && !dateDeRetourPrevue.isEmpty()) ? LocalDate.parse(dateDeRetourPrevue) : null;
            Exemplaire exemplaire = exemplaireService.findById(idExemplaire);
            if (exemplaire == null) {
                model.addAttribute("error", "Exemplaire non trouvé.");
                model.addAttribute("idExemplaire", idExemplaire);
                model.addAttribute("idReservation", idReservation);
                model.addAttribute("login", login);
                model.addAttribute("datePret", datePret);
                model.addAttribute("dateDeRetourPrevue", dateDeRetourPrevue);
                model.addAttribute("adjustDirection", adjustDirection);
                model.addAttribute("livre", null);
                model.addAttribute("today", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
                return "backOffice/formPret";
            }
            Livre livre = exemplaire.getLivre();

            // Vérification de la contrainte dateDeRetourPrevue > datePret si fournie
            if (dateRetourParsed != null && !dateRetourParsed.isAfter(datePretParsed)) {
                model.addAttribute("error", "La date de retour prévue doit être postérieure à la date du prêt.");
                model.addAttribute("idExemplaire", idExemplaire);
                model.addAttribute("idReservation", idReservation);
                model.addAttribute("login", login);
                model.addAttribute("datePret", datePret);
                model.addAttribute("dateDeRetourPrevue", dateDeRetourPrevue);
                model.addAttribute("adjustDirection", adjustDirection);
                model.addAttribute("livre", livre);
                model.addAttribute("today", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
                return "backOffice/formPret";
            }

            PretService.PretResult pretResult;
            if (idReservation != null) {
                ReservationService.ReservationResult reservationResult = reservationService.transformerEnPret(idReservation, login, datePretParsed, dateRetourParsed, adjustDirection);
                if (reservationResult.getReservation() == null) {
                    model.addAttribute("error", reservationResult.getMessage());
                    model.addAttribute("idExemplaire", idExemplaire);
                    model.addAttribute("idReservation", idReservation);
                    model.addAttribute("login", login);
                    model.addAttribute("datePret", datePret);
                    model.addAttribute("dateDeRetourPrevue", dateDeRetourPrevue);
                    model.addAttribute("adjustDirection", adjustDirection);
                    model.addAttribute("livre", livre);
                    model.addAttribute("today", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
                    return "backOffice/formPret";
                }
                pretResult = new PretService.PretResult(
                        pretRepository.findByAdherentAndExemplaireAndDateDuPret(
                                adherentService.findByUserAccount(userAccountService.findByLogin(login)),
                                exemplaire,
                                datePretParsed
                        ),
                        reservationResult.getMessage(),
                        null,
                        null
                );
            } else {
                pretResult = pretService.createPret(login, idExemplaire, datePretParsed, dateRetourParsed, adjustDirection);
            }

            if (pretResult.getPret() == null) {
                model.addAttribute("error", pretResult.getMessage());
                model.addAttribute("idExemplaire", idExemplaire);
                model.addAttribute("idReservation", idReservation);
                model.addAttribute("login", login);
                model.addAttribute("datePret", datePret);
                model.addAttribute("dateDeRetourPrevue", dateDeRetourPrevue);
                model.addAttribute("adjustDirection", adjustDirection);
                model.addAttribute("livre", livre);
                model.addAttribute("today", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
                return "backOffice/formPret";
            }

            Adherent adherent = adherentService.findByUserAccount(userAccountService.findByLogin(login));
            model.addAttribute("pret", pretResult.getPret());
            model.addAttribute("adherent", adherent);
            model.addAttribute("exemplaire", exemplaire);
            model.addAttribute("livre", livre);
            model.addAttribute("personne", adherent.getUserAccount().getPersonne());
            if (pretResult.getMessage() != null) {
                model.addAttribute("message", pretResult.getMessage());
            }
            model.addAttribute("success", "Prêt enregistré avec succès.");
            model.addAttribute("today", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
            return "backOffice/detailsPret";
        } catch (DateTimeParseException e) {
            model.addAttribute("error", "Format de date invalide pour la date du prêt ou de retour.");
            model.addAttribute("idExemplaire", idExemplaire);
            model.addAttribute("idReservation", idReservation);
            model.addAttribute("login", login);
            model.addAttribute("datePret", datePret);
            model.addAttribute("dateDeRetourPrevue", dateDeRetourPrevue);
            model.addAttribute("adjustDirection", adjustDirection);
            model.addAttribute("livre", exemplaireService.findById(idExemplaire) != null ? exemplaireService.findById(idExemplaire).getLivre() : null);
            model.addAttribute("today", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
            return "backOffice/formPret";
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors du prêt : " + e.getMessage());
            model.addAttribute("idExemplaire", idExemplaire);
            model.addAttribute("idReservation", idReservation);
            model.addAttribute("login", login);
            model.addAttribute("datePret", datePret);
            model.addAttribute("dateDeRetourPrevue", dateDeRetourPrevue);
            model.addAttribute("adjustDirection", adjustDirection);
            model.addAttribute("livre", exemplaireService.findById(idExemplaire) != null ? exemplaireService.findById(idExemplaire).getLivre() : null);
            model.addAttribute("today", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
            return "backOffice/formPret";
        }
    }

    @GetMapping("/return")
    public String showReturnForm(HttpSession session, Model model) {
        UserAccount user = (UserAccount) session.getAttribute("user");
        if (user == null || !"BIBLIOTHECAIRE".equals(user.getRole())) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        model.addAttribute("today", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        return "backOffice/returnPret";
    }

    @PostMapping("/return")
    public String showActivePrets(@RequestParam String login, HttpSession session, Model model) {
        UserAccount user = (UserAccount) session.getAttribute("user");
        if (user == null || !"BIBLIOTHECAIRE".equals(user.getRole())) {
            return "redirect:/login";
        }

        UserAccount userAccount = userAccountService.findByLogin(login);
        if (userAccount == null || !"MEMBRE".equals(userAccount.getRole())) {
            model.addAttribute("error", "Aucun adhérent trouvé avec ce login.");
            model.addAttribute("today", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
            return "backOffice/returnPret";
        }

        Adherent adherent = adherentService.findByUserAccount(userAccount);
        if (adherent == null) {
            model.addAttribute("error", "Aucun adhérent associé à ce compte.");
            model.addAttribute("today", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
            return "backOffice/returnPret";
        }

        List<Pret> activePrets = pretRepository.findByAdherentAndDateDeRetourReelleIsNull(adherent);
        if (activePrets.isEmpty()) {
            model.addAttribute("error", "Aucun prêt en cours pour cet adhérent.");
            model.addAttribute("today", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
            return "backOffice/returnPret";
        }

        List<Map<String, Object>> pretDisplayList = activePrets.stream().map(pret -> {
            Map<String, Object> pretDisplay = new HashMap<>();
            pretDisplay.put("pret", pret);
            pretDisplay.put("dateDuPret", pret.getDateDuPret());
            pretDisplay.put("dateDeRetourPrevue", pret.getDateDeRetourPrevue());
            return pretDisplay;
        }).collect(Collectors.toList());

        model.addAttribute("adherent", adherent);
        model.addAttribute("prets", pretDisplayList);
        model.addAttribute("today", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        return "backOffice/returnPret";
    }

    @GetMapping("/formReturn")
    public String showFormReturn(@RequestParam Integer idPret, HttpSession session, Model model) {
        UserAccount user = (UserAccount) session.getAttribute("user");
        if (user == null || !"BIBLIOTHECAIRE".equals(user.getRole())) {
            return "redirect:/login";
        }

        Pret pret = pretRepository.findById(idPret).orElse(null);
        if (pret == null) {
            model.addAttribute("error", "Prêt non trouvé.");
            model.addAttribute("today", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
            return "backOffice/returnPret";
        }
        if (pret.getDateDeRetourReelle() != null) {
            model.addAttribute("error", "Ce prêt a déjà été retourné.");
            model.addAttribute("today", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
            return "backOffice/returnPret";
        }

        Map<String, Object> pretDisplay = new HashMap<>();
        pretDisplay.put("pret", pret);
        pretDisplay.put("dateDuPret", pret.getDateDuPret());
        pretDisplay.put("dateDeRetourPrevue", pret.getDateDeRetourPrevue());

        model.addAttribute("pretDisplay", pretDisplay);
        model.addAttribute("pret", pret);
        model.addAttribute("adherent", pret.getAdherent());
        model.addAttribute("exemplaire", pret.getExemplaire());
        model.addAttribute("livre", pret.getExemplaire().getLivre());
        model.addAttribute("personne", pret.getAdherent().getUserAccount().getPersonne());
        model.addAttribute("today", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        return "backOffice/formReturnPret";
    }

    @PostMapping("/processReturn")
    public String processReturn(
            @RequestParam Integer idPret,
            @RequestParam String dateDeRetourReelle,
            HttpSession session,
            Model model) {
        UserAccount user = (UserAccount) session.getAttribute("user");
        if (user == null || !"BIBLIOTHECAIRE".equals(user.getRole())) {
            return "redirect:/login";
        }

        try {
            LocalDate dateRetour = LocalDate.parse(dateDeRetourReelle);
            Pret pret = pretRepository.findById(idPret).orElse(null);
            if (pret == null) {
                model.addAttribute("error", "Prêt non trouvé.");
                model.addAttribute("today", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
                return "backOffice/returnPret";
            }
            if (pret.getDateDeRetourReelle() != null) {
                model.addAttribute("error", "Ce prêt a déjà été retourné.");
                model.addAttribute("today", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
                return "backOffice/returnPret";
            }
            if (dateRetour.isBefore(pret.getDateDuPret()) || dateRetour.isEqual(pret.getDateDuPret())) {
                Map<String, Object> pretDisplay = new HashMap<>();
                pretDisplay.put("pret", pret);
                pretDisplay.put("dateDuPret", pret.getDateDuPret());
                pretDisplay.put("dateDeRetourPrevue", pret.getDateDeRetourPrevue());
                model.addAttribute("pretDisplay", pretDisplay);
                model.addAttribute("pret", pret);
                model.addAttribute("adherent", pret.getAdherent());
                model.addAttribute("exemplaire", pret.getExemplaire());
                model.addAttribute("livre", pret.getExemplaire().getLivre());
                model.addAttribute("personne", pret.getAdherent().getUserAccount().getPersonne());
                model.addAttribute("today", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
                model.addAttribute("error", "La date de retour réelle doit être postérieure à la date du prêt (" + pret.getDateDuPret() + ").");
                return "backOffice/formReturnPret";
            }

            PretService.ReturnResult result = pretService.returnPret(idPret, dateRetour);
            if (result.getPret() == null) {
                model.addAttribute("error", result.getMessage());
                model.addAttribute("today", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
                return "backOffice/returnPret";
            }

            Map<String, Object> pretDisplay = new HashMap<>();
            pretDisplay.put("pret", result.getPret());
            pretDisplay.put("dateDuPret", result.getPret().getDateDuPret());
            pretDisplay.put("dateDeRetourPrevue", result.getPret().getDateDeRetourPrevue());
            pretDisplay.put("dateDeRetourReelle", result.getPret().getDateDeRetourReelle());

            model.addAttribute("pretDisplay", pretDisplay);
            model.addAttribute("pret", result.getPret());
            model.addAttribute("adherent", result.getPret().getAdherent());
            model.addAttribute("exemplaire", result.getPret().getExemplaire());
            model.addAttribute("livre", result.getPret().getExemplaire().getLivre());
            model.addAttribute("personne", result.getPret().getAdherent().getUserAccount().getPersonne());
            if (result.getMessage() != null) {
                model.addAttribute("message", result.getMessage());
            }
            model.addAttribute("success", "Retour enregistré avec succès.");
            model.addAttribute("today", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
            return "backOffice/detailsPret";
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors du retour : " + e.getMessage());
            model.addAttribute("today", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
            return "backOffice/returnPret";
        }
    }

    @GetMapping("/reservationsEnAttente")
    public String listReservationsEnAttente(HttpSession session, Model model) {
        UserAccount user = (UserAccount) session.getAttribute("user");
        System.out.println("ReservationsEnAttente: user=" + (user != null ? user.getLogin() : "null"));
        if (user == null || !"BIBLIOTHECAIRE".equals(user.getRole())) {
            model.addAttribute("error", "Vous devez être connecté en tant que bibliothécaire pour accéder à cette page.");
            model.addAttribute("today", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
            return "redirect:/login";
        }
        List<Reservation> reservations = reservationService.findReservationsByStatut(Reservation.StatutReservationEnum.EN_ATTENTE);
        model.addAttribute("reservations", reservations);
        model.addAttribute("today", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        return "backOffice/reservationsEnAttente";
    }

    @GetMapping("/reservationsValidees")
    public String listReservationsValidees(HttpSession session, Model model) {
        UserAccount user = (UserAccount) session.getAttribute("user");
        System.out.println("ReservationsValidees: user=" + (user != null ? user.getLogin() : "null"));
        if (user == null || !"BIBLIOTHECAIRE".equals(user.getRole())) {
            model.addAttribute("error", "Vous devez être connecté en tant que bibliothécaire pour accéder à cette page.");
            model.addAttribute("today", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
            return "redirect:/login";
        }
        List<Reservation> reservations = reservationService.findReservationsByStatut(Reservation.StatutReservationEnum.VALIDE);
        model.addAttribute("reservations", reservations);
        model.addAttribute("today", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        return "backOffice/reservationsValidees";
    }

    @PostMapping("/validerReservation")
    public String validerReservation(
            @RequestParam("idReservation") Integer idReservation,
            @RequestParam("dateValidation") String dateValidation,
            HttpSession session,
            Model model) {
        UserAccount user = (UserAccount) session.getAttribute("user");
        System.out.println("ValiderReservation: user=" + (user != null ? user.getLogin() : "null") + ", idReservation=" + idReservation);
        if (user == null || !"BIBLIOTHECAIRE".equals(user.getRole())) {
            model.addAttribute("error", "Vous devez être connecté en tant que bibliothécaire pour effectuer cette action.");
            model.addAttribute("today", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
            return "redirect:/login";
        }
        try {
            ReservationService.ReservationResult result = reservationService.validerReservation(idReservation, LocalDate.parse(dateValidation));
            if (result.getReservation() == null) {
                model.addAttribute("error", result.getMessage());
            } else {
                model.addAttribute("message", result.getMessage());
                if (result.isLateValidation()) {
                    model.addAttribute("isLateValidation", true);
                    model.addAttribute("dateLimiteRecuperation", result.getDateLimiteRecuperation());
                }
            }
            model.addAttribute("reservations", reservationService.findReservationsByStatut(Reservation.StatutReservationEnum.EN_ATTENTE));
            model.addAttribute("today", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
            return "backOffice/reservationsEnAttente";
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la validation de la réservation : " + e.getMessage());
            model.addAttribute("reservations", reservationService.findReservationsByStatut(Reservation.StatutReservationEnum.EN_ATTENTE));
            model.addAttribute("today", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
            return "backOffice/reservationsEnAttente";
        }
    }

    @PostMapping("/refuserReservation")
    public String refuserReservation(
            @RequestParam("idReservation") Integer idReservation,
            HttpSession session,
            Model model) {
        UserAccount user = (UserAccount) session.getAttribute("user");
        System.out.println("RefuserReservation: user=" + (user != null ? user.getLogin() : "null") + ", idReservation=" + idReservation);
        if (user == null || !"BIBLIOTHECAIRE".equals(user.getRole())) {
            model.addAttribute("error", "Vous devez être connecté en tant que bibliothécaire pour effectuer cette action.");
            model.addAttribute("today", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
            return "redirect:/login";
        }
        try {
            ReservationService.ReservationResult result = reservationService.refuserReservation(idReservation);
            model.addAttribute("message", result.getMessage());
            model.addAttribute("reservations", reservationService.findReservationsByStatut(Reservation.StatutReservationEnum.EN_ATTENTE));
            model.addAttribute("today", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
            return "backOffice/reservationsEnAttente";
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors du refus de la réservation : " + e.getMessage());
            model.addAttribute("reservations", reservationService.findReservationsByStatut(Reservation.StatutReservationEnum.EN_ATTENTE));
            model.addAttribute("today", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
            return "backOffice/reservationsEnAttente";
        }
    }

    @GetMapping("/penalises")
    public String listPenalises(HttpSession session, Model model) {
        UserAccount user = (UserAccount) session.getAttribute("user");
        if (user == null || !"BIBLIOTHECAIRE".equals(user.getRole())) {
            model.addAttribute("error", "Vous devez être connecté en tant que bibliothécaire pour accéder à cette page.");
            model.addAttribute("today", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
            return "redirect:/login";
        }
        List<Penalisation> penalites = penalisationRepository.findByDateFinPenalisationAfter(LocalDate.now());
        model.addAttribute("penalites", penalites);
        model.addAttribute("today", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        return "backOffice/penalises";
    }

    @GetMapping("/historique")
    public String listHistorique(HttpSession session, Model model) {
        UserAccount user = (UserAccount) session.getAttribute("user");
        if (user == null || !"BIBLIOTHECAIRE".equals(user.getRole())) {
            model.addAttribute("error", "Vous devez être connecté en tant que bibliothécaire pour accéder à cette page.");
            model.addAttribute("today", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
            return "redirect:/login";
        }
        List<HistoriquePaiement> paiements = historiquePaiementService.findAll();
        List<HistoriqueEtat> etats = historiqueEtatRepository.findAll();
        model.addAttribute("paiements", paiements);
        model.addAttribute("etats", etats);
        model.addAttribute("today", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        return "backOffice/historique";
    }

    @GetMapping("/pretsEnCours")
    public String listPretsEnCours(HttpSession session, Model model) {
        UserAccount user = (UserAccount) session.getAttribute("user");
        if (user == null || !"BIBLIOTHECAIRE".equals(user.getRole())) {
            model.addAttribute("error", "Vous devez être connecté en tant que bibliothécaire pour accéder à cette page.");
            model.addAttribute("today", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
            return "redirect:/login";
        }
        List<Pret> prets = pretRepository.findByDateDeRetourReelleIsNull();
        model.addAttribute("prets", prets);
        model.addAttribute("today", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        return "backOffice/pretsEnCours";
    }

    @GetMapping("/parametres")
    public String parametres(HttpSession session, Model model) {
        UserAccount user = (UserAccount) session.getAttribute("user");
        if (user == null || !"BIBLIOTHECAIRE".equals(user.getRole())) {
            model.addAttribute("error", "Vous devez être connecté en tant que bibliothécaire pour accéder à cette page.");
            model.addAttribute("today", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
            return "redirect:/login";
        }
        List<Profil> profils = profilRepository.findAll();
        model.addAttribute("profils", profils);
        model.addAttribute("today", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        return "backOffice/parametres";
    }

    @PostMapping("/updateDelaiReservation")
    public String updateDelaiReservation(
            @RequestParam("idProfil") Integer idProfil,
            @RequestParam("delaiSupplementaireReservation") Integer delaiSupplementaireReservation,
            HttpSession session,
            Model model) {
        UserAccount user = (UserAccount) session.getAttribute("user");
        if (user == null || !"BIBLIOTHECAIRE".equals(user.getRole())) {
            model.addAttribute("error", "Vous devez être connecté en tant que bibliothécaire pour effectuer cette action.");
            model.addAttribute("today", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
            return "redirect:/login";
        }
        Profil profil = profilRepository.findById(idProfil).orElse(null);
        if (profil == null) {
            model.addAttribute("error", "Profil non trouvé.");
        } else {
            if (delaiSupplementaireReservation < 0) {
                model.addAttribute("error", "Le délai supplémentaire ne peut pas être négatif.");
            } else {
                profil.setDelaiSupplementaireReservation(delaiSupplementaireReservation);
                profilRepository.save(profil);
                model.addAttribute("message", "Délai supplémentaire mis à jour avec succès pour le profil " + profil.getProfil() + ".");
            }
        }
        model.addAttribute("profils", profilRepository.findAll());
        model.addAttribute("today", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        return "backOffice/parametres";
    }

    @PostMapping("/resetDelaiReservation")
    public String resetDelaiReservation(@RequestParam("idProfil") Integer idProfil, HttpSession session, Model model) {
        UserAccount user = (UserAccount) session.getAttribute("user");
        if (user == null || !"BIBLIOTHECAIRE".equals(user.getRole())) {
            model.addAttribute("error", "Vous devez être connecté en tant que bibliothécaire pour effectuer cette action.");
            model.addAttribute("today", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
            return "redirect:/login";
        }
        Profil profil = profilRepository.findById(idProfil).orElse(null);
        if (profil == null) {
            model.addAttribute("error", "Profil non trouvé.");
        } else {
            profil.setDelaiSupplementaireReservation(2);
            profilRepository.save(profil);
            model.addAttribute("message", "Délai supplémentaire réinitialisé à 2 jours pour le profil " + profil.getProfil() + ".");
        }
        model.addAttribute("profils", profilRepository.findAll());
        model.addAttribute("today", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        return "backOffice/parametres";
    }
}
package controller;

import jakarta.servlet.http.HttpSession;
import model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import service.*;
import repository.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
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

    @GetMapping({"", "/"})
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
        model.addAttribute("today", Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
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
            model.addAttribute("today", Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
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
                    model.addAttribute("today", Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
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
            model.addAttribute("today", Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
            return "backOffice/inscriptionAdherent";
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de l'inscription : " + e.getMessage());
            model.addAttribute("profils", profilService.findAll());
            model.addAttribute("today", Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
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
        model.addAttribute("today", Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
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
                model.addAttribute("today", Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
                return "backOffice/reabonnement";
            }

            Adherent adherent = adherentService.findByUserAccount(userAccount);
            if (adherent == null) {
                model.addAttribute("error", "Aucun adhérent associé à ce compte.");
                model.addAttribute("today", Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
                return "backOffice/reabonnement";
            }

            Abonnement dernierAbonnement = abonnementService.findLastByAdherent(adherent);
            if (dernierAbonnement == null) {
                model.addAttribute("error", "Aucun abonnement existant pour cet adhérent.");
                model.addAttribute("today", Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
                return "backOffice/reabonnement";
            }

            LocalDate datePaiementParsed = LocalDate.parse(datePaiement);
            if (datePaiementParsed.isBefore(dernierAbonnement.getDateDebut())) {
                model.addAttribute("error", "La date de paiement doit être postérieure ou égale à la date de début du dernier abonnement.");
                model.addAttribute("today", Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
                return "backOffice/reabonnement";
            }

            LocalDate dateDebut = dernierAbonnement.getDateFin();
            LocalDate dateFin;
            Profil profil = adherent.getProfil();
            if (dateFinAbonnement != null && !dateFinAbonnement.isEmpty()) {
                dateFin = LocalDate.parse(dateFinAbonnement);
                if (dateFin.isBefore(dernierAbonnement.getDateFin()) || dateFin.equals(dernierAbonnement.getDateFin())) {
                    model.addAttribute("error", "La date de fin d'abonnement doit être postérieure à la date de fin du dernier abonnement.");
                    model.addAttribute("today", Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
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
            return "backOffice/detailsAdherent";
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors du réabonnement : " + e.getMessage());
            model.addAttribute("today", Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
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
            model.addAttribute("today", Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
            return "backOffice/reabonnement";
        }

        UserAccount userAccount = adherent.getUserAccount();
        Abonnement dernierAbonnement = abonnementService.findLastByAdherent(adherent);
        model.addAttribute("adherent", adherent);
        model.addAttribute("userAccount", userAccount);
        model.addAttribute("abonnement", dernierAbonnement);
        model.addAttribute("personne", userAccount.getPersonne());
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
        return "backOffice/pret";
    }

    @GetMapping("/formPret")
    public String showFormPret(@RequestParam Integer idLivre, HttpSession session, Model model) {
        UserAccount user = (UserAccount) session.getAttribute("user");
        if (user == null || !"BIBLIOTHECAIRE".equals(user.getRole())) {
            return "redirect:/login";
        }
        Integer idExemplaire = pretService.findAvailableExemplaire(idLivre);
        if (idExemplaire == null) {
            model.addAttribute("error", "Aucun exemplaire disponible pour ce livre.");
            model.addAttribute("livres", livreService.findAll());
            return "backOffice/pret";
        }
        Livre livre = livreService.findAll().stream()
                .filter(l -> l.getIdLivre().equals(idLivre))
                .findFirst()
                .orElse(null);
        model.addAttribute("idLivre", idLivre);
        model.addAttribute("idExemplaire", idExemplaire);
        model.addAttribute("livre", livre);
        model.addAttribute("today", Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
        return "backOffice/formPret";
    }

    @PostMapping("/pret")
    public String processPret(
            @RequestParam String login,
            @RequestParam Integer idExemplaire,
            @RequestParam(required = false) String adjustDirection,
            HttpSession session,
            Model model) {
        UserAccount user = (UserAccount) session.getAttribute("user");
        if (user == null || !"BIBLIOTHECAIRE".equals(user.getRole())) {
            return "redirect:/login";
        }

        try {
            PretService.PretResult result = pretService.createPret(login, idExemplaire, adjustDirection);
            if (result.getPret() == null) {
                model.addAttribute("error", result.getMessage());
                model.addAttribute("livres", livreService.findAll());
                return "backOffice/pret";
            }

            Adherent adherent = adherentService.findByUserAccount(userAccountService.findByLogin(login));
            Exemplaire exemplaire = exemplaireService.findById(idExemplaire);
            model.addAttribute("pret", result.getPret());
            model.addAttribute("adherent", adherent);
            model.addAttribute("exemplaire", exemplaire);
            model.addAttribute("livre", exemplaire.getLivre());
            model.addAttribute("personne", adherent.getUserAccount().getPersonne());
            if (result.getMessage() != null) {
                model.addAttribute("message", result.getMessage());
            }
            model.addAttribute("success", "Prêt enregistré avec succès.");
            return "backOffice/detailsPret";
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors du prêt : " + e.getMessage());
            model.addAttribute("livres", livreService.findAll());
            return "backOffice/pret";
        }
    }

    @GetMapping("/return")
    public String showReturnForm(HttpSession session, Model model) {
        UserAccount user = (UserAccount) session.getAttribute("user");
        if (user == null || !"BIBLIOTHECAIRE".equals(user.getRole())) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        model.addAttribute("today", Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
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
            model.addAttribute("today", Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
            return "backOffice/returnPret";
        }

        Adherent adherent = adherentService.findByUserAccount(userAccount);
        if (adherent == null) {
            model.addAttribute("error", "Aucun adhérent associé à ce compte.");
            model.addAttribute("today", Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
            return "backOffice/returnPret";
        }

        List<Pret> activePrets = pretRepository.findByAdherentAndDateDeRetourReelleIsNull(adherent);
        if (activePrets.isEmpty()) {
            model.addAttribute("error", "Aucun prêt en cours pour cet adhérent.");
            model.addAttribute("today", Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
            return "backOffice/returnPret";
        }

        // Convertir les LocalDate en Date pour l'affichage dans le JSP
        List<Map<String, Object>> pretDisplayList = activePrets.stream().map(pret -> {
            Map<String, Object> pretDisplay = new HashMap<>();
            pretDisplay.put("pret", pret);
            pretDisplay.put("dateDuPret", pret.getDateDuPret() != null ? 
                Date.from(pret.getDateDuPret().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()) : null);
            pretDisplay.put("dateDeRetourPrevue", pret.getDateDeRetourPrevue() != null ? 
                Date.from(pret.getDateDeRetourPrevue().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()) : null);
            return pretDisplay;
        }).collect(Collectors.toList());

        model.addAttribute("adherent", adherent);
        model.addAttribute("prets", pretDisplayList);
        model.addAttribute("today", Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
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
            model.addAttribute("today", Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
            return "backOffice/returnPret";
        }
        if (pret.getDateDeRetourReelle() != null) {
            model.addAttribute("error", "Ce prêt a déjà été retourné.");
            model.addAttribute("today", Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
            return "backOffice/returnPret";
        }

        // Convertir les LocalDate en Date pour l'affichage dans le JSP
        Map<String, Object> pretDisplay = new HashMap<>();
        pretDisplay.put("pret", pret);
        pretDisplay.put("dateDuPret", pret.getDateDuPret() != null ? 
            Date.from(pret.getDateDuPret().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()) : null);
        pretDisplay.put("dateDeRetourPrevue", pret.getDateDeRetourPrevue() != null ? 
            Date.from(pret.getDateDeRetourPrevue().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()) : null);

        model.addAttribute("pretDisplay", pretDisplay);
        model.addAttribute("pret", pret);
        model.addAttribute("adherent", pret.getAdherent());
        model.addAttribute("exemplaire", pret.getExemplaire());
        model.addAttribute("livre", pret.getExemplaire().getLivre());
        model.addAttribute("personne", pret.getAdherent().getUserAccount().getPersonne());
        model.addAttribute("today", Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
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
                model.addAttribute("today", Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
                return "backOffice/returnPret";
            }
            if (pret.getDateDeRetourReelle() != null) {
                model.addAttribute("error", "Ce prêt a déjà été retourné.");
                model.addAttribute("today", Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
                return "backOffice/returnPret";
            }
            // Valider que la date de retour réelle est postérieure à la date du prêt
            if (dateRetour.isBefore(pret.getDateDuPret()) || dateRetour.isEqual(pret.getDateDuPret())) {
                // Préparer les données pour réafficher le formulaire avec l'erreur
                Map<String, Object> pretDisplay = new HashMap<>();
                pretDisplay.put("pret", pret);
                pretDisplay.put("dateDuPret", pret.getDateDuPret() != null ? 
                    Date.from(pret.getDateDuPret().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()) : null);
                pretDisplay.put("dateDeRetourPrevue", pret.getDateDeRetourPrevue() != null ? 
                    Date.from(pret.getDateDeRetourPrevue().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()) : null);
                model.addAttribute("pretDisplay", pretDisplay);
                model.addAttribute("pret", pret);
                model.addAttribute("adherent", pret.getAdherent());
                model.addAttribute("exemplaire", pret.getExemplaire());
                model.addAttribute("livre", pret.getExemplaire().getLivre());
                model.addAttribute("personne", pret.getAdherent().getUserAccount().getPersonne());
                model.addAttribute("today", Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
                model.addAttribute("error", "La date de retour réelle doit être postérieure à la date du prêt (" + pret.getDateDuPret() + ").");
                return "backOffice/formReturnPret";
            }

            PretService.ReturnResult result = pretService.returnPret(idPret, dateRetour);
            if (result.getPret() == null) {
                model.addAttribute("error", result.getMessage());
                model.addAttribute("today", Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
                return "backOffice/returnPret";
            }

            // Convertir les LocalDate en Date pour l'affichage dans le JSP
            Map<String, Object> pretDisplay = new HashMap<>();
            pretDisplay.put("pret", result.getPret());
            pretDisplay.put("dateDuPret", result.getPret().getDateDuPret() != null ? 
                Date.from(result.getPret().getDateDuPret().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()) : null);
            pretDisplay.put("dateDeRetourPrevue", result.getPret().getDateDeRetourPrevue() != null ? 
                Date.from(result.getPret().getDateDeRetourPrevue().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()) : null);
            pretDisplay.put("dateDeRetourReelle", result.getPret().getDateDeRetourReelle() != null ? 
                Date.from(result.getPret().getDateDeRetourReelle().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()) : null);

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
            return "backOffice/detailsPret";
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors du retour : " + e.getMessage());
            model.addAttribute("today", Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
            return "backOffice/returnPret";
        }
    }
}

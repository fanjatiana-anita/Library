package controller;

import jakarta.servlet.http.HttpSession;
import model.Adherent;
import model.Abonnement;
import model.Personne;
import model.Profil;
import model.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import service.AdherentService;
import service.AbonnementService;
import service.PersonneService;
import service.ProfilService;
import service.UserAccountService;

import java.time.LocalDate;
import java.util.List;

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
        model.addAttribute("today", java.util.Date.from(LocalDate.now().atStartOfDay().atZone(java.time.ZoneId.systemDefault()).toInstant()));
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

        // Vérifier l'unicité du login
        if (userAccountService.findByLogin(login) != null) {
            model.addAttribute("error", "Ce login est déjà utilisé.");
            model.addAttribute("profils", profilService.findAll());
            model.addAttribute("today", java.util.Date.from(LocalDate.now().atStartOfDay().atZone(java.time.ZoneId.systemDefault()).toInstant()));
            return "backOffice/inscriptionAdherent";
        }

        try {
            // Créer une nouvelle personne
            Personne personne = new Personne();
            personne.setNomPersonne(nomPersonne);
            personne.setDateDeNaissance(LocalDate.parse(dateDeNaissance));
            personne.setSexe(sexe);
            personne.setAdresse(adresse);
            personne = personneService.save(personne);

            // Créer un compte utilisateur
            UserAccount userAccount = new UserAccount();
            userAccount.setPersonne(personne);
            userAccount.setLogin(login);
            userAccount.setMotDePasse(motDePasse); // À hasher dans une implémentation réelle
            userAccount.setRole("MEMBRE");
            userAccount = userAccountService.save(userAccount);

            // Créer un adhérent
            Adherent adherent = new Adherent();
            adherent.setUserAccount(userAccount);
            Profil profil = profilService.findById(idProfil);
            adherent.setProfil(profil);
            adherent.setStatutAdherent(Adherent.StatutAdherentEnum.valueOf(statutAdherent.toUpperCase()));
            LocalDate adhesionDate = LocalDate.parse(dateAdhesion);
            adherent.setDateAdhesion(adhesionDate);
            adherent = adherentService.save(adherent);

            // Créer un abonnement
            Abonnement abonnement = new Abonnement();
            abonnement.setAdherent(adherent);
            abonnement.setDateDebut(adhesionDate);
            LocalDate dateFin;
            if (dateFinAbonnement != null && !dateFinAbonnement.isEmpty()) {
                dateFin = LocalDate.parse(dateFinAbonnement);
                if (dateFin.isBefore(adhesionDate)) {
                    model.addAttribute("error", "La date de fin d'abonnement doit être postérieure à la date d'adhésion.");
                    model.addAttribute("profils", profilService.findAll());
                    model.addAttribute("today", java.util.Date.from(LocalDate.now().atStartOfDay().atZone(java.time.ZoneId.systemDefault()).toInstant()));
                    return "backOffice/inscriptionAdherent";
                }
            } else {
                dateFin = adhesionDate.plusDays(profil.getDureeAbonnement());
            }
            abonnement.setDateFin(dateFin);
            abonnementService.save(abonnement);

            model.addAttribute("success", "Adhérent inscrit et abonné avec succès.");
            model.addAttribute("profils", profilService.findAll());
            model.addAttribute("today", java.util.Date.from(LocalDate.now().atStartOfDay().atZone(java.time.ZoneId.systemDefault()).toInstant()));
            return "backOffice/inscriptionAdherent";
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de l'inscription : " + e.getMessage());
            model.addAttribute("profils", profilService.findAll());
            model.addAttribute("today", java.util.Date.from(LocalDate.now().atStartOfDay().atZone(java.time.ZoneId.systemDefault()).toInstant()));
            return "backOffice/inscriptionAdherent";
        }
    }
}
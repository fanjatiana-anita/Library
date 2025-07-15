package controller;

import jakarta.servlet.http.HttpSession;
import model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import service.ExemplaireService;
import service.LivreService;
import service.PretService;
import service.ReservationService;
import repository.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/frontoffice")
public class FrontOfficeController {

    @Autowired
    private LivreService livreService;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private PretService pretService;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private PretRepository pretRepository;

    @Autowired
    private ProlongementRepository prolongementRepository;

    @Autowired
    private PenalisationRepository penalisationRepository;
    

    @Autowired
    private ExemplaireService exemplaireService;


    @Autowired
    private AdherentRepository adherentRepository;

    @Autowired
    private service.AbonnementService abonnementService;

    @GetMapping("/adherents/{id}")
    @ResponseBody
    public Map<String, Object> getAdherentInfos(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        Adherent adherent = adherentRepository.findById(id).orElse(null);
        if (adherent == null) {
            result.put("error", "Adhérent non trouvé");
            return result;
        }
        UserAccount user = adherent.getUserAccount();
        result.put("id", adherent.getIdAdherent());
        result.put("login", user != null ? user.getLogin() : null);
        result.put("statut", adherent.getStatutAdherent());

        Abonnement abonnement = abonnementService.findLastByAdherent(adherent);
        boolean abonnementActif = false;
        LocalDate dateFinAbonnement = null;
        if (abonnement != null) {
            dateFinAbonnement = abonnement.getDateFin();
            abonnementActif = dateFinAbonnement != null && !dateFinAbonnement.isBefore(LocalDate.now());
        }
        result.put("abonnementActif", abonnementActif);
        result.put("dateFinAbonnement", dateFinAbonnement);

        Profil profil = adherent.getProfil();
        int quotaMaxPret = profil.getQuotaMaxPret();
        int quotaMaxReservation = profil.getQuotaMaxReservation();
        int quotaMaxProlongement = profil.getQuotaMaxProlongement();

        int nbPrets = pretRepository.findByAdherentAndDateDeRetourReelleIsNull(adherent).size();
        int nbReservations = reservationRepository.findByAdherentAndStatutReservationIn(adherent, List.of(model.Reservation.StatutReservationEnum.EN_ATTENTE, model.Reservation.StatutReservationEnum.VALIDE)).size();
        int nbProlongements = 0;
        List<model.Pret> pretsActifs = pretRepository.findByAdherentAndDateDeRetourReelleIsNull(adherent);
        for (model.Pret pret : pretsActifs) {
            List<model.Prolongement> prolongements = prolongementRepository.findByPret(pret);
            if (prolongements != null && !prolongements.isEmpty()) {
                for (model.Prolongement p : prolongements) {
                    if (p.getStatutProlongement() == model.Prolongement.StatutProlongementEnum.EN_ATTENTE ||
                        p.getStatutProlongement() == model.Prolongement.StatutProlongementEnum.VALIDE) {
                        nbProlongements++;
                    }
                }
            }
        }
        result.put("quotaPret", nbPrets);
        result.put("quotaMaxPret", quotaMaxPret);
        result.put("quotaReservation", nbReservations);
        result.put("quotaMaxReservation", quotaMaxReservation);
        result.put("quotaProlongement", nbProlongements);
        result.put("quotaMaxProlongement", quotaMaxProlongement);

        boolean sanctionne = !penalisationRepository.findByAdherentAndDateFinPenalisationAfter(adherent, LocalDate.now()).isEmpty();
        result.put("sanctionne", sanctionne);

        return result;
    }

    @GetMapping("/livres/{idLivre}/exemplaires")
    @ResponseBody
    public List<Exemplaire> getExemplairesByLivreId(@PathVariable Integer idLivre) {
        return exemplaireService.findExemplairesByLivreId(idLivre);
    }
    
    @GetMapping({"", "/accueil"})
    public String home(Model model, HttpSession session) {
        UserAccount user = (UserAccount) session.getAttribute("user");
        System.out.println("Home: user=" + (user != null ? user.getLogin() : "null"));
        if (user == null || !"MEMBRE".equals(user.getRole())) {
            model.addAttribute("error", "Vous devez être connecté en tant que membre pour accéder à cette page.");
            return "redirect:/login";
        }
        Adherent adherent = user.getAdherent();
        if (adherent == null) {
            model.addAttribute("error", "Aucun adhérent associé à ce compte.");
            return "redirect:/login";
        }

        List<Reservation> reservationsActives = reservationRepository.findByAdherentAndStatutReservationIn(
                adherent, List.of(Reservation.StatutReservationEnum.EN_ATTENTE, Reservation.StatutReservationEnum.VALIDE));
        int nombreReservationsActives = reservationsActives.size();

        List<Pret> pretsActifs = pretRepository.findByAdherentAndDateDeRetourReelleIsNull(adherent);
        int nombrePretsActifs = pretsActifs.size();

        int nombreProlongements = 0;
        for (Pret pret : pretsActifs) {
            List<Prolongement> prolongements = prolongementRepository.findByPret(pret);
            if (prolongements != null && !prolongements.isEmpty()) {
                for (Prolongement p : prolongements) {
                    if (p.getStatutProlongement() == Prolongement.StatutProlongementEnum.EN_ATTENTE ||
                        p.getStatutProlongement() == Prolongement.StatutProlongementEnum.VALIDE) {
                        nombreProlongements++;
                    }
                }
            }
        }

        // Pénalisation active
        List<Penalisation> penalites = penalisationRepository.findByAdherentAndDateFinPenalisationAfter(adherent, LocalDate.now());
        Penalisation penalisationActive = penalites.isEmpty() ? null : penalites.get(0);

        // Ajouter les attributs au modèle
        model.addAttribute("login", user.getLogin());
        model.addAttribute("adherent", adherent);
        model.addAttribute("nombreReservationsActives", nombreReservationsActives);
        model.addAttribute("nombrePretsActifs", nombrePretsActifs);
        model.addAttribute("nombreProlongements", nombreProlongements);
        model.addAttribute("penalisationActive", penalisationActive);

        return "frontOffice/accueil";
    }

    @GetMapping("/reservations")
    public String listLivres(Model model, HttpSession session) {
        UserAccount user = (UserAccount) session.getAttribute("user");
        System.out.println("Reservations: user=" + (user != null ? user.getLogin() : "null"));
        if (user == null || !"MEMBRE".equals(user.getRole())) {
            model.addAttribute("error", "Vous devez être connecté en tant que membre pour accéder à cette page.");
            return "redirect:/login";
        }
        List<Livre> livres = livreService.findAll();
        model.addAttribute("livres", livres);
        return "frontOffice/reservations";
    }

    @GetMapping("/formReservation")
    public String formReservation(@RequestParam("idLivre") Integer idLivre, Model model, HttpSession session) {
        UserAccount user = (UserAccount) session.getAttribute("user");
        System.out.println("FormReservation: user=" + (user != null ? user.getLogin() : "null"));
        if (user == null || !"MEMBRE".equals(user.getRole())) {
            model.addAttribute("error", "Vous devez être connecté en tant que membre pour accéder à cette page.");
            return "redirect:/login";
        }
        Integer idExemplaire = pretService.findAvailableExemplaire(idLivre);
        if (idExemplaire == null) {
            model.addAttribute("error", "Aucun exemplaire disponible pour ce livre.");
            return "redirect:/frontoffice/reservations";
        }
        model.addAttribute("idLivre", idLivre);
        model.addAttribute("idExemplaire", idExemplaire);
        model.addAttribute("dateReservation", LocalDate.now());
        return "frontOffice/formReservation";
    }

    @PostMapping("/processReservation")
    public String processReservation(
            @RequestParam("idLivre") Integer idLivre,
            @RequestParam("dateReservation") String dateReservation,
            @RequestParam("datePretPrevue") String datePretPrevue,
            HttpSession session,
            Model model) {
        UserAccount user = (UserAccount) session.getAttribute("user");
        System.out.println("ProcessReservation: user=" + (user != null ? user.getLogin() : "null"));
        if (user == null || !"MEMBRE".equals(user.getRole())) {
            model.addAttribute("error", "Vous devez être connecté en tant que membre pour effectuer une réservation.");
            return "redirect:/login";
        }
        String login = user.getLogin();
        try {
            ReservationService.ReservationResult result = reservationService.createReservation(
                login, idLivre, LocalDate.parse(dateReservation), LocalDate.parse(datePretPrevue));
            if (result.getReservation() == null) {
                model.addAttribute("error", result.getMessage());
                model.addAttribute("livres", livreService.findAll());
                return "frontOffice/reservations";
            }
            model.addAttribute("reservation", result.getReservation());
            model.addAttribute("message", result.getMessage());
            return "frontOffice/detailsReservation";
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la réservation : " + e.getMessage());
            model.addAttribute("livres", livreService.findAll());
            return "frontOffice/reservations";
        }
    }

    @GetMapping("/mesReservations")
    public String listReservations(Model model, HttpSession session) {
        UserAccount user = (UserAccount) session.getAttribute("user");
        System.out.println("MesReservations: user=" + (user != null ? user.getLogin() : "null"));
        if (user == null || !"MEMBRE".equals(user.getRole())) {
            model.addAttribute("error", "Vous devez être connecté en tant que membre pour accéder à cette page.");
            return "redirect:/login";
        }
        List<Reservation> reservations = reservationService.findReservationsByLogin(user.getLogin());
        List<Map<String, Object>> reservationDisplayList = reservations.stream().map(reservation -> {
            Map<String, Object> display = new HashMap<>();
            display.put("reservation", reservation);
            if (reservation.getDateDeReservation() != null) {
                display.put("dateDeReservationAsDate",
                        Date.from(reservation.getDateDeReservation().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            }
            if (reservation.getDateDuPretPrevue() != null) {
                display.put("dateDuPretPrevueAsDate",
                        Date.from(reservation.getDateDuPretPrevue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            }
            if (reservation.getDateValidation() != null && reservation.getDateDuPretPrevue() != null) {
                boolean isLate = reservation.getDateValidation().isAfter(reservation.getDateDuPretPrevue());
                reservation.setLateValidation(isLate);
                if (reservation.getDateLimiteRecuperation() != null) {
                    display.put("dateLimiteRecuperationAsDate",
                            Date.from(reservation.getDateLimiteRecuperation().atStartOfDay(ZoneId.systemDefault()).toInstant()));
                }
            }
            return display;
        }).collect(Collectors.toList());

        model.addAttribute("reservations", reservationDisplayList);
        return "frontOffice/mesReservations";
    }

    @GetMapping("/detailsReservation")
    public String detailsReservation(@RequestParam("idReservation") Integer idReservation, Model model, HttpSession session) {
        UserAccount user = (UserAccount) session.getAttribute("user");
        System.out.println("DetailsReservation: user=" + (user != null ? user.getLogin() : "null") + ", idReservation=" + idReservation);
        if (user == null || !"MEMBRE".equals(user.getRole())) {
            model.addAttribute("error", "Vous devez être connecté en tant que membre pour accéder à cette page.");
            return "redirect:/login";
        }
        Reservation reservation = reservationService.findById(idReservation);
        if (reservation == null || !reservation.getAdherent().getUserAccount().getLogin().equals(user.getLogin())) {
            model.addAttribute("error", "Réservation non trouvée ou accès non autorisé.");
            return "redirect:/frontoffice/mesReservations";
        }
        Map<String, Object> reservationDisplay = new HashMap<>();
        reservationDisplay.put("reservation", reservation);
        if (reservation.getDateDeReservation() != null) {
            reservationDisplay.put("dateDeReservationAsDate",
                    Date.from(reservation.getDateDeReservation().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }
        if (reservation.getDateDuPretPrevue() != null) {
            reservationDisplay.put("dateDuPretPrevueAsDate",
                    Date.from(reservation.getDateDuPretPrevue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }
        if (reservation.getDateValidation() != null && reservation.getDateDuPretPrevue() != null) {
            boolean isLate = reservation.getDateValidation().isAfter(reservation.getDateDuPretPrevue());
            reservation.setLateValidation(isLate);
            if (reservation.getDateLimiteRecuperation() != null) {
                reservationDisplay.put("dateLimiteRecuperationAsDate",
                        Date.from(reservation.getDateLimiteRecuperation().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            }
        }
        model.addAttribute("reservationDisplay", reservationDisplay);
        model.addAttribute("message", "Détails de la réservation");
        return "frontOffice/detailsReservation";
    }

    @GetMapping("/mesPrets")
    public String listPrets(Model model, HttpSession session) {
        UserAccount user = (UserAccount) session.getAttribute("user");
        System.out.println("MesPrets: user=" + (user != null ? user.getLogin() : "null"));
        if (user == null || !"MEMBRE".equals(user.getRole())) {
            model.addAttribute("error", "Vous devez être connecté en tant que membre pour accéder à cette page.");
            return "redirect:/login";
        }
        Adherent adherent = user.getAdherent();
        if (adherent == null) {
            model.addAttribute("error", "Aucun adhérent associé à ce compte.");
            return "redirect:/login";
        }
        List<Pret> prets = pretRepository.findByAdherentAndDateDeRetourReelleIsNull(adherent);
        // Calcul du quota global utilisé uniquement sur les prêts non retournés
        List<Prolongement> tousProlongements = prolongementRepository.findByAdherent(adherent);
        int nbProlongementsUtilises = (int) tousProlongements.stream()
            .filter(p -> p.getStatutProlongement() != Prolongement.StatutProlongementEnum.REFUSE && p.getPret().getDateDeRetourReelle() == null)
            .count();
        int quotaRestant = adherent.getProfil().getQuotaMaxProlongement() - nbProlongementsUtilises;

        List<Map<String, Object>> pretDisplayList = prets.stream().map(pret -> {
            Map<String, Object> display = new HashMap<>();
            display.put("pret", pret);
            if (pret.getDateDuPret() != null) {
                display.put("dateDuPretAsDate",
                        Date.from(pret.getDateDuPret().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            }
            if (pret.getDateDeRetourPrevue() != null) {
                display.put("dateDeRetourPrevueAsDate",
                        Date.from(pret.getDateDeRetourPrevue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            }
            // Ajout du statut du prolongement et de l'objet prolongement
            List<Prolongement> prolongements = prolongementRepository.findByPret(pret);
            if (prolongements != null && !prolongements.isEmpty()) {
                Prolongement dernierProlongement = prolongements.get(prolongements.size() - 1); // le plus récent
                display.put("statutProlongement", dernierProlongement.getStatutProlongement());
                display.put("prolongement", dernierProlongement);
                display.put("quotaProlongement", 0); // Demande déjà faite, quota non disponible
            } else {
                display.put("statutProlongement", null);
                display.put("prolongement", null);
                display.put("quotaProlongement", quotaRestant > 0 ? 1 : 0);
            }
            return display;
        }).collect(Collectors.toList());

        model.addAttribute("prets", pretDisplayList);
        return "frontOffice/mesPrets";
    }

    @GetMapping("/formProlongement")
    public String formProlongement(@RequestParam("idPret") Integer idPret, Model model, HttpSession session) {
        UserAccount user = (UserAccount) session.getAttribute("user");
        System.out.println("FormProlongement: user=" + (user != null ? user.getLogin() : "null") + ", idPret=" + idPret);
        if (user == null || !"MEMBRE".equals(user.getRole())) {
            model.addAttribute("error", "Vous devez être connecté en tant que membre pour accéder à cette page.");
            return "redirect:/login";
        }
        Adherent adherent = user.getAdherent();
        if (adherent == null) {
            model.addAttribute("error", "Aucun adhérent associé à ce compte.");
            return "redirect:/login";
        }
        Pret pret = pretRepository.findById(idPret).orElse(null);
        if (pret == null || !pret.getAdherent().equals(adherent)) {
            model.addAttribute("error", "Prêt non trouvé ou accès non autorisé.");
            return "redirect:/frontoffice/mesPrets";
        }
        model.addAttribute("idPret", idPret);
        model.addAttribute("dateDemandeProlongement", LocalDate.now());
        model.addAttribute("dateRetourPrevueActuelle", pret.getDateDeRetourPrevue());
        model.addAttribute("dateRetourPrevueApresProlongement", pret.getDateDeRetourPrevue().plusDays(adherent.getProfil().getDureeMaxPret()));
        model.addAttribute("livre", pret.getExemplaire().getLivre());
        return "frontOffice/formProlongement";
    }

    @PostMapping("/processProlongement")
    public String processProlongement(
            @RequestParam("idPret") Integer idPret,
            @RequestParam("dateDemandeProlongement") String dateDemandeProlongement,
            @RequestParam(required = false) String dateRetourPrevueApresProlongement,
            HttpSession session,
            Model model) {
        UserAccount user = (UserAccount) session.getAttribute("user");
        System.out.println("ProcessProlongement: user=" + (user != null ? user.getLogin() : "null") + ", idPret=" + idPret);
        if (user == null || !"MEMBRE".equals(user.getRole())) {
            model.addAttribute("error", "Vous devez être connecté en tant que membre pour effectuer une demande de prolongement.");
            return "redirect:/login";
        }
        String login = user.getLogin();
        try {
            LocalDate dateDemande = LocalDate.parse(dateDemandeProlongement);
            LocalDate dateRetour = dateRetourPrevueApresProlongement != null && !dateRetourPrevueApresProlongement.isEmpty()
                    ? LocalDate.parse(dateRetourPrevueApresProlongement)
                    : null;
            PretService.ProlongementResult result = pretService.requestProlongement(login, idPret, dateDemande, dateRetour);
            if (result.getProlongement() == null) {
                model.addAttribute("error", result.getMessage());
                model.addAttribute("prets", pretRepository.findByAdherentAndDateDeRetourReelleIsNull(user.getAdherent())
                        .stream()
                        .map(pret -> {
                            Map<String, Object> display = new HashMap<>();
                            display.put("pret", pret);
                            if (pret.getDateDuPret() != null) {
                                display.put("dateDuPretAsDate",
                                        Date.from(pret.getDateDuPret().atStartOfDay(ZoneId.systemDefault()).toInstant()));
                            }
                            if (pret.getDateDeRetourPrevue() != null) {
                                display.put("dateDeRetourPrevueAsDate",
                                        Date.from(pret.getDateDeRetourPrevue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
                            }
                            return display;
                        })
                        .collect(Collectors.toList()));
                return "frontOffice/mesPrets";
            }
            model.addAttribute("prolongement", result.getProlongement());
            model.addAttribute("message", result.getMessage());
            return "frontOffice/detailsProlongement";
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la demande de prolongement : " + e.getMessage());
            model.addAttribute("prets", pretRepository.findByAdherentAndDateDeRetourReelleIsNull(user.getAdherent())
                    .stream()
                    .map(pret -> {
                        Map<String, Object> display = new HashMap<>();
                        display.put("pret", pret);
                        if (pret.getDateDuPret() != null) {
                            display.put("dateDuPretAsDate",
                                    Date.from(pret.getDateDuPret().atStartOfDay(ZoneId.systemDefault()).toInstant()));
                        }
                        if (pret.getDateDeRetourPrevue() != null) {
                            display.put("dateDeRetourPrevueAsDate",
                                    Date.from(pret.getDateDeRetourPrevue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
                        }
                        return display;
                    })
                    .collect(Collectors.toList()));
            return "frontOffice/mesPrets";
        }
    }

    @GetMapping("/detailsProlongement")
    public String detailsProlongement(@RequestParam("idProlongement") Integer idProlongement, Model model, HttpSession session) {
        UserAccount user = (UserAccount) session.getAttribute("user");
        System.out.println("DetailsProlongement: user=" + (user != null ? user.getLogin() : "null") + ", idProlongement=" + idProlongement);
        if (user == null || !"MEMBRE".equals(user.getRole())) {
            model.addAttribute("error", "Vous devez être connecté en tant que membre pour accéder à cette page.");
            return "redirect:/login";
        }
        Prolongement prolongement = prolongementRepository.findById(idProlongement).orElse(null);
        if (prolongement == null || !prolongement.getAdherent().getUserAccount().getLogin().equals(user.getLogin())) {
            model.addAttribute("error", "Demande de prolongement non trouvée ou accès non autorisé.");
            return "redirect:/frontoffice/mesPrets";
        }
        Map<String, Object> prolongementDisplay = new HashMap<>();
        prolongementDisplay.put("prolongement", prolongement);
        if (prolongement.getDateDemandeProlongement() != null) {
            prolongementDisplay.put("dateDemandeProlongementAsDate",
                    Date.from(prolongement.getDateDemandeProlongement().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }
        if (prolongement.getDateRetourPrevueApresProlongement() != null) {
            prolongementDisplay.put("dateRetourPrevueApresProlongementAsDate",
                    Date.from(prolongement.getDateRetourPrevueApresProlongement().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }
        model.addAttribute("prolongementDisplay", prolongementDisplay);
        model.addAttribute("message", "Détails de la demande de prolongement");
        return "frontOffice/detailsProlongement";
    }


}
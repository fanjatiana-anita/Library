package controller;

import model.Adherent;
import model.Livre;
import model.Penalisation;
import model.Pret;
import model.Reservation;
import model.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import service.LivreService;
import service.PretService;
import service.ReservationService;
import repository.PenalisationRepository;
import repository.PretRepository;
import repository.ReservationRepository;

import jakarta.servlet.http.HttpSession;
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
    private PenalisationRepository penalisationRepository;

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

        // Nombre de réservations actives (EN_ATTENTE ou VALIDE)
        List<Reservation> reservationsActives = reservationRepository.findByAdherentAndStatutReservationIn(
                adherent, List.of(Reservation.StatutReservationEnum.EN_ATTENTE, Reservation.StatutReservationEnum.VALIDE));
        int nombreReservationsActives = reservationsActives.size();

        // Nombre de prêts actifs (dateDeRetourReelle IS NULL)
        List<Pret> pretsActifs = pretRepository.findByAdherentAndDateDeRetourReelleIsNull(adherent);
        int nombrePretsActifs = pretsActifs.size();

        // Nombre total de prolongements utilisés
        int nombreProlongements = pretsActifs.stream().mapToInt(Pret::getNombreProlongement).sum();

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
        // Convertir les LocalDate en java.util.Date pour compatibilité avec fmt:formatDate
        List<Map<String, Object>> reservationDisplayList = reservations.stream().map(reservation -> {
            Map<String, Object> display = new HashMap<>();
            display.put("reservation", reservation);
            // Convertir dateDeReservation
            if (reservation.getDateDeReservation() != null) {
                display.put("dateDeReservationAsDate",
                        Date.from(reservation.getDateDeReservation().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            }
            // Convertir dateDuPretPrevue
            if (reservation.getDateDuPretPrevue() != null) {
                display.put("dateDuPretPrevueAsDate",
                        Date.from(reservation.getDateDuPretPrevue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            }
            // Convertir dateLimiteRecuperation
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
        // Créer une Map pour stocker les dates converties
        Map<String, Object> reservationDisplay = new HashMap<>();
        reservationDisplay.put("reservation", reservation);
        // Convertir dateDeReservation
        if (reservation.getDateDeReservation() != null) {
            reservationDisplay.put("dateDeReservationAsDate",
                    Date.from(reservation.getDateDeReservation().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }
        // Convertir dateDuPretPrevue
        if (reservation.getDateDuPretPrevue() != null) {
            reservationDisplay.put("dateDuPretPrevueAsDate",
                    Date.from(reservation.getDateDuPretPrevue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }
        // Convertir dateLimiteRecuperation et calculer isLateValidation
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
}
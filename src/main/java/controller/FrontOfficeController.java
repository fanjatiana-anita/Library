package controller;

import model.Livre;
import model.Reservation;
import model.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import service.LivreService;
import service.ReservationService;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/frontoffice")
public class FrontOfficeController {

    @Autowired
    private LivreService livreService;

    @Autowired
    private ReservationService reservationService;

    @GetMapping({"", "/accueil"})
    public String home(Model model, HttpSession session) {
        UserAccount user = (UserAccount) session.getAttribute("user");
        System.out.println("Home: user=" + (user != null ? user.getLogin() : "null"));
        if (user == null || !"MEMBRE".equals(user.getRole())) {
            model.addAttribute("error", "Vous devez être connecté en tant que membre pour accéder à cette page.");
            return "redirect:/login";
        }
        model.addAttribute("login", user.getLogin());
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
        Integer idExemplaire = reservationService.findAvailableExemplaire(idLivre);
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
            @RequestParam("idExemplaire") Integer idExemplaire,
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
        ReservationService.ReservationResult result = reservationService.createReservation(
            login, idExemplaire, LocalDate.parse(dateReservation), LocalDate.parse(datePretPrevue));
        if (result.getReservation() == null) {
            model.addAttribute("error", result.getMessage());
            model.addAttribute("livres", livreService.findAll());
            return "frontOffice/reservations";
        }
        model.addAttribute("reservation", result.getReservation());
        model.addAttribute("message", result.getMessage());
        return "frontOffice/detailsReservation";
    }
}

package controller;

import jakarta.servlet.http.HttpSession;
import model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import repository.ProlongementRepository;
import repository.PretRepository;
import service.PretService;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/backoffice")
public class BackOfficeProlongementController {

    @Autowired
    private ProlongementRepository prolongementRepository;

    @Autowired
    private PretRepository pretRepository;

    @Autowired
    private PretService pretService;

    // Liste des prolongements à valider
    @GetMapping("/prolongements")
    public String listProlongementsAValider(Model model, HttpSession session) {
        List<Prolongement> prolongements = prolongementRepository.findByStatutProlongement(Prolongement.StatutProlongementEnum.EN_ATTENTE);
        model.addAttribute("prolongements", prolongements);
        return "backOffice/listeProlongementsAValider";
    }

    // Liste des prolongements validés
    @GetMapping("/prolongementsValides")
    public String listProlongementsValides(Model model, HttpSession session) {
        List<Prolongement> prolongements = prolongementRepository.findByStatutProlongement(Prolongement.StatutProlongementEnum.VALIDE);
        model.addAttribute("prolongements", prolongements);
        return "backOffice/listeProlongementsValides";
    }

    // Formulaire de validation/refus
    @GetMapping("/formValidationProlongement")
    public String formValidationProlongement(@RequestParam("idProlongement") Integer idProlongement, Model model) {
        Prolongement prolongement = prolongementRepository.findById(idProlongement).orElse(null);
        if (prolongement == null) {
            model.addAttribute("error", "Prolongement non trouvé.");
            return "redirect:/backoffice/prolongements";
        }
        model.addAttribute("prolongement", prolongement);
        model.addAttribute("dateValidation", LocalDate.now());
        return "backOffice/formValidationProlongement";
    }

    // Traitement de la validation/refus
    @PostMapping("/processValidationProlongement")
    public String processValidationProlongement(
            @RequestParam("idProlongement") Integer idProlongement,
            @RequestParam("dateValidation") String dateValidation,
            @RequestParam("action") String action,
            Model model) {
        try {
            String message;
            if ("valider".equals(action)) {
                message = pretService.validerProlongement(idProlongement, LocalDate.parse(dateValidation));
            } else if ("refuser".equals(action)) {
                message = pretService.refuserProlongement(idProlongement);
            } else {
                message = "Action inconnue.";
            }
            if (!message.contains("validé") && !message.contains("refusé")) {
                model.addAttribute("error", message);
                return "backOffice/formValidationProlongement";
            }
            model.addAttribute("message", message);
            return "redirect:/backoffice/prolongements";
        } catch (Exception e) {
            model.addAttribute("error", "Erreur : " + e.getMessage());
            return "backOffice/formValidationProlongement";
        }
    }
}

package controller;

import jakarta.servlet.http.HttpSession;
import model.UserAccount;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/backoffice")
public class BackOfficeController {

    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        UserAccount user = (UserAccount) session.getAttribute("user");
        if (user == null || !"BIBLIOTHECAIRE".equals(user.getRole())) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        return "backOffice/dashboard";
    }
}
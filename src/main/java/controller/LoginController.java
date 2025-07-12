package controller;

import model.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import service.UserAccountService;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

    @Autowired
    private UserAccountService userAccountService;

    @GetMapping("/login")
    public String showLoginForm() {
        return "frontOffice/loginForm";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String login, @RequestParam String password, HttpSession session, Model model) {
        UserAccount user = userAccountService.findByLogin(login);
        if (user != null && user.getMotDePasse().equals(password)) {
            session.setAttribute("user", user);
            if ("MEMBRE".equals(user.getRole())) {
                return "redirect:/frontoffice/accueil";
            } else if ("BIBLIOTHECAIRE".equals(user.getRole())) {
                return "redirect:/backoffice/dashboard";
            }
        }
        model.addAttribute("error", "Identifiants incorrects");
        return "frontOffice/loginForm";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
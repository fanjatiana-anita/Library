package controller;

import jakarta.servlet.http.HttpSession;
import model.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import service.UserAccountService;

@Controller
public class LoginController {

    @Autowired
    private UserAccountService userAccountService;

  
}
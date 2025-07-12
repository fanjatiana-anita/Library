package controller;

import model.Adherent;
import model.Personne;
import model.Profil;
import model.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import service.*;

import java.time.LocalDate;

@Controller
public class InscriptionController {

    @Autowired private PersonneService personneService;
    @Autowired private UserAccountService userAccountService;
    @Autowired private AdherentService adherentService;
    @Autowired private ProfilService profilService;

   
}
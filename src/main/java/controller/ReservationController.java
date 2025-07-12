package controller;

import jakarta.servlet.http.HttpSession;
import model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import service.*;
import repository.ExemplaireRepository;

import java.time.LocalDate;
import java.util.List;

@Controller
// @RequestMapping("/frontoffice")
public class ReservationController {

    @Autowired private LivreService livreService;
    @Autowired private ReservationService reservationService;
    @Autowired private AdherentService adherentService;
    @Autowired private ExemplaireRepository exemplaireRepository;

   
}
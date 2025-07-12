package service;

import jakarta.transaction.Transactional;
import model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.*;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
public class ReservationService {

    @Autowired private ReservationRepository reservationRepository;
    @Autowired private ExemplaireRepository exemplaireRepository;
    @Autowired private AdherentRepository adherentRepository;
    @Autowired private PenalisationRepository penalisationRepository;
    @Autowired private RestrictionProfilLivreRepository restrictionProfilLivreRepository;
    
}
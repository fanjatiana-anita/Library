package repository;

import model.Penalisation;
import model.Adherent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PenalisationRepository extends JpaRepository<Penalisation, Integer> {
    List<Penalisation> findByAdherentAndDateFinPenalisationAfter(Adherent adherent, LocalDate date);
    List<Penalisation> findByDateFinPenalisationAfter(LocalDate date);
}

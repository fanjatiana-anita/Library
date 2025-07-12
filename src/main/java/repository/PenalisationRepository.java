package repository;

import model.Penalisation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface PenalisationRepository extends JpaRepository<Penalisation, Integer> {
    List<Penalisation> findByAdherentIdAdherentAndDateFinPenalisationAfter(Integer idAdherent, LocalDate date);
}
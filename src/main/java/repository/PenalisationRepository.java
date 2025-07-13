package repository;

import model.Adherent;
import model.Penalisation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PenalisationRepository extends JpaRepository<Penalisation, Integer> {
    List<Penalisation> findByAdherentAndDateFinPenalisationAfter(Adherent adherent, LocalDate date);
}
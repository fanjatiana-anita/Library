package repository;

import model.Abonnement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AbonnementRepository extends JpaRepository<Abonnement, Integer> {
    List<Abonnement> findByAdherentIdAdherentAndDateFinAfter(Integer idAdherent, LocalDate date);
}
package repository;

import model.Prolongement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProlongementRepository extends JpaRepository<Prolongement, Integer> {
    // List<Prolongement> findByStatutProlongement(Prolongement.StatutProlongement statut);
}
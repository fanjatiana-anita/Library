package repository;

import model.Pret;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PretRepository extends JpaRepository<Pret, Integer> {
    // List<Pret> findByAdherentIdAdherentAndDateDeRetourReelleIsNull(Integer idAdherent);
}
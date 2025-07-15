package repository;

import model.Adherent;
import model.Pret;
import model.Prolongement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProlongementRepository extends JpaRepository<Prolongement, Integer> {
    List<Prolongement> findByAdherent(Adherent adherent);
    List<Prolongement> findByStatutProlongement(Prolongement.StatutProlongementEnum statut);
    List<Prolongement> findByPret(Pret pret);
    List<Prolongement> findByPretAndStatutProlongement(Pret pret, Prolongement.StatutProlongementEnum statut);
}
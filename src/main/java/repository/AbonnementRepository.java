package repository;

import model.Abonnement;
import model.Adherent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AbonnementRepository extends JpaRepository<Abonnement, Integer> {
    List<Abonnement> findByAdherentOrderByDateDebutDesc(Adherent adherent);
}
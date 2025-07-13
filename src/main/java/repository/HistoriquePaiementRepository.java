package repository;

import model.HistoriquePaiement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoriquePaiementRepository extends JpaRepository<HistoriquePaiement, Integer> {
}
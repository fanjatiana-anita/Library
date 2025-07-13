package repository;

import model.Adherent;
import model.Exemplaire;
import model.Pret;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PretRepository extends JpaRepository<Pret, Integer> {
    List<Pret> findByAdherentAndDateDeRetourReelleIsNull(Adherent adherent);
    List<Pret> findByAdherentAndDateDeRetourReelleIsNullAndDateDeRetourPrevueBefore(Adherent adherent, LocalDate date);
    Pret findByAdherentAndExemplaireAndDateDeRetourReelleIsNull(Adherent adherent, Exemplaire exemplaire);
}
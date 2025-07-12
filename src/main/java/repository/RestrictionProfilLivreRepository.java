package repository;

import model.RestrictionProfilLivre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestrictionProfilLivreRepository extends JpaRepository<RestrictionProfilLivre, Integer> {
    RestrictionProfilLivre findByLivreIdLivreAndProfilIdProfil(Integer idLivre, Integer idProfil);
}
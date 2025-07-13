package repository;

import model.Livre;
import model.RestrictionProfilLivre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestrictionProfilLivreRepository extends JpaRepository<RestrictionProfilLivre, Integer> {
    List<RestrictionProfilLivre> findByLivre(Livre livre);
}
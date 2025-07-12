package repository;

import model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ProfilRepository extends JpaRepository<Profil, Integer> {
    Profil findByIdProfil(Integer idProfil);
}
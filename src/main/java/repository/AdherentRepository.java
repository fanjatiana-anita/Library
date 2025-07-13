package repository;

import model.Adherent;
import model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdherentRepository extends JpaRepository<Adherent, Integer> {
    Adherent findByUserAccount(UserAccount userAccount);
    Adherent findByUserAccountLogin(String login);
}
package repository;

import model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount, Integer> {

    Optional<UserAccount> findByLoginAndMotDePasse(String login, String motDePasse);
    Optional<UserAccount> findByLogin(String login);
}
package service;

import jakarta.transaction.Transactional;
import model.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.UserAccountRepository;

@Service
public class UserAccountService {

    @Autowired
    private UserAccountRepository userAccountRepository;

    public void save(UserAccount userAccount) {
        userAccountRepository.save(userAccount);
    }

    @Transactional
    public UserAccount authenticate(String login, String password) {
        return userAccountRepository.findByLoginAndMotDePasse(login, password)
                   .filter(UserAccount::isEstActif)
                   .orElse(null);
    }
}
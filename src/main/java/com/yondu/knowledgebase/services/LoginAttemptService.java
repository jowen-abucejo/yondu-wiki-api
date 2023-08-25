package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.entities.LoginAttempt;
import com.yondu.knowledgebase.entities.User;
import com.yondu.knowledgebase.repositories.LoginAttemptRepository;
import com.yondu.knowledgebase.repositories.UserRepository;
import org.apache.juli.logging.Log;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LoginAttemptService {
    private final UserRepository userRepository;

    private final LoginAttemptRepository loginAttemptRepository;

    public LoginAttemptService(UserRepository userRepository, LoginAttemptRepository loginAttemptRepository) {
        this.userRepository = userRepository;
        this.loginAttemptRepository = loginAttemptRepository;
    }

    public void logLoginAttempt(User user) {

        LoginAttempt userAttempt = loginAttemptRepository.findByUser(user);

        if (userAttempt!=null) {
            userAttempt.setLastAttempt(LocalDateTime.now());
            userAttempt.setAttempts(userAttempt.getAttempts()+1);
            userAttempt.setRestricted(false);
            loginAttemptRepository.save(userAttempt);

            if(userAttempt.getAttempts()==5) {
                userAttempt.setRemoveRestriction(userAttempt.getLastAttempt().plusMinutes(30));
                userAttempt.setRestricted(true);
                loginAttemptRepository.save(userAttempt);
            }
        } else  {
            LoginAttempt loginAttempt = new LoginAttempt();
            loginAttempt.setUser(user);
            loginAttempt.setLastAttempt(LocalDateTime.now());
            loginAttempt.setAttempts(1);
            loginAttempt.setRestricted(false);
            loginAttemptRepository.save(loginAttempt);
        }
    }
    public void resetAttempts(User user) {
        LoginAttempt attempt = loginAttemptRepository.findByUser(user);
        attempt.setAttempts(0);
        attempt.setLastAttempt(null);
        attempt.setRestricted(false);
        attempt.setRemoveRestriction(null);
        loginAttemptRepository.save(attempt);
    }
}

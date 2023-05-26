package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.Utils.Util;
import com.yondu.knowledgebase.entities.User;
import com.yondu.knowledgebase.exceptions.InvalidCredentialsException;
import com.yondu.knowledgebase.exceptions.MissingFieldException;
import com.yondu.knowledgebase.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private Logger log = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User login(User user) throws MissingFieldException, InvalidCredentialsException, Exception{
        log.info("AuthService.login()");
        log.info("user : " + user.toString());

        // Check data
        if(Util.isNullOrWhiteSpace(user.getEmail())){
            throw new MissingFieldException("email");
        }else if(Util.isNullOrWhiteSpace(user.getPassword())){
            throw new MissingFieldException("password");
        }

        User fetchedUser = userRepository.getUserByEmail(user.getEmail());
        if(fetchedUser == null){
            throw new InvalidCredentialsException();
        }

        if(passwordEncoder.matches(user.getPassword(), fetchedUser.getPassword())){
            fetchedUser.setPassword("");
            return fetchedUser;
        }else{
            throw new InvalidCredentialsException();
        }
    }
}

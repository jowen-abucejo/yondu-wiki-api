package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.email.EmailDTO;
import com.yondu.knowledgebase.DTO.user.UserDTO;
import com.yondu.knowledgebase.Utils.Util;
import com.yondu.knowledgebase.entities.User;
import com.yondu.knowledgebase.exceptions.InvalidCredentialsException;
import com.yondu.knowledgebase.exceptions.MissingFieldException;
import com.yondu.knowledgebase.exceptions.ResourceNotFoundException;
import com.yondu.knowledgebase.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private Logger log = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User login(UserDTO.LoginRequest request) {
        log.info("AuthService.login()");
        log.info("user : " + request);

        // Check data
        if(Util.isNullOrWhiteSpace(request.email())){
            throw new MissingFieldException("email");
        }else if(Util.isNullOrWhiteSpace(request.password())){
            throw new MissingFieldException("password");
        }

        User fetchedUser = userRepository.getUserByEmail(request.email());
        if(fetchedUser == null){
            throw new InvalidCredentialsException();
        }

        if(passwordEncoder.matches(request.password(), fetchedUser.getPassword())){
            fetchedUser.setPassword("");
            return fetchedUser;
        }else{
            throw new InvalidCredentialsException();
        }
    }


    public boolean checkEmail(UserDTO.ShortRequest request) {
        log.info("UserService.checkEmail()");
        log.info("request : " + request);

        userRepository.fetchUserByEmail(request.email()).orElseThrow(() -> new ResourceNotFoundException("User not found."));
        return true;
    }

    public void forgotPassword(UserDTO.ShortRequest request) {
        log.info("UserService.forgotPassword()");
        log.info("request : " + request);

        User user = userRepository.findByEmail(request.email()).orElseThrow(() -> new ResourceNotFoundException("User not found."));

        String temporaryPassword = Util.passwordGenerator();
        String encodedPassword = passwordEncoder.encode(temporaryPassword);

        user.setPassword(encodedPassword);
        user.setPasswordExpiration(LocalDateTime.now());

        userRepository.save(user);

        emailService.forgotPasswordEmail(new EmailDTO.NewUserRequest(user.getEmail(), null, temporaryPassword, "FORGOT-PASSWORD"));
    }
}

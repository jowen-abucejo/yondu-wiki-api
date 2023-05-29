package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.Utils.Util;
import com.yondu.knowledgebase.entities.User;
import com.yondu.knowledgebase.enums.Status;
import com.yondu.knowledgebase.exceptions.InvalidEmailException;
import com.yondu.knowledgebase.exceptions.MissingFieldException;
import com.yondu.knowledgebase.exceptions.UserException;
import com.yondu.knowledgebase.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    public User createNewUser(User user) throws MissingFieldException, InvalidEmailException, Exception {
        log.info("UserService.createNewUser()");
        log.info("user : " + user.toString());

        // Check for nulls
        if (Util.isNullOrWhiteSpace(user.getEmail()))
            throw new MissingFieldException("email");
        else if (Util.isNullOrWhiteSpace(user.getUsername()))
            throw new MissingFieldException("username");
        else if (Util.isNullOrWhiteSpace(user.getPassword()))
            throw new MissingFieldException("password");
        else if (Util.isNullOrWhiteSpace(user.getFirstName()))
            throw new MissingFieldException("first name");
        else if (Util.isNullOrWhiteSpace(user.getLastName()))
            throw new MissingFieldException("last name");
        else if (!Util.isEmailValid(user.getEmail()))
            throw new InvalidEmailException();

        // Encrypt password
        String password = passwordEncoder.encode(user.getPassword());
        user.setPassword(password);
        user.setStatus(Status.ACTIVE.getCode());
        user.setCreatedAt(LocalDate.now());

        User createdUser = userRepository.save(user);
        createdUser.setPassword("");
        return createdUser;
    }

    public User deactivateUser(User user) throws UserException, MissingFieldException, Exception {
        log.info("UserService.deactivateUser()");
        log.info("user : " + user.toString());

        // Check for nulls
        if (Util.isNullOrWhiteSpace(user.getEmail()))
            throw new MissingFieldException("email");

        user = userRepository.getUserByEmail(user.getEmail());
        if (user == null) {
            throw new UserException("User not found.");
        }
        user.setStatus(Status.INACTIVE.getCode());

        User updatedUser = userRepository.save(user);
        updatedUser.setPassword("");

        return updatedUser;
    }

    @Override
    public User loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(
                String.format("Active user with email of %s not found", email)));
    }

    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        if(user.isEmpty()){
            throw new UserException("User Not Found");
        }else{
            return user.get();
        }
    }
}

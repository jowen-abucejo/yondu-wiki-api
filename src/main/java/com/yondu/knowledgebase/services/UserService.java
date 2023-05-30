package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.page.PaginatedResponse;
import com.yondu.knowledgebase.DTO.user.UserDTO;
import com.yondu.knowledgebase.DTO.user.UserDTOMapper;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    public User createNewUser(UserDTO.GeneralInfo user) throws MissingFieldException, InvalidEmailException, Exception {
        log.info("UserService.createNewUser()");
        log.info("user : " + user.toString());

        // Check for nulls
        if (Util.isNullOrWhiteSpace(user.email()))
            throw new MissingFieldException("email");
        else if (Util.isNullOrWhiteSpace(user.username()))
            throw new MissingFieldException("username");
        else if (Util.isNullOrWhiteSpace(user.password()))
            throw new MissingFieldException("password");
        else if (Util.isNullOrWhiteSpace(user.firstName()))
            throw new MissingFieldException("first name");
        else if (Util.isNullOrWhiteSpace(user.lastName()))
            throw new MissingFieldException("last name");
        else if (!Util.isEmailValid(user.email()))
            throw new InvalidEmailException();

        User checkUser = userRepository.findByEmail(user.email()).orElse(null);
        if(checkUser != null){
            throw new UserException("The user " + user.email() + " is already existing in the system.");
        }

        User newUser = new User(user);

        // Encrypt password
        String password = passwordEncoder.encode(user.password());
        newUser.setPassword(password);
        newUser.setStatus(Status.ACTIVE.getCode());
        newUser.setCreatedAt(LocalDate.now());

        User createdUser = userRepository.save(newUser);
        createdUser.setPassword("");
        return createdUser;
    }

    public User deactivateUser(UserDTO.GeneralInfo user) throws UserException, MissingFieldException, Exception {
        log.info("UserService.deactivateUser()");
        log.info("user : " + user.toString());

        // Check for nulls
        if (Util.isNullOrWhiteSpace(user.email()))
            throw new MissingFieldException("email");

        User oldUser = userRepository.getUserByEmail(user.email());
        if (oldUser == null) {
            throw new UserException("User not found.");
        }
        oldUser.setStatus(Status.INACTIVE.getCode());

        User updatedUser = userRepository.save(oldUser);
        updatedUser.setPassword("");

        return updatedUser;
    }

    @Override
    public User loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(
                String.format("Active user with email of %s not found", email)));
    }

//    public List<User> getAllUser() {
//        return userRepository.findAll();
//    }

    public PaginatedResponse<UserDTO.GeneralResponse> getAllUser(String searchKey, int page, int size) throws Exception {
        log.info("UserService.getAllUser()");
        log.info("searchKey : " + searchKey);
        log.info("page : " + page);
        log.info("size : " + size);

        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<User> userPages = userRepository.findAll(searchKey, pageRequest);
        List<User> users = userPages.getContent();

        List<UserDTO.GeneralResponse> userDTOs = users.stream()
                .map(user -> UserDTOMapper.mapToGeneralResponse(user))
                .collect(Collectors.toList());

        PaginatedResponse<UserDTO.GeneralResponse> paginatedResponse = new PaginatedResponse<UserDTO.GeneralResponse>(userDTOs, page, size, (long)userDTOs.size());

        return paginatedResponse;
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

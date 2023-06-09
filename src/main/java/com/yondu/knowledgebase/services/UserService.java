package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.page.PaginatedResponse;
import com.yondu.knowledgebase.DTO.user.UserDTO;
import com.yondu.knowledgebase.DTO.user.UserDTOMapper;
import com.yondu.knowledgebase.Utils.Util;
import com.yondu.knowledgebase.entities.User;
import com.yondu.knowledgebase.enums.Status;
import com.yondu.knowledgebase.exceptions.*;
import com.yondu.knowledgebase.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired PasswordChangesService passwordChangesService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    public User createNewUser(UserDTO.WithRolesRequest user) {
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

        userRepository.save(newUser);
        User createdUser = userRepository.fetchUserByEmail(newUser.getEmail()).orElseThrow(() -> new UserException("Cannot find user."));
        return createdUser;
    }

    public User deactivateUser(UserDTO.ShortRequest user) {
        log.info("UserService.deactivateUser()");
        log.info("user : " + user.toString());

        // Check for nulls
        if (Util.isNullOrWhiteSpace(user.email()))
            throw new MissingFieldException("email");

        User oldUser = userRepository.fetchUserByEmail(user.email()).orElseThrow(() -> new UserException("User not found"));
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

    public PaginatedResponse<UserDTO.WithRolesResponse> getAllUser(String searchKey, int page, int size) {
        log.info("UserService.getAllUser()");
        log.info("searchKey : " + searchKey);
        log.info("page : " + page);
        log.info("size : " + size);

        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<User> userPages = userRepository.findAll(searchKey, pageRequest);
        List<User> users = userPages.getContent();

        if(users.isEmpty()){
            throw new NoContentException("No content found");
        }

        List<UserDTO.WithRolesResponse> userDTOs = users.stream()
                .map(user -> UserDTOMapper.mapToWithRolesResponse(user))
                .collect(Collectors.toList());

        PaginatedResponse<UserDTO.WithRolesResponse> paginatedResponse = new PaginatedResponse<UserDTO.WithRolesResponse>(userDTOs, page, size, (long)userDTOs.size());

        return paginatedResponse;
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User Not Found"));
    }

    public User changeProfilePhoto(MultipartFile file) {
        log.info("UserService.changeProfilePhoto()");
        log.info("file : " + file);

        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Upload photo to S3, retrieves path.
        user.setProfilePhoto("");

        userRepository.save(user);
        return user;
    }

    public User updatePassword(UserDTO.ChangePassRequest request) {
        log.info("UserService.updatePassword()");
        log.info("request : " + request);

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(passwordEncoder.matches(request.oldPassword(), user.getPassword())){
            String newEncodedPassword = passwordEncoder.encode(request.newPassword());
            user.setPassword(newEncodedPassword);
        }else{
            throw new InvalidCredentialsException();
        }

        if(passwordChangesService.isPasswordExist(user, request.newPassword())){
           throw new PasswordRepeatException();
        }

        User updatedUser = userRepository.save(user);
        passwordChangesService.saveNewPassword(user, request.newPassword());
        return updatedUser;
    }

    public User viewMyProfile() {
        log.info("UserService.viewMyProfile()");

        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}

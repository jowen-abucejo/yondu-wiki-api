package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.page.PaginatedResponse;
import com.yondu.knowledgebase.DTO.user.UserDTO;
import com.yondu.knowledgebase.DTO.user.UserDTOMapper;
import com.yondu.knowledgebase.Utils.Util;
import com.yondu.knowledgebase.entities.Role;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
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
        else if(user.roles().isEmpty())
            throw new MissingFieldException("role");

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

        return userRepository.save(oldUser);
    }

    public User activateUser(UserDTO.ShortRequest user) {
        log.info("UserService.activateUser()");
        log.info("user : " + user.toString());

        // Check for nulls
        if (Util.isNullOrWhiteSpace(user.email()))
            throw new MissingFieldException("email");
        System.out.println(user.email());
        User oldUser = userRepository.fetchUserInactiveByEmail(user.email()).orElseThrow(() -> new UserException("User not found"));
        System.out.println(user.email());
        oldUser.setStatus(Status.ACTIVE.getCode());

        return userRepository.save(oldUser);
    }

    public User updateUser(long id, UserDTO.WithRolesRequest user) {
        log.info("UserService.updateUser()");
        log.info("id : " + id);
        log.info("user : " + user);

        User fetchedUser = userRepository.findById(id).orElseThrow(() -> new UserException("User not found."));

        if(!Util.isNullOrWhiteSpace(user.email())) {
            if (!Util.isEmailValid(user.email()))
                throw new InvalidEmailException();

            fetchedUser.setEmail(user.email());
        }

        if(!Util.isNullOrWhiteSpace(user.username())){
            fetchedUser.setUsername(user.username());
        }

        if(!Util.isNullOrWhiteSpace(user.password())){
            String encryptPassword = passwordEncoder.encode(user.password());
            fetchedUser.setPassword(encryptPassword);
        }

        if(!Util.isNullOrWhiteSpace(user.firstName())){
            fetchedUser.setFirstName(user.firstName());
        }

        if(!Util.isNullOrWhiteSpace(user.lastName())){
            fetchedUser.setLastName(user.lastName());
        }

        if(!Util.isNullOrWhiteSpace(user.profilePhoto())) {
            fetchedUser.setProfilePhoto(user.profilePhoto());
        }

        if(!Util.isNullOrWhiteSpace(user.position())){
            fetchedUser.setPosition(user.position());
        }

        if(user.roles() != null){
            if(user.roles().isEmpty()) {
                throw new MissingFieldException("role");
            }
            else{
                Set<Role> roles = user.roles()
                        .stream()
                        .map(r -> new Role(r.getId()))
                        .collect(Collectors.toSet());

                fetchedUser.setRole(roles);
            }
        }


        User updatedUser = userRepository.save(fetchedUser);
        return updatedUser;
    }

    @Override
    public User loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(
                String.format("Active user with email of %s not found", email)));
    }

    public PaginatedResponse<UserDTO.WithRolesResponse> getAllUser(String searchKey, String statusFilter, String roleFilter, int page, int size) {
        log.info("UserService.getAllUser()");
        log.info("searchKey: " + searchKey);
        log.info("statusFilter: " + statusFilter);
        log.info("roleFilter: " + roleFilter);
        log.info("page: " + page);
        log.info("size: " + size);

        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<User> userPages;

        if (!statusFilter.isEmpty() && !roleFilter.isEmpty()) {
            userPages = userRepository.findAllByFullNameAndStatusAndRole(searchKey, statusFilter, roleFilter, pageRequest);
        } else if (!statusFilter.isEmpty()) {
            userPages = userRepository.findAllByFullNameAndStatus(searchKey, statusFilter, pageRequest);
        } else if (!roleFilter.isEmpty()) {
            userPages = userRepository.findAllByFullNameAndRole(searchKey, roleFilter, pageRequest);
        } else if(!searchKey.isEmpty()){
            userPages = userRepository.findAllByFullName(searchKey, pageRequest);
        }else{
            userPages = userRepository.findAll(pageRequest);
        }

        List<User> users = userPages.getContent();

        if (users.isEmpty()) {
            throw new NoContentException("No content found");
        }

        List<UserDTO.WithRolesResponse> userDTOs = users.stream()
                .map(user -> UserDTOMapper.mapToWithRolesResponse(user))
                .collect(Collectors.toList());

        PaginatedResponse<UserDTO.WithRolesResponse> paginatedResponse = new PaginatedResponse<>(userDTOs, page, size, (long) userPages.getTotalPages());

        return paginatedResponse;
    }


    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User Not Found"));
    }

    public User changeProfilePhoto(UserDTO.ChangePhotoRequest path) {
        log.info("UserService.changeProfilePhoto()");
        log.info("path : " + path);

        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        user.setProfilePhoto(path.path());

        return userRepository.save(user);
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

    public PaginatedResponse<UserDTO.ShortResponse> getUsersByPermission(String searchKey, int page, int size, Long permissionId) {
        log.info("UserService.getUsersByPermission()");
        log.info("searchKey : " + searchKey);
        log.info("page : " + page);
        log.info("size : " + size);
        log.info("permissionId : " + permissionId);

        PageRequest pageRequest = PageRequest.of(page - 1, size);

        Page<User> users = userRepository.findAllByFullNameAndHasPermission(searchKey, permissionId, pageRequest);
        if(users.isEmpty()){
            throw new NoContentException("No users found...");
        }

        List<UserDTO.ShortResponse> userDTOs = users
                .get()
                .map(user -> UserDTOMapper.mapToShortResponse(user))
                .collect(Collectors.toList());
        return new PaginatedResponse<>(userDTOs, page, size, users.getTotalElements());
    }
}

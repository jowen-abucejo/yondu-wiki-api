package com.yondu.knowledgebase.controllers;

import com.yondu.knowledgebase.DTO.ApiResponse;
import com.yondu.knowledgebase.DTO.page.PaginatedResponse;
import com.yondu.knowledgebase.DTO.user.UserDTO;
import com.yondu.knowledgebase.DTO.user.UserDTOMapper;
import com.yondu.knowledgebase.Utils.Util;
import com.yondu.knowledgebase.entities.User;
import com.yondu.knowledgebase.exceptions.InvalidEmailException;
import com.yondu.knowledgebase.exceptions.MissingFieldException;
import com.yondu.knowledgebase.exceptions.UserException;
import com.yondu.knowledgebase.repositories.UserRepository;
import com.yondu.knowledgebase.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("user")
public class UserController {

    private final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("")
    public ResponseEntity<?> getAllUser(@RequestParam(defaultValue = "") String searchKey, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "30") int size){
        log.info("UserController.getAllUser()");
        log.info("searchKey : " + searchKey);
        log.info("page : " + page);
        log.info("size : " + size);

        ResponseEntity response = null;

        searchKey = "%" + searchKey + "%";

        PaginatedResponse<UserDTO.GeneralResponse> fetchedUsers = userService.getAllUser(searchKey, page, size);

        ApiResponse apiResponse = ApiResponse.success(fetchedUsers, "success");
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id){
        log.info("UserController.getUserById()");
        log.info("id : " + id);

        User user = userService.getUserById(id);

        UserDTO.GeneralResponse userDTO = UserDTOMapper.mapToGeneralResponse(user);

        ApiResponse apiResponse = ApiResponse.success(userDTO, "success");
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('CREATE_USERS')")
    public ResponseEntity<?> createNewUser(@RequestBody com.yondu.knowledgebase.DTO.user.UserDTO.GeneralInfo user) {
        log.info("UserController.createNewUser()");
        log.info("user : " + user.toString());

        User createdUser = userService.createNewUser(user);
        com.yondu.knowledgebase.DTO.user.UserDTO.GeneralResponse userResponse = UserDTOMapper.mapToGeneralResponse(createdUser);
        ApiResponse apiResponse = ApiResponse.success(userResponse, "success");

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @PostMapping("/deactivate")
    @PreAuthorize("hasAuthority('DEACTIVATE_USERS')")
    public ResponseEntity<?> deactivateUser(@RequestBody com.yondu.knowledgebase.DTO.user.UserDTO.GeneralInfo user) {
        log.info("UserController.deactivateUser()");
        log.info("user : " + user.toString());

        User deactivatedUser = userService.deactivateUser(user);

        com.yondu.knowledgebase.DTO.user.UserDTO.GeneralResponse userResponse = UserDTOMapper.mapToGeneralResponse(deactivatedUser);
        ApiResponse apiResponse = ApiResponse.success(userResponse, "success");

        return ResponseEntity.ok(apiResponse);
    }
}

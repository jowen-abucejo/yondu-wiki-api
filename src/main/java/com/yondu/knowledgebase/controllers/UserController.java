package com.yondu.knowledgebase.controllers;

import com.yondu.knowledgebase.DTO.ApiResponse;
import com.yondu.knowledgebase.DTO.page.PaginatedResponse;
import com.yondu.knowledgebase.DTO.user.UserDTO;
import com.yondu.knowledgebase.DTO.user.UserDTOMapper;
import com.yondu.knowledgebase.Utils.Util;
import com.yondu.knowledgebase.entities.User;
import com.yondu.knowledgebase.enums.Permission;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public ResponseEntity<ApiResponse<PaginatedResponse<UserDTO.WithRolesResponse>>> getAllUser(@RequestParam(defaultValue = "") String searchKey, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "30") int size){
        log.info("UserController.getAllUser()");
        log.info("searchKey : " + searchKey);
        log.info("page : " + page);
        log.info("size : " + size);

        ResponseEntity response = null;

        searchKey = "%" + searchKey + "%";

        PaginatedResponse<UserDTO.WithRolesResponse> fetchedUsers = userService.getAllUser(searchKey, page, size);

        ApiResponse apiResponse = ApiResponse.success(fetchedUsers, "success");
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO.WithRolesResponse>> getUserById(@PathVariable Long id){
        log.info("UserController.getUserById()");
        log.info("id : " + id);

        User user = userService.getUserById(id);

        UserDTO.WithRolesResponse userDTO = UserDTOMapper.mapToWithRolesResponse(user);

        ApiResponse apiResponse = ApiResponse.success(userDTO, "success");
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('CREATE_USERS')")
    public ResponseEntity<ApiResponse<UserDTO.WithRolesResponse>> createNewUser(@RequestBody UserDTO.WithRolesRequest user) {
        log.info("UserController.createNewUser()");
        log.info("user : " + user.toString());

        User createdUser = userService.createNewUser(user);
        UserDTO.WithRolesResponse userResponse = UserDTOMapper.mapToWithRolesResponse(createdUser);
        ApiResponse apiResponse = ApiResponse.success(userResponse, "success");

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @PostMapping("/deactivate")
    @PreAuthorize("hasAuthority('DEACTIVATE_USERS')")
    public ResponseEntity<ApiResponse<UserDTO.WithRolesResponse>> deactivateUser(@RequestBody UserDTO.ShortRequest user) {
        log.info("UserController.deactivateUser()");
        log.info("user : " + user.toString());

        User deactivatedUser = userService.deactivateUser(user);

        UserDTO.GeneralResponse userResponse = UserDTOMapper.mapToGeneralResponse(deactivatedUser);
        ApiResponse apiResponse = ApiResponse.success(userResponse, "success");

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/activate")
    @PreAuthorize("hasAuthority('DEACTIVATE_USERS')")
    public ResponseEntity<ApiResponse<UserDTO.WithRolesResponse>> activateUser(@RequestBody UserDTO.ShortRequest user) {
        log.info("UserController.activateUser()");
        log.info("user : " + user.toString());

        User activatedUser = userService.activateUser(user);

        UserDTO.GeneralResponse userResponse = UserDTOMapper.mapToGeneralResponse(activatedUser);
        ApiResponse apiResponse = ApiResponse.success(userResponse, "success");

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("{id}/update")
    @PreAuthorize("hasAuthority('UPDATE_USERS')")
    public ResponseEntity<ApiResponse<UserDTO.WithRolesResponse>> updateUser(@PathVariable long id, @RequestBody UserDTO.WithRolesRequest user) {
        log.info("UserController.updateUser()");
        log.info("id : " + id);
        log.info("user : " + user);

        User updatedUser = userService.updateUser(id, user);
        UserDTO.WithRolesResponse response = UserDTOMapper.mapToWithRolesResponse(updatedUser);

        return ResponseEntity.ok(ApiResponse.success(response, "success"));
    }

    /*
     *  TODO
     *   Create endpoint uploading new profile picture
     *   Wait for S3 Bucket, Secret, and Region
     */
    @PostMapping("/update/photo")
    public ResponseEntity<ApiResponse<UserDTO.GeneralResponse>> changeProfilePhoto(@RequestParam("file") MultipartFile file) {
        log.info("UserController.changeProfilePhoto()");
        log.info("file : " + file);

        User userWithPhotoPath = userService.changeProfilePhoto(file);
        UserDTO.GeneralResponse response = UserDTOMapper.mapToGeneralResponse(userWithPhotoPath);

        return ResponseEntity.ok(ApiResponse.success(response, file.getOriginalFilename()));
    }

    @PostMapping("/update/password")
    public ResponseEntity<ApiResponse<UserDTO.BaseResponse>> updatePassword(@RequestBody UserDTO.ChangePassRequest request) {
        log.info("UserController.updatePassword");
        log.info("request : " + request);

        User user = userService.updatePassword(request);
        UserDTO.BaseResponse userResponse = UserDTOMapper.mapToBaseResponse(user);

        ApiResponse apiResponse = ApiResponse.success(userResponse, "Successfully changed password");
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserDTO.GeneralResponse>> viewMyProfile() {
        log.info("UserController.viewMyProfile()");

        User user = userService.viewMyProfile();
        UserDTO.GeneralResponse userDTO = UserDTOMapper.mapToGeneralResponse(user);
        ApiResponse response = ApiResponse.success(userDTO, "success");

        return ResponseEntity.ok(response);
    }
}

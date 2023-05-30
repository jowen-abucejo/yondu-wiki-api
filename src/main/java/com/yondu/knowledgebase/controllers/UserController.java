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

        try{
            PaginatedResponse<UserDTO.GeneralResponse> fetchedUsers = userService.getAllUser(searchKey, page, size);

            ApiResponse apiResponse = ApiResponse.success(fetchedUsers, "success");
            response = ResponseEntity.ok(apiResponse);
        }catch (Exception ex) {
            ex.printStackTrace();
            response = ResponseEntity.internalServerError().build();
        }

        return response;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id){
        log.info("UserController.getUserById()");
        log.info("id : " + id);

        ResponseEntity response = null;

        try{
            User user = userService.getUserById(id);

            if(user==null){
                response = ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }else{
                UserDTO.GeneralResponse userDTO = UserDTOMapper.mapToGeneralResponse(user);

                ApiResponse apiResponse = ApiResponse.success(userDTO, "success");
                response = ResponseEntity.ok(apiResponse);
            }

        }catch (Exception ex){
            ex.printStackTrace();
            response = ResponseEntity.internalServerError().build();
        }

        return response;
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('CREATE_USERS')")
    public ResponseEntity<?> createNewUser(@RequestBody com.yondu.knowledgebase.DTO.user.UserDTO.GeneralInfo user) {
        log.info("UserController.createNewUser()");
        log.info("user : " + user.toString());

        ResponseEntity response = null;

        try{
            User createdUser = userService.createNewUser(user);
            com.yondu.knowledgebase.DTO.user.UserDTO.GeneralResponse userResponse = UserDTOMapper.mapToGeneralResponse(createdUser);
            ApiResponse apiResponse = ApiResponse.success(userResponse, "success");

            response = ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
        }catch (InvalidEmailException invalidEmailException) {
            invalidEmailException.printStackTrace();

            Map<String, Object> errorMap = new HashMap<>();
            errorMap.put("error", invalidEmailException.getMessage());
            errorMap.put("date", Util.convertDate(Calendar.getInstance().getTime()));

            response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMap);
        }catch (MissingFieldException missingFieldException) {
            missingFieldException.printStackTrace();

            Map<String, Object> errorMap = new HashMap<>();
            errorMap.put("error", missingFieldException.getMessage());
            errorMap.put("date", Util.convertDate(Calendar.getInstance().getTime()));

            response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMap);
        }catch (UserException userException) {
            userException.printStackTrace();

            ApiResponse apiResponse = ApiResponse.error(userException.getMessage());
            response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
        }catch (Exception exception){
            exception.printStackTrace();

            Map<String, Object> errorMap = new HashMap<>();
            errorMap.put("error", exception.getMessage());
            errorMap.put("date", LocalDate.now());

            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMap);
        }

        return response;
    }

    @PostMapping("/deactivate")
    @PreAuthorize("hasAuthority('DEACTIVATE_USERS')")
    public ResponseEntity<?> deactivateUser(@RequestBody com.yondu.knowledgebase.DTO.user.UserDTO.GeneralInfo user) {
        log.info("UserController.deactivateUser()");
        log.info("user : " + user.toString());

        ResponseEntity response = null;
        Map<String, Object> errorMap = new HashMap<>();

        try{
            User deactivatedUser = userService.deactivateUser(user);

            com.yondu.knowledgebase.DTO.user.UserDTO.GeneralResponse userResponse = UserDTOMapper.mapToGeneralResponse(deactivatedUser);
            ApiResponse apiResponse = ApiResponse.success(userResponse, "success");

            response = ResponseEntity.ok(apiResponse);
        }catch(UserException userException) {
            userException.printStackTrace();

            errorMap.put("error", userException.getMessage());
            errorMap.put("date", LocalDate.now());

            response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMap);
        }catch (MissingFieldException missingFieldException) {
            missingFieldException.printStackTrace();

            errorMap.put("error", missingFieldException.getMessage());
            errorMap.put("date", LocalDate.now());

            response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMap);
        }catch (Exception ex) {
            ex.printStackTrace();

            errorMap.put("error", ex.getMessage());
            errorMap.put("date", LocalDate.now());

            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMap);
        }

        return response;
    }
}

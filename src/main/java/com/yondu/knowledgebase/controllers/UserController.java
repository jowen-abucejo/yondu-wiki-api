package com.yondu.knowledgebase.controllers;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("user")
public class UserController {

    private final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/create")
    public ResponseEntity<?> createNewUser(@RequestBody User user) {
        log.info("UserController.createNewUser()");
        log.info("user : " + user.toString());

        ResponseEntity response = null;

        try{
            User createdUser = userService.createNewUser(user);
            response = ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
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
    public ResponseEntity<?> deactivateUser(@RequestBody User user) {
        log.info("UserController.deactivateUser()");
        log.info("user : " + user.toString());

        ResponseEntity response = null;
        Map<String, Object> errorMap = new HashMap<>();

        try{
            User deactivatedUser = userService.deactivateUser(user);
            response = ResponseEntity.ok(deactivatedUser);
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

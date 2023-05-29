package com.yondu.knowledgebase.controllers;

import com.yondu.knowledgebase.DTO.ApiResponse;
import com.yondu.knowledgebase.DTO.UserDTO;
import com.yondu.knowledgebase.Utils.Util;
import com.yondu.knowledgebase.config.TokenUtil;
import com.yondu.knowledgebase.entities.User;
import com.yondu.knowledgebase.exceptions.InvalidCredentialsException;
import com.yondu.knowledgebase.exceptions.MissingFieldException;
import com.yondu.knowledgebase.services.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("auth")
public class AuthController {

    private final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @Autowired
    private TokenUtil tokenUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody com.yondu.knowledgebase.DTO.user.UserDTO.LoginRequest request) {
        log.info("AuthController.login()");
        log.info("user : " + request);
        ResponseEntity response = null;

        try{
            User fetchedUser = authService.login(request);
            UserDTO userDTO = new UserDTO(fetchedUser);
            String token = tokenUtil.generateToken(fetchedUser);

            Map<String, Object> res = new HashMap<>();
            res.put("user", userDTO);
            res.put("token", token);

            ApiResponse r = ApiResponse.success(res, "success");

            response = ResponseEntity.ok(r);
        }catch (MissingFieldException missingFieldException) {
            missingFieldException.printStackTrace();

            Map<String, Object> errorMap = new HashMap<>();
            errorMap.put("error", missingFieldException.getMessage());
            errorMap.put("date", Util.convertDate(Calendar.getInstance().getTime()));

            response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMap);
        }catch (InvalidCredentialsException invalidCredentialsException) {
            invalidCredentialsException.printStackTrace();

            Map<String, Object> errorMap = new HashMap<>();
            errorMap.put("error", invalidCredentialsException.getMessage());
            errorMap.put("date", Util.convertDate(Calendar.getInstance().getTime()));

            response = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMap);
        }catch (Exception ex){
            ex.printStackTrace();

            Map<String, Object> errorMap = new HashMap<>();
            errorMap.put("error", ex.getMessage());
            errorMap.put("date", Util.convertDate(Calendar.getInstance().getTime()));

            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMap);
        }

        return response;
    }
}

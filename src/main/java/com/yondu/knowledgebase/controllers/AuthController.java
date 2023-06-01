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

        User fetchedUser = authService.login(request);
        UserDTO userDTO = new UserDTO(fetchedUser);
        String token = tokenUtil.generateToken(fetchedUser);

        Map<String, Object> res = new HashMap<>();
        res.put("user", userDTO);
        res.put("token", token);

        ApiResponse apiResponse = ApiResponse.success(res, "success");
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/check")
    public ResponseEntity<?> checkEmail(@RequestBody com.yondu.knowledgebase.DTO.user.UserDTO.LoginRequest request) {
        log.info("AuthController.checkEmail()");
        log.info("request : " + request.toString());

        boolean isUserExist = authService.checkEmail(request); // Automatically throws an exception if user is not found.
        return ResponseEntity.ok(ApiResponse.success(request, "user exists."));
    }
}

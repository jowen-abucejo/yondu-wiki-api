package com.yondu.knowledgebase.controllers;

import com.yondu.knowledgebase.DTO.ApiResponse;
import com.yondu.knowledgebase.DTO.email.EmailDTO;
import com.yondu.knowledgebase.services.implementations.EmailServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/email")
public class EmailController {
    private final EmailServiceImpl emailService;

    public EmailController(EmailServiceImpl emailService) {
        this.emailService = emailService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<EmailDTO.BaseResponse>> sendEmailNotification (@RequestBody EmailDTO.GeneralRequest request){
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(emailService.sendEmail(request),"Email sent Successfully!"));
    }

    @PostMapping ("/new-users")
    public ResponseEntity<ApiResponse<EmailDTO.BaseResponse>> newUserEmailNotification (@RequestBody EmailDTO.NewUserRequest request){
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(emailService.createUserEmailNotification(request),"Email sent Successfully!"));
    }
}

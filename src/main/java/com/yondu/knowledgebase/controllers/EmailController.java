package com.yondu.knowledgebase.controllers;

import com.yondu.knowledgebase.DTO.ApiResponse;
import com.yondu.knowledgebase.DTO.email.EmailRequestDTO;
import com.yondu.knowledgebase.DTO.email.EmailResponseDTO;
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
    public ResponseEntity<ApiResponse<EmailResponseDTO>> sendEmailNotification (@RequestBody EmailRequestDTO emailRequestDTO){
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(emailService.sendEmail(emailRequestDTO),"Email sent Successfully!"));
    }
}

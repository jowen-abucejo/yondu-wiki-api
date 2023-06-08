package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.email.EmailRequestDTO;
import com.yondu.knowledgebase.DTO.email.EmailResponseDTO;

public interface EmailService {
    public EmailResponseDTO sendEmail (EmailRequestDTO emailRequestDTO);
}

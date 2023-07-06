package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.email.EmailDTO;

public interface EmailService {
    public EmailDTO.BaseResponse sendEmail (EmailDTO.GeneralRequest request);
    public EmailDTO.BaseResponse createUserEmailNotification (EmailDTO.NewUserRequest request);
    public EmailDTO.BaseResponse forgotPasswordEmail(EmailDTO.NewUserRequest request);
}

package com.yondu.knowledgebase.services.implementations;

import com.yondu.knowledgebase.DTO.email.EmailDTO;
import com.yondu.knowledgebase.DTO.email.EmailDTOMapper;
import com.yondu.knowledgebase.entities.User;
import com.yondu.knowledgebase.exceptions.InvalidNotificationTypeException;
import com.yondu.knowledgebase.repositories.UserRepository;
import com.yondu.knowledgebase.services.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final ThreadPoolTaskExecutor executor;
    private final UserRepository userRepository;

    public EmailServiceImpl(JavaMailSender mailSender, TemplateEngine templateEngine, ThreadPoolTaskExecutor executor, UserRepository userRepository) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.executor = executor;
        this.userRepository = userRepository;
    }

    @Override
    public EmailDTO.BaseResponse sendEmail (EmailDTO.GeneralRequest request){
        User toUser = userRepository.getUserByEmail(request.to());
        User fromUser = userRepository.getUserByEmail(request.from());

        CompletableFuture.runAsync(() -> {

            MimeMessage email = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(email, "UTF-8");

            Context context = new Context();
            context.setVariable("userProfileLink", request.fromProfile());
            context.setVariable("fromUser", request.from());
            context.setVariable("contentType", request.contentType().toLowerCase());
            context.setVariable("contentLink", request.contentLink().toLowerCase());

            Map<String, String> templateDetails = getEmailTemplateDetails(request.notificationType());

            try {
                String htmlContent = templateEngine.process(templateDetails.get("templateName"), context);
                helper.setTo(request.to());
                helper.setSubject(templateDetails.get("subjectText"));
                helper.setText(htmlContent, true);
                mailSender.send(email);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }, executor);

        return EmailDTOMapper.generalRequestToBaseResponse(request, toUser, fromUser);
    }

    @Override
    public EmailDTO.BaseResponse createUserEmailNotification (EmailDTO.NewUserRequest request){
        User toUser = userRepository.getUserByEmail(request.to());
        User fromUser = userRepository.getUserByEmail(request.from());

        CompletableFuture.runAsync(() -> {

            MimeMessage email = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(email, "UTF-8");

            Context context = new Context();
            context.setVariable("toUser", toUser.getFirstName().toUpperCase());
            context.setVariable("temporaryPassword", request.temporaryPassword());
            context.setVariable("userName", toUser.getEmail());

            Map<String, String> templateDetails = getEmailTemplateDetails(request.notificationType());

            try {
                String htmlContent = templateEngine.process(templateDetails.get("templateName"), context);
                helper.setTo(request.to());
                helper.setSubject(templateDetails.get("subjectText"));
                helper.setText(htmlContent, true);
                mailSender.send(email);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }, executor);

        return EmailDTOMapper.newUserRequestToBaseResponse(request, toUser, fromUser);
    }

    @Override
    public EmailDTO.BaseResponse forgotPasswordEmail(EmailDTO.NewUserRequest request) {
        User toUser = userRepository.getUserByEmail(request.to());
        User fromUser = userRepository.getUserByEmail(request.to());

        CompletableFuture.runAsync(() -> {

            MimeMessage email = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(email, "UTF-8");

            Context context = new Context();
            context.setVariable("toUser", toUser.getFirstName().toUpperCase());
            context.setVariable("temporaryPassword", request.temporaryPassword());

            Map<String, String> templateDetails = getEmailTemplateDetails(request.notificationType());

            try {
                String htmlContent = templateEngine.process(templateDetails.get("templateName"), context);
                helper.setTo(request.to());
                helper.setSubject(templateDetails.get("subjectText"));
                helper.setText(htmlContent, true);
                mailSender.send(email);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }, executor);

        return EmailDTOMapper.newUserRequestToBaseResponse(request, toUser, fromUser);
    }

    private Map<String, String> getEmailTemplateDetails(String notificationType) {
        Map<String, String> templateDetails = new HashMap<>();

        if (notificationType.equals("MENTION")) {
            templateDetails.put("templateName", "Mention");
            templateDetails.put("subjectText", "[MENTION] You have been mentioned!");
        } else if (notificationType.equals("COMMENT")) {
            templateDetails.put("templateName", "Comment");
            templateDetails.put("subjectText", "[COMMENT] Engaging discussion on your content!");
        } else if (notificationType.equals("RATE")) {
            templateDetails.put("templateName", "Rate");
            templateDetails.put("subjectText", "[RATE] Someone rated your content!");
        } else if (notificationType.equals("CONTENT")) {
            templateDetails.put("templateName", "Content");
            templateDetails.put("subjectText", "[UPDATE] See the latest update!");
        } else if (notificationType.equals("CREATION")) {
            templateDetails.put("templateName", "NewUser");
            templateDetails.put("subjectText", "[CREATED] Account Created");
        }else if(notificationType.equals("FORGOT-PASSWORD")){
            templateDetails.put("templateName", "ForgotPassword");
            templateDetails.put("subjectText", "[FORGOT PASSWORD] You forgot password!");
        }else {
            throw new InvalidNotificationTypeException("Invalid Notification Type");
        }

        return templateDetails;
    }
}

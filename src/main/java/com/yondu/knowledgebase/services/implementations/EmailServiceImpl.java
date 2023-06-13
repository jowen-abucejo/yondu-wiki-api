package com.yondu.knowledgebase.services.implementations;

import com.yondu.knowledgebase.DTO.email.EmailRequestDTO;
import com.yondu.knowledgebase.DTO.email.EmailResponseDTO;
import com.yondu.knowledgebase.exceptions.InvalidNotificationTypeException;
import com.yondu.knowledgebase.services.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final ThreadPoolTaskExecutor executor;

    public EmailServiceImpl(JavaMailSender mailSender, TemplateEngine templateEngine, ThreadPoolTaskExecutor executor) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.executor = executor;
    }

    @Override
    public EmailResponseDTO sendEmail (EmailRequestDTO emailRequestDTO){
        CompletableFuture.runAsync(() -> {
            MimeMessage email = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(email, "UTF-8");

            Context context = new Context();
            context.setVariable("userProfileLink",emailRequestDTO.getFromLink());
            context.setVariable("fromUser", emailRequestDTO.getFrom());
            context.setVariable("contentType", emailRequestDTO.getContentType().toLowerCase());
            context.setVariable("contentLink", emailRequestDTO.getContentLink());

            Map<String, String> templateDetails = getEmailTemplateDetails(emailRequestDTO.getNotificationType());

            try {
                String htmlContent = templateEngine.process(templateDetails.get("templateName"), context);
                helper.setTo(emailRequestDTO.getTo());
                helper.setSubject(templateDetails.get("subjectText"));
                helper.setText(htmlContent, true);
                mailSender.send(email);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }, executor);

        return new EmailResponseDTO(LocalDateTime.now(), "knowledgebase0630@gmail.com", emailRequestDTO.getTo());
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
        } else {
            throw new InvalidNotificationTypeException("Invalid Notification Type");
        }

        return templateDetails;
    }
}

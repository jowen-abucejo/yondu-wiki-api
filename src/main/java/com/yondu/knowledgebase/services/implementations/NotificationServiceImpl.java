package com.yondu.knowledgebase.services.implementations;

import com.yondu.knowledgebase.DTO.email.EmailRequestDTO;
import com.yondu.knowledgebase.DTO.notification.NotificationDTO;
import com.yondu.knowledgebase.DTO.notification.NotificationDTOMapper;
import com.yondu.knowledgebase.DTO.page.PaginatedResponse;
import com.yondu.knowledgebase.Utils.Util;
import com.yondu.knowledgebase.entities.Notification;
import com.yondu.knowledgebase.entities.User;
import com.yondu.knowledgebase.enums.ContentType;
import com.yondu.knowledgebase.enums.NotificationType;
import com.yondu.knowledgebase.exceptions.MissingFieldException;
import com.yondu.knowledgebase.exceptions.NoContentException;
import com.yondu.knowledgebase.exceptions.ResourceNotFoundException;
import com.yondu.knowledgebase.exceptions.UserException;
import com.yondu.knowledgebase.repositories.NotificationRepository;
import com.yondu.knowledgebase.repositories.UserRepository;
import com.yondu.knowledgebase.services.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    private NotificationRepository notificationRepository;
    private UserRepository userRepository;
    private EmailServiceImpl emailService;

    public NotificationServiceImpl(NotificationRepository notificationRepository, UserRepository userRepository,EmailServiceImpl emailService) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    private final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

    @Override
    public NotificationDTO.BaseResponse createNotification(NotificationDTO.BaseRequest notification) {
        log.info("NotificationServiceImpl.createNotification()");
        log.info("notification : " + notification);

        // Validations
        if(notification.userId() <= 0){
            throw new MissingFieldException("userId");
        }else if(Util.isNullOrWhiteSpace(notification.message())){
            throw new MissingFieldException("message");
        }

        User user = userRepository.findById(notification.userId()).orElseThrow(()-> new ResourceNotFoundException(String.format("User ID not found: %d",notification.userId())));

        Notification newNotification = NotificationDTOMapper.mapBaseToEntity(notification);
        newNotification.setTimestamp(LocalDateTime.now());
        newNotification.setRead(false);
        Notification createdNotification = null;
        try{
            createdNotification = notificationRepository.save(newNotification);
        }catch (Exception ex){
            throw ex;
        }

        if(newNotification.getType() == null){
            newNotification.setType(NotificationType.GENERAL.getCode());
        }

        String redirectingLink = getLinkForEmailNotification(newNotification);
        emailService.sendEmail(new EmailRequestDTO(user.getEmail(),newNotification.getNotificationType(),newNotification.getType(),redirectingLink));

        NotificationDTO.BaseResponse notificationBase = NotificationDTOMapper.mapEntityToBaseResponse(createdNotification);
        return notificationBase;
    }

    @Override
    public PaginatedResponse<NotificationDTO.BaseResponse> getUserNotifications(int page, int size) {
        log.info("NotificationServiceImpl.getUserNotifications()");
        log.info("page : " + page);
        log.info("size : " + size);

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<Notification> fetchedNotification = notificationRepository.getNotificationsByUser(user, pageRequest);

        if(fetchedNotification.isEmpty()){
            throw new NoContentException("There are no notifications found.");
        }

        List<NotificationDTO.BaseResponse> notifications = fetchedNotification.getContent()
                .stream()
                .map(notification -> NotificationDTOMapper.mapEntityToBaseResponse(notification))
                .collect(Collectors.toList());

        PaginatedResponse paginatedResponse = new PaginatedResponse(
                notifications,
                page,
                size,
                (long)notifications.size()
        );

        return paginatedResponse;
    }

    @Override
    public NotificationDTO.Base readNotification(long notificationId) {
        log.info("NotificationServiceImpl.readNotification()");
        log.info("notificationId : " + notificationId);

        Notification notification = notificationRepository.findById(notificationId).orElseThrow(() -> new ResourceNotFoundException("Notification not found."));
        if(!notification.isRead()){
            notification.setRead(true);
            notification = notificationRepository.save(notification);
        }

        NotificationDTO.Base notificationBase = NotificationDTOMapper.mapEntityToBase(notification);
        return notificationBase;
    }

    @Override
    public boolean readAllNotification() {
        log.info("NotificationServiceImpl.readAllNotification()");

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        notificationRepository.readAllNotification(user);

        return true;
    }

    private String getLinkForEmailNotification (Notification notification){
        String link = "";
        if (notification.getType().toUpperCase().equals(ContentType.COMMENT)){
            link = String.format("http://localhost:8080/comments/%d",notification.getTypeId());
        } else if (notification.getType().toUpperCase().equals(ContentType.PAGE)) {
            link = String.format("http://localhost:8080/pages/%d",notification.getTypeId());
        } else if (notification.getType().toUpperCase().equals(ContentType.POST)) {
            link = String.format("http://localhost:8080/post/%d",notification.getTypeId());
        }
        return link;
    }
}

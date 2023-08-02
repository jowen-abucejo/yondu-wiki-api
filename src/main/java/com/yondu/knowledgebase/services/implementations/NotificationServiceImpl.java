package com.yondu.knowledgebase.services.implementations;

import com.yondu.knowledgebase.DTO.email.EmailDTO;
import com.yondu.knowledgebase.DTO.notification.NotificationDTO;
import com.yondu.knowledgebase.DTO.notification.NotificationDTO.BaseResponse;
import com.yondu.knowledgebase.DTO.notification.NotificationDTOMapper;
import com.yondu.knowledgebase.DTO.page.PaginatedResponse;
import com.yondu.knowledgebase.DTO.user.UserDTOMapper;
import com.yondu.knowledgebase.Utils.Util;
import com.yondu.knowledgebase.config.WebSocketHandler;
import com.yondu.knowledgebase.entities.Notification;
import com.yondu.knowledgebase.entities.User;
import com.yondu.knowledgebase.enums.ContentType;
import com.yondu.knowledgebase.enums.NotificationType;
import com.yondu.knowledgebase.exceptions.MissingFieldException;
import com.yondu.knowledgebase.exceptions.NoContentException;
import com.yondu.knowledgebase.exceptions.ResourceNotFoundException;
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

import java.io.IOException;
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

    @Autowired
    private WebSocketHandler webSocketHandler;

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
        User fromUser = userRepository.findById(notification.fromUserId()).orElseThrow(()-> new ResourceNotFoundException(String.format("User ID not found: %d",notification.userId())));

        Notification newNotification = NotificationDTOMapper.mapBaseToEntity(notification, user, fromUser);
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

        Map <String,String> links = getLinksForEmailNotificationTemplate(newNotification);
        EmailDTO.GeneralRequest email = new EmailDTO.GeneralRequest(user.getEmail(),fromUser.getEmail(),links.get("fromUserLink"),newNotification.getNotificationType(),newNotification.getType(),links.get("contentLink"));
        emailService.sendEmail(email);

        NotificationDTO.BaseResponse notificationBase = NotificationDTOMapper.mapEntityToBaseResponse(createdNotification);
        try {
            webSocketHandler.sendMessageToClient(notificationBase.user().id(), notificationBase);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    @Override
    public NotificationDTO.TotalUnreadNotification getTotalUnreadNotification () {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long totalUnreadNotification = notificationRepository.totalUnreadNotification(user);
        NotificationDTO.TotalUnreadNotification response =  NotificationDTOMapper.mapToTotalUnreadNotification(user,totalUnreadNotification);
        return response;
    }

    private Map <String,String> getLinksForEmailNotificationTemplate (Notification notification){
        Map <String,String> data = new HashMap<>();
        if (notification.getType().equals(ContentType.REPLY.getCode())){
            data.put("fromUserLink",String.format("http://localhost:8080/user/%d", notification.getFromUser().getId()));
            data.put("contentLink",String.format("http://localhost:8080/comments/%d",notification.getTypeId()));
        } else if (notification.getType().equals(ContentType.PAGE.getCode())) {
            data.put("fromUserLink",String.format("http://localhost:8080/user/%d", notification.getFromUser().getId()));
            data.put("contentLink",String.format("http://localhost:8080/pages/%d",notification.getTypeId()));
        } else if (notification.getType().equals(ContentType.POST.getCode())) {
            data.put("fromUserLink",String.format("http://localhost:8080/user/%d", notification.getFromUser().getId()));
            data.put("contentLink",String.format("http://localhost:8080/posts/%d",notification.getTypeId()));
        }
        return data;
    }

    @Override
    public PaginatedResponse<NotificationDTO.BaseResponse> getUserUnreadNotifications(int page, int size) {
         User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<Notification> fetchedNotification = notificationRepository.getUnreadNotification(user, pageRequest);

        if(fetchedNotification.isEmpty()){
            throw new NoContentException("There are no unread notifications found.");
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
}

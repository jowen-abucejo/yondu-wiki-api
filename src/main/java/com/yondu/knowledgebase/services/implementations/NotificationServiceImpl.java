package com.yondu.knowledgebase.services.implementations;

import com.yondu.knowledgebase.DTO.notification.NotificationDTO;
import com.yondu.knowledgebase.DTO.notification.NotificationDTOMapper;
import com.yondu.knowledgebase.DTO.page.PaginatedResponse;
import com.yondu.knowledgebase.Utils.Util;
import com.yondu.knowledgebase.entities.Notification;
import com.yondu.knowledgebase.entities.User;
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
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    private final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

    @Override
    public NotificationDTO.Base createNotification(NotificationDTO.Base notification) {
        log.info("NotificationServiceImpl.createNotification()");
        log.info("notification : " + notification);

        // Validations
        if(notification.userId() <= 0){
            throw new MissingFieldException("userId");
        }else if(Util.isNullOrWhiteSpace(notification.message())){
            throw new MissingFieldException("message");
        }

        Notification newNotification = NotificationDTOMapper.mapBaseToEntity(notification);
        newNotification.setTimestamp(LocalDateTime.now());
        newNotification.setRead(false);
        Notification createdNotification = notificationRepository.save(newNotification);

        NotificationDTO.Base notificationBase = NotificationDTOMapper.mapEntityToBase(createdNotification);
        return notificationBase;
    }

    @Override
    public PaginatedResponse<NotificationDTO.Base> getUserNotifications(long userId, int page, int size) {
        log.info("NotificationServiceImpl.getUserNotifications()");
        log.info("userId : " + userId);
        log.info("page : " + page);
        log.info("size : " + size);

        User user = new User(userId);
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<Notification> fetchedNotification = notificationRepository.getNotificationsByUser(user, pageRequest);

        if(fetchedNotification.isEmpty()){
            throw new NoContentException("There are no notifications found.");
        }

        List<NotificationDTO.Base> notifications = fetchedNotification.getContent()
                .stream()
                .map(notification -> NotificationDTOMapper.mapEntityToBase(notification))
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
}

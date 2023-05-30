package com.yondu.knowledgebase.DTO.notification;

import com.yondu.knowledgebase.entities.Notification;
import com.yondu.knowledgebase.entities.User;

public class NotificationDTOMapper {

    public static NotificationDTO.Base mapEntityToBase(Notification notification){
        return new NotificationDTO.Base(notification.getId(),
                notification.getUser().getId(),
                notification.getMessage(),
                notification.getTimestamp(),
                notification.isRead());
    }

    public static Notification mapBaseToEntity(NotificationDTO.Base base) {
        return new Notification(
                base.id(),
                new User(base.userId()),
                base.message(),
                base.timestamp(),
                base.isRead()
        );
    }
}

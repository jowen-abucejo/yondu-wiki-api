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

    public static Notification mapBaseToEntity(NotificationDTO.BaseRequest base) {
        return new Notification(
                new User(base.userId()),
                base.message(),
                base.type(),
                base.typeId()
        );
    }

    public static NotificationDTO.BaseResponse mapEntityToBaseResponse(Notification notification) {
        return new NotificationDTO.BaseResponse(
                notification.getId(),
                notification.getUser().getId(),
                notification.getMessage(),
                notification.getTimestamp(),
                notification.isRead(),
                notification.getType(),
                notification.getTypeId()
        );
    }
}

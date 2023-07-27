package com.yondu.knowledgebase.DTO.notification;

import com.yondu.knowledgebase.DTO.user.UserDTOMapper;
import com.yondu.knowledgebase.entities.Notification;
import com.yondu.knowledgebase.entities.User;

public class NotificationDTOMapper {

    public static NotificationDTO.Base mapEntityToBase(Notification notification){
        return new NotificationDTO.Base(notification.getId(),
                notification.getUser().getId(),
                notification.getFromUser().getId(),
                notification.getMessage(),
                notification.getTimestamp(),
                notification.isRead(),
                notification.getNotificationType());
    }

    public static Notification mapBaseToEntity(NotificationDTO.BaseRequest base, User user, User fromUser) {
        return new Notification(
                user,
                fromUser,
                base.message(),
                base.notificationType(),
                base.type(),
                base.typeId()
        );
    }

    public static NotificationDTO.BaseResponse mapEntityToBaseResponse(Notification notification) {
        return new NotificationDTO.BaseResponse(
                notification.getId(),
                UserDTOMapper.mapToGeneralResponse(notification.getUser()),
                UserDTOMapper.mapToGeneralResponse(notification.getFromUser()),
                notification.getMessage(),
                notification.getTimestamp(),
                notification.isRead(),
                notification.getType(),
                notification.getTypeId(),
                notification.getNotificationType()
        );
    }

    public static  NotificationDTO.TotalUnreadNotification mapToTotalUnreadNotification (User user, Long totalUnread){
        return new NotificationDTO.TotalUnreadNotification(
                UserDTOMapper.mapToGeneralResponse(user),
                totalUnread
        );
    }
}

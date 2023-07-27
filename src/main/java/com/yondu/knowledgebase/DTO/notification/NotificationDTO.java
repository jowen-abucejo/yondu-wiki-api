package com.yondu.knowledgebase.DTO.notification;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yondu.knowledgebase.DTO.user.UserDTO;

import java.time.LocalDateTime;

public class NotificationDTO {

    public record Base(long id, long userId, long fromUserId, String message, LocalDateTime timestamp, boolean isRead, String notificationType) {}
    public record BaseRequest(@JsonProperty("user_id") long userId, @JsonProperty("from_user_id") long fromUserId, String message, @JsonProperty("notification_type") String notificationType, String type, @JsonProperty("type_id") long typeId) {}
    public record BaseResponse(long id, UserDTO.GeneralResponse user, UserDTO.GeneralResponse fromUser, String message, LocalDateTime timestamp, boolean isRead, String type, long typeId, String notificationType) {}
    public record TotalUnreadNotification (UserDTO.GeneralResponse user, long totalUnreadNotifications){}
}

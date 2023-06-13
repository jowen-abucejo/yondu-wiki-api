package com.yondu.knowledgebase.DTO.notification;

import java.time.LocalDateTime;

public class NotificationDTO {

    public record Base(long id, long userId, long fromUserId, String message, LocalDateTime timestamp, boolean isRead) {}
    public record BaseRequest(long userId, long fromUserId, String message, String notificationType, String type, long typeId) {}
    public record BaseResponse(long id, long userId, long fromUserId, String message, LocalDateTime timestamp, boolean isRead, String type, long typeId) {}
}

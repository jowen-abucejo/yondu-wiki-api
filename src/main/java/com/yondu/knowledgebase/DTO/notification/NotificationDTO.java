package com.yondu.knowledgebase.DTO.notification;

import java.time.LocalDateTime;

public class NotificationDTO {

    public record Base(long id, long userId, String message, LocalDateTime timestamp, boolean isRead) {}
    public record BaseRequest(long userId, String message, String notificationType, String type, long typeId) {}
    public record BaseResponse(long id, long userId, String message, LocalDateTime timestamp, boolean isRead, String type, long typeId) {}
}

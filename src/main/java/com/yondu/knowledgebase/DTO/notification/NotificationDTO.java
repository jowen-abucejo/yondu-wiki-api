package com.yondu.knowledgebase.DTO.notification;

import java.time.LocalDateTime;

public class NotificationDTO {

    public record Base(long id, long userId, String message, LocalDateTime timestamp, boolean isRead) {}
}
